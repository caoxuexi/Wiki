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
            <a-button type="primary">
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
</template>

<script>
import axios from "axios";

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
        this.ebooks = data.content;
        // 重置分页按钮
        this.pagination.current = params.page;
        this.pagination.total = data.content.total;
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
    }
  },
  mounted() {
    this.handleQuery({})
  },
  data() {
    return {
      columns: columns,
      pagination: {
        current: 1,
        pageSize: 10,
        total: 0
      },
      loading: false,
      ebooks: "",
      param: "",
    }
  }
}
</script>