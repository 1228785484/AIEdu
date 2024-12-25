<template>
  <div class="container">
    <div class="sidebar">
      <nav>
        <ul>
          <li @click="currentComponent = 'UserProfileManagement'" :class="{ active: currentComponent === 'UserProfileManagement' }">
            <img src="@/assets/task.png" alt="用户管理" /> 用户管理
          </li>
          <li @click="currentComponent = 'CourseManagement'" :class="{ active: currentComponent === 'CourseManagement' }">
            <img src="@/assets/test.png" alt="课程管理" /> 课程管理
          </li>
          <li @click="currentComponent = 'StudentManagement'" :class="{ active: currentComponent === 'StudentManagement' }">
            <img src="@/assets/frequency.png" alt="学生管理" /> 学生管理
          </li>
          <li @click="currentComponent = 'StudyRecords'" :class="{ active: currentComponent === 'StudyRecords' }">
            <img src="@/assets/progress.png" alt="学生学习情况" /> 学生学习情况
          </li>
          <li @click="currentComponent = 'AiTeaching'" :class="{ active: currentComponent === 'AiTeaching' }">
            <img src="@/assets/test.png" alt="AI教案" /> AI教案
          </li>
        </ul>
      </nav>
    </div>
    <div class="main-content">
      <component :is="currentComponent" />
    </div>
  </div>
</template>

<script>
import UserProfileManagement from './teacher/UserProfileManagement.vue';
import CourseManagement from './teacher/CourseManagement.vue';
import StudentManagement from './teacher/StudentManagement.vue';
import StudyRecords from './teacher/StudyRecords.vue';
import AiTeaching from './teacher/AiTeaching.vue';

export default {
  name: 'TeacherView',
  components: {
    UserProfileManagement,
    CourseManagement,
    StudentManagement,
    StudyRecords,
    AiTeaching,
  },
  data() {
    return {
      currentComponent: 'UserProfileManagement', // 默认显示个人资料
    };
  },
};
</script>

<style scoped>
.container {
  display: flex;
  height: 100vh; /* 使容器占满整个视口高度 */
  background-color: #f4f4f4; /* 设置整体背景颜色 */
}

.sidebar {
  width: 20%;
  background-color: #ffffff; /* 左侧背景颜色改为白色 */
  padding: 20px;
  box-shadow: 2px 0 5px rgba(0, 0, 0, 0.1);
}

.sidebar nav ul {
  list-style-type: none;
  padding: 0;
}

.sidebar nav ul li {
  cursor: pointer;
  margin: 10px 0;
  padding: 10px;
  border-radius: 4px;
  transition: background-color 0.3s;
  display: flex;
  align-items: center;
}

.sidebar nav ul li img {
  margin-right: 10px;
}

.sidebar nav ul li:hover {
  background-color: #73b4f4; /* 悬浮时的浅蓝色 */
}

.sidebar nav ul li.active {
  background-color: #59a5f0; /* 选中时的浅蓝色 */
  font-weight: bold;
}

.main-content {
  width: 75%;
  padding: 20px;
  overflow-y: auto; /* 允许内容区域滚动 */
  background-color: #f4f4f4; /* 右侧背景颜色 */
}
</style>
