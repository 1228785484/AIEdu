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
      <p style="font-size: 18px;">选择课程的总人数: {{ enrolledStudents.length }}</p>
      <div style="display: flex; align-items: center;">
        <el-input
          v-model="searchStudentQuery"
          placeholder="输入学生用户名查找"
          class="search-input"
          style="width: 300px; font-size: 16px;" 
        />
        <el-button 
          @click="searchStudents" 
          :disabled="!selectedCourse"
          type="primary" 
          style="margin-left: 10px; font-size: 16px;">
          查询
        </el-button>
        <el-button 
          @click="resetSearch" 
          style="margin-left: 10px; font-size: 16px;">
          返回
        </el-button>
        <el-select v-model="selectedCourse" placeholder="选择课程" style="width: 200px; margin-left: 10px;">
          <el-option
            v-for="course in courseList"
            :key="course.courseId" 
            :label="course.title" 
            :value="course.courseId" 
          />
      </el-select>
      </div>
    </div>

    <!-- 新增：渲染搜索结果 -->
    <!-- <div v-if="searchResult">
        <h3>搜索结果</h3>
        <p>用户ID: {{ searchResult.userId }}</p>
        <p>用户名: {{ searchResult.username }}</p>
        <p>邮箱: {{ searchResult.email }}</p>
        <p>学习进度: {{ searchResult.progress }}%</p>
    </div> -->
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
      <tbody v-if="!searchResult">
        <tr v-for="user in enrolledStudents" :key="user.userId">
          <td>{{ user.userId }}</td>
          <td>{{ user.username }}</td>
          <td>{{ user.email }}</td>
          <td>{{ user.progress }}%</td>
          <td>
            <el-button type="text" @click="viewUserDetails(user.userId, user.username)">查看</el-button>
          </td>
          <td>
            <el-button type="text" style="color: #F56C6C;" @click="confirmDeleteUser(user)">删除</el-button>
          </td>
        </tr>
      </tbody>
      <tbody v-else>
        <tr>
          <td>{{ searchResult.userId }}</td>
          <td>{{ searchResult.username }}</td>
          <td>{{ searchResult.email }}</td>
          <td>{{ searchResult.progress }}%</td>
          <td>
            <el-button type="text" @click="viewUserDetails(searchResult.userId, searchResult.username)">查看</el-button>
          </td>
          <td>
            <el-button type="text" style="color: #F56C6C;" @click="confirmDeleteUser(searchResult)">删除</el-button>
          </td>
        </tr>
      </tbody>
    </table>
    <!-- 手动添加用户的对话框 -->
    <el-dialog 
      title="手动添加学生" 
      v-model="dialogVisible"
      width="40%"
      :close-on-click-modal="false"
    >
      <el-form :model="form" label-width="80px" style="padding: 20px;">
        <el-form-item label="用户ID" style="margin-bottom: 25px;">
          <el-input v-model="newUserId" placeholder="请输入用户ID" />
        </el-form-item>
        <el-form-item label="用户名" style="margin-bottom: 25px;">
          <el-input v-model="newUsername" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="邮箱" style="margin-bottom: 25px;">
          <el-input v-model="newEmail" placeholder="请输入邮箱" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer" style="padding: 20px 0 10px;">
          <el-button @click="dialogVisible = false" style="margin-right: 15px;">取 消</el-button>
          <el-button type="primary" @click="addStudent">确 定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'StudentManagement',
  setup() {
    const router = useRouter()
    const searchStudentQuery = ref('')
    const selectedCourse = ref('')
    const courseList = ref([])
    const users = ref([])
    const enrolledStudents = ref([])
    const searchResult = ref(null)
    const newUserId = ref('')
    const newUsername = ref('')
    const newEmail = ref('')
    const dialogVisible = ref(false)

    // 显示添加用户的对话框
    const showAddUserDialog = () => {
      if (!selectedCourse.value) {
          ElMessage.warning('请先选择课程');
          return;
      }
      dialogVisible.value = true; // 显示对话框
    };

    // 添加学生到 enrolledStudents 列表
    const addStudent = () => {
        if (!newUserId.value || !newUsername.value || !newEmail.value) {
            ElMessage.warning('请填写所有字段');
            return;
        }

        // 检查用户是否已存在
        const existingStudent = enrolledStudents.value.find(student => 
            student.userId === parseInt(newUserId.value) || 
            student.username === newUsername.value || 
            student.email === newEmail.value
        );
        
        if (existingStudent) {
            ElMessage.warning('该用户已在列表中');
            return;
        }

        // 添加新用户到列表末尾
        enrolledStudents.value.push({
            userId: parseInt(newUserId.value),
            username: newUsername.value,
            email: newEmail.value,
            progress: 0 // 默认学习进度为0
        });

        // 清空输入框
        newUserId.value = '';
        newUsername.value = '';
        newEmail.value = '';

        ElMessage.success('用户添加成功');
        dialogVisible.value = false; // 关闭对话框
    };

    // 确认删除用户
    const confirmDeleteUser = (student) => {
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
            deleteUser(student); // 直接传递学生对象进行删除
        })
        .catch(() => {
            ElMessage({
                type: 'info',
                message: '已取消删除'
            });
        });
    };

    // 删除用户
    const deleteUser = (student) => {
        // 从 enrolledStudents 中删除该用户
        enrolledStudents.value = enrolledStudents.value.filter(s => s.userId !== student.userId);
        ElMessage.success('删除成功');
    };

    // 搜索学生
    const searchStudents = async () => {
        if (!selectedCourse.value) { // 检查是否选择了课程
            ElMessage.warning('请先选择课程'); // 提示用户选择课程
            return; // 如果没有选择课程，直接返回
        }

        // 清空之前的搜索结果
        searchResult.value = null;

        // 查找用户名
        const foundInEnrolled = enrolledStudents.value.find(student => student.username === searchStudentQuery.value);
        const foundInUsers = users.value.find(user => user.username === searchStudentQuery.value);

        if (foundInEnrolled) {
            // 如果在 enrolledStudents 中找到用户
            searchResult.value = foundInEnrolled; // 设置搜索结果为找到的用户
        } else if (foundInUsers) {
            // 如果在 users 中找到用户但不在 enrolledStudents 中
            ElMessage.warning('用户未选该课程');
        } else {
            // 如果在 users 中也找不到用户
            ElMessage.warning('该用户不存在');
        }
    };

    // 获取用户列表
    const fetchUserList = async () => {
        try {
            const response = await fetch(`http://localhost:8008/user/list?pageNum=1&pageSize=15`, {
              method: 'GET',
              headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`,
                'Content-Type': 'application/json'
              }
            });

            if (!response.ok) {
                throw new Error('获取用户列表失败');
            }

            const result = await response.json();
            users.value = result.data.records; // 假设返回的是用户数组
            console.log(users.value)
            // filterEnrolledStudents(); // 筛选选了指定课程的学生
            
        } catch (error) {
            console.error('获取用户列表失败:', error);
            ElMessage.error('获取用户列表失败，请稍后重试');
        }
    };

    // 获取用户角色
    const fetchUserRole = async (userId) => {
        try {
            const response = await fetch(`http://localhost:8008/api/roles/${userId}`, {
                method: 'GET',
                headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`,
                'Content-Type': 'application/json'
              }
            });

            if (!response.ok) {
                throw new Error('获取用户角色失败');
            }

            const result = await response.json();
            if(result.data){
              // console.log(result.data,'这是角色')
              return result.data.roleName
            }else{
              return null
            }
           
        } catch (error) {
            console.error('获取用户角色失败:', error);
            return null; // 返回null表示获取角色失败
        }
    };

    // 获取学习进度
    const fetchStudyProgress = async (userId, courseId) => {
        try {
            const response = await fetch(`http://localhost:8008/api/course/course-completion-percentage?userId=${userId}&courseId=${courseId}`, {
                method: 'GET',
                headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`,
                'Content-Type': 'application/json'
              }

            });

            if (!response.ok) {
                throw new Error('获取学习进度失败');
            }

            const result = await response.json()
            console.log(result.data.completionPercentage,'这是进度');
            return parseInt(result.data.completionPercentage) || 0; // 假设返回的结果中有progress字段
        } catch (error) {
            console.error('获取学习进度失败:', error);
            return 0; // 默认返回0表示未开始
        }
    };

    // 筛选选了指定课程的学生
    const filterEnrolledStudents = async () => {
        enrolledStudents.value = []; // 清空之前的结果
        for (const user of users.value) {
          const role = await fetchUserRole(user.userId); // 获取用户角色
          console.log(role,'这是角色')
          if (role === 'student') {
            console.log(JSON.parse(JSON.stringify(user)).userId,selectedCourse.value,'这是用户Id和选择的')
              const isEnrolled = await checkEnrollmentStatus(JSON.parse(JSON.stringify(user)).userId, selectedCourse.value);
              console.log(isEnrolled,'这是判断学生是否选了这个课程')
              if (isEnrolled) {
                const progress = await fetchStudyProgress(user.userId, selectedCourse.value); // 获取学习进度
                enrolledStudents.value.push({
                    userId: user.userId,
                    username: user.username,
                    email: user.email,
                    progress: progress // 添加学习进度
                }); // 添加选了课程的学生
                  // enrolledStudents.value.push(user); // 添加选了课程的学生
              }
          }
        }
        console.log(enrolledStudents,'这是选了这门课的学生')
    };

    // 检查用户是否选了指定课程
    const checkEnrollmentStatus = async (userId, courseId) => {
        try {
            const response = await fetch(`http://localhost:8008/api/course/check-enrollment?courseId=${courseId}&userId=${userId}`, {
              method: 'GET',
              headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`,
                'Content-Type': 'application/json'
              }
            });

            if (!response.ok) {
                throw new Error('检查选课状态失败');
            }

            const result = await response.json();
            console.log(result,'这是从判断是否选课的接口的数据')
            return result.data; // 假设返回的结果中有isEnrolled字段
        } catch (error) {
            console.error('检查选课状态失败:', error);
            return false; // 默认返回未选课
        }
    };

    // 在初始化时获取用户列表
    onMounted(() => {
        fetchUserList(); // 获取用户列表
        fetchCourseList(); // 获取课程列表
    });

    // 获取课程列表
    const fetchCourseList = async () => {
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
          courseList.value = result.data // 更新课程列表
        } else {
          throw new Error(result.msg || '获取课程列表失败')
        }
      } catch (error) {
        console.error('获取课程列表失败:', error)
        ElMessage.error('获取课程列表失败，请稍后重试')
      }
    }

    // 监听选中的课程变化
    watch(selectedCourse, (newCourseId) => {
      console.log('Selected course changed:', newCourseId);
      if (newCourseId) {
        filterEnrolledStudents();
      }
    })

    // 查看用户详情
    const viewUserDetails = (userId, userName) => {
      console.log('viewUserDetails called with userId:', userId);
      if (!selectedCourse.value) {
        ElMessage.warning('请先选择课程');
        return;
      }
      console.log('Setting currentStudentId:', userId);
      console.log('Setting currentCourseId:', selectedCourse.value);
      
      // 直接跳转到学生学习情况路由，并传递用户信息
      router.push({ 
        name: 'StudyRecords',
        query: { 
          userId: userId,
          userName: userName,
          courseId: selectedCourse.value
        }
      });
    };

    // 重置搜索
    const resetSearch = async () => {
      searchStudentQuery.value = ''
      // await fetchCourseUsers()
    }

    // 返回上一页
    const goBack = () => {
      router.go(-1)
    }

    return {
      searchStudentQuery,
      selectedCourse,
      courseList,
      enrolledStudents,
      searchResult,
      dialogVisible,
      newUserId,
      newUsername,
      newEmail,
      showAddUserDialog,
      addStudent,
      searchStudents,
      resetSearch,
      confirmDeleteUser,
      viewUserDetails,
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