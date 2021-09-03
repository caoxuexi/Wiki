<template>
  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <p>
        <a-form layout="inline" :model="param">
          <a-form-item>
            <a-button type="primary" @click="handleQuery()">
              查询
            </a-button>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="add()">
              新增
            </a-button>
          </a-form-item>
        </a-form>
      </p>
      <p>
        <a-alert
            class="tip"
            message="小提示：这里的分类会显示到首页的侧边菜单"
            type="info"
            closable
        />
      </p>
      <a-table
          v-if="levels.length > 0"
          :columns="columns"
          :row-key="record => record.id"
          :data-source="levels"
          :loading="loading"
          :pagination="false"
          :defaultExpandAllRows="true"
      >
        <template #cover="{ text: cover }">
          <img v-if="cover" :src="cover" alt="avatar"/>
        </template>
        <template v-slot:action="{ text, record }">
          <a-space size="small">
            <a-button type="primary" @click="edit(record)">
              编辑
            </a-button>
            <a-popconfirm
                title="删除后不可恢复，确认删除?"
                ok-text="是"
                cancel-text="否"
                @confirm="handleDelete(record.id)"
            >
              <a-button type="danger">
                删除
              </a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </a-table>
    </a-layout-content>
  </a-layout>
  <a-modal
      title="分类表单"
      v-model:visible="modalVisible"
      :confirm-loading="modalLoading"
      @ok="handleModalOk"
  >
    <a-form :model="category" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
      <a-form-item label="名称">
        <a-input v-model:value="category.name"/>
      </a-form-item>
      <a-form-item label="父分类">
        <a-select
            v-model:value="category.parent"
            ref="select"
        >
          <!--    一级分类      -->
          <a-select-option :value="0">
            无
          </a-select-option>
          <!--   disable用来防止父分类选到自己       -->
          <a-select-option v-for="c in levels" :key="c.id" :value="c.id" :disabled="category.id === c.id">
            {{ c.name }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="顺序">
        <a-input v-model:value="category.sort"/>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script>
import axios from "axios";
import {message} from 'ant-design-vue';
import {Tool} from "@/util/tool";


const columns = [
  {
    title: '名称',
    dataIndex: 'name'
  },
  {
    title: '顺序',
    dataIndex: 'sort'
  },
  {
    title: 'Action',
    key: 'action',
    slots: {customRender: 'action'}
  }
];

export default {
  name: 'AdminCategory',
  components: {},
  methods: {
    handleQuery() {
      this.loading = true;
      // 如果不清空现有数据，则编辑保存重新加载数据后，再点编辑，则列表显示的还是编辑前的数据
      this.levels = [];
      axios.get("/category/all").then((response) => {
        this.loading = false;
        const data = response.data;
        if (data.success) {
          this.categorys = data.content;
          console.log("原始数组：", this.categorys);

          this.levels = [];
          this.levels = Tool.array2Tree(this.categorys, 0);
          console.log("树形结构：", this.levels);
        } else {
          message.error(data.message);
        }
      });
    },
    /**
     * 对话框选择确定的逻辑，即保存数据
     */
    handleModalOk() {
      this.modalLoading = true;
      this.category.category1Id = this.categoryIds[0];
      this.category.category2Id = this.categoryIds[1];
      axios.post("/category/save", this.category).then((response) => {
        this.modalLoading = false;
        const data = response.data; // data = commonResp
        if (data.success) {
          this.modalVisible = false;
          // 重新加载列表
          this.handleQuery();
        } else {
          message.error(data.message);
        }
      });
    },
    /**
     * 删除分类
     */
    handleDelete(id) {
      axios.delete("/category/delete/" + id).then((response) => {
        const data = response.data; // data = commonResp
        if (data.success) {
          // 重新加载列表
          this.handleQuery();
        } else {
          message.error(data.message);
        }
      });
    },
    /**
     * 点击操作中的编辑按钮，弹出对话框
     */
    edit(record) {
      console.log(record)
      this.modalVisible = true;
      //如果直接把record的值赋给this.category则会出现修改category致record也修改的情况，
      // 所以我们更希望是一个深考别，
      this.category = Tool.copy(record);
      this.categoryIds = [this.category.category1Id, this.category.category2Id]
    },
    /**
     * 新增分类
     */
    add() {
      this.modalVisible = true;
      this.category = {};
    }
  },
  mounted() {
    this.handleQuery()
  },
  data() {
    return {
      columns: columns,
      // 分类相关
      loading: false,
      categorys: [],
      //搜索的参数（先保留）
      param: {},
      //表单相关
      category: "",
      categoryIds: "",
      modalVisible: false,
      modalLoading: false,
      levels: ""
    }
  }
}
</script>
