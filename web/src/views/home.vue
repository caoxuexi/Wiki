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
        <a-sub-menu v-for="item in level1" :key="item.id" :disabled="true">
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
      <a-list item-layout="vertical" size="large" :grid="{gutter:20,column:3}"
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
                <a :href="item.href">{{ item.name }}</a>
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

export default {
  name: 'Home',
  components: {
    StarOutlined,
    LikeOutlined,
    MessageOutlined,
  },

  mounted() {
    axios.get("/ebook/list", {
      params: {
        page: 1,
        size: 1000
      }
    }).then((response) => {
      const data = response.data;
      this.ebooks = data.content.list
    })
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
      ebooks: []
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