<template>
  <a-layout-footer style="text-align: center">
    文档协同开发 {{user.name}}
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
