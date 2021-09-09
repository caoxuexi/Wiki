<template>
  <a-layout>
    <a-layout-sider width="200" style="background: #fff">
      <a-menu
          mode="inline"
          :style="{ height: '100%', borderRight: 0 }"
          @click="handleClick"
          :openKeys="openKeys"
      >
        <a-menu-item key="welcome">
          <MailOutlined/>
          <span>欢迎</span>
        </a-menu-item>
        <a-sub-menu v-for="item in level1" :key="item.id">
          <template v-slot:title>
            <span><user-outlined/>{{ item.name }}</span>
          </template>
          <a-menu-item v-for="child in item.children" :key="child.id">
            <MailOutlined/>
            <span>{{ child.name }}</span>
          </a-menu-item>
        </a-sub-menu>
        <a-menu-item key="tip" :disabled="true">
          <span>以上菜单在分类管理配置</span>
        </a-menu-item>
      </a-menu>
    </a-layout-sider>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <div class="welcome" v-show="isShowWelcome">
        <!--   欢迎页面，后期用组件代替     -->
        <span>欢迎来到电子书</span>
      </div>
      <a-list v-show="!isShowWelcome" item-layout="vertical" size="large" :grid="{gutter:20,column:3}"
              :data-source="ebooks">
        <template #footer>
          <div>
            <b>ant design vue</b>
            footer part
          </div>
        </template>
        <template #renderItem="{ item }">  <!--它会将data-source项拿出来命名为item-->
          <a-list-item key="item.name">
            <template #actions>
          <span v-for="{ type, text } in actions" :key="type">
            <component v-bind:is="type" style="margin-right: 8px"/>
            {{ text }}
          </span>
            </template>
            <a-list-item-meta :description="item.description">
              <template #title>
                <router-link :to="'/doc?ebookId='+item.id">{{ item.name }}</router-link>
              </template>
              <template #avatar>
                <a-avatar :src="item.cover"/>
              </template>
            </a-list-item-meta>
          </a-list-item>
        </template>
      </a-list>
    </a-layout-content>
  </a-layout>
</template>

<script>
import axios from 'axios'
import {StarOutlined, LikeOutlined, MessageOutlined} from '@ant-design/icons-vue';
import {Tool} from "@/util/tool";
import {message} from "ant-design-vue";

export default {
  name: 'Home',
  components: {
    StarOutlined,
    LikeOutlined,
    MessageOutlined,
  },
  methods: {
    /**
     * 二级分类菜单点击方法
     * @param value
     */
    handleClick(value) {
      console.log(value)
      if (value.key === "welcome") {
        this.isShowWelcome = true;
      } else {
        this.categoryId2 = value.key;
        this.isShowWelcome = false;
        this.handleQueryEbook();
      }
    },
    /**
     * 查询分类数据
     */
    handleQueryCategory() {
      axios.get("/category/all").then((response) => {
        const data = response.data;
        if (data.success) {
          this.categorys = data.content;
          console.log("原始数组：", this.categorys);

          this.level1 = [];
          this.level1 = Tool.array2Tree(this.categorys, 0);
          console.log("树形结构：", this.level1);
        } else {
          message.error(data.message);
        }
      });
    },
    /**
     * 根据二级分类id分页查询电子书
     */
    handleQueryEbook() {
      axios.get("/ebook/list", {
        params: {
          page: 1,
          size: 1000,
          categoryId2: this.categoryId2,
        },
      }).then((response) => {
        const data = response.data;
        this.ebooks = data.content.list;
      });
    }
  },

  mounted() {
    this.handleQueryCategory()
  },

  data() {
    return {
      actions: [
        {
          type: 'StarOutlined',  //星星
          text: '156',
        },
        {
          type: 'LikeOutlined',  //点赞
          text: '156',
        },
        {
          type: 'MessageOutlined', //评论
          text: '2',
        },
      ],
      ebooks: [],
      level1: [],
      //显示欢迎页面还是显示电子书
      isShowWelcome: true,
      //通过菜单选中的二级分类的id
      categoryId2: 0,
    }
  }
}
</script>


<style scoped>
.ant-avatar {
  width: 50px;
  height: 50px;
  line-height: 50px;
  border-radius: 8%;
  margin: 5px 0;
}
</style>