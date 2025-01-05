<template>
  <div id="main-container">
    <!-- 动态背景装饰 -->
    <div class="dynamic-bg">
      <div class="gradient-blob blob-1"></div>
      <div class="gradient-blob blob-2"></div>
      <div class="gradient-blob blob-3"></div>
      <div class="grid-overlay"></div>
    </div>
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
import { useRouter } from "vue-router";
import { marked } from "marked";
import { ref } from 'vue';

const messages = ref([]);
const userInput = ref('');
const isLoading = ref(false);
const isConnected = ref(true);
const userAvatar = require("@/assets/客户头像.png");
const botAvatar = require("@/assets/AI老师.png");
const showClearConfirm = ref(false);

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

</script>

<style scoped>
/* 基础容器样式优化 */
#main-container {
  display: flex;
  justify-content: center;
  width: 100%;
  max-width: 1280px;
  height: calc(100vh - 120px);
  margin: 20px auto;
  padding: 0 20px;
  gap: 20px;
  position: relative;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

/* 动态背景样式 */
.dynamic-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: -1;
}

.gradient-blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(40px);
  opacity: 0.5;
  animation: float 20s infinite;
}

.blob-1 {
  width: 250px;
  height: 250px;
  top: -50px;
  right: -50px;
  background: linear-gradient(135deg, #a5b4fc33, #818cf833);
  animation-delay: -2s;
}

.blob-2 {
  width: 200px;
  height: 200px;
  bottom: -50px;
  left: 20%;
  background: linear-gradient(135deg, #93c5fd33, #60a5fa33);
  animation-delay: -5s;
}

.blob-3 {
  width: 300px;
  height: 300px;
  top: 40%;
  left: -100px;
  background: linear-gradient(135deg, #c7d2fe33, #818cf833);
  animation-delay: -7s;
}

.grid-overlay {
  position: absolute;
  width: 200%;
  height: 200%;
  top: -50%;
  left: -50%;
  background-image: 
    linear-gradient(rgba(145, 167, 255, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(145, 167, 255, 0.03) 1px, transparent 1px);
  background-size: 20px 20px;
  transform: rotate(-45deg);
  animation: gridMove 60s linear infinite;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) rotate(0deg); }
  25% { transform: translate(10px, -10px) rotate(2deg); }
  50% { transform: translate(-5px, 15px) rotate(-1deg); }
  75% { transform: translate(-15px, -5px) rotate(1deg); }
}

@keyframes gridMove {
  0% { transform: rotate(-45deg) translateY(0); }
  100% { transform: rotate(-45deg) translateY(-100px); }
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
  padding: 20px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  border-radius: 16px 16px 0 0;
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
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.header-avatar:hover {
  transform: scale(1.05);
}

.header-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.header-title {
  font-size: 1.2rem;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 4px;
}

.header-subtitle {
  font-size: 0.9rem;
  color: #64748b;
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
  margin-left: 8px;
  transition: all 0.3s ease;
}

.status-indicator.connected {
  background: linear-gradient(135deg, #34d399 0%, #10b981 100%);
  box-shadow: 0 0 12px rgba(16, 185, 129, 0.4);
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
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(4px);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  background: rgba(255, 255, 255, 0.95);
}

.action-btn i {
  color: #64748b;
  font-size: 14px;
  transition: color 0.3s ease;
}

.action-btn:hover i {
  color: #3b82f6;
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
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  max-width: 80%;
  line-height: 1.5;
  transition: transform 0.3s ease;
}

.message-bubble:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
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
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border-radius: 0 0 16px 16px;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
  padding: 16px;
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
  padding: 12px;
  border: none;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.8);
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
  background-color: #f9f9f9;
  border-radius: 10px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  padding: 20px;
  max-width: 400px;
  margin: auto;
}

.settings-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.settings-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 10px;
}

.settings-group {
  margin-bottom: 20px; /* 增加组之间的间距 */
}

.settings-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 15px;
  padding: 10px;
  border-radius: 8px;
  background-color: #ffffff;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  transition: background-color 0.3s, transform 0.3s;
}

.settings-item:hover {
  background-color: #f1f1f1;
  transform: translateY(-2px);
}

.icon {
  margin-right: 10px;
  color: #2196F3;
}

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

.size-buttons {
  display: flex;
  gap: 10px;
}

.size-btn {
  padding: 5px 10px;
  cursor: pointer;
  border: none;
  border-radius: 5px;
  background-color: #e0e0e0;
  transition: background-color 0.3s;
}

.size-btn.active {
  background-color: #2196F3;
  color: white;
}

.size-btn:hover {
  background-color: #bdbdbd;
}

.reset-btn {
  margin-top: 10px;
  background-color: #f44336; /* 红色背景 */
  color: white;
  border: none;
  padding: 5px 10px;
  cursor: pointer;
  border-radius: 5px;
  transition: background-color 0.3s;
}

.reset-btn:hover {
  background-color: #d32f2f;
}

.settings-footer {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

.settings-btn {
  padding: 10px 15px;
  border: none;
  border-radius: 5px;
  background-color: #2196F3;
  color: white;
  cursor: pointer;
  transition: background-color 0.3s;
}

.settings-btn:hover {
  background-color: #1976d2;
}

.cancel {
  background-color: #e0e0e0;
  color: black;
}

.cancel:hover {
  background-color: #bdbdbd;
}
</style>

