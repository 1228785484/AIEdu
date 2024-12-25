<template>
  <div class="learning-progress">
    <div class="progress-header">
      <h3>学习进度</h3>
      <el-button link @click="$router.back()">返回</el-button>
    </div>
    
    <div class="progress-content">
      <!-- 学习统计 -->
      <div class="stats-container">
        <div class="stat-item">
          <div class="stat-label">学习</div>
          <div class="stat-value">{{ studyStatus }}</div>
        </div>
        <div class="stat-item">
          <div class="stat-label">测验</div>
          <div class="stat-value">{{ quizStatus }}</div>
        </div>
        <div class="stat-item">
          <div class="stat-label">分数</div>
          <div class="stat-value">{{ score }}分</div>
        </div>
        <div class="stat-item">
          <div class="stat-label">学习次数</div>
          <div class="stat-value">{{ studyTimes }}次</div>
        </div>
        <div class="stat-item">
          <div class="stat-label">学习时长</div>
          <div class="stat-value">{{ studyDuration }}分钟</div>
        </div>
      </div>

      <!-- 课程树 -->
      <div class="course-tree-container">
        <h4>课程大纲</h4>
        <el-tree
          :data="treeData"
          :props="defaultProps"
          @node-click="handleNodeClick"
          :default-expand-all="true"
          node-key="id"
          :highlight-current="true"
          :expand-on-click-node="false"
        >
          <template #default="{ node, data }">
            <span class="custom-tree-node">
              <span>{{ node.label }}</span>
              <span v-if="data.status" class="node-status" :class="data.status">
                {{ data.status === 'completed' ? '已完成' : '进行中' }}
              </span>
            </span>
          </template>
        </el-tree>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { fetchCourseTree, fetchStudentProgress } from '../api/course'

export default {
  name: 'LearningProgress',
  props: {
    userId: {
      type: String,
      required: true
    },
    courseId: {
      type: String,
      required: true
    }
  },
  setup(props) {
    const treeData = ref([])
    const studyStatus = ref('未开始')
    const quizStatus = ref('未开始')
    const score = ref(0)
    const studyTimes = ref(0)
    const studyDuration = ref(0)

    const defaultProps = {
      children: 'children',
      label: 'name'
    }

    const handleNodeClick = (data) => {
      console.log('节点点击:', data)
    }

    const loadCourseTree = async () => {
      try {
        const data = await fetchCourseTree(props.courseId)
        treeData.value = data
      } catch (error) {
        console.error('加载课程树失败:', error)
      }
    }

    const loadProgress = async () => {
      try {
        const data = await fetchStudentProgress(props.userId, props.courseId)
        studyStatus.value = data.studyStatus
        quizStatus.value = data.quizStatus
        score.value = data.score
        studyTimes.value = data.studyTimes
        studyDuration.value = data.studyDuration
      } catch (error) {
        console.error('加载学习进度失败:', error)
      }
    }

    onMounted(() => {
      loadCourseTree()
      loadProgress()
    })

    return {
      treeData,
      defaultProps,
      handleNodeClick,
      studyStatus,
      quizStatus,
      score,
      studyTimes,
      studyDuration
    }
  }
}
</script>

<style scoped>
.learning-progress {
  padding: 20px;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.progress-content {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}

.stats-container {
  display: flex;
  justify-content: space-between;
  margin-bottom: 30px;
  flex-wrap: wrap;
  gap: 20px;
}

.stat-item {
  background: #f5f7fa;
  padding: 15px 25px;
  border-radius: 8px;
  text-align: center;
  flex: 1;
  min-width: 120px;
}

.stat-label {
  color: #606266;
  font-size: 14px;
  margin-bottom: 8px;
}

.stat-value {
  color: #303133;
  font-size: 20px;
  font-weight: bold;
}

.course-tree-container {
  border-top: 1px solid #ebeef5;
  padding-top: 20px;
}

.custom-tree-node {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  padding-right: 8px;
}

.node-status {
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 4px;
}

.node-status.completed {
  background: #f0f9eb;
  color: #67c23a;
}

.node-status.in-progress {
  background: #ecf5ff;
  color: #409eff;
}
</style>
