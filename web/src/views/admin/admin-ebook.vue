<template>

  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <a-table
          :columns="columns"
          :row-key="record => record.id"
          :data-source="ebooks"
          :pagination="pagination"
          :loading="loading"
          @change="handleTableChange"
      >
        <template #cover="{ text: cover }">
          <img v-if="cover" :src="cover" alt="avatar"/>
        </template>
        <template v-slot:action="{ text, record }">
          <a-space size="small">
            <a-button type="primary" @click="edit(record)">
              编辑
            </a-button>
            <a-button type="danger">
              删除
            </a-button>
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
        <a-input v-model:value="ebook.cover" />
      </a-form-item>
      <a-form-item label="名称">
        <a-input v-model:value="ebook.name" />
      </a-form-item>
      <a-form-item label="分类">
        <a-cascader
            v-model:value="categoryIds"
            :field-names="{ label: 'name', value: 'id', children: 'children' }"
            :options="level1"
        />
      </a-form-item>
      <a-form-item label="描述">
        <a-input v-model:value="ebook.description" type="textarea" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script>
import axios from "axios";
import { message } from 'ant-design-vue';
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
      console.log("看看自带的分页参数都有啥：" + pagination);
      this.handleQuery({
        page: pagination.current,
        size: pagination.pageSize
      });
    },
    /**
     * 对话框选择确定的逻辑
     */
    handleModalOk () {
      this.modalLoading = true;
      this.ebook.category1Id =  this.categoryIds[0];
      this.ebook.category2Id = this.categoryIds[1];
      axios.post("/ebook/save",  this.ebook.value).then((response) => {
        this.modalLoading = false;
        const data = response.data; // data = commonResp
        if (data.success) {
          this.modalVisible = false;
          // 重新加载列表
          this.handleQuery({
            page:  this.pagination.current,
            size:  this.pagination.pageSize,
          });
        } else {
          message.error(data.message);
        }
      });
    },
    /**
     * 点击操作中的编辑按钮，弹出对话框
     */
    edit (record) {
      console.log(record)
      this.modalVisible = true;
      this.ebook = Tool.copy(record);
      this.categoryIds = [this.ebook.category1Id, this.ebook.category2Id]
    }
  },
  mounted() {
    this.handleQuery({
      page:1,
      size: this.pagination.pageSize
    })
  },
  data() {
    return {
      columns: columns,
      // 电子书相关
      loading: false,
      ebooks: "",
      pagination: {
        current: 1,
        pageSize: 4,
        total: 0
      },
      param: "",
      //表单相关
      ebook:"",
      categoryIds:"",
      modalVisible:false,
      modalLoading:false,
      level1:""
    }
  }
}
</script>