<template>
  <div id="main-container">
    <!-- 中间聊天框 -->
    <div id="chat-container">
      <div class="chat-header">
        <div class="header-left">
          <img :src="botAvatar" class="header-avatar" alt="AI助手" />
          <div class="header-info">
            <div class="header-title">AI学习助手</div>
            <div class="header-subtitle">专注教育辅导</div>
          </div>
        </div>
        <div class="header-right">
          <div class="header-status">
            <span class="status-text">{{ isConnected ? '在线' : '离线' }}</span>
            <div class="status-indicator" :class="{ 'connected': isConnected }"></div>
          </div>
          <div class="header-actions">
            <button class="action-btn" title="清空对话" @click="showClearConfirm = true">
              <i class="fas fa-trash-alt"></i>
            </button>
            <button class="action-btn" title="设置" @click="showSettings = true">
              <i class="fas fa-cog"></i>
            </button>
          </div>
        </div>
      </div>
      
      <!-- 清空对话确认弹窗 -->
      <div class="modal-overlay" v-if="showClearConfirm" @click="showClearConfirm = false">
        <div class="modal-content" @click.stop>
          <h3>确认清空对话</h3>
          <p>是否确认清空所有对话记录？此操作不可撤销。</p>
          <div class="modal-actions">
            <button class="modal-btn cancel" @click="showClearConfirm = false">取消</button>
            <button class="modal-btn confirm" @click="clearChat">确认清空</button>
          </div>
        </div>
      </div>
      
      <!-- 设置面板 -->
      <div v-if="showSettings" class="settings-panel">
        <div class="settings-header">
          <h3>设置</h3>
          <button class="close-btn" @click="showSettings = false">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="settings-body">
          <div class="settings-group">
            <div class="settings-title">界面设置</div>
            <div class="settings-item">
              <span>深色模式</span>
              <label class="switch">
                <input type="checkbox" v-model="settings.darkMode">
                <span class="slider"></span>
              </label>
            </div>
            <div class="settings-item">
              <span>字体大小</span>
              <div class="size-buttons">
                <button 
                  v-for="size in ['小', '中', '大']" 
                  :key="size"
                  :class="['size-btn', settings.fontSize === size ? 'active' : '']"
                  @click="settings.fontSize = size"
                >
                  {{ size }}
                </button>
              </div>
            </div>
          </div>
          <div class="settings-group">
            <div class="settings-title">语言设置</div>
            <div class="settings-item">
              <span>界面语言</span>
              <div class="language-select">
                <select v-model="settings.language">
                  <option value="zh">中文</option>
                  <option value="en">English</option>
                </select>
              </div>
            </div>
          </div>
          <div class="settings-group">
            <div class="settings-title">聊天记录</div>
            <div class="settings-item">
              <span>保存聊天记录</span>
              <label class="switch">
                <input type="checkbox" v-model="settings.saveHistory">
                <span class="slider"></span>
              </label>
            </div>
            <button class="clear-history" @click="clearHistory">
              {{ settings.language === 'zh' ? '清除聊天记录' : 'Clear History' }}
              <i class="fas fa-trash-alt"></i>
            </button>
          </div>
        </div>
        <div class="settings-footer">
          <button class="settings-btn cancel" @click="showSettings = false">取消</button>
          <button class="settings-btn save" @click="saveSettings">保存</button>
        </div>
      </div>
      
      <div id="chat-box" ref="chatBox">
        <div
          v-for="(message, index) in messages"
          :key="index"
          class="message"
          :class="[message.isUser ? 'user-message' : 'ai-message']"
        >
          <div class="message-content">
            <img
              :src="message.isUser ? userAvatar : botAvatar"
              :alt="message.isUser ? '用户' : 'AI助手'"
              class="avatar"
            />
            <div class="message-bubble">
              <div class="message-header">
                <span class="sender-name">{{ message.isUser ? '你' : 'AI助手' }}</span>
                <span class="message-time">{{ new Date().toLocaleTimeString() }}</span>
              </div>
              <div class="message-body">
                <div v-if="!message.isUser">
                  <div v-if="message.type === 'plan'" class="plan-message">
                    <div class="plan-title">
                      <i class="fas fa-list-check"></i>
                      学习计划
                    </div>
                    <ul class="plan-details">
                      <li v-for="(week, idx) in message.data" :key="idx">{{ week }}</li>
                    </ul>
                  </div>
                  <div v-else-if="message.type === 'error'" class="error-message">
                    <div class="error-title">
                      <i class="fas fa-exclamation-triangle"></i>
                      错误
                    </div>
                    <p>{{ message.text }}</p>
                  </div>
                  <div v-else class="general-message">
                    <div v-html="renderMarkdown(message.text)"></div>
                  </div>
                </div>
                <span v-else v-html="message.text"></span>
              </div>
            </div>
          </div>
        </div>
        <div v-if="isLoading" class="loading-indicator">
          <div class="loading-animation">
            <div class="circle"></div>
            <div class="circle"></div>
            <div class="circle"></div>
          </div>
          <span>AI思考中...</span>
        </div>
      </div>
      <div class="input-area">
        <div class="input-container">
          <div class="input-wrapper">
            <textarea
              v-model="userInput"
              class="message-input"
              placeholder="输入你的问题..."
              @keyup.enter.exact="sendMessage"
              @keydown.enter.exact.prevent
            ></textarea>
            <div class="input-border"></div>
          </div>
          <button 
            class="send-button" 
            @click="sendMessage"
            :disabled="!userInput.trim()"
          >
            <span class="button-content">
              <i class="fas fa-paper-plane"></i>
              <span class="send-effect"></span>
            </span>
          </button>
        </div>
      </div>
    </div>

    <!-- 右侧下拉框容器 -->
    <div id="dropdown-container">
      <div class="course-panel">
        <div class="course-header">
          <h3>课程目录</h3>
          <div class="course-categories">
            <button class="category-btn active">
              <i class="fas fa-code"></i>
              编程开发
            </button>
          </div>
        </div>
        
        <div class="course-list">
          <div class="course-group">
            <div class="group-header">
              <i class="fab fa-python"></i>
              <span>Python系列</span>
            </div>
            <div class="course-item" @click="selectCourse('python_basic')">
              <div class="course-info">
                <h4>Python基础教程</h4>
                <div class="course-meta">
                  <span class="difficulty beginner">入门</span>
                  <span class="duration">30课时</span>
                </div>
              </div>
              <i class="fas fa-chevron-right"></i>
            </div>
            <div class="course-item" @click="selectCourse('python_advance')">
              <div class="course-info">
                <h4>Python进阶开发</h4>
                <div class="course-meta">
                  <span class="difficulty intermediate">进阶</span>
                  <span class="duration">40课时</span>
                </div>
              </div>
              <i class="fas fa-chevron-right"></i>
            </div>
          </div>

          <div class="course-group">
            <div class="group-header">
              <i class="fas fa-code"></i>
              <span>C/C++系列</span>
            </div>
            <div class="course-item" @click="selectCourse('c')">
              <div class="course-info">
                <h4>C语言基础</h4>
                <div class="course-meta">
                  <span class="difficulty beginner">入门</span>
                  <span class="duration">35课时</span>
                </div>
              </div>
              <i class="fas fa-chevron-right"></i>
            </div>
            <div class="course-item" @click="selectCourse('cpp')">
              <div class="course-info">
                <h4>C++编程进阶</h4>
                <div class="course-meta">
                  <span class="difficulty advanced">高级</span>
                  <span class="duration">45课时</span>
                </div>
              </div>
              <i class="fas fa-chevron-right"></i>
            </div>
          </div>

          <div class="course-group">
            <div class="group-header">
              <i class="fas fa-network-wired"></i>
              <span>计算机基础</span>
            </div>
            <div class="course-item" @click="selectCourse('computer_network')">
              <div class="course-info">
                <h4>计算机网络</h4>
                <div class="course-meta">
                  <span class="difficulty intermediate">进阶</span>
                  <span class="duration">38课时</span>
                </div>
              </div>
              <i class="fas fa-chevron-right"></i>
            </div>
            <div class="course-item" @click="selectCourse('operating_system')">
              <div class="course-info">
                <h4>操作系统</h4>
                <div class="course-meta">
                  <span class="difficulty intermediate">进阶</span>
                  <span class="duration">42课时</span>
                </div>
              </div>
              <i class="fas fa-chevron-right"></i>
            </div>
            <div class="course-item" @click="selectCourse('computer_organization')">
              <div class="course-info">
                <h4>计算机组成原理</h4>
                <div class="course-meta">
                  <span class="difficulty advanced">高级</span>
                  <span class="duration">40课时</span>
                </div>
              </div>
              <i class="fas fa-chevron-right"></i>
            </div>
          </div>

          <div class="course-group">
            <div class="group-header">
              <i class="fas fa-shield-alt"></i>
              <span>网络安全</span>
            </div>
            <div class="course-item" @click="selectCourse('network_security')">
              <div class="course-info">
                <h4>网络安全基础</h4>
                <div class="course-meta">
                  <span class="difficulty intermediate">进阶</span>
                  <span class="duration">36课时</span>
                </div>
              </div>
              <i class="fas fa-chevron-right"></i>
            </div>
          </div>

          <div class="course-group">
            <div class="group-header">
              <i class="fas fa-database"></i>
              <span>数据库</span>
            </div>
            <div class="course-item" @click="selectCourse('database')">
              <div class="course-info">
                <h4>数据库原理与应用</h4>
                <div class="course-meta">
                  <span class="difficulty intermediate">进阶</span>
                  <span class="duration">32课时</span>
                </div>
              </div>
              <i class="fas fa-chevron-right"></i>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import { useRouter } from "vue-router";
import { marked } from "marked";

const messages = ref([
  { text: "你好！我是AI助手，很高兴为您服务。请问有什么我可以帮您的吗？", isUser: false },
]);

const userInput = ref("");
const isLoading = ref(false);
const isConnected = ref(true);
const userAvatar = require("@/assets/客户头像.png");
const botAvatar = require("@/assets/AI老师.png");
const showClearConfirm = ref(false);
const showSettings = ref(false);

const settings = ref({
  darkMode: false,
  fontSize: '中',
  language: 'zh',
  saveHistory: true
});

const translations = {
  zh: {
    settings: '设置',
    darkMode: '深色模式',
    fontSize: '字体大小',
    small: '小',
    medium: '中',
    large: '大',
    language: '语言',
    saveHistory: '保存聊天记录',
    clearHistory: '清除聊天记录',
    cancel: '取消',
    save: '保存',
    confirmClear: '确定要清除所有聊天记录吗？此操作不可恢复。'
  },
  en: {
    settings: 'Settings',
    darkMode: 'Dark Mode',
    fontSize: 'Font Size',
    small: 'Small',
    medium: 'Medium',
    large: 'Large',
    language: 'Language',
    saveHistory: 'Save Chat History',
    clearHistory: 'Clear History',
    cancel: 'Cancel',
    save: 'Save',
    confirmClear: 'Are you sure you want to clear all chat history? This action cannot be undone.'
  }
};

const t = computed(() => translations[settings.value.language]);

const router = useRouter();

const sendMessage = async () => {
  if (userInput.value.trim()) {
    messages.value.push({ text: userInput.value, isUser: true });
    const inputText = userInput.value;
    userInput.value = "";

    scrollToBottom();
    isLoading.value = true;

    const token = localStorage.getItem('token');
    const userId = localStorage.getItem('userid');
    
    try {
      const response = await fetch("http://localhost:8008/api/test/askAi", {
        method: "POST",
        headers: { 
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        },
        credentials: 'include',
        body: JSON.stringify({
          query: inputText,
          userId: userId
        }),
      });

      if (!response.ok) {
        if (response.status === 401) {
          router.push('/login');
          throw new Error("请先登录");
        }
        throw new Error("从服务器获取响应失败");
      }

      const data = await response.json();

      if (data.code !== 200) {
        messages.value.push({
          type: 'error',
          text: data.msg || "请求失败",
          isUser: false
        });
        scrollToBottom();
        return;
      }

      const aiResponse = data.data;
      let cleanedText = aiResponse.answer.replace(/\*\*(.*?)\*\*/g, '$1');

      // 直接显示完整消息
      messages.value.push({
        text: cleanedText,
        isUser: false,
        type: data.type === 'plan' ? 'plan' : 'text',
        data: data.type === 'plan' ? data.data : null
      });

      scrollToBottom();
    } catch (error) {
      messages.value.push({
        type: 'error',
        text: `错误：${error.message}`,
        isUser: false,
      });
    } finally {
      isLoading.value = false;
    }
  }
};

const scrollToBottom = () => {
  setTimeout(() => {
    const chatBox = document.getElementById("chat-box");
    chatBox.scrollTop = chatBox.scrollHeight;
  }, 0);
};

const renderMarkdown = (text) => {
  return marked.parse(text);
};

async function selectCourse(course) {
  if (course === 'c') {
    try {
      const userId = parseInt(localStorage.getItem('userid'));
      const token = localStorage.getItem('token');
      
      const checkResponse = await fetch(`http://localhost:8008/api/course/check-enrollment?userId=${userId}&courseId=1`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      const checkResult = await checkResponse.json();
      
      if (checkResult.code === 200 && checkResult.data === true) {
        localStorage.setItem('selectedCourseId', '1');
        router.push("/learning");
        return;
      }
      
      const response = await fetch('http://localhost:8008/api/course/enroll', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
          userId: userId,
          courseId: 1
        })
      });
      
      const result = await response.json();
      
      if (result.code === 200) {
        localStorage.setItem('selectedCourseId', '1');
        router.push("/learning");
      } else {
        console.error('选课失败:', result.msg);
      }
    } catch (error) {
      console.error('选课请求失败:', error);
    }
  } else {
    console.log(`Selected course: ${course}`);
  }
}

const clearChat = () => {
  messages.value = [];  // 清空消息数组
  showClearConfirm.value = false;  // 关闭确认弹窗
};

const saveSettings = () => {
  localStorage.setItem('chatSettings', JSON.stringify(settings.value));
  showSettings.value = false;
  // 应用设置
  applySettings();
};

const applySettings = () => {
  // 应用深色模式
  document.body.classList.toggle('dark-mode', settings.value.darkMode);
  // 应用字体大小
  document.documentElement.style.setProperty('--font-size-base', 
    settings.value.fontSize === '小' ? '14px' : 
    settings.value.fontSize === '大' ? '18px' : '16px'
  );
  // 应用语言
  document.documentElement.setAttribute('lang', settings.value.language);
};

const clearHistory = () => {
  if (confirm(t.value.confirmClear)) {
    messages.value = [];
    localStorage.removeItem('chatHistory');
  }
};

// 在组件挂载时加载设置
import { onMounted } from 'vue';
onMounted(() => {
  const savedSettings = localStorage.getItem('chatSettings');
  if (savedSettings) {
    settings.value = JSON.parse(savedSettings);
    applySettings();
  }
});
</script>

<style scoped>
/* 基础样式 */
#main-container {
  display: flex;
  justify-content: center;
  width: 100%;
  max-width: 1280px;
  height: calc(100vh - 120px); /* 减小整体高度 */
  margin: 20px auto;
  padding: 0 20px;
  gap: 20px;
  position: relative;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8eb 100%);
  overflow: hidden;
}

/* 聊天框样式优化 */
#chat-container {
  flex: 1;
  min-width: 600px;
  max-width: 800px;
  height: 100%;
  display: flex;
  flex-direction: column;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(31, 38, 135, 0.15);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.18);
  overflow: hidden;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  background: linear-gradient(135deg, #6e8efb, #a777e3);
  color: white;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: 2px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.header-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.header-title {
  font-size: 16px;
  font-weight: 600;
}

.header-subtitle {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.8);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-status {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 20px;
}

.status-text {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.9);
}

.status-indicator {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #f44336;
  transition: all 0.3s ease;
}

.status-indicator.connected {
  background: #4caf50;
  box-shadow: 0 0 0 0 rgba(76, 175, 80, 0.4);
  animation: pulse 2s infinite;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: none;
  background: rgba(255, 255, 255, 0.1);
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.action-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-1px);
}

.action-btn:active {
  transform: translateY(0);
}

@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(76, 175, 80, 0.4);
  }
  70% {
    box-shadow: 0 0 0 6px rgba(76, 175, 80, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(76, 175, 80, 0);
  }
}

#chat-box {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: rgba(248, 249, 250, 0.9);
  scroll-behavior: smooth;
}

.message {
  margin: 12px 0;
  opacity: 0;
  animation: slideIn 0.3s ease forwards;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message-content {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 8px 15px;
  width: 100%;
}

.user-message {
  display: flex;
  justify-content: flex-end;
}

.ai-message {
  display: flex;
  justify-content: flex-start;
}

.message-bubble {
  background: white;
  padding: 12px 16px;
  border-radius: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  max-width: 80%;
  line-height: 1.5;
}

.user-message .message-bubble {
  background: linear-gradient(135deg, #6e8efb, #a777e3);
  color: white;
  border-bottom-right-radius: 5px;
  margin-right: 12px;
}

.ai-message .message-bubble {
  background: #f8f9fa;
  border-bottom-left-radius: 5px;
  margin-left: 12px;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
  font-size: 12px;
  color: rgba(0, 0, 0, 0.5);
}

.user-message .message-header {
  color: rgba(255, 255, 255, 0.8);
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: 2px solid white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  object-fit: cover;
}

.user-message .message-content {
  flex-direction: row-reverse;
}

/* 加载动画 */
.loading-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin: 20px 0;
}

.loading-animation {
  display: flex;
  gap: 6px;
}

.circle {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: linear-gradient(135deg, #6e8efb, #a777e3);
  animation: bounce 0.5s alternate infinite;
}

.circle:nth-child(2) { animation-delay: 0.2s; }
.circle:nth-child(3) { animation-delay: 0.4s; }

@keyframes bounce {
  to {
    transform: translateY(-8px);
  }
}

/* 输入区域样式 */
.input-area {
  padding: 20px;
  background: var(--bg-secondary, #f5f7fa);
  border-top: 1px solid var(--border-color, #e0e0e0);
}

.input-container {
  display: flex;
  gap: 12px;
  max-width: 900px;
  margin: 0 auto;
  background: var(--bg-primary, #fff);
  border-radius: 12px;
  padding: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.input-container:focus-within {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
}

.input-wrapper {
  flex: 1;
  position: relative;
}

.message-input {
  width: 100%;
  min-height: 24px;
  max-height: 120px;
  padding: 8px 16px;
  border: none;
  border-radius: 8px;
  background: transparent;
  font-size: 14px;
  line-height: 1.5;
  color: var(--text-primary, #333);
  resize: none;
  outline: none;
  transition: all 0.3s ease;
}

.message-input::placeholder {
  color: var(--text-secondary, #999);
}

.input-border {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(135deg, #6e8efb, #a777e3);
  transform: scaleX(0);
  transition: all 0.3s ease;
}

.input-container:focus-within .input-border {
  transform: scaleX(1);
}

.send-button {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border: none;
  border-radius: 10px;
  background: #2196f3;
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
  padding: 0;
}

.send-button:hover:not(:disabled) {
  background: #1976d2;
  transform: translateY(-1px);
}

.send-button:active:not(:disabled) {
  transform: translateY(1px);
}

.send-button:disabled {
  background: #ccc;
  cursor: not-allowed;
  opacity: 0.7;
}

.send-button i {
  font-size: 16px;
}

.button-content {
  display: flex;
  align-items: center;
  gap: 4px;
}

.send-effect {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: white;
  transform: translateX(0);
  transition: all 0.3s ease;
}

.send-button:hover .send-effect {
  transform: translateX(4px);
}

/* 计划消息样式 */
.plan-message {
  background: rgba(110, 142, 251, 0.1);
  border-radius: 8px;
  padding: 12px;
  margin-top: 8px;
}

.plan-title {
  font-weight: 600;
  color: #6e8efb;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.plan-details {
  list-style-type: none;
  padding: 0;
  margin: 0;
}

.plan-details li {
  padding: 8px 0;
  border-bottom: 1px solid rgba(110, 142, 251, 0.1);
}

.plan-details li:last-child {
  border-bottom: none;
}

/* 错误消息样式 */
.error-message {
  background: rgba(244, 67, 54, 0.1);
  border-radius: 8px;
  padding: 12px;
  margin-top: 8px;
}

.error-title {
  color: #f44336;
  font-weight: 600;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 滚动条样式 */
#chat-box::-webkit-scrollbar {
  width: 6px;
}

#chat-box::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.1);
  border-radius: 3px;
}

#chat-box::-webkit-scrollbar-thumb {
  background: rgba(110, 142, 251, 0.5);
  border-radius: 3px;
}

#chat-box::-webkit-scrollbar-thumb:hover {
  background: rgba(110, 142, 251, 0.7);
}

/* 右侧下拉框样式 */
#dropdown-container {
  width: 280px;
  min-width: 280px;
  height: 100%;
  overflow: hidden;
}

.course-panel {
  height: 100%;
  background: white;
  border-radius: 15px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.course-header {
  padding: 20px;
  border-bottom: 1px solid #eee;
  background: white;
  border-radius: 15px 15px 0 0;
}

.course-header h3 {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 1.2em;
  font-weight: 600;
}

.course-categories {
  display: flex;
  gap: 10px;
}

.category-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 20px;
  background: #f5f5f5;
  color: #666;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.9em;
}

.category-btn.active {
  background: #2196f3;
  color: white;
}

.course-list {
  flex: 1;
  overflow-y: auto;
  padding: 15px;
}

.course-group {
  margin-bottom: 25px;
}

.group-header {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 15px;
  color: #666;
  font-weight: 500;
  font-size: 0.95em;
}

.group-header i {
  color: #2196f3;
  font-size: 1.1em;
}

.course-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 15px;
  margin: 8px 0;
  border-radius: 10px;
  background: white;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #eee;
}

.course-item:hover {
  background: #f8f9fa;
  transform: translateY(-2px);
  border-color: #2196f3;
}

.course-info {
  flex: 1;
}

.course-info h4 {
  margin: 0 0 5px 0;
  color: #333;
  font-size: 0.95em;
  font-weight: 500;
}

.course-meta {
  display: flex;
  gap: 12px;
  font-size: 0.8em;
}

.difficulty {
  padding: 2px 8px;
  border-radius: 12px;
  font-weight: 500;
}

.beginner {
  background: #e3f2fd;
  color: #1565c0;
}

.intermediate {
  background: #fff3e0;
  color: #e65100;
}

.advanced {
  background: #fbe9e7;
  color: #d32f2f;
}

.duration {
  color: #666;
  display: flex;
  align-items: center;
}

.course-item i.fa-chevron-right {
  color: #ccc;
  font-size: 0.9em;
  transition: all 0.3s ease;
}

.course-item:hover i.fa-chevron-right {
  color: #2196f3;
  transform: translateX(3px);
}

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 24px;
  border-radius: 12px;
  width: 90%;
  max-width: 400px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.modal-content h3 {
  margin: 0 0 16px;
  color: #333;
  font-size: 18px;
}

.modal-content p {
  margin: 0 0 24px;
  color: #666;
  font-size: 14px;
  line-height: 1.5;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.modal-btn {
  padding: 8px 16px;
  border-radius: 6px;
  border: none;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.modal-btn.cancel {
  background: #f5f5f5;
  color: #666;
}

.modal-btn.cancel:hover {
  background: #eee;
}

.modal-btn.confirm {
  background: #f44336;
  color: white;
}

.modal-btn.confirm:hover {
  background: #d32f2f;
}

/* 设置面板样式 */
.settings-panel {
  position: absolute;
  top: 60px;
  right: 20px;
  width: 320px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  z-index: 1000;
  overflow: hidden;
}

.settings-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: #f8f9fa;
  border-bottom: 1px solid #eee;
}

.settings-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}

.close-btn {
  background: none;
  border: none;
  padding: 4px;
  cursor: pointer;
  color: #666;
  border-radius: 4px;
  transition: all 0.2s;
}

.close-btn:hover {
  background: rgba(0, 0, 0, 0.05);
  color: #333;
}

.settings-body {
  padding: 20px;
}

.settings-group {
  margin-bottom: 24px;
}

.settings-title {
  font-size: 14px;
  font-weight: 600;
  color: #666;
  margin-bottom: 12px;
}

.settings-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.settings-item span {
  font-size: 14px;
  color: #333;
}

/* 开关样式 */
.switch {
  position: relative;
  display: inline-block;
  width: 40px;
  height: 20px;
}

.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  transition: .4s;
  border-radius: 20px;
}

.slider:before {
  position: absolute;
  content: "";
  height: 16px;
  width: 16px;
  left: 2px;
  bottom: 2px;
  background-color: white;
  transition: .4s;
  border-radius: 50%;
}

input:checked + .slider {
  background-color: #2196F3;
}

input:checked + .slider:before {
  transform: translateX(20px);
}

/* 字体大小按钮组 */
.size-buttons {
  display: flex;
  gap: 8px;
}

.size-btn {
  padding: 4px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background: white;
  cursor: pointer;
  transition: all 0.2s;
}

.size-btn.active {
  background: #2196F3;
  color: white;
  border-color: #2196F3;
}

/* 清除历史按钮 */
.clear-history {
  width: 100%;
  padding: 8px;
  margin-top: 8px;
  border: 1px solid #ff4d4f;
  border-radius: 4px;
  background: white;
  color: #ff4d4f;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.2s;
}

.clear-history:hover {
  background: #fff1f0;
}

.settings-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 20px;
  background: #f8f9fa;
  border-top: 1px solid #eee;
}

.settings-btn {
  padding: 6px 16px;
  border-radius: 4px;
  border: none;
  cursor: pointer;
  transition: all 0.2s;
}

.settings-btn.save {
  background: #2196F3;
  color: white;
}

.settings-btn.save:hover {
  background: #1976D2;
}

.settings-btn.cancel {
  background: #f5f5f5;
  color: #666;
}

.settings-btn.cancel:hover {
  background: #e0e0e0;
}

/* 语言选择下拉框样式 */
.language-select select {
  padding: 6px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background: white;
  cursor: pointer;
  font-size: 14px;
  color: #333;
  width: 100px;
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 24 24'%3E%3Cpath fill='%23666' d='M7 10l5 5 5-5z'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 8px center;
  padding-right: 28px;
}

.language-select select:focus {
  outline: none;
  border-color: #2196F3;
}

/* 深色模式样式 */
:root {
  --font-size-base: 16px;
}

.dark-mode {
  background: #1a1a1a;
  color: #fff;
}

.dark-mode .settings-panel {
  background: #2d2d2d;
}

.dark-mode .settings-header,
.dark-mode .settings-footer {
  background: #252525;
  border-color: #333;
}

.dark-mode .settings-title {
  color: #aaa;
}

.dark-mode .settings-item span {
  color: #fff;
}

.dark-mode .size-btn {
  background: #333;
  border-color: #444;
  color: #fff;
}

.dark-mode .size-btn.active {
  background: #2196F3;
  border-color: #2196F3;
}

.dark-mode .clear-history {
  background: #333;
  border-color: #ff4d4f;
}

.dark-mode .clear-history:hover {
  background: #3a2829;
}

.dark-mode .settings-btn.cancel {
  background: #333;
  color: #fff;
}

.dark-mode .settings-btn.cancel:hover {
  background: #404040;
}

.dark-mode .close-btn {
  color: #aaa;
}

.dark-mode .close-btn:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
}
</style>
