<template>
  <div class="report-container">
    <div class="back-button" @click="goBack">
      <div class="back-icon-wrapper">
        <el-icon><ArrowLeft /></el-icon>
      </div>
      <span>返回</span>
    </div>
    <div class="nav-menu">
      <div class="page-title" @mouseenter="showTooltip = true" @mouseleave="showTooltip = false">
        学习评价
        <div class="tooltip" v-show="showTooltip">全方位的学习数据分析与评估报告系统，提供章节学习情况、学情分析和测评记录等多维度的数据展示。</div>
      </div>
      <div class="nav-item" :class="{ active: activeNav === 'chapter' }" @click="activeNav = 'chapter'">
        <el-icon class="nav-icon"><Document /></el-icon>
        章节报告
      </div>
      <div class="nav-item" :class="{ active: activeNav === 'learning' }" @click="activeNav = 'learning'">
        <el-icon class="nav-icon"><DataAnalysis /></el-icon>
        学情报告
      </div>
      <div class="nav-item" :class="{ active: activeNav === 'assessment' }" @click="activeNav = 'assessment'">
        <el-icon class="nav-icon"><TrendCharts /></el-icon>
        测评轨迹
      </div>
    </div>
    <div class="main-content">
      <div class="content-area">
        <div v-if="activeNav === 'chapter'" class="chapter-content">
          <el-tabs v-model="selectedChapter" class="chapter-tabs" @tab-change="handleChapterChange">
            <el-tab-pane 
              v-for="chapter in courseTree" 
              :key="chapter.id"
              :label="chapter.label"
              :name="chapter.id"
            >
              <div class="sub-chapters">
                <div 
                  v-for="subChapter in chapter.children"
                  :key="subChapter.id"
                  class="sub-chapter-item"
                  :class="{ active: activeUnit === subChapter.id }"
                  @click="selectUnit(subChapter)"
                >
                  {{ subChapter.label }}
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
          <div class="report-content">
            <div class="chapter-report" v-if="currentReport">
              <h3>{{ currentReport.chapterName }} - 章节报告</h3>
              <div class="report-stats">
                <el-row :gutter="20">
                  <el-col :span="8">
                    <div class="stat-card">
                      <div class="stat-title">完成率</div>
                      <div class="stat-value">{{ currentReport.completionRate }}%</div>
                    </div>
                  </el-col>
                  <el-col :span="8">
                    <div class="stat-card">
                      <div class="stat-title">正确率</div>
                      <div class="stat-value">{{ currentReport.accuracyRate }}%</div>
                    </div>
                  </el-col>
                  <el-col :span="8">
                    <div class="stat-card">
                      <div class="stat-title">掌握度</div>
                      <div class="stat-value">{{ currentReport.masteryLevel }}%</div>
                    </div>
                  </el-col>
                </el-row>
              </div>
              <div class="report-details">
                <h4>知识点掌握情况</h4>
                <div class="knowledge-points">
                  <el-progress
                    v-for="point in currentReport.knowledgePoints"
                    :key="point.name"
                    :percentage="point.mastery"
                    :text-inside="true"
                    :stroke-width="18"
                    :format="format => `${point.name}: ${format}%`"
                  />
                </div>
              </div>
              <div class="learning-suggestions" v-if="currentReport.suggestions">
                <h4>学习建议</h4>
                <el-card class="suggestion-card">
                  <div v-for="(suggestion, index) in currentReport.suggestions" :key="index" class="suggestion-item">
                    <el-icon><InfoFilled /></el-icon>
                    <span>{{ suggestion }}</span>
                  </div>
                </el-card>
              </div>
            </div>
            <div class="no-report" v-else>
              <el-empty description="请选择章节查看报告" />
            </div>
          </div>
        </div>
        <div v-if="activeNav === 'learning'" class="learning-report-container">
          <!-- 整体学习情况报告 -->
          <div class="overall-report-section">
            <div class="section-header">
              <h3 class="section-title">整体学习情况报告</h3>
              <el-button 
                type="primary" 
                :icon="Refresh"
                size="small"
                :loading="isReportLoading"
                @click="generateLearningReport"
              >
                更新报告
              </el-button>
            </div>
            <div class="report-content" v-if="learningReport">
              <div class="report-item">
                <h4>学习进度</h4>
                <p>{{ learningReport.progress }}</p>
              </div>
              <div class="report-item">
                <h4>知识掌握情况</h4>
                <p>{{ learningReport.mastery }}</p>
              </div>
              <div class="report-item">
                <h4>学习建议</h4>
                <ul class="suggestion-list">
                  <li v-for="(suggestion, index) in learningReport.suggestions" :key="index">
                    {{ suggestion }}
                  </li>
                </ul>
              </div>
            </div>
            <div v-else class="loading-text">
              正在生成学习报告...
            </div>
          </div>

          <!-- 单元测验平均分图表 -->
          <div class="score-chart-section">
            <h3 class="section-title">单元测验平均分</h3>
            <div class="score-chart">
              <VChart class="chart" :option="quizChartOption" />
            </div>
          </div>
        </div>
        <div v-if="activeNav === 'assessment'" class="assessment-container">
          <!-- 图表切换按钮 -->
          <div class="chart-switch">
            <el-radio-group v-model="selectedChart" size="large">
              <el-radio-button label="realtime">实时数据</el-radio-button>
              <el-radio-button label="radar">能力雷达</el-radio-button>
            </el-radio-group>
          </div>

          <!-- 实时数据图表 -->
          <div v-if="selectedChart === 'realtime'" class="chart-section">
            <h3 class="section-title">学习轨迹实时数据</h3>
            <div class="data-chart">
              <VChart class="chart" :option="dataRecordOption" />
            </div>
          </div>

          <!-- 能力雷达图 -->
          <div v-if="selectedChart === 'radar'" class="chart-section">
            <h3 class="section-title">能力评估雷达图</h3>
            <div class="radar-chart">
              <VChart class="chart" :option="radarChartOption" />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Document, DataAnalysis, TrendCharts, InfoFilled, Refresh } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import VChart from 'vue-echarts'
import {
  GridComponent,
  TooltipComponent,
  LegendComponent,
  TitleComponent,
  RadarComponent
} from 'echarts/components'
import { LineChart, RadarChart } from 'echarts/charts'
import { use } from 'echarts/core'

// 注册必要的组件
use([
  GridComponent,
  TooltipComponent,
  LegendComponent,
  TitleComponent,
  LineChart,
  RadarChart,
  RadarComponent
])

const router = useRouter()
const activeNav = ref('chapter')
const showTooltip = ref(false)
const selectedChapter = ref(null)
const courseTree = ref([])
const currentReport = ref(null)
const learningReport = ref(null)
const selectedChart = ref('realtime')
const isReportLoading = ref(false)
const activeUnit = ref(null)
const unitQuizScores = ref(null)
const quizChartOption = ref({
  grid: {
    top: 100,
    right: 40,
    bottom: 40,
    left: 60,
    containLabel: true
  },
  tooltip: {
    trigger: 'axis',
    formatter: '{b}: {c}分'
  },
  xAxis: {
    type: 'category',
    data: [],
    axisLine: {
      lineStyle: {
        color: '#333'
      }
    },
    name: '章节'
  },
  yAxis: {
    type: 'value',
    min: 0,
    max: 100,
    name: '分数',
    axisLine: {
      lineStyle: {
        color: '#333'
      }
    }
  },
  series: []
})

const loadCourseTree = async () => {
  try {
    const courseId = localStorage.getItem('selectedCourseId')
    const token = localStorage.getItem('token')
    const response = await fetch(`http://localhost:8008/api/course/${courseId}/tree`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    const result = await response.json()
    
    const transformNode = (node) => ({
      id: node.id,
      label: node.name,
      children: Array.isArray(node.children) ? node.children.map(child => transformNode(child)) : []
    })
    
    if (result && result.length > 0 && result[0].children) {
      courseTree.value = result[0].children.map(chapter => transformNode(chapter))
      if (courseTree.value.length > 0) {
        selectedChapter.value = courseTree.value[0].id
      }
    }
  } catch (error) {
    console.error('Error loading course tree:', error)
  }
}

const handleChapterChange = async (chapterId) => {
  selectedChapter.value = chapterId
  const chapterName = courseTree.value.find(chapter => chapter.id === chapterId)?.label || '未知章节'
  
  try {
    const token = localStorage.getItem('token')
    const userId = localStorage.getItem('userid')
    const response = await fetch("http://localhost:8008/api/test/askAi", {
      method: "POST",
      headers: { 
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`
      },
      body: JSON.stringify({
        query: `请根据以下章节生成一份学习报告：${chapterName}。
               报告需要包含以下内容：
               1. 完成率（百分比）
               2. 正确率（百分比）
               3. 掌握度（百分比）
               4. 知识点掌握情况（列出3-5个关键知识点及其掌握程度）
               5. 针对性的学习建议（2-3条）
               请以JSON格式返回数据，格式如下：
               {
                 "completionRate": 85,
                 "accuracyRate": 90,
                 "masteryLevel": 88,
                 "knowledgePoints": [
                   {"name": "知识点1", "mastery": 85},
                   {"name": "知识点2", "mastery": 90}
                 ],
                 "suggestions": [
                   "建议1",
                   "建议2"
                 ]
               }`,
        userId: userId
      }),
    })

    if (!response.ok) {
      throw new Error("获取报告失败")
    }

    const result = await response.json()
    if (result.code === 200) {
      try {
        // 尝试解析AI返回的JSON字符串
        const reportData = JSON.parse(result.data.answer.replace(/```json\n?|\n?```/g, ''))
        currentReport.value = {
          chapterName,
          ...reportData
        }
      } catch (parseError) {
        console.error('解析AI响应失败:', parseError)
        ElMessage.error('生成报告失败，请重试')
        currentReport.value = null
      }
    } else {
      ElMessage.error(result.msg || '生成报告失败')
      currentReport.value = null
    }
  } catch (error) {
    console.error('Error generating report:', error)
    ElMessage.error('生成报告失败')
    currentReport.value = null
  }
}

const selectUnit = (unit) => {
  activeUnit.value = unit.id
  // 加载该单元的具体报告数据
}

const goBack = () => {
  router.go(-1)
}

const generateLearningReport = async () => {
  if (isReportLoading.value) return
  
  isReportLoading.value = true
  try {
    const userId = localStorage.getItem('userid')
    const courseId = localStorage.getItem('selectedCourseId')
    
    const response = await fetch('http://localhost:8008/api/test/askAi', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      },
      body: JSON.stringify({
        userId: userId,
        courseId: courseId,
        query: '请根据用户的学习数据生成一份详细的学情报告，包括：1. 整体学习进度 2. 知识点掌握情况 3. 针对性的学习建议'
      })
    })

    if (!response.ok) {
      throw new Error('生成报告失败')
    }

    const result = await response.json()
    
    learningReport.value = {
      progress: result.data.progress || '暂无进度数据',
      mastery: result.data.mastery || '暂无掌握度数据',
      suggestions: result.data.suggestions || ['暂无学习建议']
    }
    
    ElMessage.success('报告更新成功')
  } catch (error) {
    console.error('生成学习报告出错:', error)
    ElMessage.error('生成学习报告失败，请稍后重试')
  } finally {
    isReportLoading.value = false
  }
};

const fetchUnitQuizScores = async () => {
  try {
    // 从本地存储获取用户ID
    const userId = localStorage.getItem('userid')
    if (!userId) {
      ElMessage.error('未找到用户信息')
      return
    }

    // 获取所有单元的测验分数
    const allUnitScores = {}
    for (const unit of courseTree.value) {
      const response = await fetch('http://0.0.0.0:8008/quiz/getUnitQuizScores', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          unitId: unit.id,
          userId: userId
        })
      })
      const data = await response.json()
      if (data.success) {
        allUnitScores[unit.label] = data.scores || {}
      }
    }
    
    unitQuizScores.value = allUnitScores
    updateQuizChart()
  } catch (error) {
    console.error('获取单元测验分数失败:', error)
    ElMessage.error('获取单元测验分数失败')
  }
}

const updateQuizChart = () => {
  if (!unitQuizScores.value) return
  
  // 处理数据
  const units = Object.keys(unitQuizScores.value)
  const series = units.map(unit => {
    const chapters = unitQuizScores.value[unit]
    return {
      name: unit,
      type: 'line',
      data: Object.values(chapters).map(score => parseFloat(score))
    }
  })

  quizChartOption.value = {
    title: {
      text: '单元测验平均分',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: units,
      top: 30
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: Object.keys(unitQuizScores.value[units[0]] || {}),
      name: '章节'
    },
    yAxis: {
      type: 'value',
      min: 0,
      max: 100,
      name: '分数'
    },
    series: series
  }
}

watch(activeNav, (newValue) => {
  if (newValue === 'learning') {
    if (!learningReport.value) {
      generateLearningReport()
    }
    // 获取单元测验分数
    fetchUnitQuizScores()
  }
})

onMounted(() => {
  loadCourseTree()
})

const radarChartOption = ref({
  title: {
    text: '能力维度分析'
  },
  tooltip: {},
  legend: {
    data: ['能力水平']
  },
  radar: {
    shape: 'polygon',
    name: {
      textStyle: {
        color: '#666',
        fontSize: 14
      }
    },
    indicator: [
      { name: '知识理解', max: 100 },
      { name: '实践应用', max: 100 },
      { name: '创新思维', max: 100 },
      { name: '学习效率', max: 100 },
      { name: '专注度', max: 100 }
    ]
  },
  series: [{
    name: '能力评估',
    type: 'radar',
    data: [
      {
        value: [80, 75, 70, 85, 90],
        name: '能力水平',
        itemStyle: {
          color: '#5284DE'
        },
        areaStyle: {
          color: 'rgba(82, 132, 222, 0.3)'
        }
      }
    ]
  }]
})

const dataRecordOption = ref({
  title: {
    text: '学习数据记录'
  },
  tooltip: {
    trigger: 'axis'
  },
  legend: {
    data: ['学习时长', '完成任务数']
  },
  xAxis: {
    type: 'category',
    data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
  },
  yAxis: [
    {
      type: 'value',
      name: '学习时长(分钟)',
      position: 'left'
    },
    {
      type: 'value',
      name: '完成任务数',
      position: 'right'
    }
  ],
  series: [
    {
      name: '学习时长',
      type: 'line',
      yAxisIndex: 0,
      data: [120, 90, 150, 80, 70, 110, 130],
      itemStyle: {
        color: '#5284DE'
      }
    },
    {
      name: '完成任务数',
      type: 'line',
      yAxisIndex: 1,
      data: [5, 4, 6, 3, 4, 5, 4],
      itemStyle: {
        color: '#91cc75'
      }
    }
  ]
})
</script>

<style scoped>
.report-container {
  position: relative;
  display: flex;
  min-height: calc(100vh - 40px);
  background-color: #f5f5f5;
  padding: 20px;
  gap: 15px;
}

.back-button {
  position: absolute;
  top: 20px;
  left: 20px;
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #409EFF;
  cursor: pointer;
  transition: all 0.3s ease;
  z-index: 2;
}

.back-icon-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  background: rgba(64, 158, 255, 0.1);
  border-radius: 8px;
  margin-right: 8px;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
}

.back-icon-wrapper::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.2), transparent);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.back-button:hover .back-icon-wrapper::before {
  opacity: 1;
}

.back-button:hover .back-icon-wrapper {
  transform: translateX(-2px);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
}

.back-button i {
  font-size: 16px;
  color: #409EFF;
  transition: transform 0.3s ease;
}

.back-button span {
  font-weight: 500;
  background: linear-gradient(to right, #409EFF, #36acfe);
  background-clip: text;
  -webkit-background-clip: text;
  color: transparent;
  -webkit-text-fill-color: transparent;
  opacity: 0.9;
  transition: opacity 0.3s ease;
}

.back-button:hover span {
  opacity: 1;
}

.nav-menu {
  width: 80px;
  flex-shrink: 0;
  background: white;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.page-title {
  text-align: center;
  padding: 20px 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  position: relative;
  cursor: help;
  border-bottom: 1px solid #f0f0f0;
}

.tooltip {
  position: absolute;
  top: 120%;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 12px 16px;
  border-radius: 4px;
  font-size: 14px;
  font-weight: normal;
  white-space: normal;
  width: 240px;
  line-height: 1.6;
  z-index: 10;
}

.tooltip::before {
  content: '';
  position: absolute;
  top: -6px;
  left: 50%;
  transform: translateX(-50%);
  border-width: 0 6px 6px 6px;
  border-style: solid;
  border-color: transparent transparent rgba(0, 0, 0, 0.8) transparent;
}

.nav-item {
  height: 70px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 14px;
  color: #666;
  gap: 6px;
  padding: 8px 0;
}

.nav-item:hover {
  color: #3f51b5;
  background: #f8f9fa;
}

.nav-item.active {
  color: #3f51b5;
  background: #e8eaf6;
}

.nav-icon {
  font-size: 24px;
  margin-bottom: 4px;
  color: inherit;
}

.nav-item:hover .nav-icon,
.nav-item.active .nav-icon {
  color: inherit;
}

.main-content {
  flex: 4;
  min-width: 0;
}

.content-area {
  background: white;
  border-radius: 4px;
  height: 100%;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.unit-tabs {
  display: flex;
  padding: 8px 16px;
  gap: 8px;
  border-bottom: 1px solid #eee;
  overflow-x: auto;
  background: #fff;
}

.tab {
  padding: 6px 16px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 13px;
  color: #666;
  border-radius: 16px;
  white-space: nowrap;
}

.tab:hover {
  color: #3f51b5;
  background: #f8f9fa;
}

.tab.active {
  color: #3f51b5;
  background: #e8eaf6;
}

.report-content {
  padding: 20px;
}

.score-chart {
  margin-bottom: 20px;
}

.chart-container {
  height: calc(100vh - 240px);
  min-height: 400px;
}

h3 {
  margin: 0 0 16px 0;
  color: #333;
  font-size: 15px;
  font-weight: 500;
}

.chapter-tabs {
  margin-bottom: 20px;
}

.chapter-content {
  width: 100%;
}

.sub-chapters {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  padding: 15px 0;
}

.sub-chapter-item {
  padding: 8px 16px;
  border-radius: 4px;
  background: #f5f7fa;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 14px;
}

.sub-chapter-item:hover {
  background: #ecf5ff;
  color: #409EFF;
}

.sub-chapter-item.active {
  background: #409EFF;
  color: white;
}

:deep(.el-tabs__header) {
  margin-bottom: 0;
}

:deep(.el-tabs__nav-wrap::after) {
  height: 1px;
}

:deep(.el-tabs__item) {
  font-size: 15px;
  height: 40px;
  line-height: 40px;
}

:deep(.el-tabs__item.is-active) {
  font-weight: 600;
}

.chapter-report {
  padding: 20px;
  background: white;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.report-stats {
  margin-bottom: 20px;
}

.stat-card {
  padding: 16px;
  background: white;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
  text-align: center;
}

.stat-title {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #333;
}

.report-details {
  margin-bottom: 20px;
}

.knowledge-points {
  padding: 16px;
  background: white;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.learning-suggestions {
  padding: 16px;
  background: white;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.suggestion-card {
  padding: 16px;
}

.suggestion-item {
  padding: 8px;
  border-bottom: 1px solid #f0f0f0;
}

.suggestion-item:last-child {
  border-bottom: none;
}

.no-report {
  padding: 20px;
  text-align: center;
}

.overall-report {
  padding: 20px;
  background: white;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.report-section {
  margin-bottom: 20px;
}

.report-section h4 {
  margin-top: 0;
}

.loading-text {
  padding: 20px;
  text-align: center;
}

.learning-report-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
  height: 100%;
  padding: 20px;
}

.overall-report-section {
  flex: 2;
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.score-chart-section {
  flex: 1;
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  min-height: 300px;
}

.section-title {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 18px;
  border-bottom: 2px solid #f0f0f0;
  padding-bottom: 10px;
}

.report-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.report-item {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 6px;
}

.report-item h4 {
  margin: 0 0 10px 0;
  color: #666;
  font-size: 16px;
}

.report-item p {
  margin: 0;
  color: #333;
  line-height: 1.6;
}

.suggestion-list {
  margin: 0;
  padding-left: 20px;
}

.suggestion-list li {
  margin-bottom: 8px;
  color: #333;
  line-height: 1.6;
}

.loading-text {
  text-align: center;
  color: #666;
  padding: 20px;
}

.score-chart {
  height: 100%;
  width: 100%;
}

.chart {
  height: 250px;
  width: 100%;
}

.assessment-container {
  padding: 20px;
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.chart-switch {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.chart-section {
  flex: 1;
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.data-chart,
.radar-chart {
  height: 100%;
  min-height: 400px;
}

.chart {
  height: 100%;
  width: 100%;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #f0f0f0;
}

.section-title {
  margin: 0;
  color: #333;
  font-size: 18px;
}

/* 适配移动端 */
@media screen and (max-width: 768px) {
  .report-container {
    flex-direction: column;
    padding: 10px;
  }

  .nav-menu {
    width: 100%;
    height: 70px;
    flex-direction: row;
  }

  .nav-item {
    flex: 1;
    height: 100%;
    font-size: 14px;
  }

  .chart-container {
    height: 400px;
  }
}
</style>
