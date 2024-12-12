<template>
  <div class="container">
    <!-- 左侧导航栏 -->
    <div class="sidebar">
      <div class="nav-section">
        <h3>学生信息</h3>
        <ul>
          <li @click="showProfileInfo">个人资料</li>
        </ul>
      </div>
      <div class="nav-section">
        <h3>课程</h3>
        <ul>
          <li @click="fetchSelectedCourses">学生选课情况</li>
          <li @click="fetchCourseSelection">课程设置</li>
        </ul>
      </div>
    </div>
    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 个人资料部分 -->
      <div v-if="showProfile" class="profile-info">
        <h2>个人资料</h2>
        <table class="info-table">
          <thead>
            <tr>
              <th style="width: 25%">用户名</th>
              <th style="width: 25%">邮箱</th>
              <th style="width: 25%">学习次数</th>
              <th style="width: 25%">学习时间</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>{{ userProfile?.username || '-' }}</td>
              <td>{{ userProfile?.email || '-' }}</td>
              <td>{{ userProfile?.studyCount || '0' }}</td>
              <td>{{ formatStudyTime(userProfile?.studyTime) || '00:00:00' }}</td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 课程详情部分 -->
      <div v-if="showCourses" class="profile-info">
        <h2>课程详情</h2>
        <table class="info-table">
          <thead>
            <tr>
              <th style="width: 15%">课程编号</th>
              <th style="width: 25%">课程</th>
              <th style="width: 35%">课程描述</th>
              <th style="width: 25%">选课人数</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="course in selectedCourses" :key="course.id">
              <td>{{ course?.courseId || '-' }}</td>
              <td>
                <span 
                  class="course-name-link" 
                  @click="navigateToCourse(course)"
                >
                  {{ course?.courseName || '-' }}
                </span>
              </td>
              <td>{{ course?.description || '-' }}</td>
              <td>{{ course?.studentCount || 0 }}</td>
            </tr>
            <tr v-if="!selectedCourses || selectedCourses.length === 0">
              <td>-</td>
              <td>-</td>
              <td>-</td>
              <td>-</td>
            </tr>
          </tbody>
        </table>

        <!-- 选课用户弹窗 -->
        <el-dialog
          v-model="showUserDialog"
          :title="`${currentCourseName || '课程'}的选课学生`"
          width="50%"
        >
          <table class="info-table">
            <thead>
              <tr>
                <th style="width: 30%">用户名</th>
                <th style="width: 40%">邮箱</th>
                <th style="width: 30%">选课时间</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="user in courseUsers" :key="user.id">
                <td>{{ user?.username || '-' }}</td>
                <td>{{ user?.email || '-' }}</td>
                <td>{{ formatDate(user?.enrollTime) || '-' }}</td>
              </tr>
              <tr v-if="!courseUsers || courseUsers.length === 0">
                <td colspan="3" style="text-align: center">暂无选课学生</td>
              </tr>
            </tbody>
          </table>
        </el-dialog>
      </div>

      <!-- 选课情况部分 -->
      <div v-if="showCourseSelection" class="profile-info">
        <div class="header-actions">
          <el-input
            v-model="searchQuery"
            placeholder="搜索课程"
            class="search-input"
            @input="searchCourses"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-button type="primary" @click="showAddCourseDialog">
            添加课程
          </el-button>
          <el-button type="danger" @click="showDeleteCourseDialog">
            删除课程
          </el-button>
        </div>

        <h2>课程设置</h2>
        <table class="info-table">
          <thead>
            <tr>
              <th style="width: 15%">课程编号</th>
              <th style="width: 25%">课程名称</th>
              <th style="width: 35%">课程描述</th>
              <th style="width: 25%">选课人数</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="course in courseSelectionData" :key="course.id">
              <td>{{ course?.courseId || '-' }}</td>
              <td>
                <span 
                  class="course-name-link" 
                  @click="navigateToCourse(course)"
                >
                  {{ course?.courseName || '-' }}
                </span>
              </td>
              <td>{{ course?.description || '-' }}</td>
              <td>{{ course?.studentCount || 0 }}</td>
            </tr>
            <tr v-if="!courseSelectionData || courseSelectionData.length === 0">
              <td>-</td>
              <td>-</td>
              <td>-</td>
              <td>-</td>
            </tr>
          </tbody>
        </table>

        <!-- 添加课程对话框 -->
        <el-dialog
          v-model="showAddDialog"
          title="添加课程"
          width="50%"
        >
          <el-form :model="newCourse" label-width="100px">
            <el-form-item label="课程编号">
              <el-input v-model="newCourse.courseId" />
            </el-form-item>
            <el-form-item label="课程名称">
              <el-input v-model="newCourse.courseName" />
            </el-form-item>
            <el-form-item label="课程描述">
              <el-input
                v-model="newCourse.description"
                type="textarea"
                rows="3"
              />
            </el-form-item>
          </el-form>
          <template #footer>
            <span class="dialog-footer">
              <el-button @click="showAddDialog = false">取消</el-button>
              <el-button type="primary" @click="addCourse">确定</el-button>
            </span>
          </template>
        </el-dialog>

        <!-- 删除课程对话框 -->
        <el-dialog
          v-model="showDeleteDialog"
          title="删除课程"
          width="50%"
        >
          <el-form :model="deleteCourse" label-width="100px">
            <el-form-item label="课程编号">
              <el-input v-model="deleteCourse.courseId" />
            </el-form-item>
          </el-form>
          <template #footer>
            <span class="dialog-footer">
              <el-button @click="showDeleteDialog = false">取消</el-button>
              <el-button type="danger" @click="confirmDeleteCourse">确定</el-button>
            </span>
          </template>
        </el-dialog>
      </div>
    </div>
  </div>
</template>

<script>
import { Search } from '@element-plus/icons-vue'

export default {
  name: 'AIAssistant',
  components: {
    Search
  },
  data() {
    return {
      userProfile: null,
      selectedCourses: [],
      courseSelectionData: [],
      showProfile: true,
      showCourses: false,
      showCourseSelection: false,
      showUserDialog: false,
      courseUsers: [],
      currentCourseName: '',
      searchQuery: '',
      showAddDialog: false,
      newCourse: {
        courseId: '',
        courseName: '',
        description: ''
      },
      originalCourseData: [],
      showDeleteDialog: false,
      deleteCourse: {
        courseId: ''
      }
    }
  },
  methods: {
    showProfileInfo() {
      this.showProfile = true;
      this.showCourses = false;
      this.showCourseSelection = false;
      this.fetchUserProfile();
    },
    async fetchUserProfile() {
      try {
        const response = await fetch('/api/user/profile', {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
            'Content-Type': 'application/json'
          }
        });
        
        if (!response.ok) {
          throw new Error('获取用户资料失败');
        }
        
        const data = await response.json();
        this.userProfile = data.userProfile;
      } catch (error) {
        console.error('获取用户资料出错:', error);
        alert('获取用户资料失败，请稍后重试');
      }
    },
    async fetchSelectedCourses() {
      this.showProfile = false;
      this.showCourses = true;
      this.showCourseSelection = false;
      
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          this.$message.error('未登录或登录已过期，请重新登录');
          this.$router.push('/login');
          return;
        }

        const response = await fetch('http://localhost:8008/api/course/list', {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          },
          credentials: 'include'
        });
        
        if (response.status === 403) {
          this.$message.error('没有访问权限，请重新登录');
          this.$router.push('/login');
          return;
        }
        
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const result = await response.json();
        
        if (result.code === 200 && Array.isArray(result.data)) {
          this.selectedCourses = result.data.map(course => ({
            courseId: course.courseId,
            courseName: course.title,
            description: course.description
          }));
        } else {
          throw new Error(result.msg || '获取课程数据失败');
        }
        
      } catch (error) {
        console.error('获取课程数据出错:', error);
        if (!window.navigator.onLine) {
          this.$message.error('网络连接已断开，请检查网络连接');
        } else if (error.message.includes('403')) {
          this.$message.error('访问权限不足，请重新登录');
          this.$router.push('/login');
        } else {
          this.$message.error('获取课程数据失败，请稍后重试');
        }
        this.selectedCourses = [];
      }
    },
    async fetchCourseSelection() {
      this.showProfile = false;
      this.showCourses = false;
      this.showCourseSelection = true;
      
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          this.$message.error('未登录或登录已过期，请重新登录');
          this.$router.push('/login');
          return;
        }

        const response = await fetch('http://localhost:8008/api/course/list', {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          },
          credentials: 'include'
        });
        
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const result = await response.json();
        
        if (result.code === 200 && Array.isArray(result.data)) {
          this.courseSelectionData = result.data.map(course => ({
            courseId: course.courseId,
            courseName: course.title,
            description: course.description,
            studentCount: course.studentCount || 0
          }));
          this.originalCourseData = [...this.courseSelectionData];
        } else {
          throw new Error(result.msg || '获取课程数据失败');
        }
      } catch (error) {
        console.error('获取课程数据出错:', error);
        this.$message.error('获取课程数据失败，请稍后重试');
        this.courseSelectionData = [];
      }
    },
    formatStudyTime(time) {
      if (!time) return '00:00:00';
      return time;
    },
    async showCourseUsers(courseId) {
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          this.$message.error('未登录或登录已过期，请重新登录');
          this.$router.push('/login');
          return;
        }

        const response = await fetch(`http://localhost:8008/api/course/enrolled-users/${courseId}`, {
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
          this.currentCourseName = this.selectedCourses.find(
            course => course.courseId === courseId
          )?.courseName || '课程';
          this.showUserDialog = true;
        } else {
          throw new Error(result.msg || '获取选课用户失败');
        }
      } catch (error) {
        console.error('获取选课用户失败:', error);
        this.$message.error('获取选课用户失败，请稍后重试');
      }
    },
    formatDate(dateString) {
      if (!dateString) return '-';
      const date = new Date(dateString);
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
    },
    searchCourses() {
      if (!this.searchQuery) {
        this.courseSelectionData = [...this.originalCourseData];
        return;
      }
      
      const query = this.searchQuery.toLowerCase();
      this.courseSelectionData = this.originalCourseData.filter(course => 
        (course.courseId?.toString().toLowerCase() || '').includes(query) ||
        (course.courseName?.toString().toLowerCase() || '').includes(query) ||
        (course.description?.toString().toLowerCase() || '').includes(query)
      );
    },
    showAddCourseDialog() {
      this.newCourse = {
        courseId: '',
        courseName: '',
        description: ''
      };
      this.showAddDialog = true;
    },
    async addCourse() {
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          this.$message.error('未登录或登录已过期，请重新登录');
          this.$router.push('/login');
          return;
        }

        const response = await fetch('http://localhost:8008/api/course/add', {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            courseId: this.newCourse.courseId,
            title: this.newCourse.courseName,
            description: this.newCourse.description
          })
        });

        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }

        const result = await response.json();
        if (result.code === 200) {
          this.$message.success('添加课程成功');
          this.showAddDialog = false;
          await this.fetchCourseSelection();
        } else {
          throw new Error(result.msg || '添加课程失败');
        }
      } catch (error) {
        console.error('添加课程失败:', error);
        this.$message.error('添加课程失败，请稍后重试');
      }
    },
    showDeleteCourseDialog() {
      this.deleteCourse = {
        courseId: ''
      };
      this.showDeleteDialog = true;
    },
    async deleteCourseById(courseId) {
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          this.$message.error('未登录或登录已过期，请重新登录');
          this.$router.push('/login');
          return;
        }

        const response = await fetch(`http://localhost:8008/api/course/delete/${courseId}`, {
          method: 'DELETE',
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
          this.$message.success('删除课程成功');
          this.showDeleteDialog = false;
          await this.fetchCourseSelection(); // 重新获取课程列表
        } else {
          throw new Error(result.msg || '删除课程失败');
        }
      } catch (error) {
        console.error('删除课程失败:', error);
        this.$message.error('删除课程失败，请稍后重试');
      }
    },
    confirmDeleteCourse() {
      if (!this.deleteCourse.courseId) {
        this.$message.warning('请输入要删除的课程编号');
        return;
      }

      this.$confirm('确认删除该课程吗？此操作不可恢复', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.deleteCourseById(this.deleteCourse.courseId);
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '取消删除'
        });
      });
    },
    navigateToCourse(course) {
      if (!course) return;
      
      this.$router.push({ 
        name: 'CourseDetail', 
        params: {
          courseId: course.courseId,
          courseName: course.courseName,
          courseDescription: course.description
        }
      });
    }
  },
  created() {
    this.fetchUserProfile();
  }
};
</script>

<style scoped>
.container {
  display: flex;
  min-height: 100vh;
}

.sidebar {
  width: 250px;
  background-color: #f5f5f5;
  padding: 20px;
}

.nav-section {
  margin-bottom: 20px;
}

.nav-section h3 {
  margin-bottom: 10px;
  color: #333;
}

.nav-section ul {
  list-style: none;
  padding: 0;
}

.nav-section li {
  padding: 8px 15px;
  cursor: pointer;
  border-radius: 4px;
}

.nav-section li:hover {
  background-color: #e0e0e0;
}

.main-content {
  flex: 1;
  padding: 20px;
}

/* 确保表格样式正确应用 */
.info-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
  background-color: white;
  border: 1px solid #ddd;
}

.info-table th,
.info-table td {
  border: 1px solid #ddd;
  padding: 12px;
  text-align: left;
}

.info-table th {
  background-color: #f5f5f5;
  font-weight: bold;
  color: #333;
}

.info-table tr:nth-child(even) {
  background-color: #fafafa;
}

.info-table tr:hover {
  background-color: #f0f0f0;
}

.profile-info {
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  margin: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

h2, h3 {
  color: #333;
  margin-bottom: 16px;
}

.course-name-link {
  color: #409EFF;
  cursor: pointer;
  text-decoration: underline;
}

.course-name-link:hover {
  color: #66b1ff;
}

.header-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  margin-bottom: 20px;
}

.search-input {
  width: 300px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.el-button--small {
  padding: 6px 12px;
  font-size: 12px;
}
</style>