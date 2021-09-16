<template>
  <a-layout-footer style="text-align: center">
    文档协同开发<span v-show="user.id">，欢迎：{{user.name}}</span>
  </a-layout-footer>
</template>

<script>
import store from "@/store";
import {notification} from "ant-design-vue";
import {Tool} from "@/util/tool";

export default {
  name: 'the-footer',
  computed:{
    user() {
      return store.state.user
    }
  },
  methods:{
    //下面这四个on方法不用我们调用，是websocket内部根据当前事件触发自动调用的
    onOpen(){
      console.log('WebSocket连接成功，状态码：', this.websocket.readyState)
    },
    onMessage (event){
      console.log('WebSocket收到消息：', event.data);
      notification['info']({
        message: '收到消息',
        description: event.data,
      });
    },
    onError(){
      console.log('WebSocket连接错误，状态码：',  this.websocket.readyState)
    },
    onClose () {
      console.log('WebSocket连接关闭，状态码：',  this.websocket.readyState)
    },
    initWebSocket() {
      // 连接成功
      this.websocket.onopen = this.onOpen;
      // 收到消息的回调
      this.websocket.onmessage = this.onMessage;
      // 连接错误
      this.websocket.onerror = this.onError;
      // 连接关闭的回调
      this.websocket.onclose = this.onClose;
    }
  },
  mounted() {
    // WebSocket，window是前端自带的参数
    if ('WebSocket' in window) {
      this.token = Tool.uuid(10);
      // 连接地址：ws://127.0.0.1:8880/ws/xxx
      this.websocket = new WebSocket(process.env.VUE_APP_WS_SERVER + '/ws/' + this.token);
      this.initWebSocket()

      // 关闭
      // websocket.close();
    } else {
      alert('当前浏览器 不支持')
    }
  },
  data(){
    return{
      websocket:"",
      token:""
    }
  }
};
</script>

