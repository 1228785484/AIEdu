<template>
  <div class="date-picker-container">
    <el-config-provider :locale="zhCn">
      <el-card class="box-card">
        <template #header>
          <div class="card-header">
            <span>学习时长查询</span>
          </div>
        </template>
        
        <el-form :model="form" label-width="120px">
          <el-form-item label="用户ID">
            <el-input v-model="form.userId" type="number" placeholder="请输入用户ID"/>
          </el-form-item>
          
          <el-form-item label="课程ID">
            <el-input v-model="form.courseId" type="number" placeholder="请输入课程ID"/>
          </el-form-item>
          
          <el-form-item label="日期范围">
            <el-date-picker
              v-model="form.dateRange"
              type="daterange"
              unlink-panels
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              :shortcuts="shortcuts"
              value-format="YYYY-MM-DD"
              format="YYYY-MM-DD"
              :size="size"
              style="width: 100%"
            />
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="queryStudyTime">查询</el-button>
          </el-form-item>
        </el-form>
        
        <!-- 结果展示 -->
        <div v-if="studyTime" class="result-container">
          <el-alert
            :title="`总学习时长：${formatDuration(studyTime)}`"
            type="success"
            :closable="false"
          />
        </div>
      </el-card>
    </el-config-provider>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'

const form = ref({
  userId: '',
  courseId: '',
  dateRange: []
})

const studyTime = ref(null)

const size = ref('default')

// 快捷选项
const shortcuts = [
  {
    text: '最近一周',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
      return [start, end]
    },
  },
  {
    text: '最近一个月',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
      return [start, end]
    },
  },
  {
    text: '最近三个月',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
      return [start, end]
    },
  },
]

// 格式化时长显示
const formatDuration = (seconds) => {
  const hours = Math.floor(seconds / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  const remainingSeconds = seconds % 60
  
  return `${hours}小时${minutes}分钟${remainingSeconds}秒`
}

// 查询学习时长
const queryStudyTime = async () => {
  if (!form.value.userId || !form.value.courseId || !form.value.dateRange?.length) {
    ElMessage.warning('请填写完整信息')
    return
  }
  
  try {
    const [startDate, endDate] = form.value.dateRange
    // 直接使用日期字符串，因为已经通过 value-format 格式化为 YYYY-MM-DD
    const response = await fetch(`http://localhost:8008/api/study-time/duration/range?userId=${form.value.userId}&courseId=${form.value.courseId}&startDate=${startDate}&endDate=${endDate}`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })
    
    if (!response.ok) {
      const errorData = await response.json()
      throw new Error(errorData.msg || '查询失败')
    }
    
    const data = await response.json()
    if (data.code === 200) {
      studyTime.value = data.data.totalDurationSeconds
    } else {
      ElMessage.error(data.msg || '查询失败')
    }
  } catch (error) {
    ElMessage.error('查询失败：' + error.message)
  }
}
</script>

<style scoped>
.date-picker-container {
  max-width: 800px;
  margin: 20px auto;
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.result-container {
  margin-top: 20px;
}

.el-date-picker {
  width: 100%;
}

:deep(.el-input__wrapper) {
  width: 100%;
}
</style>
