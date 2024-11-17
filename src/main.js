// src/main.js
import { createApp } from 'vue';  // 导入 Vue
import App from './App.vue';  // 导入根组件
import router from './router';  // 导入路由配置
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

// 创建 Vue 应用并使用 Vue Router
const app = createApp(App)

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(router)  // 启用 Vue Router
app.use(ElementPlus)
app.mount('#app');  // 挂载 Vue 应用到页面上的 #app 元素
