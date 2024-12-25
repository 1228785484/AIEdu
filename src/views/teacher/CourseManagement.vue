<template>
  <div class="profile-info">
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

    <h2>课程详情</h2>
    <table class="info-table">
      <thead>
        <tr>
          <th style="width: 10%">课程编号</th>
          <th style="width: 25%">课程名称</th>
          <th style="width: 35%">课程描述</th>
          <th style="width: 10%">编辑</th>
          <th style="width: 10%">查看选课学生</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="course in courseSelectionData" :key="course.id">
          <td>{{ course?.courseId || '-' }}</td>
          <td style="color: black;">{{ course?.courseName || '-' }}</td>
          <td>{{ course?.description || '-' }}</td>
          <td>
            <el-button type="primary" size="small" @click="navigateToCourse(course.courseId)">编辑</el-button>
          </td>
          <td>
            <el-button type="text" @click="viewEnrolledStudents(course.courseId)">查看选课学生</el-button>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- 添加课程对话框 -->
    <el-dialog v-model="showAddDialog" title="添加课程" width="50%">
      <el-form :model="newCourse" label-width="100px">
        <el-form-item label="课程编号">
          <el-input v-model="newCourse.courseId" />
        </el-form-item>
        <el-form-item label="课程名称">
          <el-input v-model="newCourse.courseName" />
        </el-form-item>
        <el-form-item label="课程描述">
          <el-input v-model="newCourse.description" type="textarea" rows="3" />
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
    <el-dialog v-model="showDeleteDialog" title="删除课程" width="50%">
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
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'CourseManagement',
  components: {
    Search
  },
  setup() {
    const searchQuery = ref('')
    const showAddDialog = ref(false)
    const showDeleteDialog = ref(false)
    const courseSelectionData = ref([])
    const newCourse = ref({
      courseId: '',
      courseName: '',
      description: ''
    })
    const deleteCourse = ref({
      courseId: ''
    })
    const selectedCourseId = ref(''); 
    const router = useRouter()

    // 获取所有课程
    const fetchCourses = async () => {
      try {
        const token = localStorage.getItem('token')
        if (!token) {
          ElMessage.error('未登录或登录已过期，请重新登录')
          router.push('/login')
          return
        }

        const response = await fetch('http://localhost:8008/api/course/list', {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        })

        if (!response.ok) {
          throw new Error('获取课程列表失败')
        }

        const result = await response.json()
        if (result.code === 200) {
          courseSelectionData.value = result.data.map(course => ({
            courseId: course.courseId,
            courseName: course.title,
            description: course.description,
            studentCount: course.studentCount || 0
          }))
        } else {
          throw new Error(result.msg || '获取课程列表失败')
        }
      } catch (error) {
        console.error('获取课程列表失败:', error)
        ElMessage.error('获取课程列表失败，请稍后重试')
      }
    }

    // 搜索课程
    const searchCourses = () => {
      if (!searchQuery.value) {
        fetchCourses()
        return
      }

      const query = searchQuery.value.toLowerCase()
      const filteredCourses = courseSelectionData.value.filter(course => 
        course.courseId.toString().toLowerCase().includes(query) ||
        course.courseName.toLowerCase().includes(query) ||
        course.description.toLowerCase().includes(query)
      )
      courseSelectionData.value = filteredCourses
    }

    // 添加课程
    const addCourse = () => {
      try {
        if (!newCourse.value.courseId || !newCourse.value.courseName) {
          ElMessage.warning('请填写课程编号和课程名称')
          return
        }

        // 检查课程ID是否已存在
        const existingCourse = courseSelectionData.value.find(
          course => course.courseId === newCourse.value.courseId
        )
        if (existingCourse) {
          ElMessage.warning('课程编号已存在')
          return
        }

        // 直接在前端添加课程
        courseSelectionData.value.push({
          courseId: newCourse.value.courseId,
          courseName: newCourse.value.courseName,
          description: newCourse.value.description,
          studentCount: 0
        })

        ElMessage.success('添加课程成功')
        showAddDialog.value = false
        // 重置表单
        newCourse.value = {
          courseId: '',
          courseName: '',
          description: ''
        }
      } catch (error) {
        console.error('Error adding course:', error)
      }
    }

    // 删除课程
    const confirmDeleteCourse = async () => {
      try {
        if (!deleteCourse.value.courseId) {
          ElMessage.warning('请输入要删除的课程编号')
          return
        }

        // 检查课程是否存在
        const courseIndex = courseSelectionData.value.findIndex(
          course => course.courseId === deleteCourse.value.courseId
        )
        if (courseIndex === -1) {
          ElMessage.warning('未找到该课程')
          return
        }

        await ElMessageBox.confirm('确认删除该课程吗？此操作不可恢复', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

        // 直接在前端删除课程
        courseSelectionData.value.splice(courseIndex, 1)

        ElMessage.success('删除课程成功')
        showDeleteDialog.value = false
        // 重置表单
        deleteCourse.value.courseId = ''
      } catch (error) {
        if (error.message !== 'cancel') {
          console.error('删除课程失败:', error)
          ElMessage.error('删除课程失败，请稍后重试')
        }
      }
    }

    // 跳转到课程章节编辑页面
    const navigateToCourse = (courseId) => {
      console.log('Navigating to course chapters:', courseId)
      router.push({
        name: 'CourseZhangjie',
        params: {
          courseId: courseId
        }
      }).catch(err => {
        console.error('Navigation error:', err)
        ElMessage.error('导航失败，请稍后重试')
      })
    }

    // 查看选课学生
    const viewEnrolledStudents = (courseId) => {
      router.push({
        name: 'StudentManagement',
        params: { courseId: courseId }
      })
    }

    // 显示添加课程对话框
    const showAddCourseDialog = () => {
      showAddDialog.value = true
    }

    // 显示删除课程对话框
    const showDeleteCourseDialog = () => {
      showDeleteDialog.value = true
    }

    // 初始加载课程列表
    onMounted(() => {
      fetchCourses()
    })

    return {
      searchQuery,
      showAddDialog,
      showDeleteDialog,
      courseSelectionData,
      newCourse,
      deleteCourse,
      searchCourses,
      addCourse,
      confirmDeleteCourse,
      navigateToCourse,
      viewEnrolledStudents,
      showAddCourseDialog,
      showDeleteCourseDialog,
      selectedCourseId,
    }
  }
}
</script>

<style scoped>
.header-actions {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.search-input {
  width: 300px;
}

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
}
</style>