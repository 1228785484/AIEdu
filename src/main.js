// src/main.js
import { createApp } from 'vue';  // 导入 Vue
import App from './App.vue';  // 导入根组件
import router from './router';  // 导入路由配置

// 创建 Vue 应用并使用 Vue Router
createApp(App)
  .use(router)  // 启用 Vue Router
  .mount('#app');  // 挂载 Vue 应用到页面上的 #app 元素
