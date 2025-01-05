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
import CheckInCalendar from '../views/TestPages/CheckInCalendar.vue';  // 导入签到日历组件
import CourseDetail from '@/views/CourseDetail.vue'
import StreamMessage from '../views/TestPages/StreamMessage.vue'  // 导入流式消息测试组件
import Teacher from '../views/Teacher.vue'; // 导入 Teacher 组件
import StudentManagement from '../views/teacher/StudentManagement.vue' // 导入 StudentManagement 组件
import StudyRecords from '../views/teacher/StudyRecords.vue' // 导入 StudyRecords 组件
import CourseZhangjie from '../components/Coursezhangjie.vue'; // 导入 CourseZhangjie 组件
import UserProfileManagement from '../views/teacher/UserProfileManagement.vue'; // 导入 UserProfileManagement 组件
import CourseManagement from '../views/teacher/CourseManagement.vue'; // 导入 CourseManagement 组件
import AiTeaching from '../views/teacher/AiTeaching.vue'; // 导入 AiTeaching 组件
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'

// 配置路由
const routes = [
  {
    path: '/',            
    redirect: '/login'    // 将根路径重定向到登录页面
  },
  {
    path: '/home',        // 将原来的首页移到 /home 路径
    name: 'HomePage',     
    component: Home,
    meta: { 
      requiresAuth: true,
      keepAlive: true
    }
  },
  {
    path: '/project-square',   // 项目广场页面路径
    name: 'ProjectSquare',     // 路由名称
    component: ProjectSquare,
    meta: { requiresAuth: true }
  },
  {
    path: '/ai-assistant',     // AI助手页面路径
    name: 'AiAssistant',       // 路由名称
    component: AiAssistant,    // 映射 AiAssistant 组件
    meta: { requiresAuth: true }
  },
  {
    path: '/ai-learning',      // AI学习页面路径
    name: 'AiLearning',        // 路由名称
    component: AiLearning,      // 映射 AiLearning 组件
    meta: { requiresAuth: true }
  },
  {
    path: '/personal-info',    // 个人信息页面路径
    name: 'PersonalInfo',      // 路由名称
    component: PersonalInfo,    // 映射 PersonalInfo 组件
    meta: { requiresAuth: true }
  },
  {
    path: '/learning',         // 学习页面路径
    name: 'Learning',          // 路由名称
    component: Learning,        // 映射 Learning 组件
    meta: {
      keepAlive: true // 确保组件不被缓存
    }
  },
  {
    path: '/report-generation', // 报告生成页面路径
    name: 'ReportGeneration',   // 路由名称
    component: ReportGeneration, // 映射 ReportGeneration 组件
    meta: { requiresAuth: true }
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
  },
  {
    path: '/check-in',         // 签到页面路径
    name: 'check-in',
    component: CheckInCalendar,
    meta: { requiresAuth: true }
  },
  {
    path: '/course/:courseId/units',
    name: 'CourseUnits',
    component: () => import('../views/CourseUnits.vue')
  },
  {
    path: '/course/:courseId/unit/:unitId',
    name: 'UnitChapters',
    component: () => import('../views/UnitChapters.vue'),
    props: route => ({
      courseId: route.params.courseId,
      unitId: route.params.unitId,
      unitTitle: decodeURIComponent(route.params.unitTitle || '')
    })
  },
  {
    path: '/course/:courseId',
    name: 'CourseDetail',
    component: CourseDetail,
    props: true,
    children: [
      {
        path: 'students',
        component: StudentManagement,
        name: 'course-students',
        meta: { requiresAuth: true }
      },
      {
        path: 'student/:userId/records',
        component: StudyRecords,
        name: 'StudyRecords',
        props: true,
        meta: { requiresAuth: true }
      }
    ]
  },
  {
    path: '/stream-message',
    name: 'stream-message',
    component: StreamMessage,
    meta: { requiresAuth: true }
  },
  {
    path: '/c-assistant',
    name: 'C语言助教',
    component: StreamMessage,
    meta: {
      requiresAuth: true  // 需要登录才能访问
    }
  },
  {
    path: '/shiki-chat',
    name: 'ShikiChat',
    component: () => import('../views/TestPages/ShikiStreamMessage.vue'),
    meta: {
      title: 'Shiki Chat',
      requiresAuth: true
    }
  },
  {
    path: '/teacher',
    component: Teacher,
    children: [
      {
        path: '',
        name: 'UserProfileManagement',
        component: UserProfileManagement,
        meta: { requiresAuth: true }
      },
      {
        path: 'course-management',
        name: 'CourseManagement',
        component: CourseManagement,
        meta: { requiresAuth: true }
      },
      {
        path: 'student-management',
        name: 'StudentManagement',
        component: StudentManagement,
        meta: { requiresAuth: true }
      },
      {
        path: 'study-records',
        name: 'StudyRecords',
        component: StudyRecords,
        meta: { requiresAuth: true }
      },
      {
        path: 'ai-teaching',
        name: 'AiTeaching',
        component: AiTeaching,
        meta: { requiresAuth: true }
      }
    ]
  },
  {
    path: '/course-zhangjie/:courseId',
    name: 'CourseZhangjie',
    component: CourseZhangjie,
    props: true,
    meta: { requiresAuth: true }
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
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
  const publicPages = ['/login', '/register'];
  const authRequired = !publicPages.includes(to.path);

  if (authRequired && !token) {
    next('/login');
  } else if (token && publicPages.includes(to.path)) {
    // 已登录用户访问登录或注册页面时重定向到首页
    next('/home');
  } else {
    next();
  }
});

export default router;
