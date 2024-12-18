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
        <el-icon class="nav-icon"><DocumentIcon /></el-icon>
        章节报告
      </div>
      <div class="nav-item" :class="{ active: activeNav === 'learning' }" @click="activeNav = 'learning'">
        <el-icon class="nav-icon"><DataAnalysis /></el-icon>
        整体报告
      </div>
      <div class="nav-item" :class="{ active: activeNav === 'assessment' }" @click="activeNav = 'assessment'">
        <el-icon class="nav-icon"><TrendCharts /></el-icon>
        学习轨迹
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
                  :class="{ 
                    active: activeUnit === subChapter.id || selectedChapter === subChapter.id 
                  }"
                  @click="selectUnit(subChapter)"
                >
                  {{ subChapter.label }}
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
          <div class="report-content">
            <!-- 加载状态 -->
            <div v-if="isLoading" class="loading-state">
              <div class="loading-effect">
                <div class="loading-circle"></div>
                <div class="loading-circle"></div>
                <div class="loading-circle"></div>
              </div>
              <div class="loading-content">
                <h3>正在生成章节报告</h3>
                <p>请稍候，AI正在分析学习数据...</p>
              </div>
            </div>
            
            <!-- 报告内容 -->
            <div v-else-if="currentReport" class="chapter-report">
              <div class="report-header">
                <h3>{{ currentReport.chapterName }} - 章节报告</h3>
                <el-button type="primary" @click="downloadReport" class="download-btn">
                  <el-icon><Download /></el-icon>
                  导出Word
                </el-button>
              </div>
              <div class="report-body">
                <div class="report-content">
                  <div 
                    v-for="(paragraph, index) in currentReport.content" 
                    :key="index" 
                    class="report-paragraph"
                    v-html="paragraph"
                  >
                  </div>
                </div>
              </div>
            </div>
            
            <!-- 空状态 -->
            <div v-else class="no-report">
              <el-empty description="请选择具体章节查看报告" />
            </div>
          </div>
        </div>
        <div v-if="activeNav === 'learning'" class="learning-report-container">
          <!-- 整体学习情况报告 -->
          <div class="overall-report-section">
            <div class="report-header">
              <h3>整体学习情况报告</h3>
              <el-button type="primary" @click="generateLearningReport" :loading="isReportLoading">
                <el-icon><Refresh /></el-icon>
                更新报告
              </el-button>
            </div>
            
            <!-- 加载状态 -->
            <div v-if="isReportLoading" class="loading-state">
              <div class="loading-effect">
                <div class="loading-circle"></div>
                <div class="loading-circle"></div>
                <div class="loading-circle"></div>
              </div>
              <div class="loading-content">
                <h3>正在生成学情报告</h3>
                <p>请稍候，AI正在分析学习数据...</p>
              </div>
            </div>
            
            <!-- 报告内容 -->
            <div v-else-if="learningReport" class="report-body">
              <div class="report-content">
                <div class="report-section">
                  <h4>学习评价</h4>
                  <p class="report-text">{{ learningReport.progress }}</p>
                </div>                       
              </div>
            </div>
            
            <!-- 空状态 -->
            <div v-else class="no-report">
              <el-empty description="暂无学情报告" />
            </div>
          </div>

          <!-- 单元测验平均分图表 -->
          <div class="score-chart-section">
            <div class="section-header">
              <h3 class="section-title">单元测验平均分</h3>
              <el-tooltip content="刷新数据" placement="top">
                <el-button 
                  type="primary" 
                  :icon="Refresh"
                  circle
                  size="small"
                  :loading="isLoading"
                  @click="fetchUnitQuizScores"
                />
              </el-tooltip>
            </div>
            <div class="score-chart">
              <div v-if="isLoading" class="chart-loading">
                <el-skeleton :rows="5" animated />
              </div>
              <VChart v-else class="chart" :option="quizChartOption" />
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
import { 
  ArrowLeft, 
  Document as DocumentIcon, 
  DataAnalysis, 
  TrendCharts, 
  Download
} from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import VChart from 'vue-echarts'
import {
  TitleComponent,
  TooltipComponent,
  GridComponent,
  LegendComponent
} from 'echarts/components'
import { LineChart, RadarChart } from 'echarts/charts'
import { use } from 'echarts/core'
import { saveAs } from 'file-saver'
import { Document, Packer, Paragraph, HeadingLevel } from 'docx'
import { marked } from 'marked'

// 注册必要的组件
use([
  GridComponent,
  TooltipComponent,
  LegendComponent,
  TitleComponent,
  LineChart,
  RadarChart
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

const isLoading = ref(false)

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

const renderMarkdown = (text) => {
  return marked.parse(text)
}

const handleChapterChange = async (chapterId) => {
  selectedChapter.value = chapterId
  
  // 查找当前章节
  const findChapter = (chapters, targetId) => {
    for (const chapter of chapters) {
      if (chapter.id === targetId) {
        return chapter
      }
      if (chapter.children && chapter.children.length > 0) {
        const found = findChapter(chapter.children, targetId)
        if (found) return found
      }
    }
    return null
  }

  const currentChapter = findChapter(courseTree.value, chapterId)
  
  // 如果不是叶子节点（还有子章节），不发送请求但保持选中状态
  if (currentChapter?.children?.length > 0) {
    console.log('请选择具体的小章节')
    ElMessage.warning('请选择具体的小章节')
    currentReport.value = null
    return
  }
  
  try {
    isLoading.value = true
    
    // 保持当前选中的章节状态
    const parentChapter = courseTree.value.find(chapter => 
      chapter.children?.some(child => child.id === chapterId)
    )
    if (parentChapter) {
      selectedChapter.value = parentChapter.id
      activeUnit.value = chapterId
    }
    
    const token = localStorage.getItem('token')
    const userId = localStorage.getItem('userid')
    
    const response = await fetch("http://localhost:8008/rating/getChapterRating", {
      method: "POST",
      headers: { 
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`
      },
      body: JSON.stringify({
        userId: userId,
        chapterId: chapterId
      })
    })

    if (!response.ok) {
      throw new Error("获取报告失败")
    }

    const result = await response.json()
    
    if (result.code === 200) {
      const paragraphs = result.data.reply.split('\n\n')
      currentReport.value = {
        chapterName: currentChapter.label,
        content: paragraphs.map(p => renderMarkdown(p))
      }
    } else {
      throw new Error(result.msg || '生成报告失败')
    }
  } catch (error) {
    console.error('获取章节报告失败:', error)
    ElMessage.error(error.message || '获取章节报告失败')
    currentReport.value = null
  } finally {
    isLoading.value = false
  }
}

const selectUnit = (unit) => {
  activeUnit.value = unit.id
  // 直接调用 handleChapterChange 获取报告
  handleChapterChange(unit.id)
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
    
    const response = await fetch('http://localhost:8008/rating/getOverallRating', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      },
      body: JSON.stringify({
        userId: userId,
        courseId: courseId
      })
    })

    if (!response.ok) {
      throw new Error('生成报告失败')
    }

    const result = await response.json()
    
    learningReport.value = {
      progress: result.data.reply || '暂无进度数据',
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
    const userId = localStorage.getItem('userid')
    const token = localStorage.getItem('token')
    if (!userId || !token) {
      ElMessage.error('未找到用户信息')
      return
    }

    // 显示加载状态
    isLoading.value = true

    // 存储所有单元的分数
    const allScores = []

    // 遍历课程树获取每个单元的ID
    for (const chapter of courseTree.value) {
      try {
        const response = await fetch('http://localhost:8008/quiz/getUnitQuizScores', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          body: JSON.stringify({
            userId: userId,
            unitId: chapter.id
          })
        })

        const result = await response.json()
        if (result.code === 200) {
          // 计算单元平均分
          const chapterScores = result.data.chapter_scores
          if (chapterScores && chapterScores.length > 0) {
            const totalScore = chapterScores.reduce((sum, chapter) => sum + chapter.score, 0)
            const averageScore = totalScore / chapterScores.length
            
            // 将单元分数添加到数组中
            allScores.push({
              unitId: result.data.unit_id,
              unitName: chapter.label,
              averageScore: parseFloat(averageScore.toFixed(2)) // 保留两位小数
            })
          }
        }
      } catch (error) {
        console.error(`获取单元 ${chapter.id} 的测验分数失败:`, error)
      }
    }

    // 按单元ID排序
    allScores.sort((a, b) => a.unitId - b.unitId)

    // 更新图表数据
    updateQuizChart(allScores)
  } catch (error) {
    console.error('获取单元测验分数失败:', error)
    ElMessage.error('获取测验分数失败')
  } finally {
    isLoading.value = false
  }
}

// 更新图表数据的函数
const updateQuizChart = (data) => {
  quizChartOption.value = {
    title: {
      text: '单元测验平均分',
      left: 'center',
      top: 20,
      textStyle: {
        color: '#303133',
        fontSize: 16,
        fontWeight: 500
      }
    },
    tooltip: {
      trigger: 'axis',
      formatter: (params) => {
        const data = params[0]
        return `${data.name}<br/>${data.seriesName}: ${data.value}分`
      }
    },
    grid: {
      top: 80,
      left: '5%',
      right: '5%',
      bottom: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: data.map(item => item.unitName),
      axisLine: {
        lineStyle: {
          color: '#909399'
        }
      },
      axisLabel: {
        color: '#606266',
        interval: 0
      }
    },
    yAxis: {
      type: 'value',
      min: 0,
      max: 100,
      name: '分数',
      nameTextStyle: {
        color: '#606266'
      },
      axisLine: {
        lineStyle: {
          color: '#909399'
        }
      },
      splitLine: {
        lineStyle: {
          type: 'dashed',
          color: '#E4E7ED'
        }
      }
    },
    series: [{
      name: '平均分',
      type: 'line',
      data: data.map(item => item.averageScore),
      smooth: true,
      symbolSize: 8,
      itemStyle: {
        color: '#409EFF'
      },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [{
            offset: 0,
            color: 'rgba(64,158,255,0.2)'
          }, {
            offset: 1,
            color: 'rgba(64,158,255,0)'
          }]
        }
      },
      markPoint: {
        data: [
          { type: 'max', name: '最高分' },
          { type: 'min', name: '最低分' }
        ]
      },
      markLine: {
        data: [
          { type: 'average', name: '平均分' }
        ]
      }
    }]
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

const downloadReport = async () => {
  if (!currentReport.value) {
    ElMessage.warning('请先选择章节生成报告')
    return
  }

  try {
    const doc = new Document({
      sections: [{
        properties: {},
        children: [
          // 标题
          new Paragraph({
            text: `${currentReport.value.chapterName} - 学习报告`,
            heading: HeadingLevel.HEADING_1,
            spacing: { after: 200 }
          }),
          
          // 正文内容 - 将每个段落转换为 Word 段落
          ...currentReport.value.content.map(paragraph => 
            new Paragraph({
              text: paragraph,
              spacing: { after: 200 },
              style: {
                paragraph: {
                  indent: {
                    firstLine: 480 // 首行缩进2字符
                  }
                }
              }
            })
          )
        ]
      }]
    })

    const blob = await Packer.toBlob(doc)
    saveAs(blob, `${currentReport.value.chapterName}-学习报告.docx`)
    ElMessage.success('报告下载成功！')
  } catch (error) {
    console.error('生成Word文档时出错:', error)
    ElMessage.error('报告生成失败，请重试')
  }
}
</script>

<style scoped>
.report-container {
  position: relative;
  display: flex;
  height: calc(100vh - 120px);
  background-color: #f5f5f5;
  padding: 20px;
  gap: 15px;
  overflow: hidden;
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
  width: 200px;
  flex-shrink: 0;
  background: white;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.page-title {
  text-align: center;
  padding: 24px 20px;
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
  width: 300px;
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
  font-size: 15px;
  color: #666;
  gap: 8px;
  padding: 12px 0;
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
  display: flex;
  flex-direction: column;
  min-width: 0;
  overflow: hidden;
}

.content-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: white;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
  overflow: hidden;
}

.chapter-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.chapter-tabs {
  flex-shrink: 0;
  padding: 10px 20px;
  background: white;
  border-bottom: 1px solid #eee;
}

.report-content {
  position: relative;
  height: 100%;
  overflow-y: auto;
}

.chapter-report {
  opacity: 1;
  transition: opacity 0.3s ease;
}

.chapter-report.loading {
  opacity: 0.5;
  pointer-events: none;
}

.report-header {
  flex-shrink: 0;
  padding: 15px 0;
  margin-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.report-body {
  flex: 1;
  overflow-y: auto;
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
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

.report-paragraph {
  margin-bottom: 16px;
  line-height: 1.8;
  color: #333;
  text-align: justify;
  text-indent: 2em;
  font-size: 15px;
}

.report-paragraph:last-child {
  margin-bottom: 0;
}

@media screen and (max-width: 768px) {
  .report-container {
    flex-direction: column;
    padding: 10px;
    height: calc(100vh - 100px);
  }

  .nav-menu {
    width: 100%;
    flex-direction: row;
    justify-content: space-around;
    padding: 10px;
  }

  .main-content {
    height: calc(100vh - 160px);
  }
}

/* 加载状态样式 */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  padding: 40px;
  background: rgba(255, 255, 255, 0.9);
}

.loading-effect {
  display: flex;
  gap: 8px;
  margin-bottom: 24px;
}

.loading-circle {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: #409EFF;
  animation: bounce 0.5s ease-in-out infinite;
}

.loading-circle:nth-child(2) {
  animation-delay: 0.1s;
}

.loading-circle:nth-child(3) {
  animation-delay: 0.2s;
}

.loading-content {
  text-align: center;
}

.loading-content h3 {
  color: #303133;
  font-size: 18px;
  margin-bottom: 8px;
  font-weight: 600;
}

.loading-content p {
  color: #909399;
  font-size: 14px;
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

/* 确保报告内容区域有相对定位，这样加载状态可以覆盖在上面 */
.report-content {
  position: relative;
  height: 100%;
  overflow-y: auto;
}

/* 添加淡入淡出效果 */
.loading-state, .chapter-report {
  transition: opacity 0.3s ease;
}

.score-chart-section {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  margin-top: 64px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-title {
  margin: 0;
  color: #303133;
  font-size: 16px;
  font-weight: 500;
}

.score-chart {
  position: relative;
  height: 300px;
  width: 100%;
}

.chart {
  height: 100%;
  width: 100%;
}

.chart-loading {
  padding: 20px;
}

/* 学情报告样式 */
.learning-report-container {
  padding: 20px;
  height: 100%;
  overflow-y: auto;
}

.overall-report-section {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.report-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.report-header h3 {
  margin: 0;
  color: #303133;
  font-size: 18px;
  font-weight: 600;
}

.report-body {
  padding: 20px;
}

.report-section {
  margin-bottom: 24px;
}

.report-section:last-child {
  margin-bottom: 0;
}

.report-section h4 {
  color: #303133;
  font-size: 16px;
  margin: 0 0 12px 0;
  font-weight: 500;
}

.report-text {
  color: #606266;
  line-height: 1.8;
  margin: 0;
  text-align: justify;
  font-size: 14px;
}

.suggestion-list {
  margin: 0;
  padding-left: 20px;
}

.suggestion-list li {
  color: #606266;
  line-height: 1.8;
  margin-bottom: 8px;
  font-size: 14px;
}

.suggestion-list li:last-child {
  margin-bottom: 0;
}

/* Markdown 样式 */
.report-paragraph :deep(p) {
  margin: 0;
  line-height: 1.8;
}

.report-paragraph :deep(strong) {
  font-weight: 600;
  color: #303133;
}

.report-paragraph :deep(em) {
  font-style: italic;
  color: #606266;
}

.report-paragraph :deep(ul), 
.report-paragraph :deep(ol) {
  padding-left: 2em;
  margin: 8px 0;
}

.report-paragraph :deep(li) {
  margin-bottom: 4px;
}

.report-paragraph :deep(code) {
  background: #f5f7fa;
  padding: 2px 6px;
  border-radius: 4px;
  font-family: monospace;
  font-size: 0.9em;
}

.report-paragraph :deep(blockquote) {
  margin: 8px 0;
  padding: 8px 16px;
  border-left: 4px solid #409EFF;
  background: #ecf5ff;
  color: #606266;
}

.report-paragraph :deep(a) {
  color: #409EFF;
  text-decoration: none;
  transition: color 0.3s;
}

.report-paragraph :deep(a:hover) {
  color: #66b1ff;
  text-decoration: underline;
}
</style>
