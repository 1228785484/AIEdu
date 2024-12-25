<template>
  <div class="profile-info">
    <div class="header-actions" style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
      <h2 class="course-title">课程/学生管理</h2>
      <div>
        <el-button type="primary" @click="showAddUserDialog">手动添加</el-button>
        <el-button type="default" @click="goBack">返回</el-button>
      </div>
    </div>

    <div style="margin-bottom: 20px;">
      <p style="font-size: 18px;">选择课程的总人数: {{ courseUsers.length }}</p>
      <div style="display: flex; align-items: center;">
        <el-input
          v-model="searchStudentQuery"
          placeholder="输入学生用户名查找"
          class="search-input"
          style="width: 300px; font-size: 16px;" 
        />
        <el-button 
          @click="searchStudents" 
          type="primary" 
          style="margin-left: 10px; font-size: 16px;">
          查询
        </el-button>
        <el-button 
          @click="resetSearch" 
          style="margin-left: 10px; font-size: 16px;">
          返回
        </el-button>
      </div>
    </div>

    <table class="info-table">
      <thead>
        <tr>
          <th style="width: 15%">用户ID</th>
          <th style="width: 20%">用户名</th>
          <th style="width: 20%">邮箱</th>
          <th style="width: 10%">学习进度</th>
          <th style="width: 10%">查看</th>
          <th style="width: 10%">删除</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="user in courseUsers" :key="user.userId">
          <td>{{ user.userId || '-' }}</td>
          <td>{{ user.username || '-' }}</td>
          <td>{{ user.email || '-' }}</td>
          <td>{{ user.studyProgress || '未开始' }}</td>
          <td>
            <el-button type="text" @click="viewUserDetails(user.userId)">查看</el-button>
          </td>
          <td>
            <el-button type="text" @click="confirmDeleteUser(user.userId)">删除</el-button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'

export default {
  name: 'StudentManagement',
  setup() {
    const route = useRoute()
    const router = useRouter()
    const searchStudentQuery = ref('')
    const courseUsers = ref([])
    const currentCourseId = ref(route.params.courseId || null)

    // 获取选课学生列表
    const fetchCourseUsers = async () => {
      try {
        const token = localStorage.getItem('token')
        if (!token) {
          ElMessage.error('未登录或登录已过期，请重新登录')
          router.push('/login')
          return
        }

        // 如果有courseId，则获取该课程的学生，否则获取所有学生
        const url = currentCourseId.value
          ? `http://localhost:8008/api/course/${currentCourseId.value}/enrolled-users`
          : 'http://localhost:8008/api/course/enrolled-users'

        const response = await fetch(url, {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        })

        if (!response.ok) {
          throw new Error('获取选课学生失败')
        }

        const result = await response.json()
        if (result.code === 200) {
          courseUsers.value = result.data.map(user => ({
            ...user,
            studyProgress: user.progress || 0
          }))
        } else {
          throw new Error(result.msg || '获取选课学生失败')
        }
      } catch (error) {
        console.error('获取选课学生失败:', error)
        ElMessage.error('获取选课学生失败，请稍后重试')
      }
    }

    // 监听courseId的变化
    watch(() => route.params.courseId, (newCourseId) => {
      currentCourseId.value = newCourseId
      fetchCourseUsers()
    })

    // 搜索学生
    const searchStudents = async () => {
      try {
        if (!searchStudentQuery.value) {
          await fetchCourseUsers()
          return
        }

        const token = localStorage.getItem('token')
        if (!token) {
          ElMessage.error('未登录或登录已过期，请重新登录')
          router.push('/login')
          return
        }

        const response = await fetch(`http://localhost:8008/api/course/search-students?query=${encodeURIComponent(searchStudentQuery.value)}`, {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        })

        if (!response.ok) {
          throw new Error('搜索学生失败')
        }

        const result = await response.json()
        if (result.code === 200) {
          courseUsers.value = result.data
        } else {
          throw new Error(result.msg || '搜索学生失败')
        }
      } catch (error) {
        console.error('搜索学生失败:', error)
        ElMessage.error('搜索学生失败，请稍后重试')
      }
    }

    // 重置搜索
    const resetSearch = async () => {
      searchStudentQuery.value = ''
      await fetchCourseUsers()
    }

    // 查看用户详情
    const viewUserDetails = async (userId) => {
      try {
        const token = localStorage.getItem('token')
        if (!token) {
          ElMessage.error('未登录或登录已过期，请重新登录')
          router.push('/login')
          return
        }

        const response = await fetch(`http://localhost:8008/api/user/${userId}`, {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        })

        if (!response.ok) {
          throw new Error('获取用户详情失败')
        }

        const result = await response.json()
        if (result.code === 200) {
          router.push({
            name: 'StudentProgress',
            params: {
              userId: userId,
              courseId: currentCourseId.value
            }
          })
        } else {
          throw new Error(result.msg || '获取用户详情失败')
        }
      } catch (error) {
        console.error('获取用户详情失败:', error)
        ElMessage.error('获取用户详情失败，请稍后重试')
      }
    }

    // 确认删除用户
    const confirmDeleteUser = (userId) => {
      ElMessageBox.confirm(
        '确定要删除该学生吗？',
        '警告',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }
      )
        .then(() => {
          deleteUser(userId)
        })
        .catch(() => {
          ElMessage({
            type: 'info',
            message: '已取消删除'
          })
        })
    }

    // 删除用户
    const deleteUser = async (userId) => {
      try {
        const token = localStorage.getItem('token')
        if (!token) {
          ElMessage.error('未登录或登录已过期，请重新登录')
          router.push('/login')
          return
        }

        const response = await fetch(`http://localhost:8008/api/course/remove-student/${userId}`, {
          method: 'DELETE',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        })

        if (!response.ok) {
          throw new Error('删除学生失败')
        }

        const result = await response.json()
        if (result.code === 200) {
          ElMessage.success('删除成功')
          await fetchCourseUsers()
        } else {
          throw new Error(result.msg || '删除学生失败')
        }
      } catch (error) {
        console.error('删除学生失败:', error)
        ElMessage.error('删除学生失败，请稍后重试')
      }
    }

    // 返回上一页
    const goBack = () => {
      router.go(-1)
    }

    // 初始化时获取学生列表
    onMounted(() => {
      fetchCourseUsers()
    })

    return {
      searchStudentQuery,
      courseUsers,
      viewUserDetails,
      confirmDeleteUser,
      searchStudents,
      resetSearch,
      goBack
    }
  }
}
</script>

<style scoped>
.info-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
}

.info-table th,
.info-table td {
  border: 1px solid #dcdfe6;
  padding: 12px;
  text-align: center;
}

.info-table th {
  background-color: #f5f7fa;
  color: #606266;
  font-weight: bold;
}

.info-table tr:hover {
  background-color: #f5f7fa;
}

.search-input {
  margin-right: 10px;
}
</style>