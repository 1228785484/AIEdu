<template>
  <div class="course-detail-container">
    <div class="page-header">
      <button class="back-button" @click="goBack">
        <i class="el-icon-arrow-left"></i> 返回
      </button>
    </div>
    <div class="course-users">
      <h2>选课学生</h2>
      <table class="elegant-table">
        <thead>
          <tr>
            <th class="table-header first-column" style="width: 33.33%">课程名称</th>
            <th class="table-header middle-column" style="width: 33.33%">用户名</th>
            <th class="table-header last-column" style="width: 33.33%">用户邮箱</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in courseUsers" :key="user.id" class="table-row">
            <td class="first-column">{{ courseName }}</td>
            <td class="middle-column">{{ user.username }}</td>
            <td class="last-column">{{ user.email }}</td>
          </tr>
          <tr v-for="n in Math.max(0, 9 - courseUsers.length)" :key="'empty-' + n" class="table-row">
            <td class="first-column">&nbsp;</td>
            <td class="middle-column">&nbsp;</td>
            <td class="last-column">&nbsp;</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CourseDetail',
  props: {
    courseId: {
      type: String,
      required: true
    },
    courseName: {
      type: String,
      default: '课程名称'
    },
    courseDescription: {
      type: String,
      default: '暂无描述'
    }
  },
  data() {
    return {
      courseUsers: []
    }
  },
  created() {
    this.fetchCourseUsers();
  },
  methods: {
    goBack() {
      if (window.history.length > 1) {
        this.$router.go(-1);
      } else {
        this.$router.push('/courses');
      }
    },
    async fetchCourseUsers() {
      try {
        const token = localStorage.getItem('token');
        const response = await fetch(`http://localhost:8008/api/course/enrolled-users/${this.courseId}`, {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        });

        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }

        const result = await response.json();
        if (result.code === 200) {
          this.courseUsers = result.data;
        } else {
          throw new Error(result.msg || '获取选课用户失败');
        }
      } catch (error) {
        console.error('获取选课用户失败:', error);
        this.$message.error('获取选课用户失败，请稍后重试');
      }
    }
  }
}
</script>

<style scoped>
.course-detail-container {
  padding: 20px;
  background-color: #f9f9f9;
}

.page-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.back-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background-color: transparent;
  color: #495057;
  border: 2px solid #6c757d;
  border-radius: 25px;
  padding: 10px 20px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 4px 6px rgba(0,0,0,0.1);
  outline: none;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.back-button:hover {
  background-color: #6c757d;
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 6px 8px rgba(0,0,0,0.15);
}

.back-button:active {
  transform: translateY(1px);
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.back-button i {
  margin-right: 10px;
  font-size: 18px;
}

.course-users h2 {
  color: #333;
  margin-bottom: 20px;
  text-align: center;
  font-weight: 600;
}

.elegant-table {
  width: 100%;
  table-layout: fixed;
  border-collapse: separate;
  border-spacing: 0;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1);
  background-color: white;
}

.table-header {
  background-color: #f8f9fa;
  color: #495057;
  font-weight: 600;
  text-transform: uppercase;
  padding: 12px;
  text-align: left;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  letter-spacing: 1px;
}

.table-row {
  transition: all 0.3s ease;
}

.table-row:nth-child(even) {
  background-color: #f8f9fa;
}

.table-row:hover {
  background-color: #e9ecef;
  transform: scale(1.01);
}

.elegant-table td {
  padding: 10px;
  color: #495057;
  height: 25px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  border-bottom: 1px solid #dee2e6;
}

/* 列的分界线 */
.first-column {
  border-right: 1px solid #dee2e6;
}

.middle-column {
  border-right: 1px solid #dee2e6;
}

.last-column {
  border-right: none;
}

/* 行的分界线 */
.table-row:last-child td {
  border-bottom: none;
}
</style>
