<template>
  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <p>
        <a-form layout="inline" :model="param">
          <a-form-item>
            <a-input v-model:value="param.loginName" placeholder="登陆名">
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
          :data-source="users"
          :pagination="pagination"
          :loading="loading"
          @change="handleTableChange"
      >
        <template v-slot:action="{ text, record }">
          <a-space size="small">
            <a-button type="primary" @click="resetPassword(record)">
              重置密码
            </a-button>
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
  <!--新建用户或者修改昵称时用的对话框-->
  <a-modal
      title="用户表单"
      v-model:visible="modalVisible"
      :confirm-loading="modalLoading"
      @ok="handleModalOk"
  >
    <a-form :model="user" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
      <a-form-item label="登陆名">
        <a-input v-model:value="user.loginName" :disabled="!!user.id"/>
      </a-form-item>
      <a-form-item label="昵称">
        <a-input v-model:value="user.name"/>
      </a-form-item>
      <a-form-item label="密码" v-show="!user.id">
        <a-input v-model:value="user.password" type="password"/>
      </a-form-item>
    </a-form>
  </a-modal>

  <!-- 重置密码的对话框表单 -->
  <a-modal
      title="重置密码"
      v-model:visible="resetModalVisible"
      :confirm-loading="resetModalLoading"
      @ok="handleResetModalOk"
  >
    <a-form :model="user" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
      <a-form-item label="新密码">
        <a-input v-model:value="user.password" type="password"/>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script>
import axios from 'axios';
import {message} from 'ant-design-vue';
import {Tool} from "@/util/tool";

let hexMd5
let KEY
const columns = [
  {
    title: '登陆名',
    dataIndex: 'loginName'
  },
  {
    title: '名称',
    dataIndex: 'name'
  },
  {
    title: '密码',
    dataIndex: 'password'
  },
  {
    title: 'Action',
    key: 'action',
    slots: {customRender: 'action'}
  }
];

export default {
  name: 'AdminUser',
  data() {
    return {
      columns: columns,
      param: {},
      users: [],
      pagination: {
        current: 1,
        pageSize: 10,
        total: 0
      },
      loading: false,
      //重置密码对话框
      resetModalVisible: false,
      resetModalLoading: false,
      //表单
      user: {},
      modalVisible: false,
      modalLoading: false,
    }
  },
  mounted() {
    this.handleQuery({
      page: 1,
      size: this.pagination.pageSize,
    });
  },
  methods: {
    /**
     * 数据查询
     **/
    handleQuery(params) {
      this.loading = true;
      // 如果不清空现有数据，则编辑保存重新加载数据后，再点编辑，则列表显示的还是编辑前的数据
      this.users = [];
      axios.get("/user/list", {
        params: {
          page: params.page,
          size: params.size,
          loginName: this.param.loginName
        }
      }).then((response) => {
        this.loading = false;
        const data = response.data;
        if (data.success) {
          this.users = data.content.list;

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
        page: this.pagination.current,
        size: this.pagination.pageSize
      });
    },


    /**
     * 对话框确认提交
     */
    handleModalOk() {
      this.modalLoading = true;

      this.user.password = hexMd5(this.user.password + KEY);

      axios.post("/user/save", this.user).then((response) => {
        this.modalLoading = false;
        const data = response.data; // data = commonResp
        if (data.success) {
          this.modalVisible = false;

          // 重新加载列表
          this.handleQuery({
            page: this.pagination.current,
            size: this.pageSize,
          });
        } else {
          message.error(data.message);
        }
      });
    },

    /**
     * 编辑用户信息
     */
    edit(record) {
      this.modalVisible = true;
      this.user = Tool.copy(record);
    },

    /**
     * 新增用户
     */
    add() {
      this.modalVisible = true;
      this.user = {};
    },

    handleDelete(id) {
      axios.delete("/user/delete/" + id).then((response) => {
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
     * 重置密码对话框确定逻辑
     */
    handleResetModalOk() {
      this.resetModalLoading = true;

      this.user.password = hexMd5(this.user.password + KEY);

      axios.post("/user/reset-password", this.user).then((response) => {
        this.resetModalLoading = false;
        const data = response.data; // data = commonResp
        if (data.success) {
          this.resetModalVisible = false;

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
     * 重置密码
     */
    resetPassword(record) {
      this.resetModalVisible = true;
      this.user = Tool.copy(record);
      this.user.password = null;
    },

  }
}
</script>

<style scoped>
img {
  width: 50px;
  height: 50px;
}
</style>
