<template>
  <div class="calendar-container">
    <div class="calendar-card">
      <div class="calendar-header">
        <h2>每日签到</h2>
        <div class="month-selector">
          <button @click="previousMonth">&lt;</button>
          <span>{{ currentYear }}年{{ currentMonth + 1 }}月</span>
          <button @click="nextMonth">&gt;</button>
        </div>
        <el-button type="primary" @click="handleCheckIn" :disabled="todayChecked">
          {{ todayChecked ? '今日已签到' : '立即签到' }}
        </el-button>
      </div>

      <div class="calendar" v-loading="loading">
        <!-- 星期头部 -->
        <div class="calendar-weekdays">
          <div v-for="weekday in weekdays" :key="weekday" class="weekday">
            {{ weekday }}
          </div>
        </div>

        <!-- 日期格子 -->
        <div class="calendar-days">
          <!-- 填充前置空白天数 -->
          <div v-for="n in firstDayOfMonth" :key="'empty-' + n" class="day empty"></div>
          
          <!-- 实际日期 -->
          <div v-for="day in daysInMonth" 
               :key="day" 
               class="day"
               :class="{
                 'today': isToday(day),
                 'checked': isDateChecked(day)
               }">
            {{ day }}
            <span v-if="isDateChecked(day)" class="check-mark">✔</span>
          </div>
        </div>
      </div>

      <div class="statistics-container">
        <div class="statistics-box">
          <div class="check-in-count">{{ monthlyCheckIns }}</div>
          <div class="check-in-label">本月已签到天数</div>
        </div>
        <div class="statistics-box">
          <div class="check-in-count">{{ consecutiveCheckIns }}</div>
          <div class="check-in-label">连续签到天数</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElButton } from 'element-plus'

const weekdays = ['日', '一', '二', '三', '四', '五', '六']
const currentYear = ref(new Date().getFullYear())
const currentMonth = ref(new Date().getMonth())
const checkedDays = ref(new Set())
const todayChecked = ref(false)
const userId = ref(1)
const loading = ref(true)
const consecutiveCheckIns = ref(0)

// 计算当月天数
const daysInMonth = computed(() => {
  return new Date(currentYear.value, currentMonth.value + 1, 0).getDate()
})

// 计算当月第一天是星期几
const firstDayOfMonth = computed(() => {
  return new Date(currentYear.value, currentMonth.value, 1).getDay()
})

// 计算签到天数
const monthlyCheckIns = computed(() => checkedDays.value.size)

// 检查是否是今天
const isToday = (day) => {
  const today = new Date()
  return today.getFullYear() === currentYear.value &&
         today.getMonth() === currentMonth.value &&
         today.getDate() === day
}

// 检查日期是否已签到
const isDateChecked = (day) => {
  return checkedDays.value.has(day)
}

// 上个月
const previousMonth = () => {
  if (currentMonth.value === 0) {
    currentMonth.value = 11
    currentYear.value--
  } else {
    currentMonth.value--
  }
  loadMonthlyCheckIns()
}

// 下个月
const nextMonth = () => {
  if (currentMonth.value === 11) {
    currentMonth.value = 0
    currentYear.value++
  } else {
    currentMonth.value++
  }
  loadMonthlyCheckIns()
}

// 签到
const handleCheckIn = async () => {
  if (!userId.value) {
    ElMessage.warning('请先输入用户ID')
    return
  }

  try {
    const response = await fetch(`http://localhost:8008/api/checkins/checkIn?userId=${userId.value}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })

    const result = await response.json()
    if (result.code === 200) {
      ElMessage.success('签到成功！')
      const today = new Date().getDate()
      checkedDays.value.add(today)
      todayChecked.value = true
      // 更新连续签到天数
      await loadConsecutiveCheckIns()
    } else {
      ElMessage.error(result.msg || '签到失败')
    }
  } catch (error) {
    ElMessage.error('签到失败：' + error.message)
  }
}

// 加载月度签到数据
const loadMonthlyCheckIns = async () => {
  if (!userId.value) return

  loading.value = true
  try {
    const response = await fetch(`http://localhost:8008/api/checkins/monthly`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      },
      body: JSON.stringify({
        userId: userId.value,
        year: currentYear.value,
        month: currentMonth.value + 1
      })
    })

    const result = await response.json()
    if (result.code === 200) {
      checkedDays.value = new Set(result.data)
      const today = new Date().getDate()
      todayChecked.value = checkedDays.value.has(today)
    } else {
      ElMessage.error(result.msg || '获取签到记录失败')
    }
  } catch (error) {
    ElMessage.error('获取签到记录失败：' + error.message)
  } finally {
    loading.value = false
  }
}

// 加载连续签到天数
const loadConsecutiveCheckIns = async () => {
  if (!userId.value) return

  try {
    const response = await fetch(`http://localhost:8008/api/checkins/consecutive?userId=${userId.value}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })

    const result = await response.json()
    if (result.code === 200) {
      consecutiveCheckIns.value = result.data
    } else {
      ElMessage.error(result.msg || '获取连续签到天数失败')
    }
  } catch (error) {
    ElMessage.error('获取连续签到天数失败：' + error.message)
  }
}

onMounted(() => {
  loadMonthlyCheckIns()
  loadConsecutiveCheckIns()
})
</script>

<style scoped>
.calendar-container {
  padding: 20px;
  display: flex;
  justify-content: center;
}

.calendar-card {
  width: 360px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
  padding: 16px;
}

.calendar-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.calendar-header h2 {
  margin: 0;
  font-size: 18px;
  color: #303133;
}

.month-selector {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
}

.month-selector button {
  background: none;
  border: none;
  color: #909399;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: all 0.3s;
}

.month-selector button:hover {
  background: #f5f7fa;
  color: #409EFF;
}

.calendar {
  margin-top: 16px;
}

.calendar-weekdays {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  text-align: center;
  margin-bottom: 8px;
}

.weekday {
  padding: 4px;
  color: #909399;
  font-size: 12px;
}

.calendar-days {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 4px;
}

.day {
  aspect-ratio: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  border-radius: 50%;
  position: relative;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s;
  width: 32px;
  height: 32px;
  margin: 2px auto;
}

.day:hover {
  background: #f5f7fa;
}

.empty {
  pointer-events: none;
}

.today {
  background: #e6f1fc;
  color: #409EFF;
  font-weight: bold;
}

.checked {
  background: #f0f9eb;
  color: #67C23A;
}

.check-mark {
  position: absolute;
  bottom: -2px;
  font-size: 10px;
  color: #67C23A;
}

.statistics-container {
  display: flex;
  justify-content: space-around;
  margin-top: 16px;
  gap: 16px;
}

.statistics-box {
  flex: 1;
  text-align: center;
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.check-in-count {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
}

.check-in-label {
  font-size: 14px;
  color: #606266;
  margin-top: 4px;
}

:deep(.el-button) {
  padding: 8px 16px;
  font-size: 14px;
}
</style>