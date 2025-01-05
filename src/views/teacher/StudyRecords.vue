<template>
    <div class="profile-info">
      <div class="header-actions" style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
        <h2>课程/学习情况/用户：{{ currentUserName }}</h2>
        <el-button 
          type="primary" 
          class="back-button" 
          size="small" 
          @click="goBack"
        >
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
              <div class="progress-label">今日学习次数</div>
              <div class="progress-label">总学习时长</div>
            </div>
            <div class="progress-content">
              <div class="progress-value">
                <el-tag :type="isChapterCompleted ? 'success' : 'info'" size="small">
                  {{ isChapterCompleted ? '已完成' : '未完成' }}
                </el-tag>
              </div>
              <div class="progress-value">
                <span class="score">{{ chapterProgress[selectedNodeId]?.score || 0 }}分</span>
              </div>
              <div class="progress-value">
                <span class="count">{{ todayStudyTimes }}次</span>
              </div>
              <div class="progress-value">
                <span class="duration">{{ currentStudyDuration > 0 ? `${currentStudyDuration}分钟` : '未学习' }}</span>
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
      const todayStudyTimes = ref(0)
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

      // 获取章节完成状态
      const fetchChapterCompletion = async (userId, chapterId) => {
        try {
          const token = localStorage.getItem('token')
          if (!token) {
            ElMessage.error('未登录或登录已过期，请重新登录')
            router.push('/login')
            return
          }

          const response = await fetch(`http://localhost:8008/api/course/chapter-completion?userId=${userId}&chapterId=${chapterId}`, {
            headers: {
              'Authorization': `Bearer ${token}`
            }
          })
          const data = await response.json()
          console.log('章节完成状态接口返回:', {
            url: `/api/course/chapter-completion?userId=${userId}&chapterId=${chapterId}`,
            response: data
          })
          if (data.code === 200) {
            isChapterCompleted.value = data.data
          }
        } catch (error) {
          console.error('获取章节完成状态失败:', error)
          ElMessage.error('获取章节完成状态失败')
        }
      }

      // 获取章节测验成绩
      const fetchChapterQuizScore = async (nodeId) => {
        try {
          const token = localStorage.getItem('token')
          if (!token) {
            ElMessage.error('未登录或登录已过期，请重新登录')
            router.push('/login')
            return
          }

          const response = await fetch('http://localhost:8008/quiz/getChapterQuizScores', {
            method: 'POST',
            headers: {
              'Authorization': `Bearer ${token}`,
              'Content-Type': 'application/json'
            },
            body: JSON.stringify({
              chapterId: nodeId,
              userId: currentUserId.value
            })
          })

          const result = await response.json()
          console.log('章节测验成绩接口返回:', {
            url: '/quiz/getChapterQuizScores',
            request: {
              chapterId: nodeId,
              userId: currentUserId.value
            },
            response: result
          })

          if (result.code === 200) {
            if (result.data && typeof result.data.score === 'number') {
              // 如果返回了有效的分数数据
              chapterProgress.value[nodeId] = {
                ...chapterProgress.value[nodeId],
                score: result.data.score,
                studyTimes: result.data.studyTimes || 0
              }
            } else {
              // 如果返回了错误信息，设置默认值
              console.warn('测验成绩数据无效:', result.data?.message || '未知错误')
              chapterProgress.value[nodeId] = {
                ...chapterProgress.value[nodeId],
                score: 0,
                studyTimes: 0
              }
            }
          } else {
            throw new Error(result.msg || '获取测验成绩失败')
          }
        } catch (error) {
          console.error('获取测验成绩失败:', error)
          ElMessage.error('获取测验成绩失败')
          // 设置默认值
          chapterProgress.value[nodeId] = {
            ...chapterProgress.value[nodeId],
            score: 0,
            studyTimes: 0
          }
        }
      }

      // 获取总学习时长
      const fetchTotalStudyDuration = async (userId, courseId) => {
        try {
          const token = localStorage.getItem('token')
          if (!token) {
            ElMessage.error('未登录或登录已过期，请重新登录')
            router.push('/login')
            return
          }

          const response = await fetch(`http://localhost:8008/api/study-time/duration/total?userId=${userId}&courseId=${courseId}`, {
            headers: {
              'Authorization': `Bearer ${token}`
            }
          })

          const result = await response.json()
          console.log('总学习时长接口返回:', {
            url: `/api/study-time/duration/total?userId=${userId}&courseId=${courseId}`,
            response: result
          })

          if (result.code === 200) {
            // 将秒转换为分钟，如果totalDurationSeconds为0则保持为0
            currentStudyDuration.value = result.data?.totalDurationSeconds ? Math.floor(result.data.totalDurationSeconds / 60) : 0
          } else {
            currentStudyDuration.value = 0
          }
        } catch (error) {
          console.error('获取总学习时长失败:', error)
          ElMessage.error('获取总学习时长失败')
          currentStudyDuration.value = 0
        }
      }

      // 获取当天学习次数
      const fetchTodayStudyTimes = async (userId) => {
        try {
          const token = localStorage.getItem('token')
          if (!token) {
            ElMessage.error('未登录或登录已过期，请重新登录')
            router.push('/login')
            return
          }

          const response = await fetch(`http://localhost:8008/api/course/study-times?userId=${userId}`, {
            headers: {
              'Authorization': `Bearer ${token}`
            }
          })
          if (!response.ok) {
          throw new Error('Failed to fetch study times');
          }
          const result = await response.json()
          console.log('当天学习次数接口返回:', {
            url: `/api/course/study-times?userId=${userId}`,
            response: result
          })
          todayStudyTimes.value = result.data
        } catch (error) {
          console.error('获取当天学习次数失败:', error)
          ElMessage.error('获取当天学习次数失败')
          todayStudyTimes.value = 0
        }
      }


      // 处理章节选择
      const handleChapterSelect = async (nodeId) => {
        console.log('选中章节:', nodeId)
        selectedNodeId.value = nodeId
        if (currentUserId.value) {
          await Promise.all([
            fetchChapterCompletion(currentUserId.value, nodeId),
            fetchChapterQuizScore(nodeId)
          ])
        }
      }

      // 提交评价
      const submitEvaluation = async () => {
        if (!evaluationText.value.trim()) {
          ElMessage.warning('请输入评价内容')
          return
        }
        
        // 暂时移除接口调用，仅清空输入框
        ElMessage.success('评价已记录')
        evaluationText.value = ''
      }

      // 返回上一页
      const goBack = () => {
        router.push('/teacher/student-management')
      }

      // 初始化数据
      const initData = async () => {
        const userId = route.query.userId
        const userName = route.query.userName
        const courseId = route.params.courseId || '1'

        if (userId && userName) {
          currentUserId.value = userId
          currentUserName.value = userName
          await Promise.all([
            fetchCourseTree(),
            fetchTotalStudyDuration(userId, courseId),
            fetchTodayStudyTimes(userId)
          ])
        } else {
          ElMessage.error('未找到学生信息')
          router.push('/teacher/student-management')
        }
      }

      onMounted(() => {
        initData()
      })

      return {
        currentUserName,
        currentUserId,
        selectedNodeId,
        evaluationText,
        isChapterCompleted,
        currentStudyDuration,
        todayStudyTimes,
        chapterProgress,
        courseTreeData,
        chapterCompletionStatus,
        goBack,
        handleChapterSelect,
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
      border-radius: 8px;
      padding: 20px;
      margin-bottom: 20px;
      box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
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

  .back-button {
    font-size: 16px;
    padding: 12px 24px;
    height: auto;
    font-weight: 500;
  }
  </style>