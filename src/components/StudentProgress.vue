<template>
  <div class="progress-section">
    <div class="progress-items">
      <div class="progress-item">
        <div class="item-label">学习</div>
        <div class="item-value" :class="{ active: status.learning }">{{ status.learning ? '已完成' : '未完成' }}</div>
      </div>
      <div class="progress-item">
        <div class="item-label">测验</div>
        <div class="item-value" :class="{ active: status.quiz }">{{ status.quiz ? '已完成' : '未完成' }}</div>
      </div>
      <div class="progress-item">
        <div class="item-label">分数</div>
        <div class="item-value">{{ score }}分</div>
      </div>
      <div class="progress-item">
        <div class="item-label">学习次数</div>
        <div class="item-value">{{ studyTimes }}次</div>
      </div>
      <div class="progress-item">
        <div class="item-label">学习时长</div>
        <div class="item-value">{{ studyDuration }}分钟</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, defineProps, onMounted } from 'vue'

const props = defineProps({
  userId: {
    type: String,
    required: true
  }
})

const status = ref({
  learning: false,
  quiz: false
})
const score = ref(0)
const studyTimes = ref(0)
const studyDuration = ref(0)

const fetchStudentProgress = async () => {
  try {
    // 这里替换为实际的API调用
    const response = await fetch(`/api/student/progress/${props.userId}`)
    const data = await response.json()
    status.value = data.status
    score.value = data.score
    studyTimes.value = data.studyTimes
    studyDuration.value = data.studyDuration
  } catch (error) {
    console.error('获取学习进度失败:', error)
  }
}

onMounted(() => {
  fetchStudentProgress()
})
</script>

<style scoped>
.progress-section {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  margin: 20px 0;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.progress-items {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
}

.progress-item {
  flex: 1;
  text-align: center;
  padding: 10px;
}

.item-label {
  color: #666;
  font-size: 14px;
  margin-bottom: 8px;
}

.item-value {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.item-value.active {
  color: #409EFF;
}
</style>
