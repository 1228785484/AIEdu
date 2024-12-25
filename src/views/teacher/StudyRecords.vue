<template>
    <div class="profile-info">
      <div class="header-actions" style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
        <h2>课程/学习情况/用户：{{ currentUserName }}</h2>
        <el-button type="primary" size="small" @click="goBack">
          <i class="el-icon-back"></i> 返回学生管理
        </el-button>
      </div>
      <div class="progress-container">
        <div class="progress-section">
          <h3>进度</h3>
          <div class="study-progress">
            <div class="progress-header">
              <div class="progress-label">完成状态</div>
              <div class="progress-label">分数</div>
              <div class="progress-label">学习次数</div>
              <div class="progress-label">学习时长</div>
            </div>
            <div class="progress-content">
              <div class="progress-value">
                <el-button 
                  :type="isChapterCompleted ? 'success' : 'info'" 
                  size="small" 
                  :style="{ backgroundColor: isChapterCompleted ? '#67C23A' : '#ccc', borderColor: isChapterCompleted ? '#67C23A' : '#ccc' }">
                  {{ isChapterCompleted ? '已完成' : '未完成' }}
                </el-button>
              </div>
              <div class="progress-value">
                <span class="score">{{ chapterProgress[selectedNodeId]?.score || 0 }}分</span>
              </div>
              <div class="progress-value">
                <span class="count">{{ chapterProgress[selectedNodeId]?.studyTimes || 0 }}次</span>
              </div>
              <div class="progress-value">
                <span class="duration">{{ currentStudyDuration }}分钟</span>
              </div>
            </div>
          </div>
          <div class="evaluation">
            <h3>评价</h3>
            <el-input
              type="textarea"
              v-model="evaluationText"
              placeholder="请输入..."
              :rows="8"
            ></el-input>
            <el-button type="primary" class="submit-btn" @click="submitEvaluation">提交</el-button>
          </div>
        </div>
        <div class="course-tree-section">
          <h3>课程树</h3>
          <CourseTree 
            :treeData="courseTreeData" 
            :selectedId="selectedNodeId"
            :userId="currentUserId"
            :chapterStatus="chapterCompletionStatus"
            @select="handleChapterSelect"
            @chapter-progress="handleChapterProgress"
          />
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import { ref, onMounted } from 'vue'
  import { useRouter, useRoute } from 'vue-router'
  import { ElMessage } from 'element-plus'
  import CourseTree from '@/components/CourseTree.vue'
  
  export default {
    name: 'StudyRecords',
    components: {
      CourseTree
    },
    setup() {
      const route = useRoute()
      const router = useRouter()
      const currentUserName = ref('')
      const currentUserId = ref('')
      const selectedNodeId = ref('')
      const evaluationText = ref('')
      const isChapterCompleted = ref(false)
      const currentStudyDuration = ref(0)
      const chapterProgress = ref({})
      const courseTreeData = ref([])
      const chapterCompletionStatus = ref({})

      // 获取课程树数据
      const fetchCourseTree = async () => {
        try {
          const token = localStorage.getItem('token')
          if (!token) {
            ElMessage.error('未登录或登录已过期，请重新登录')
            router.push('/login')
            return
          }

          const courseId = route.params.courseId || '1'

          const response = await fetch(`http://localhost:8008/api/course/${courseId}/tree`, {
            method: 'GET',
            headers: {
              'Authorization': `Bearer ${token}`,
              'Content-Type': 'application/json'
            }
          })

          if (!response.ok) {
            throw new Error(`服务器响应错误: ${response.status} ${response.statusText}`)
          }

          const result = await response.json()

          // 处理数据，添加isOpen字段
          const processTreeData = (nodes) => {
            if (!Array.isArray(nodes)) {
              console.error('Invalid nodes data:', nodes)
              return []
            }
            return nodes.map(node => ({
              ...node,
              isOpen: false,  // 默认折叠所有节点
              children: node.children ? processTreeData(node.children) : []
            }))
          }

          if (Array.isArray(result) && result.length > 0) {
            const courseData = result[0]
            if (courseData.children) {
              courseTreeData.value = processTreeData(courseData.children)
            } else {
              courseTreeData.value = processTreeData([courseData])
            }
          } else {
            courseTreeData.value = []
          }
        } catch (error) {
          console.error('获取课程树失败:', error)
          ElMessage.error(error.message || '获取课程树失败，请稍后重试')
        }
      }

      // 获取学习时长
      const fetchStudyDuration = async () => {
        try {
          const token = localStorage.getItem('token')
          if (!token) {
            ElMessage.error('未登录或登录已过期，请重新登录')
            router.push('/login')
            return
          }

          const response = await fetch(`http://localhost:8008/api/study-time/duration?userId=${currentUserId.value}&chapterId=${selectedNodeId.value}`, {
            method: 'GET',
            headers: {
              'Authorization': `Bearer ${token}`,
              'Content-Type': 'application/json'
            }
          })

          console.log('Response status:', response.status)
          console.log('Response headers:', Object.fromEntries(response.headers.entries()))

          if (!response.ok) {
            throw new Error(`服务器响应错误: ${response.status} ${response.statusText}`)
          }

          const responseText = await response.text()
          console.log('Raw response text:', responseText)

          let result
          try {
            result = JSON.parse(responseText)
            console.log('Parsed response:', result)
          } catch (parseError) {
            console.error('JSON parse error:', parseError)
            throw new Error('服务器返回的数据格式无效')
          }

          if (!result) {
            throw new Error('服务器返回空数据')
          }

          if (result.code !== 200) {
            throw new Error(result.msg || `服务器返回错误代码: ${result.code}`)
          }

          if (!result.data) {
            throw new Error('服务器返回数据缺少data字段')
          }

          currentStudyDuration.value = Math.floor(result.data.durationSeconds / 60)
        } catch (error) {
          console.error('获取学习时长失败:', {
            error: error,
            message: error.message,
            stack: error.stack
          })
          ElMessage.error(error.message || '获取学习时长失败，请稍后重试')
        }
      }

      // 获取章节进度
      const fetchChapterProgress = async (nodeId) => {
        try {
          const token = localStorage.getItem('token')
          if (!token) {
            ElMessage.error('未登录或登录已过期，请重新登录')
            router.push('/login')
            return
          }

          const response = await fetch(`http://localhost:8008/api/course/chapter-progress?userId=${currentUserId.value}&chapterId=${nodeId}`, {
            method: 'GET',
            headers: {
              'Authorization': `Bearer ${token}`,
              'Content-Type': 'application/json'
            }
          })

          console.log('Response status:', response.status)
          console.log('Response headers:', Object.fromEntries(response.headers.entries()))

          if (!response.ok) {
            throw new Error(`服务器响应错误: ${response.status} ${response.statusText}`)
          }

          const responseText = await response.text()
          console.log('Raw response text:', responseText)

          let result
          try {
            result = JSON.parse(responseText)
            console.log('Parsed response:', result)
          } catch (parseError) {
            console.error('JSON parse error:', parseError)
            throw new Error('服务器返回的数据格式无效')
          }

          if (!result) {
            throw new Error('服务器返回空数据')
          }

          if (result.code !== 200) {
            throw new Error(result.msg || `服务器返回错误代码: ${result.code}`)
          }

          if (!result.data) {
            throw new Error('服务器返回数据缺少data字段')
          }

          chapterProgress.value[nodeId] = {
            isCompleted: result.data.isCompleted,
            score: result.data.score || 0,
            studyTimes: result.data.studyTimes || 0
          }
          isChapterCompleted.value = result.data.isCompleted
        } catch (error) {
          console.error('获取章节进度失败:', {
            error: error,
            message: error.message,
            stack: error.stack
          })
          ElMessage.error(error.message || '获取章节进度失败，请稍后重试')
        }
      }

      // 处理章节选择
      const handleChapterSelect = async (nodeId) => {
        selectedNodeId.value = nodeId
        await Promise.all([
          fetchChapterProgress(nodeId),
          fetchStudyDuration()
        ])
      }

      // 处理章节进度更新
      const handleChapterProgress = async (progress) => {
        const { chapterId } = progress
        await fetchChapterProgress(chapterId)
      }

      // 提交评价
      const submitEvaluation = async () => {
        try {
          if (!evaluationText.value.trim()) {
            ElMessage.warning('请输入评价内容')
            return
          }

          const token = localStorage.getItem('token')
          if (!token) {
            ElMessage.error('未登录或登录已过期，请重新登录')
            router.push('/login')
            return
          }

          const response = await fetch('http://localhost:8008/api/course/submit-evaluation', {
            method: 'POST',
            headers: {
              'Authorization': `Bearer ${token}`,
              'Content-Type': 'application/json'
            },
            body: JSON.stringify({
              userId: currentUserId.value,
              chapterId: selectedNodeId.value,
              evaluation: evaluationText.value
            })
          })

          console.log('Response status:', response.status)
          console.log('Response headers:', Object.fromEntries(response.headers.entries()))

          if (!response.ok) {
            throw new Error(`服务器响应错误: ${response.status} ${response.statusText}`)
          }

          const responseText = await response.text()
          console.log('Raw response text:', responseText)

          let result
          try {
            result = JSON.parse(responseText)
            console.log('Parsed response:', result)
          } catch (parseError) {
            console.error('JSON parse error:', parseError)
            throw new Error('服务器返回的数据格式无效')
          }

          if (!result) {
            throw new Error('服务器返回空数据')
          }

          if (result.code !== 200) {
            throw new Error(result.msg || `服务器返回错误代码: ${result.code}`)
          }

          ElMessage.success('评价提交成功')
          evaluationText.value = '' // 清空评价内容
        } catch (error) {
          console.error('提交评价失败:', {
            error: error,
            message: error.message,
            stack: error.stack
          })
          ElMessage.error(error.message || '提交评价失败，请稍后重试')
        }
      }

      // 返回上一页
      const goBack = () => {
        router.push('/teacher/student-management')
      }

      // 初始化
      onMounted(async () => {
        currentUserId.value = route.params.userId
        currentUserName.value = route.params.userName || '未知用户'
        await fetchCourseTree()
      })

      return {
        currentUserName,
        currentUserId,
        selectedNodeId,
        evaluationText,
        isChapterCompleted,
        currentStudyDuration,
        chapterProgress,
        courseTreeData,
        chapterCompletionStatus,
        goBack,
        handleChapterSelect,
        handleChapterProgress,
        submitEvaluation
      }
    }
  }
  </script>
  
  <style scoped>
  .profile-info {
      padding: 24px;
      background-color: #f9f9f9;
      border-radius: 12px;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  }

  .header-actions {
      padding-bottom: 20px;
      border-bottom: 2px solid #007BFF; /* 修改横线颜色为蓝色 */
  }

  .header-actions h2 {
      margin: 0;
      color: #333;
      font-size: 22px;
      font-weight: 600;
  }

  .progress-container {
      display: flex;
      gap: 20px;
      margin-top: 30px;
  }
  
  .progress-section {
      flex: 1;
      background-color: #ffffff;
      border-radius: 10px;
      padding: 24px;
      box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  }
  
  .course-tree-section {
      width: 280px;
      background-color: #ffffff;
      border-radius: 10px;
      padding: 24px;
      overflow-y: auto;
      max-height: 500px;
  }

  h3 {
      margin: 0 0 20px 0;
      color: #007BFF;
      font-size: 18px;
      font-weight: 600;
  }
  
  .study-progress {
      background-color: #fefefe;
      border-radius: 8px;
      padding: 20px;
      box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  }
  
  .progress-header, .progress-content {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 20px;
      align-items: center;
  }

  .progress-header {
      margin-bottom: 20px;
      padding-bottom: 12px;
      border-bottom: 1px solid #ccc; /* 修改横线颜色为灰色 */
  }
  
  .evaluation {
      margin-top: 30px;
      background-color: #ffffff;
      border-radius: 8px;
      padding: 20px;
      box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  }
  
  .submit-btn {
      margin-top: 20px;
      width: 130px;
      background-color: #007BFF;
      color: #fff;
  }
  
  .progress-label {
      font-weight: 500;
      color: #606266;
      font-size: 15px;
  }
  
  .progress-value {
      text-align: center;
      color: #303133;
  }

  .progress-value .score,
  .progress-value .count,
  .progress-value .duration {
      font-size: 18px;
      font-weight: 500;
  }

  :deep(.el-textarea__inner) {
      border-radius: 6px;
      border-color: #dcdfe6;
  }

  :deep(.el-textarea__inner:focus) {
      border-color: #007BFF;
  }
  </style>