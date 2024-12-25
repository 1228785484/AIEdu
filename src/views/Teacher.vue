<template>
  <div class="container">
    <div class="sidebar">
      <nav>
        <ul>
          <li @click="currentComponent = 'UserProfileManagement'" :class="{ active: currentComponent === 'UserProfileManagement' }">用户管理</li>
          <li @click="currentComponent = 'CourseManagement'" :class="{ active: currentComponent === 'CourseManagement' }">课程管理</li>
          <li @click="currentComponent = 'StudentManagement'" :class="{ active: currentComponent === 'StudentManagement' }">学生管理</li>
          <li @click="currentComponent = 'StudyRecords'" :class="{ active: currentComponent === 'StudyRecords' }">学生学习情况</li>
          <li @click="currentComponent = 'AiTeaching'" :class="{ active: currentComponent === 'AiTeaching' }">AI教案</li>
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
}

.sidebar {
  width: 25%;
  background-color: #f4f4f4;
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
}

.sidebar nav ul li:hover {
  background-color: #e0e0e0;
}

.sidebar nav ul li.active {
  background-color: #d0d0d0; /* 高亮当前选中的项 */
  font-weight: bold;
}

.main-content {
  width: 75%;
  padding: 20px;
  overflow-y: auto; /* 允许内容区域滚动 */
}
</style>
