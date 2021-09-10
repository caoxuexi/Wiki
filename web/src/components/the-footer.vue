<template>
  <a-layout-footer style="text-align: center">
    文档协同开发<span v-show="user.id">，欢迎：{{user.name}}</span>
  </a-layout-footer>
</template>

<script>
  import store from "@/store";

  export default {
    name: 'the-footer',
    computed:{
      user() {
        return store.state.user
      }
    }
  };
</script>
