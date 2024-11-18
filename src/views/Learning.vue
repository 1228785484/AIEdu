<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div id="main-container">
    <!-- 左侧模块 -->
    <div id="left-container">
      <div id="left-content">
        <img src="@/assets/AI老师.png" alt="Avatar" id="logo" />
      </div>
    </div>

    <!-- 中间模块（学习计划） -->
    <div id="middle-container">
      <div id="middle-content">
        <!-- 学习计划模块 -->
        <div id="learning-plan-container">
          <div class="learning-plan-item">
            <span class="label">签到</span>
            <div class="sign-in">
              <span>{{ signInCount }}/{{ totalDaysInMonth }}</span>
              <div class="dropdown-container">
                <!-- 选择日期按钮，点击弹出日历 -->
                <el-button @click="toggleCalendar" :disabled="isAlreadySignedToday">选择今天签到</el-button>
                <!-- 显示日历 -->
                <el-date-picker
                  v-if="isCalendarVisible"
                  v-model="selectedDate"
                  type="date"
                  placeholder="选择日期"
                  :disabled-date="disabledDate"
                  @change="handleDateChange"
                  format="yyyy-MM-dd"
                  :default-value="today"
                />
              </div>
              <el-button @click="handleSignIn" :disabled="isAlreadySignedToday || !selectedDate">签到</el-button>
            </div>
          </div>
          <div class="learning-plan-item">
            <span class="label">学习计划</span>
            <span class="value">{{ progress }}%</span>
          </div>
          <div class="learning-plan-item">
            <span class="label">任务点</span>
            <span class="value">11/20</span>
          </div>
          <div class="learning-plan-item">
            <span class="label">测试</span>
            <span class="value">0/0</span>
          </div>
          <div class="learning-plan-item">
            <span class="label">学习次数</span>
            <span class="value">10</span>
          </div>
          <div class="learning-plan-item">
            <span class="label">学习进度</span>
            <div class="progress-container">
              <div class="progress-arch" :style="{ width: progress + '%' }">
                <div class="progress-bar"></div>
              </div>
              <span class="progress-percentage">{{ progress }}%</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧模块（仅保留选择日期的提示） -->
    <div id="right-container">
      <div id="right-content">
        <div class="calendar-container">
          <span class="label">请选择日期进行签到</span>
          <div v-if="isAlreadySignedToday">
            <span class="signed-message">今天您已经签到！</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { ElButton, ElDatePicker } from 'element-plus';

// 当前已签到次数
const signInCount = ref(0);

// 已签到的日期，格式如 '2024-11-01'
const signedDates = ref([]);

// 当前日期
const currentDate = new Date();

// 获取当前月份的天数
const totalDaysInMonth = computed(() => {
  const month = currentDate.getMonth(); // 获取当前月
  const year = currentDate.getFullYear(); // 获取当前年
  return new Date(year, month + 1, 0).getDate(); // 获取该月的最后一天，返回的日期是天数
});

// 当前选择的日期
const selectedDate = ref(null);

// 控制日历的显示
const isCalendarVisible = ref(false);

// 获取今天的日期
const today = computed(() => {
  return new Date().toISOString().split('T')[0]; // 获取今天的日期（YYYY-MM-DD格式）
});

// 检查日期是否已签到
const isDateSigned = (date) => {
  return signedDates.value.includes(date);
};

// 禁用过去和未来的日期
const disabledDate = (date) => {
  // 获取当前日期并转换为 YYYY-MM-DD 格式
  const formattedDate = `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`;
  
  // 只允许今天的日期进行签到，其他日期（包括未来和过去的日期）都禁用
  return formattedDate !== today.value || isDateSigned(formattedDate);
};

// 签到操作
const handleSignIn = () => {
  if (selectedDate.value && !isDateSigned(selectedDate.value)) {
    signedDates.value.push(selectedDate.value); // 增加签到日期
    signInCount.value++; // 增加签到次数
    isCalendarVisible.value = false; // 签到后隐藏日历
  }
};

// 当选择日期时的处理逻辑
const handleDateChange = (date) => {
  if (isDateSigned(date)) {
    alert('该日期已经签到');
  }
};

// 切换日历显示/隐藏
const toggleCalendar = () => {
  isCalendarVisible.value = !isCalendarVisible.value;
};

// 是否已签到今天
const isAlreadySignedToday = computed(() => {
  return isDateSigned(today.value); // 判断今天是否已签到
});
</script>

<style scoped>
/* 主容器 */
#main-container {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 20px;
}

/* 左侧模块 */
#left-container {
  width: 450px;
  height: 650px;
  background-color: #f0f0f0;
  border-radius: 15px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

#left-content {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}

#logo {
  width: 90%;
  height: auto;
  max-width: 400px;
}

/* 中间模块 */
#middle-container {
  width: 500px;
  height: 650px;
  background-color: #ffffff;
  border-radius: 15px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

#middle-content {
  padding: 20px;
  height: 100%;
  overflow-y: auto;
}

/* 学习计划模块 */
#learning-plan-container {
  margin-top: 30px;
}

.learning-plan-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
  padding: 10px;
  background-color: #f0f8ff;
  border-radius: 8px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.learning-plan-item .label {
  font-weight: bold;
}

.learning-plan-item .value {
  color: #007bff;
}

/* 签到按钮 */
.sign-in {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.el-button {
  margin-left: 10px;
}

/* 学习进度 - 倒U形图 */
.progress-container {
  position: relative;
  width: 100%;
  height: 60px;
  background-color: #e2ffe2;
  border-radius: 50% 50% 0 0;
  overflow: hidden;
}

.progress-arch {
  position: absolute;
  bottom: 0;
  left: 0;
  height: 100%;
  background-color: #007bff;
  transition: width 0.3s ease;
}

.progress-bar {
  height: 100%;
  width: 100%;
}

.progress-percentage {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 18px;
  font-weight: bold;
  color: #fff;
}

/* 右侧模块 */
#right-container {
  width: 350px;
  height: 650px;
  background-color: #f8f9fa;
  border-radius: 15px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

#right-content {
  padding: 20px;
  height: 100%;
}

/* 日历部分 */
.calendar-container {
  display: flex;
  flex-direction: column;
}

.signed-message {
  color: green;
  font-weight: bold;
}

.dropdown-container {
  display: flex;
  align-items: center;
}

.el-date-picker {
  margin-left: 10px;
}
</style>
