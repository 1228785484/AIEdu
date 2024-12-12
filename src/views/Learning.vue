<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div id="main-container">
    <!-- 左侧模块 -->
    <div id="left-container">
      <div id="left-content">
        <div class="left-header">
          <h2>课程大纲</h2>
          <div class="header-underline"></div>
        </div>

        <!-- 课程进度统计 -->
        <div class="course-stats">
          <div class="stat-item">
            <div class="stat-icon">
              <img src="@/assets/task.png" alt="任务点">
            </div>
            <div class="stat-content">
              <span class="stat-label">任务点</span>
              <span class="stat-value">{{note.task}}/{{note.sumTask}}</span>
            </div>
          </div>

          <div class="stat-item">
            <div class="stat-icon">
              <img src="@/assets/test.png" alt="测试">
            </div>
            <div class="stat-content">
              <span class="stat-label">测试</span>
              <span class="stat-value">{{note.test}}/{{note.sumTest}}</span>
            </div>
          </div>

          <div class="stat-item">
            <div class="stat-icon">
              <img src="@/assets/frequency.png" alt="学习次数">
            </div>
            <div class="stat-content">
              <span class="stat-label">学习次数</span>
              <span class="stat-value">{{note.frequency}}</span>
            </div>
          </div>

          <div class="stat-item">
            <div class="stat-icon">
              <img src="@/assets/progress.png" alt="学习进度">
            </div>
            <div class="stat-content">
              <span class="stat-label">学习进度</span>
              <span class="stat-value">{{progressValue}}%</span>
            </div>
          </div>

          <div class="progress-chart-container">
            <e-charts class="progress-chart" :option="option" />
          </div>
        </div>
      </div>
    </div>

    <!-- 中间模块（学习计划） -->
    <div id="middle-container">
      <div id="middle-content">
        <div class="action-buttons">
          <span 
            class="action-btn" 
            :class="{ active: selectedAction === 'learn' }"
            @click="selectAction('learn')"
          >
            学习
          </span>
          <span 
            class="action-btn" 
            :class="{ active: selectedAction === 'test' }"
            @click="selectAction('test')"
          >
            测验
          </span>
        </div>
        <div class="divider"></div>
        
        <!-- 内容区域 -->
        <div class="content-wrapper">
          <div v-if="selectedAction === 'learn'" class="content-section learn-section">
            <div class="section-content">
              <!-- 显示章节内容 -->
              <div v-html="markdownToHtml"></div>
            </div>
          </div>
          <div v-if="selectedAction === 'test'" class="content-section test-section">
            <div class="section-content">
              <!-- 提交确认弹窗 -->
              <div v-if="showSubmitModal" class="submit-modal">
                <div class="modal-content">
                  <img src="@/assets/thinking-character.gif" alt="思考的小人" class="thinking-character">
                  <div class="modal-text">是否确认提交答案？</div>
                  <div class="modal-buttons">
                    <button class="modal-btn yes-btn" @click="confirmSubmit">Yes</button>
                    <button class="modal-btn no-btn" @click="cancelSubmit">No</button>
                  </div>
                </div>
              </div>

              <!-- 提示消息 -->
              <div v-if="showMessage" class="message-popup">
                {{ messageText }}
              </div>

              <!-- 测验内容加载完成后显示的弹窗 -->
              <div v-if="showQuizModal && testData.content && !isQuizLoading" class="quiz-modal">
                <div class="modal-content">
                  <img src="@/assets/cute-character.gif" alt="可爱的小人" class="cute-character">
                  <div class="modal-buttons">
                    <button class="modal-btn start-btn" @click="startQuiz">开始测验</button>
                    <button class="modal-btn cancel-btn" @click="cancelQuiz">取消</button>
                  </div>
                </div>
              </div>
              
              <!-- 加载状态显示 -->
              <div v-if="isQuizLoading" class="loading-container">
                <div class="loading-spinner"></div>
                <p>测验内容加载中...</p>
              </div>
              <!-- 测验内容 -->
              <div v-else>
                <!-- 测验加载失败或未选择章节 -->
                <div v-if="!testData.content" class="no-content-message">
                  <p>{{ testData.content === null ? '请选择一个章节开始测验' : '测验加载失败，请重试' }}</p>
                </div>
                
                <!-- 只有在成功加载测验内容后才显示测验界面 -->
                <div v-else>
                  <!-- 题目内容 -->
                  <div v-html="testData.content"></div>
                  
                  <!-- 只在测验内容加载后且未提交答案时显示倒计时和提交按钮 -->
                  <template v-if="!showResults">
                    <div class="countdown">剩余时间：{{ countdownDisplay }}</div>
                    <button v-if="timeLeft > 0" @click="submitAnswers" class="submit-btn">
                      提交答案
                    </button>
                    <div v-else class="time-message">时间结束，禁止答题</div>
                  </template>
                  
                  <!-- 只在提交答案后显示测验结果 -->
                  <div v-if="showResults" class="results-section">
                    <div class="score">得分：{{ score }}分</div>
                    <div class="answers">
                      <div v-for="(question, index) in que.value" :key="index">
                        <div class="question-text">{{ index + 1 }}. {{ question.question }}</div>
                        <div class="user-answer" :class="{ incorrect: userAnswers[index] !== question.answer }">
                          用户答案：{{ userAnswers[index] || '未作答' }}
                        </div>
                        <div class="correct-answer">正确答案：{{ question.answer }}</div>
                        <div v-if="userAnswers[index] !== question.answer" class="explanation">
                          解析：{{ question.explanation }}
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
        </div>
      </div>
    </div>

    <!-- 右侧模块（仅保留选择日期的提示） -->
    <div id="right-container">
      <div id="right-content">
        <div id="plan-title">学习计划</div>
        <div id="plan-line"></div><!--在学习计划下面添加一条横线-->
        <div id="scrollable-area">
          <!-- 目录树 -->
          <el-tree
            style="max-width: 600px"
            :data="treeData"
            :props="defaultProps"
            :current-node-key="currentChapterId"
            @node-click="handleNodeClick"
            :highlight-current="false"
          >
            <template #default="{ node }">
              <div 
                style="display: flex; align-items: center; width: 100%"
                :class="{ 'selected-node': currentChapterId === node.data.id }"
              >
                <span 
                  v-if="!node.children || node.children.length === 0"
                  class="status-dot"
                  :class="{ 'completed': completedChapters[node.data.id] }"
                ></span>
                <span style="margin-left: 8px">{{ node.label }}</span>
              </div>
            </template>
          </el-tree>

           <!-- 添加生成报告按钮 -->
           <div class="report-btn-container">
            <button class="report-btn" @click="goToReport">
              <i class="fas fa-file-alt"></i>
              生成学习报告
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed, defineExpose} from 'vue';
import { ElTree } from 'element-plus';
import {marked} from 'marked';
import { useRouter } from 'vue-router';
import { onBeforeRouteLeave, onBeforeRouteUpdate } from 'vue-router';
import { tryOnUnmounted } from '@vueuse/core';
import { onActivated, onDeactivated } from 'vue';

const router = useRouter();

// 添加加载状态
const isQuizLoading = ref(false);

// 定义当前选中的节点
const currentNode = ref(null);

// 添加跳转方法
const goToReport = () => {
  router.push('/report-generation');
};

// 计算属性，将Markdown转换为HTML
const markdownToHtml = computed(() =>
 {
  return marked(sectionData.value.content
);
});

var data1 = 12;
var data2 = 20;
var data3 = 0;
var data4 = 0;
var data5 = 3;
let note = ref({
  task: data1,
  sumTask: data2,
  test: data3,
  sumTest: data4,
  frequency: data5
});

//另一种进度图
//另一种进度图
var progressValue = ref(0);
var option = ref({
  title: {
    text: progressValue.value + '%',
    textStyle: {
      color: '#28BCFE',
      fontSize: '25px'
    },
    subtext: '加载进度',
    subtextStyle: {
      color: '#a9a9a9',
      fontSize: '10px',
    },
    itemGap: 20,
    left: 'center',
    top: '43%'
  },
  grid: [{ x: '7%', y: '7%', width: '33%', height: '100%' }],
  angleAxis: {
    startAngle: 180,
    max: 360,
    clockwise: true,
    show: false
  },
  radiusAxis: {
    type: 'category',
    show: true,
    axisLabel: { show: false },
    axisLine: { show: false },
    axisTick: { show: false }
  },
  polar: {
    center: ['50%', '60%'],
    radius: '150%',
  },
  series: [
    {
      type: 'bar',
      z: 2,
      data: [progressValue.value * 180 / 100],
      showBackground: true,
      backgroundStyle: { color: 'transparent' },
      coordinateSystem: 'polar',
      roundCap: true,
      barWidth: 20,
      barGap: '-100%',
      itemStyle: {
        opacity: 1,
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            { offset: 0, color: '#25BFFF' },
            { offset: 1, color: '#5284DE' }
          ],
          shadowBlur: 5,
          shadowColor: '#f92a2a'
        }
      }
    },
    {
      type: 'bar',
      z: 1,
      data: [180],
      coordinateSystem: 'polar',
      roundCap: true,
      barWidth: 20,
      barGap: '-100%',
      itemStyle: { opacity: 1, color: '#093368' }
    }
  ]
});

// const selectedAction = ref(''); // 用于跟踪当前选中的动作
function selectAction(action) {
  if (quizStarted.value && selectedAction.value === 'test' && !showResults.value) {
    // 如果正在测验中且未显示结果，显示确认弹窗
    showSubmitModal.value = true;
    // 保存用户想要切换到的动作
    pendingAction.value = action;
  } else {
    selectedAction.value = action;
  }
}

// 目录树数据改为响应式
const treeData = ref([]);

// 加载课程树数据的方法
const loadCourseTree = async () => {
  try {
    const courseId = localStorage.getItem('selectedCourseId');
    if (!courseId) {
      console.error('No course selected');
      return;
    }

    const token = localStorage.getItem('token');
    const response = await fetch(`http://localhost:8008/api/course/${courseId}/tree`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });

    if (!response.ok) {
      throw new Error('Failed to fetch course tree');
    }

    const result = await response.json();
    console.log('Received course tree:', result);

    const transformNode = (node) => {
      return {
        id: node.id,
        label: `${node.name}`,
        children: Array.isArray(node.children) ? node.children.map(child => transformNode(child)) : []
      };
    };

    if (result && result.length > 0 && result[0].children) {
      treeData.value = result[0].children.map(chapter => transformNode(chapter));
    } else {
      console.error('Invalid course tree structure');
      treeData.value = [];
    }
  } catch (error) {
    console.error('Error loading course tree:', error);
    treeData.value = [];
  }
};

onMounted(() => {
  loadCourseTree();
  updateLearningProgress();
  updateStudyTimes();
});

// el-tree 需要的默认属性配置
const defaultProps = {
  children: 'children',
  label: 'label'
};

// 响应式变量，用于存储章节内容
const sectionData = ref({
  content: '' // 初始化为空字符串
});

// 响应式变量，用于存储测验内容
const testData = ref({
  content: null // 初始化为空字符串
});

const que = ref('')
const quizId = ref()

// 修改初始化状态
const selectedAction = ref('learn'); // 默认显示学习界面

// 添加一个变量来存储当前选中的章节ID
const currentChapterId = ref(null);

//更新任务点的函数
// 添加新的函数用于获取任务点数据
const updateTaskPoints = async (unitId) => {
  try {
    const userId = localStorage.getItem('userid');
    const response = await fetch(`http://localhost:8008/api/course/unit-completion?userId=${userId}&unitId=${unitId}`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    });

    if (!response.ok) {
      throw new Error('Failed to fetch task points');
    }

    const result = await response.json();
    console.log(result)
    // 更新任务点数据
    note.value.task = result.data.completedChapters;
    note.value.sumTask = result.data.totalChapters;
  } catch (error) {
    console.error('Error fetching task points:', error);
  }
};

//更新测验点的函数
const updateTestPoints = async (unitId) => {
  try {
    const userId = localStorage.getItem('userid');
    const response = await fetch(`http://localhost:8008/api/course/quiz-completion?userId=${userId}&unitId=${unitId}`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    });

    if (!response.ok) {
      throw new Error('Failed to fetch test points');
    }

    const result = await response.json();
    console.log(result)
    // 更新任务点数据
    note.value.test = result.data.completedQuizzes;
    note.value.sumTest = result.data.totalQuizzes;
  } catch (error) {
    console.error('Error fetching test points:', error);
  }
};

// 添加获取学习进度的函数
const updateLearningProgress = async () => {
  try {
    const userId = localStorage.getItem('userid');
    const courseId = localStorage.getItem('selectedCourseId');
    
    const response = await fetch(`http://localhost:8008/api/course/course-completion-percentage?userId=${userId}&courseId=${courseId}`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    });

    if (!response.ok) {
      throw new Error('Failed to fetch learning progress');
    }

    const result = await response.json();
    // 更新进度值
    console.log(result)
    progressValue.value = parseInt(result.data.completionPercentage);
    
    // 更新图表配置
    option.value.title.text = progressValue.value + '%';
    option.value.series[0].data = [progressValue.value * 180 / 100];
  } catch (error) {
    console.error('Error fetching learning progress:', error);
  }
};

// 添加获取学习次数的函数
const updateStudyTimes = async () => {
  try {
    const userId = localStorage.getItem('userid');
    
    const response = await fetch(`http://localhost:8008/api/course/study-times?userId=${userId}`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    });

    if (!response.ok) {
      throw new Error('Failed to fetch study times');
    }

    const result = await response.json();
    console.log(result)
    // 更新学习次数值
    note.value.frequency = result.data
    
  } catch (error) {
    console.error('Error fetching study times:', error);
  }
};

// 添加更新学习次数的函数
const updateLearningCount = async (chapterId) => {
  try {
    const userId = localStorage.getItem('userid');
    const response = await fetch('http://localhost:8008/api/course/study-times-update', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      },
      body: JSON.stringify({
        userId: parseInt(userId),
        chapterId: parseInt(chapterId)
      })
    });
    
    if (response.ok) {
      console.log('学习次数更新成功');
    } else {
      console.error('更新学习次数失败:', response.statusText);
    }
  } catch (error) {
    console.error('更新学习次数失败:', error);
  }
};


// 修改 handleNodeClick 函数
const handleNodeClick = async (nodeData) => {
  currentNode.value = nodeData;
  // 如果正在测验中且未显示结果，显示确认弹窗
  if (quizStarted.value && selectedAction.value === 'test' && !showResults.value) {
    showSubmitModal.value = true;
    // 保存用户想要切换到的节点
    pendingNode.value = nodeData;
    return;
  }

  // 原有的处理逻辑
  const chapterId = nodeData.id;
  currentChapterId.value = chapterId;
  const userId = localStorage.getItem('userid');

  console.log('Clicked node ID:', chapterId);
  console.log('User ID:', userId);

  // 如果不是叶子节点，直接返回
  if (nodeData.children && nodeData.children.length > 0) {
    console.log("这是根节点")
    await updateTaskPoints(chapterId);
    await updateTestPoints(chapterId);
    return;
  }
  try {
    // 获取章节内容
    const sectionResponse = await fetch(`http://localhost:8008/api/test/genContent`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      },
      body: JSON.stringify({
        userId: userId,
        chapterId: chapterId
      })
    });
    // ... handle section response ...
    if (sectionResponse.ok) {
      const sectionResult = await sectionResponse.json();
      if (sectionResult && sectionResult.data && sectionResult.data.content) {
        sectionData.value = { content: sectionResult.data.content };
        selectedAction.value = 'learn'; // 切换到学习界面
      } else {
        sectionData.value = { content: '无法加载内容' };
      }
    }
    // 设置测验加载状态
    isQuizLoading.value = true;
    try {
      const quizResponse = await fetch(`http://localhost:8008/api/test/genQuiz`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        },
        body: JSON.stringify({
          userId: userId,
          chapterId: chapterId
        })
      });
      if (quizResponse.ok) {
        const quizResult = await quizResponse.json();
        quizId.value = quizResult.data.quiz_id;
        que.value = quizResult.data.questions;
        if (quizResult && quizResult.data && quizResult.data.questions) {
          testData.value = { content: renderQuizQuestions(quizResult.data.questions) };
          showQuizModal.value = true; // 显示弹窗
          quizStarted.value = false; // 重置测验状态
          showResults.value = false; // 重置结果显示状态
          timeLeft.value = totalMinutes * 60; // 重置倒计时时间
          if (timerId) {
            clearInterval(timerId); // 清除之前的定时器
            timerId = null;
          }
        } else {
          testData.value = { content: '无法加载测验内容' };
        }
      }
    } catch (error) {
      console.error('Error loading quiz:', error);
      testData.value = { content: '测验加载失败，请重试' };
    } finally {
      // 无论成功失败都关闭加载状态
      isQuizLoading.value = false;
    }

  } catch (error) {
    console.error('Error:', error);
    sectionData.value = { content: '请求失败，请稍后重试' };
    testData.value = { content: '请求失败，请稍后重试' };
  }
};


// 响应式变量，用于存储用户答案
const answers = ref([]);
// 响应式变量，用于控制是否显示结果
const showResults = ref(false);
// 响应式变量，用于存储用户的得分
const score = ref(0);

 //渲染题目
 //渲染测验题目的函数
 function renderQuizQuestions(questions) {
  return questions.map((question, index) => {
    let questionHtml = `<div class="question">${index + 1}.${question.question} ${question.type === 'single' ? '(单选题)' : '(多选)'}</div>`;
    questionHtml += `<div class="options">`;
    for (const [option, text] of Object.entries(question.options)) {
      const inputType = question.type === 'single' ? 'radio' : 'checkbox';
      questionHtml += `<div class="option">
        <input type="${inputType}" id="question-${index}-${option}" name="question-${index}" value="${option}" style="display:none">
        <label for="question-${index}-${option}" class="option-box" style="display:flex; align-items:center; border:1px solid rgb(236,201,237); padding:10px; margin:5px 0; cursor:pointer; border-radius:4px; background-color:white; transition: all 0.3s ease">
          <span style="width:30px; height:30px; border:2px solid rgb(236,201,237); border-radius:50%; display:flex; align-items:center; justify-content:center; background-color:white">${option}</span>
          <span style="flex:1; text-align:center; margin-left:10px">${text}</span>
        </label>
      </div>`;
    }
    questionHtml += `</div>`;

    // 添加答案显示区域（初始隐藏）
    questionHtml += `
      <div id="answer-display-${index}" style="display:none; margin: 15px 0;">
        <div class="user-answer" style="color: #666; margin-bottom: 5px; display: flex; align-items: center;">
          你的答案：<span id="user-answer-${index}"></span>
          <span id="answer-icon-${index}" style="margin-left: 20px;"></span>
        </div>
        <div class="correct-answer" style="color: #28a745; margin-bottom: 5px; display: flex; align-items: center;">
          正确答案：<span id="correct-answer-${index}"></span>
          <span id="score-display-${index}" style="margin-left: 40px; color: #666;"></span>
        </div>
        <div class="explanation" style="color: #666; margin-top: 5px;">
          解析：<span id="explanation-${index}"></span>
        </div>
      </div>
    `;
    // 添加横线，除了最后一题
    if (index < questions.length - 1) {
      questionHtml += `<div style="height: 1px; background-color: black; margin: 30px 0;"></div>`;
    }
    questionHtml += `
      <style>
        .option-box:hover {
          background-color: rgb(245,230,245) !important;
          transform: translateX(5px);
        }
        input[type="radio"]:checked + label.option-box,
        input[type="checkbox"]:checked + label.option-box {
          background-color: rgb(236,201,237) !important;
          border-color: rgb(236,201,237);
        }
        input[type="radio"]:checked + label.option-box span:first-child,
        input[type="checkbox"]:checked + label.option-box span:first-child {
          background-color: rgb(236,201,237);
          border-color: black;
          color: black;
        }
      </style>
    `;
    return questionHtml;
  }).join('');
}

const totalMinutes = 10; // 总倒计时时间（分钟）
const timeLeft = ref(totalMinutes * 60); // 初始化倒计时时间（秒）
let timerId = null;

// 计算倒计时显示
const countdownDisplay = computed(() => {
  const minutes = Math.floor(timeLeft.value / 60);
  const seconds = timeLeft.value % 60;
  return `${minutes}:${seconds.toString().padStart(2, '0')}`;
});

// 开始倒计时
const startCountdown = () => {
  if (!quizStarted.value) return; // 如果测验未开始，不启动倒计时
  
  timeLeft.value = totalMinutes * 60;
  if (timerId !== null) {
    clearInterval(timerId);
  }
  timerId = setInterval(() => {
    if (timeLeft.value > 0) {
      timeLeft.value--;
    } else {
      clearInterval(timerId);
    }
  }, 1000);
};

// 添加开始测验方法
const startQuiz = () => {
  showQuizModal.value = false;
  quizStarted.value = true;
  showResults.value = false; // 确保结果不显示
  timeLeft.value = totalMinutes * 60; // 重置倒计时时间
  startCountdown(); // 开始倒计时
};

// 添加取消测验方法
const cancelQuiz = () => {
  showQuizModal.value = false;
  selectedAction.value = 'learn'; // 返回学习界面
};

const quizData =ref()

// 添加完成状态追踪
const completedChapters = ref({});

// 新增响应式变量
const showSubmitModal = ref(false);
const showMessage = ref(false);
const messageText = ref('');

// 修改原有的 submitAnswers 方法
function submitAnswers() {
  if (!quizStarted.value || timeLeft.value <= 0) return;
  showSubmitModal.value = true; // 显示确认弹窗
}

// 显示临时消息的方法
const showTemporaryMessage = (message) => {
  messageText.value = message;
  showMessage.value = true;
  setTimeout(() => {
    showMessage.value = false;
  }, 1000);
};

// 确认提交方法
const confirmSubmit = () => {
  showSubmitModal.value = false;
  
  let totalScore = 0;
  const userAnswers = [];
  let c = JSON.parse(JSON.stringify(que))._value;

  // 禁用所有输入框
  function disableInputs() {
    const allInputs = document.querySelectorAll('input[type="radio"], input[type="checkbox"]');
    allInputs.forEach(input => {
      input.disabled = true;
    });
  }

  if (c) {
    c.forEach((question, index) => {
      const questionId = `question-${index}`;
      const selectedInputs = document.querySelectorAll(`input[name="${questionId}"]:checked`);
      
      if (selectedInputs.length > 0) {
        // 根据题目类型处理答案
        if (question.type === 'single') {
          // 单选题：只取第一个选中的值
          const userAnswer = selectedInputs[0].value;
          userAnswers.push(userAnswer);
          
          // 判断答案是否正确
          if (userAnswer === question.answer) {
            totalScore += 10;
          }
          // 显示答案和解析
          document.getElementById(`user-answer-${index}`).textContent = userAnswer;
          document.getElementById(`correct-answer-${index}`).textContent = question.answer;
          document.getElementById(`explanation-${index}`).textContent = question.explanations;
          document.getElementById(`answer-display-${index}`).style.display = 'block';

          // 判断答案是否正确并显示对应图标和得分
          const isCorrect = userAnswer === question.answer;
          document.getElementById(`answer-icon-${index}`).textContent = isCorrect ? '✅' : '❌';
          document.getElementById(`score-display-${index}`).textContent = `你的得分：${isCorrect ? '10.0' : '0.0'}`;
        } else if (question.type === 'multiple') {
          // 多选题：收集所有选中的值
          const userAnswer = Array.from(selectedInputs).map(input => input.value).sort();
          userAnswers.push(userAnswer);
          // 判断多选题答案是否完全正确
          const correctAnswer = Array.isArray(question.answer) ? 
            question.answer.sort() : 
            [question.answer].sort();
            
          if (JSON.stringify(userAnswer) === JSON.stringify(correctAnswer)) {
            totalScore += 10;
          }
         // 显示答案和解析
         document.getElementById(`user-answer-${index}`).textContent = userAnswer.join(', ');
          document.getElementById(`correct-answer-${index}`).textContent = 
            Array.isArray(question.answer) ? question.answer.join(', ') : question.answer;
          document.getElementById(`explanation-${index}`).textContent = question.explanations;
          document.getElementById(`answer-display-${index}`).style.display = 'block';
          
          // 判断答案是否正确并显示对应图标和得分
          const isCorrect = JSON.stringify(userAnswer) === JSON.stringify(correctAnswer);
          document.getElementById(`answer-icon-${index}`).textContent = isCorrect ? '✅' : '❌';
          document.getElementById(`score-display-${index}`).textContent = `你的得分：${isCorrect ? '10.0' : '0.0'}`;
        }
      } else {
        // 如果没有选择答案，推入null或空数组
        userAnswers.push(question.type === 'single' ? null : []);
        document.getElementById(`user-answer-${index}`).textContent = '未作答';
        document.getElementById(`correct-answer-${index}`).textContent = 
          Array.isArray(question.answer) ? question.answer.join(', ') : question.answer;
        document.getElementById(`explanation-${index}`).textContent = question.explanations;
        document.getElementById(`answer-display-${index}`).style.display = 'block';

         // 未作答显示错误图标和0分
         document.getElementById(`answer-icon-${index}`).textContent = '❌';
        document.getElementById(`score-display-${index}`).textContent = '你的得分：0.0';
      }
      clearInterval(timerId);
      timerId = null;
    });
  }

  // 更新答案数组和分数
  answers.value = userAnswers;
  score.value = totalScore;
  
  // 显示结果
  showResults.value = true;

  // 准备提交数据
  quizData.value = {
    quizId: quizId.value,
    userId: Number(localStorage.getItem('userid')),
    questions: JSON.stringify(que.value),
    score: totalScore,
    timeLeft: timeLeft.value

  };

  // 更新章节完成状态
  if (currentChapterId.value) {
    completedChapters.value[currentChapterId.value] = true;
  }

  console.log(quizData.value);
  // 提交到后端
  if (que.value) {
    submitQuizScore(quizData.value);
  }
  // 禁用输入框
  disableInputs();

  // 停止倒计时
  if (timerId) {
    clearInterval(timerId);
    timerId = null;
  }

  // 显示结果
  showResults.value = true;
  quizStarted.value = false; // 重置测验状态

  // 显示提交成功消息
  showTemporaryMessage('提交成功！');
}



// async function submitQuizScore(quizData) {
//   try {
//     const response = await fetch('http://localhost:8008/quiz/submitQuiz', {
//       method: 'POST',
//       headers: {
//         'Content-Type': 'application/json',
//         'Authorization': `Bearer ${localStorage.getItem('token')}`
//       },
//       body: JSON.stringify(quizData)
//     });

//     if (!response.ok) {
//       throw new Error(`HTTP error! status: ${response.status}`);
//     }

//     const data = await response.json();
//     console.log('Success:', data);
//     console.log('Time:',timeLeft.value)
//   } catch (error) {
//     console.error('Error:', error);
//   }
// }


// 修改提交测验分数的函数
const submitQuizScore = async (quizData) => {
  try {
    const response = await fetch('http://localhost:8008/quiz/submitQuiz', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      },
      body: JSON.stringify(quizData)
    });

    if (response.ok) {
      // 测验分数提交成功后，更新学习次数
      await updateLearningCount(currentNode.value.id);
      console.log('提交测验成功')
      showSubmitModal.value = false;
      // ... 其他成功后的操作
    } else {
      throw new Error('提交测验失败');
    }
  } catch (error) {
    console.error('提交测验失败:', error);
  }
};

// 组件卸载时清除定时器
onUnmounted(() => {
  if (timerId) {
    clearInterval(timerId);
    timerId = null;
  }
});

// 在 script setup 中添加新的响应式变量和方法
const showQuizModal = ref(true); // 控制弹窗显示
const quizStarted = ref(false); // 控制测验是否已开始

// 在 script setup 中添加新的响应式变量
const pendingAction = ref(null);
const pendingNode = ref(null);

// 修改 cancelSubmit 函数
const cancelSubmit = () => {
  showSubmitModal.value = false;
  pendingAction.value = null;
  pendingNode.value = null;
};


//建立webSocket连接
// 在组件挂载时添加页面可见性监听器
// 在组件卸载时移除监听器
// 在组件激活和停用时也相应地添加和移除监听器
// 这样，当用户：
// 最小化浏览器窗口
// 切换到其他标签页
// 切换到其他应用程序
// 都会触发 WebSocket 连接的断开，当用户重新回到页面时，会自动重新建立连接。这样可以更准确地记录用户的实际学习时长。

let ws = null;
let reconnectTimer = null;
let isHandle = false;

// 添加页面可见性变化的处理函数
const handleVisibilityChange = () => {
  if (document.hidden) {
    // 页面被隐藏（最小化或切换到其他标签）
    console.log('页面被隐藏，关闭 WebSocket');
    closeWs();
  } else {
    // 页面重新可见
    console.log('页面重新可见，重新连接 WebSocket');
    initWebSocket();
  }
};

// 添加初始化 WebSocket 的函数
const initWebSocket = () => {
  // 先清理现有连接
  closeWs();
  
  const userId = localStorage.getItem('userid');
  const courseId = localStorage.getItem('selectedCourseId');
  
  if (!userId || !courseId) {
    console.warn('缺少必要的连接参数');
    return;
  }

  console.log('正在初始化 WebSocket 连接...');
  const wsUrl = `ws://localhost:8008/study-time?userId=${userId}&courseId=${courseId}`;
  
  try {
    ws = new WebSocket(wsUrl);
    ws.addEventListener('open', openHandle);
    ws.addEventListener('close', closeHandle);
    ws.addEventListener('message', messageHandle);
    ws.addEventListener('error', errorHandle);
  } catch (error) {
    console.error('WebSocket 初始化失败:', error);
  }
};

const openHandle = () => {
  console.log('WebSocket 连接成功建立');
  isHandle = false;
};

const closeHandle = () => {
  console.log('WebSocket 连接已关闭');
  if (!isHandle) {
    scheduleReconnect();
  }
};

const messageHandle = ({ data }) => {
  console.log('收到消息:', data);
};

const errorHandle = (error) => {
  console.error('WebSocket 错误:', error);
};

const scheduleReconnect = () => {
  if (reconnectTimer) {
    clearTimeout(reconnectTimer);
  }
  reconnectTimer = setTimeout(() => {
    console.log('尝试重新连接...');
    initWebSocket();
  }, 1000);
};

const closeWs = () => {
  isHandle = true;
  if (reconnectTimer) {
    clearTimeout(reconnectTimer);
    reconnectTimer = null;
  }
  if (ws) {
    ws.removeEventListener('open', openHandle);
    ws.removeEventListener('close', closeHandle);
    ws.removeEventListener('message', messageHandle);
    ws.removeEventListener('error', errorHandle);
    if (ws.readyState === WebSocket.OPEN) {
      ws.close();
    }
    ws = null;
  }
  console.log('WebSocket 连接已清理');
};

// 组件挂载时
onMounted(() => {
  console.log('组件挂载，初始化 WebSocket');
  // 添加页面可见性变化监听器
  document.addEventListener('visibilitychange', handleVisibilityChange);
  setTimeout(() => {
    initWebSocket();
  }, 100);
});

// 路由离开时
onBeforeRouteLeave((to, from, next) => {
  console.log('路由离开，关闭 WebSocket');
  closeWs();
  next();
});

// 路由更新时
onBeforeRouteUpdate((to, from, next) => {
  console.log('路由更新，重新初始化 WebSocket');
  initWebSocket();
  next();
});

// 组件卸载时
tryOnUnmounted(() => {
  console.log('组件卸载，关闭 WebSocket');
  // 移除页面可见性变化监听器
  document.removeEventListener('visibilitychange', handleVisibilityChange);
  closeWs();
});

// 组件激活时（从缓存中被重新激活）
onActivated(() => {
  console.log('组件激活，初始化 WebSocket');
  // 重新添加页面可见性变化监听器
  document.addEventListener('visibilitychange', handleVisibilityChange);
  initWebSocket();
});

// 组件停用时（被缓存）
onDeactivated(() => {
  console.log('组件停用，关闭 WebSocket');
  // 移除页面可见性变化监听器
  document.removeEventListener('visibilitychange', handleVisibilityChange);
  closeWs();
});

defineExpose({
  initWebSocket,
  closeWs
});

</script>


<style scoped>
/* 主容器 */
#main-container {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    padding: 20px;
    margin: 0 auto;
    max-width: 1400px;
    width: 100%;
    box-sizing: border-box;
    gap: 20px;
}

/* 左侧模块 */
#left-container {
    width: 25%;
    min-width: 250px;
    height: 650px;
    background-color: #f8f9fa;
    border-radius: 15px;
    box-shadow: 0 10px 10px rgb(237, 201, 237);
    flex-shrink: 0;
    overflow: hidden;
}

#left-content {
    padding: 20px;
    height: 100%;
    display: flex;
    flex-direction: column;
    box-sizing: border-box;
}

/* 左侧头部样式 */
.left-header {
    margin-bottom: 20px;
}

.left-header h2 {
    color: #333;
    font-size: 1.5em;
    margin: 0;
    padding: 0;
    font-weight: 600;
}

.header-underline {
    height: 3px;
    background: linear-gradient(to right, rgb(236, 198, 236), transparent);
    margin-top: 8px;
    border-radius: 2px;
}

/*倒计时样式*/
.countdown {
  background-color:rgb(79, 132, 230);
  color:#f1f1f1;
  position: absolute;
  top: 0;
  right: 0;
  border: 2px solid rgb(79, 132, 230);
  padding: 5px; 
  border-radius: 5px; 
}

.time-message {
  color:rgb(79, 132, 230);
  font-weight: bold;
  text-align: center;
}

/* 课程树样式 */
.course-tree {
    flex: 1;
    overflow: auto;
    padding-right: 10px;
    margin-top: 10px;
}

/* 自定义树节点样式 */
.custom-tree {
    background: transparent;
    font-size: 14px;
}

.custom-tree :deep(.el-tree-node__content) {
    height: 36px;
    border-radius: 6px;
    margin: 4px 0;
    transition: all 0.3s ease;
}

.custom-tree :deep(.el-tree-node__content:hover) {
    background-color: rgb(245, 230, 245);
}

.custom-tree :deep(.el-tree-node.is-current > .el-tree-node__content) {
    background-color: rgb(236, 198, 236);
    color: #333;
}

.custom-tree-node {
    display: flex;
    align-items: center;
    justify-content: space-between;
    width: 100%;
    padding: 0 6px;
}

.node-label {
    font-size: 14px;
    color: #333;
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.node-count {
    background-color: rgb(236, 198, 236);
    color: #fff;
    padding: 2px 6px;
    border-radius: 10px;
    font-size: 12px;
    margin-left: 8px;
}

/* 自定义滚动条 */
.course-tree::-webkit-scrollbar {
    width: 6px;
}

.course-tree::-webkit-scrollbar-track {
    background: #f1f1f1;
    border-radius: 3px;
}

.course-tree::-webkit-scrollbar-thumb {
    background: #ddd;
    border-radius: 3px;
}

.course-tree::-webkit-scrollbar-thumb:hover {
    background: #ccc;
}

/* 左侧统计项样式 */
.course-stats {
    margin: 15px 0;
    padding: 15px;
    background: white;
    border-radius: 10px;
    box-shadow: 0 2px 6px rgba(236, 198, 236, 0.2);
}

.stat-item {
    display: flex;
    align-items: center;
    padding: 12px;
    margin: 8px 0;
    background: #f8f9fa;
    border-radius: 8px;
    transition: all 0.3s ease;
}

.stat-item:hover {
    transform: translateX(5px);
    background: rgb(245, 230, 245);
}

.stat-icon {
    width: 32px;
    height: 32px;
    margin-right: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
}

.stat-icon img {
    width: 24px;
    height: 24px;
    object-fit: contain;
}

.stat-content {
    flex: 1;
    display: flex;
    justify-content: space-between;
    align-items: center;
    min-width: 0;
}

.stat-label {
    color: #666;
    font-size: 14px;
    font-weight: 500;
}

.stat-value {
    color: rgb(236, 198, 236);
    font-size: 15px;
    font-weight: 600;
    margin-left: 8px;
}

.progress-chart-container {
    margin-top: 15px;
    height: 160px;
    background: #f8f9fa;
    border-radius: 8px;
    padding: 15px;
    transition: all 0.3s ease;
    display: flex;
    justify-content: center;
    align-items: center;
}

.progress-chart-container:hover {
    transform: translateX(5px);
    background: rgb(245, 230, 245);
}

.progress-chart {
    width: 80%;
    height: 80%;
}

/* 自定义图表样式 */
:deep(.echarts-container) {
    display: flex;
    justify-content: center;
    align-items: center;
}

/* 中间模块 */
#middle-container {
    width: 50%;
    min-width: 500px;
    height: 650px;
    background-color: #f8f9fa;
    border-radius: 15px;
    box-shadow: 0 10px 10px rgb(237, 201, 237);
    flex-shrink: 0;
    overflow: hidden;
    position: relative;
}

#middle-content {
    padding: 20px;
    height: 100%;
    width: 100%;
    position: relative;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;
    max-width: 100%;
    overflow-x: hidden;
}

.action-buttons {
    display: flex;
    justify-content: flex-start;
    gap: 20px;
    margin-bottom: 15px;
    padding: 0 10px;
}

.action-btn {
    padding: 10px 30px;
    font-size: 16px;
    border: none;
    background-color: white;
    color: #666;
    border-radius: 6px;
    cursor: pointer;
    transition: all 0.3s ease;
    outline: none;
}

.action-btn:hover {
    background-color: rgb(245, 230, 245);
    transform: translateY(-1px);
}

.action-btn.active {
    background-color: rgb(236, 198, 236);
    color: white;
    transform: translateY(1px);
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.divider {
    height: 2px;
    background-color: rgb(244, 219, 245);
    margin: 0 10px 15px 10px;
}

.content-wrapper {
    flex: 1;
    overflow: hidden;
    position: relative;
    width: 100%;
}

.content-section {
    height: 100%;
    overflow: auto;
    padding: 20px;
    box-sizing: border-box;
    background-color: white;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
    width: 100%;
    max-width: 100%;
    word-wrap: break-word;
    overflow-x: hidden;
}

.section-content {
    line-height: 1.6;
    color: #333;
    width: 100%;
    max-width: 100%;
    word-wrap: break-word;
    overflow-wrap: break-word;
}

/* 右侧模块 */
#right-container {
    width: 25%;
    min-width: 280px;
    height: 650px;
    background-color: #f8f9fa;
    border-radius: 15px;
    box-shadow: 0 10px 10px rgb(237, 201, 237);
    flex-shrink: 0;
}

#right-content {
    padding: 20px;
    height: 100%;
}

#plan-title{
  font-size:1.5em;
  font-weight:bold;
  margin-bottom:20px;
}

#plan-line{
  height:1px;
  background-color:rgb(244, 219, 245);
  margin-bottom:10px;
}

/* 树形目录样式 */
.el-tree {
  margin-top: 20px;
  max-height: 400px;
  overflow-y: auto;
  color:black;
}

/* 章节数据展示部分 */
#section-content {
  margin-top: 20px;
  padding: 10px;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 2px 5px #a7caf0;
}

/* 自定义滚动条样式 */
.content-section::-webkit-scrollbar,
#scrollable-area::-webkit-scrollbar {
    width: 8px;
    height: 8px;
}
.content-section::-webkit-scrollbar-thumb:hover,
#scrollable-area::-webkit-scrollbar-thumb:hover {
    background: #aaa;
}

/* 左侧模块 */
#left-content {
    padding: 20px;
    height: 100%;
    overflow-y: auto;
}

.chart{
    height:250px;
    width: 280px;
}

/* 学习计划模块 */
#learning-plan-container {
    margin-top: 30px;
}

.learning-plan-item.vertical {
  position:relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 25px;
  padding: 10px;
  background-color: #f0f8ff;
  border-radius: 8px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.learning-plan-item.vertical .label {
  position:absolute;
  top:30%;
  left:15%;
  font-weight: bold;
  margin-bottom: 10px;
  margin-top:5px;
}

.learning-plan-item.vertical .value {
  position: absolute;
  top:35%;
  left:80%;
  color: #007bff;
}

.image{
  position: absolute;
  top:40%;
  left:6%
}

/* 中间模块 */
#middle-content {
    padding: 20px;
    height: 100%;
    overflow-y: auto;
    position:absolute;
}
/* 提交按钮样式 */
button {
  padding: 10px 20px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  margin-top: 20px;
}

button:hover {
  background-color: #0056b3;
}
/* 按钮容器样式 */
.button-container {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

/* 按钮样式 */
.action-button {
  padding: 10px 20px;
  background-color: white;
  border: none;
  color: #666;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s;
  margin-right:20px;
}

/* 选中状态的按钮样式 */
.action-button.selected {
  background-color: rgb(236, 198, 236);
  color: white;
}

/*横线样式 */
#middle-line{
  height:1px;
  width:100%;
  background-color:rgb(244, 219, 245);
  margin-bottom:10px;
}

/*内容区域样式 */
#content-area{
  height:100%;
  overflow-y:auto;
}

/*学习内容区域样式 */
#learn-section{
  background-color: #a7caf0;
  padding:20px;
  border-radius: 8px;
}

/* 添加边框样式 */
.border-container {
  border: 15px solid transparent;
}

/* 当选中学习时，应用紫色边框 */
.purple-border {
  border-color: rgb(237, 201, 237);
  height:490px;
  width:280px;
}

/*测试内容区域样式 */
.tets-section{
  background-color:rgb(245, 205, 245) ;
  padding:20px;
  border-radius:8px;
}
/* .quiz-container
 {
  
  border: #a7caf0;
  height: 500px;

} */
.question
 {
  margin-bottom: 20px
;
}
.option
 {
  display: block;
  margin: 5px 0;
}

.report-btn-container {
  margin-top: 20px;
  padding: 0 10px;
}

.report-btn {
  width: 100%;
  padding: 12px;
  background: rgb(236, 198, 236);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 14px;
  transition: all 0.3s ease;
}

.report-btn:hover {
  background: rgb(226, 178, 226);
  transform: translateY(-1px);
}

.report-btn i {
  font-size: 16px;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid rgb(236, 198, 236);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-container p {
  color: #666;
  font-size: 14px;
  margin: 0;
}

/* 添加状态点样式 */
.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: rgb(226, 178, 226);
  border: 1.5px solid white;
  box-shadow: 0 0 0 1px rgb(226, 178, 226);
  flex-shrink: 0;

}

.status-dot.completed {
  background-color: #52c41a;
  box-shadow: 0 0 0 1px #52c41a;
}

/* 添加选中节点的样式 */
.selected-node {
  background-color: rgb(236, 198, 236) !important;
  border-radius: 4px;
  padding: 4px;
}

.no-content-message {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
  color: #666;
  font-size: 16px;
  text-align: center;
}

.submit-btn {
  display: block;
  margin: 20px auto;
  padding: 12px 30px;
  background-color: rgb(236, 198, 236);
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 16px;
  transition: all 0.3s ease;
}

.submit-btn:hover {
  background-color: rgb(226, 178, 226);
  transform: translateY(-2px);
}

.submit-btn:active {
  transform: translateY(1px);
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 300px;
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid rgb(236, 198, 236);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 20px;
}

.loading-container p {
  color: #666;
  font-size: 16px;
  margin: 0;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.quiz-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  padding: 30px;
  border-radius: 15px;
  text-align: center;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  animation: modalFadeIn 0.3s ease;
}

.cute-character {
  width: 200px;
  height: 200px;
  margin-bottom: 20px;
  border-radius: 10px;
}

.modal-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 20px;
}

.modal-btn {
  padding: 12px 30px;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.start-btn {
  background-color: rgb(236, 198, 236);
  color: white;
}

.start-btn:hover {
  background-color: rgb(226, 178, 226);
  transform: translateY(-2px);
}

.cancel-btn {
  background-color: #f5f5f5;
  color: #666;
}

.cancel-btn:hover {
  background-color: #e8e8e8;
  transform: translateY(-2px);
}

@keyframes modalFadeIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.submit-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.thinking-character {
  width: 200px;
  height: 200px;
  margin-bottom: 20px;
  border-radius: 10px;
}

.modal-text {
  font-size: 18px;
  color: #333;
  margin-bottom: 20px;
}

.yes-btn {
  background-color: rgb(236, 198, 236);
  color: white;
}

.yes-btn:hover {
  background-color: rgb(226, 178, 226);
}

.no-btn {
  background-color: #f5f5f5;
  color: #666;
}

.no-btn:hover {
  background-color: #e8e8e8;
}

.message-popup {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 15px 30px;
  border-radius: 8px;
  font-size: 16px;
  z-index: 1001;
  animation: fadeInOut 1s ease;
}

@keyframes fadeInOut {
  0% {
    opacity: 0;
    transform: translate(-50%, -50%) scale(0.9);
  }
  20% {
    opacity: 1;
    transform: translate(-50%, -50%) scale(1);
  }
  80% {
    opacity: 1;
    transform: translate(-50%, -50%) scale(1);
  }
  100% {
    opacity: 0;
    transform: translate(-50%, -50%) scale(0.9);
  }
}

</style>
