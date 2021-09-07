<template>
  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <p>
        <a-form layout="inline" :model="param">
          <a-form-item>
            <a-input v-model:value="param.name" placeholder="名称">
            </a-input>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="handleQuery({page: 1, size: pagination.pageSize})">
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
      <a-table
          :columns="columns"
          :row-key="record => record.id"
          :data-source="ebooks"
          :pagination="pagination"
          :loading="loading"
          @change="handleTableChange"
      >
        <template #cover="{ text: cover }">
          <img class="ebookImg" v-if="cover" :src="cover" alt="avatar"/>
        </template>
        <template v-slot:category="{ text, record }">
          <span>{{ getCategoryName(record.category1Id) }} / {{ getCategoryName(record.category2Id) }}</span>
        </template>
        <template v-slot:action="{ text, record }">
          <a-space size="small">
            <router-link :to="'/admin/doc?ebookId='+record.id">
              <a-button type="primary">
                文档管理
              </a-button>
            </router-link>
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
      title="电子书表单"
      v-model:visible="modalVisible"
      :confirm-loading="modalLoading"
      @ok="handleModalOk"
  >
    <a-form :model="ebook" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
      <a-form-item label="封面">
        <a-input v-model:value="ebook.cover"/>
      </a-form-item>
      <a-form-item label="名称">
        <a-input v-model:value="ebook.name"/>
      </a-form-item>
      <a-form-item label="分类">
        <a-cascader
            v-model:value="categoryIds"
            :field-names="{ label: 'name', value: 'id', children: 'children' }"
            :options="level1"
        />
      </a-form-item>
      <a-form-item label="描述">
        <a-input v-model:value="ebook.description" type="textarea"/>
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
    title: '封面',
    dataIndex: 'cover',
    slots: {customRender: 'cover'}
  },
  {
    title: '名称',
    dataIndex: 'name'
  },
  {
    title: '分类',
    slots: {customRender: 'category'}
  },
  {
    title: '文档数',
    dataIndex: 'docCount'
  },
  {
    title: '阅读数',
    dataIndex: 'viewCount'
  },
  {
    title: '点赞数',
    dataIndex: 'voteCount'
  },
  {
    title: '操作',
    key: 'action',
    slots: {customRender: 'action'}
  }
];


export default {
  name: 'AdminEbook',
  components: {},
  methods: {
    handleQuery(params) {
      this.loading = true;
      axios.get("/ebook/list", {
        params: {
          page: params.page,
          size: params.size,
          name: this.param.name
        }
      }).then((response) => {
        this.loading = false;
        const data = response.data;
        if (data.success) {
          this.ebooks = data.content.list;
          // 重置分页按钮
          this.pagination.current = params.page;
          this.pagination.total = data.content.total;
        } else {
          message.error(data.message);
        }
      });
    },
    /**
     * 表格点击页码时触发
     */
    handleTableChange(pagination) {
      this.handleQuery({
        page: pagination.current,
        size: pagination.pageSize
      });
    },
    /**
     * 对话框选择确定的逻辑，即保存数据
     */
    handleModalOk() {
      this.modalLoading = true;
      //获取对话框中所选择的层级数据
      this.ebook.category1Id = this.categoryIds[0];
      this.ebook.category2Id = this.categoryIds[1];
      axios.post("/ebook/save", this.ebook).then((response) => {
        this.modalLoading = false;
        const data = response.data; // data = commonResp
        if (data.success) {
          this.modalVisible = false;
          // 重新加载列表
          this.handleQuery({
            page: this.pagination.current,
            size: this.pagination.pageSize,
          });
        } else {
          message.error(data.message);
        }
      });
    },
    /**
     * 删除电子书
     */
    handleDelete(id) {
      axios.delete("/ebook/delete/" + id).then((response) => {
        const data = response.data; // data = commonResp
        if (data.success) {
          // 重新加载列表
          this.handleQuery({
            page: this.pagination.current,
            size: this.pagination.pageSize,
          });
        } else {
          message.error(data.message);
        }
      });
    },
    /**
     * 查询分类信息
     */
    handleQueryCategory() {
      this.loading = true;
      axios.get("/category/all").then((response) => {
        this.loading = false;
        const data = response.data;
        if (data.success) {
          this.categorys = data.content;
          console.log("原始数组：", this.categorys);

          this.level1 = [];
          this.level1 = Tool.array2Tree(this.categorys, 0);
          console.log("树形结构：", this.level1);

          // 加载完分类后，再加载电子书，否则如果分类树加载很慢，则电子书渲染会报错
          this.handleQuery({
            page: 1,
            size:  this.pagination.pageSize,
          });
        } else {
          message.error(data.message);
        }
      });
    },
    /**
     * 获取电子书所属分类的方法
     */
    getCategoryName (cid) {
      let result = "";
      this.categorys.forEach((item) => {
        // console.log(item)
        if (item.id === cid) {
          // return item.name; // 注意，这里直接return不起作用
          result = item.name;
        }
      });
      return result;
    },
    /**
     * 点击操作中的编辑按钮，弹出对话框
     */
    edit(record) {
      console.log(record)
      this.modalVisible = true;
      //如果直接把record的值赋给this.ebook则会出现修改ebook致record也修改的情况，
      // 所以我们更希望是一个深考别，
      this.ebook = Tool.copy(record);
      this.categoryIds = [this.ebook.category1Id, this.ebook.category2Id]
    },
    /**
     * 新增电子书
     */
    add() {
      this.modalVisible = true;
      this.ebook = {};
    }
  },
  mounted() {
    this.handleQueryCategory();
  },
  data() {
    return {
      columns: columns,
      // 电子书相关
      loading: false,
      ebooks: [],
      pagination: {
        current: 1,
        pageSize: 5,
        total: 0
      },

      //搜索的参数
      param: {},

      //表单相关(含分类的id)
      ebook: "",
      categoryIds: [],  //a—model中选定的两级category数组
      modalVisible: false,
      modalLoading: false,

      //分类的层级展示数据
      level1: [],
      //总的分类数据
      categorys: [],
    }
  }
}
</script>

<style>
.ebookImg {
  width: 50px;
  height: 50px
}
</style>