<template>
  <a-layout-header class="header">
    <div class="logo">西电知识库</div>
    <a-popconfirm
        title="确认退出登录?"
        ok-text="是"
        cancel-text="否"
        @confirm="logout()"
    >
      <a class="login-menu" v-show="user.id">
        <span>退出登录</span>
      </a>
    </a-popconfirm>
    <a class="login-menu" v-show="user.id">
      <span>您好：{{ user.name }}</span>
    </a>
    <a class="login-menu" v-show="!user.id" @click="showLoginModal">
      <span>登录</span>
    </a>
    <a-menu
        theme="dark"
        mode="horizontal"
        :style="{ lineHeight: '64px' }"
    >
      <!--  key无所谓只要每个item不一样即可    -->
      <a-menu-item key="/">
        <router-link to="/">首页</router-link>
      </a-menu-item>
      <a-menu-item key="/admin/user" v-if="user.id">
        <router-link to="/admin/user">用户管理</router-link>
      </a-menu-item>
      <a-menu-item key="/admin/ebook"  v-if="user.id">
        <router-link to="/admin/ebook">项目管理</router-link>
      </a-menu-item>
      <a-menu-item key="/admin/category"  v-if="user.id">
        <router-link to="/admin/category">分类管理</router-link>
      </a-menu-item>
      <a-menu-item key="/about">
        <router-link to="/about">关于我们</router-link>
      </a-menu-item>
    </a-menu>

    <a-modal
        title="登录"
        v-model:visible="loginModalVisible"
        :confirm-loading="loginModalLoading"
        @ok="login"
    >
      <a-form :model="loginUser" :rules="rules" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="登录名" name="loginName">
          <a-input v-model:value="loginUser.loginName"/>
        </a-form-item>
        <a-form-item label="密码" name="password">
          <a-input v-model:value="loginUser.password" type="password"/>
        </a-form-item>
      </a-form>
    </a-modal>
  </a-layout-header>
</template>

<script>
import axios from 'axios';
import {message} from 'ant-design-vue';
import store from "@/store";
import md5 from "../../public/js/md5";

export default {
  name: 'the-header',
  data() {
    return {
      loginModalVisible: false,
      loginModalLoading: false,
      loginUser: {
        loginName: "cao",
        password: ""
      },
      rules: {
        loginName: [{
          required: true,
          message: "用户名不能为空",
          trigger: 'blur'
        }],
        password: [{
          required: true,
          validator: async (rule, value) => {
            if (value.length < 6 || value.length > 20) {
              return Promise.reject('密码长度不符合规范，需在6~20位之间')
            } else {
              return Promise.resolve();
            }
          },
          trigger: 'blur'
        }]
      }
    }
  },
  computed: {
    user() {
      return store.state.user
    }
  },
  methods: {
    showLoginModal() {
      this.loginModalVisible = true;
    },

    // 登录
    login() {
      console.log("开始登录");
      this.loginModalLoading = true;
      this.loginUser.password = md5.hexMd5(this.loginUser.password + md5.KEY);
      axios.post('/user/login', this.loginUser).then((response) => {
        this.loginModalLoading = false;
        const data = response.data;
        if (data.success) {
          this.loginModalVisible = false;
          message.success("登录成功！");

          store.commit("setUser", data.content);
        } else {
          message.error(data.message);
        }
      });
    },

    // 退出登录
    logout() {
      console.log("退出登录开始");
      axios.get('/user/logout/' + this.user.token).then((response) => {
        const data = response.data;
        if (data.success) {
          message.success("退出登录成功！");
          store.commit("setUser", {});
        } else {
          message.error(data.message);
        }
      });
    },
  }
}
</script>

<style>

.logo {
  width: 120px;
  height: 31px;
  /*background: rgba(255, 255, 255, 0.2);*/
  /*margin: 16px 28px 16px 0;*/
  float: left;
  color: white;
  font-size: 18px;
}

.login-menu {
  float: right;
  color: white;
  padding-left: 10px;
}
</style>
