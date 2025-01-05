<template>
  <div class="container">
    <div class="sidebar">
      <nav>
        <ul>
          <li @click="$router.push('/teacher')" :class="{ active: $route.name === 'UserProfileManagement' }">
            <img src="@/assets/task.png" alt="用户管理" /> 用户管理
          </li>
          <li @click="$router.push('/teacher/course-management')" :class="{ active: $route.name === 'CourseManagement' }">
            <img src="@/assets/test.png" alt="课程管理" /> 课程管理
          </li>
          <li @click="$router.push('/teacher/student-management')" :class="{ active: $route.name === 'StudentManagement' }">
            <img src="@/assets/frequency.png" alt="学生管理" /> 学生管理
          </li>
          <li @click="$router.push('/teacher/study-records')" :class="{ active: $route.name === 'StudyRecords' }">
            <img src="@/assets/progress.png" alt="学生学习情况" /> 学生学习情况
          </li>
          <li @click="$router.push('/teacher/ai-teaching')" :class="{ active: $route.name === 'AiTeaching' }">
            <img src="@/assets/test.png" alt="AI教案" /> AI教案
          </li>
        </ul>
      </nav>
    </div>
    <div class="main-content">
      <router-view />
    </div>
  </div>
</template>

<script>
import { defineComponent, onMounted } from 'vue';
import { useRouter } from 'vue-router';

export default defineComponent({
  name: 'TeacherView',
  setup() {
    const router = useRouter();

    onMounted(() => {
      // 检查是否需要显示学生学习情况
      const studentId = localStorage.getItem('currentStudentId');
      if (studentId) {
        console.log('Found studentId in localStorage:', studentId);
        // 使用路由导航到学习情况页面
        router.push({
          name: 'StudyRecords',
          query: {
            userId: studentId,
            courseId: localStorage.getItem('currentCourseId')
          }
        });
        // 清除存储的ID，避免影响下次访问
        localStorage.removeItem('currentStudentId');
        localStorage.removeItem('currentCourseId');
      }
    });

    return {};
  }
});
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
