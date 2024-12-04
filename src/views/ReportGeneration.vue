<template>
  <div class="report-container">
    <div class="report-header">
      <div class="header-left">
        <button class="back-btn" @click="goBack">
          <i class="fas fa-arrow-left"></i> 返回
        </button>
        <h2>学习评估报告</h2>
      </div>
    </div>

    <div class="report-content">
      <div class="report-section">
        <h3>学习进度概览</h3>
        <div class="progress-overview">
          <div class="progress-item">
            <div class="progress-label">总体完成度</div>
            <div class="progress-bar">
              <div class="progress" :style="{ width: progressValue + '%' }"></div>
            </div>
            <div class="progress-value">{{ progressValue }}%</div>
          </div>
          <div class="stats-grid">
            <div class="stat-box">
              <div class="stat-title">已完成任务</div>
              <div class="stat-value">{{ note.task }}/{{ note.sumTask }}</div>
            </div>
            <div class="stat-box">
              <div class="stat-title">测试完成</div>
              <div class="stat-value">{{ note.test }}/{{ note.sumTest }}</div>
            </div>
            <div class="stat-box">
              <div class="stat-title">学习次数</div>
              <div class="stat-value">{{ note.frequency }}</div>
            </div>
          </div>
        </div>
      </div>

      <div class="report-section">
        <h3>能力评估</h3>
        <div class="ability-chart">
          <e-charts :option="radarOption" style="height: 300px;" />
        </div>
      </div>

      <div class="report-section">
        <h3>学习建议</h3>
        <div class="suggestions">
          <div class="suggestion-item" v-for="(suggestion, index) in suggestions" :key="index">
            <div class="suggestion-icon">
              <i :class="suggestion.icon"></i>
            </div>
            <div class="suggestion-content">
              <div class="suggestion-title">{{ suggestion.title }}</div>
              <div class="suggestion-text">{{ suggestion.text }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

// 从父组件获取的数据
const progressValue = ref(60);
const note = ref({
  task: 12,
  sumTask: 20,
  test: 0,
  sumTest: 0,
  frequency: 3
});

// 雷达图配置
const radarOption = {
  title: {
    text: '能力维度分析'
  },
  radar: {
    indicator: [
      { name: '知识掌握', max: 100 },
      { name: '学习效率', max: 100 },
      { name: '专注度', max: 100 },
      { name: '任务完成', max: 100 },
      { name: '测试成绩', max: 100 }
    ]
  },
  series: [{
    type: 'radar',
    data: [{
      value: [80, 70, 85, 75, 65],
      name: '能力评分'
    }]
  }]
};

// 学习建议数据
const suggestions = ref([
  {
    icon: 'fas fa-lightbulb',
    title: '知识巩固建议',
    text: '建议多做练习题，加强对核心概念的理解'
  },
  {
    icon: 'fas fa-clock',
    title: '时间管理建议',
    text: '建议合理安排学习时间，每次学习不要超过45分钟'
  },
  {
    icon: 'fas fa-book',
    title: '学习方法建议',
    text: '可以尝试制作知识导图，帮助理解知识体系'
  }
]);

const goBack = () => {
  router.back();
};

</script>

<style scoped>
.report-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.report-header {
  margin-bottom: 30px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.back-btn {
  padding: 8px 15px;
  background: #f5f5f5;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
}

.back-btn:hover {
  background: #e0e0e0;
}

.report-section {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.progress-overview {
  margin-top: 20px;
}

.progress-item {
  margin-bottom: 20px;
}

.progress-label {
  margin-bottom: 8px;
  color: #666;
}

.progress-bar {
  height: 8px;
  background: #eee;
  border-radius: 4px;
  overflow: hidden;
}

.progress {
  height: 100%;
  background: linear-gradient(to right, #1976d2, #64b5f6);
  border-radius: 4px;
  transition: width 0.3s ease;
}

.progress-value {
  text-align: right;
  margin-top: 4px;
  color: #1976d2;
  font-weight: bold;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-top: 20px;
}

.stat-box {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  text-align: center;
}

.stat-title {
  color: #666;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #1976d2;
}

.ability-chart {
  margin-top: 20px;
  height: 300px;
}

.suggestions {
  margin-top: 20px;
}

.suggestion-item {
  display: flex;
  gap: 15px;
  padding: 15px;
  border-bottom: 1px solid #eee;
}

.suggestion-item:last-child {
  border-bottom: none;
}

.suggestion-icon {
  width: 40px;
  height: 40px;
  background: #e3f2fd;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #1976d2;
}

.suggestion-content {
  flex: 1;
}

.suggestion-title {
  font-weight: 500;
  margin-bottom: 5px;
}

.suggestion-text {
  color: #666;
  line-height: 1.5;
}
</style>
