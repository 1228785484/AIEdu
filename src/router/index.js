import { createRouter, createWebHistory } from 'vue-router';

// 导入各个页面组件
import Home from '../views/Home.vue';
import ProjectSquare from '../views/ProjectSquare.vue';
import AiAssistant from '../views/AiAssistant.vue';
import AiLearning from '../views/AiLearning.vue';
import PersonalInfo from '../views/PersonalInfo.vue';
import Learning from '../views/Learning.vue';
import ReportGeneration from '../views/ReportGeneration.vue';
import DatePicker from '../views/TestPages/DatePicker.vue';
import WebsocketTest from '../views/TestPages/WebsocketTest.vue';
import FileUploader from '../views/TestPages/FileUploader.vue';  // 导入 FileUploader 组件


// 配置路由
const routes = [
  {
    path: '/',            // 根路径
    name: 'Home',          // 路由名称
    component: Home       // 映射 Home 组件
  },
  {
    path: '/project-square',   // 项目广场页面路径
    name: 'ProjectSquare',     // 路由名称
    component: ProjectSquare  // 映射 ProjectSquare 组件
  },
  {
    path: '/ai-assistant',     // AI助手页面路径
    name: 'AiAssistant',       // 路由名称
    component: AiAssistant    // 映射 AiAssistant 组件
  },
  {
    path: '/ai-learning',      // AI学习页面路径
    name: 'AiLearning',        // 路由名称
    component: AiLearning      // 映射 AiLearning 组件
  },
  {
    path: '/personal-info',    // 个人信息页面路径
    name: 'PersonalInfo',      // 路由名称
    component: PersonalInfo    // 映射 PersonalInfo 组件
  },
  {
    path: '/learning',         // 学习页面路径
    name: 'Learning',          // 路由名称
    component: Learning        // 映射 Learning 组件
  },
  {
    path: '/report-generation', // 报告生成页面路径
    name: 'ReportGeneration',   // 路由名称
    component: ReportGeneration // 映射 ReportGeneration 组件
  },
  {
    path: '/date-picker',
    name: 'date-picker',
    component: DatePicker,
    meta: { requiresAuth: true }
  },
  {
    path: '/websocket-test',
    name: 'websocket-test',
    component: WebsocketTest,
    meta: { requiresAuth: true }
  },
  {
    path: '/file-uploader',
    name: 'file-uploader',
    component: FileUploader,
    meta: { requiresAuth: true }
  }
];

// 创建路由实例
const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),  // 使用 HTML5 History 模式
  routes  // 路由配置
});

// 添加全局路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token');
  const publicPages = ['/login', '/register']; // 不需要登录就能访问的页面
  const authRequired = !publicPages.includes(to.path);

  // 如果需要登录且没有token
  if (authRequired && !token) {
    // 如果是首页，允许访问但在组件内部处理登录状态
    if (to.path === '/') {
      next();
    } else {
      next('/');
    }
  } else {
    next();
  }
});

export default router;
