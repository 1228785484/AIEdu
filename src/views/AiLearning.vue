<template>
  <div id="main-container">
    <!-- 左侧Logo容器 -->
    <div id="logo-container">
      <img src="@/assets/AI老师.png" alt="Logo" id="logo" />
    </div>

    <!-- 中间聊天框 -->
    <div id="chat-container">
      <div id="chat-box" ref="chatBox">
        <div
          v-for="(message, index) in messages"
          :key="index"
          class="message"
          :class="message.isUser ? 'user-message' : 'ai-message'"
        >
          <img
            :src="message.isUser ? userAvatar : botAvatar"
            :alt="message.isUser ? '用户' : 'AI助手'"
            class="avatar"
          />
          <div class="text">
            <div v-if="!message.isUser">
              <div v-if="message.type === 'plan'" class="plan-message">
                <div class="plan-title">学习计划</div>
                <ul class="plan-details">
                  <li v-for="(week, idx) in message.data" :key="idx">{{ week }}</li>
                </ul>
              </div>
              <div v-else-if="message.type === 'error'" class="error-message">
                <div class="error-title">错误</div>
                <p>{{ message.text }}</p>
              </div>
              <div v-else class="general-message">
                <span>{{ message.text }}</span>
              </div>
            </div>
            <span v-else v-html="message.text"></span>
          </div>
        </div>
        <div v-if="isLoading" class="loading-indicator">
          <span>AI正在输入...</span>
        </div>
      </div>
      <div id="user-input-area">
        <input
          type="text"
          v-model="userInput"
          @keydown.enter="sendMessage"
          placeholder="输入你的消息..."
          autofocus
          id="user-input"
        />
        <button @click="sendMessage" id="send-btn">发送</button>
      </div>
    </div>

    <!-- 右侧下拉框容器 -->
    <div id="dropdown-container">
      <select v-model="selectedCourse" @change="handleCourseChange" id="course-select">
        <option value="" disabled selected>选择课程</option>
        <option value="c-language">C语言</option>
        <option value="python">Python</option>
        <option value="java">Java</option>
      </select>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";

// 消息数据和状态
const messages = ref([
  { text: "AI助手为您服务", isUser: false },
]);

const userInput = ref("");
const isLoading = ref(false);
const userAvatar = require("@/assets/客户头像.png");
const botAvatar = require("@/assets/AI老师.png");

const router = useRouter(); // 使用 Vue Router

// 控制选中的课程
const selectedCourse = ref('');

const sendMessage = async () => {
  if (userInput.value.trim()) {
    messages.value.push({ text: userInput.value, isUser: true });
    const inputText = userInput.value;
    userInput.value = "";

    scrollToBottom();
    isLoading.value = true;

    // 从 localStorage 获取 token
    const token = localStorage.getItem('token');
    
    try {
      const response = await fetch("http://localhost:8008/api/test/askAi", {
        method: "POST",
        headers: { 
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}` // 添加 token 到请求头
        },
        credentials: 'include',
        body: JSON.stringify({
          query: inputText
        }),
      });

      if (!response.ok) {
        if (response.status === 401) {
          // token 过期或无效，重定向到登录页面
          router.push('/login');
          throw new Error("请先登录");
        }
        throw new Error("从服务器获取响应失败");
      }

      const data = await response.json();
      
      // 假设返回的数据是以下格式
      const responseMessage = {
        text: data.answer, 
        isUser: false
      };
      if (data.type === 'plan') {
        responseMessage.type = 'plan';
        responseMessage.data = data.data; // 假设返回的是一个学习计划数组
      } else if (data.error) {
        responseMessage.type = 'error';
        responseMessage.text = data.error;
      }

      messages.value.push(responseMessage);
      scrollToBottom();
    } catch (error) {
      messages.value.push({
        type: 'error',
        text: `错误：${error.message}`,
        isUser: false,
      });
      scrollToBottom();
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

// 处理下拉框变化，跳转到不同的学习页面
const handleCourseChange = () => {
  if (selectedCourse.value === 'c-language') {
    // 如果选择了 C语言，跳转到对应的学习页面
    router.push("/learning");
  }
};
</script>

<style scoped>
/* 容器的整体布局 */
#main-container {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 20px;
  width: 100%;
  height: 100%; /* 确保容器高度填满整个页面 */
  position: relative;
}

/* 左侧Logo部分 */
#logo-container {
  width: 450px;
  height: 650px;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: rgba(240, 240, 240, 0.8); /* 半透明背景以确保不遮挡背景图 */
  border-radius: 15px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  margin-left: -100px; /* 向左移动 */
}

#logo {
  width: 90%;
  height: auto;
  max-height: 400%;
}

/* 中间聊天窗口 */
#chat-container {
  width: 928px;
  height: 750px;
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-left: 20px;
  margin-right: 50px;
}

#chat-box {
  width: 100%;
  height: 100%;
  overflow-y: auto;
  border: 2px solid #ccc;
  border-radius: 15px;
  padding: 20px;
  background: #f7f7f7;
  box-shadow: inset 0 0 10px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  gap: 10px;
}

#chat-box::-webkit-scrollbar {
  display: none;
}

.message {
  display: flex;
  align-items: center;
  max-width: 75%;
  padding: 12px;
  border-radius: 12px;
  margin-bottom: 12px;
  word-wrap: break-word;
  background-color: #f0f0f0;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.user-message {
  background-color: #d1e7ff;
  align-self: flex-end;
}

.ai-message {
  background-color: #e2ffe2;
  align-self: flex-start;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 10px;
}

.text {
  background-color: transparent;
  padding: 10px;
  border-radius: 5px;
  max-width: 70%;
}

/* 学习计划 */
.plan-message {
  padding: 10px;
  background-color: #f4f8fc;
  border-radius: 10px;
  margin-top: 10px;
}

.plan-title {
  font-weight: bold;
  margin-bottom: 8px;
  font-size: 18px;
}

.plan-details {
  list-style-type: disc;
  padding-left: 20px;
}

/* 错误消息 */
.error-message {
  padding: 10px;
  background-color: #fff4f4;
  border-radius: 10px;
  color: red;
  margin-top: 10px;
}

.error-title {
  font-weight: bold;
}

/* 输入框和按钮 */
#user-input-area {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
}

#user-input {
  width: 80%;
  padding: 12px;
  border-radius: 25px;
  border: 1px solid #ccc;
  font-size: 16px;
}

#send-btn {
  width: 15%;
  padding: 12px;
  margin-left: 10px;
  border-radius: 25px;
  background-color: #007bff;
  color: white;
}

#send-btn:hover {
  background-color: #0056b3;
}

/* 右侧下拉框容器样式 */
#dropdown-container {
  width: 300px;
  height: 650px;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: rgba(248, 249, 250, 0.9); /* 半透明背景 */
  border-radius: 15px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

#course-select {
  padding: 12px;
  border-radius: 10px;
  border: 1px solid #ccc;
  font-size: 16px;
  background-color: #ffffff;
  transition: all 0.3s ease;
}

#course-select:focus {
  border-color: #007bff;
  box-shadow: 0 0 8px rgba(0, 123, 255, 0.5);
}

/* 加载指示器 */
.loading-indicator {
  text-align: center;
  color: #007bff;
  font-style: italic;
  margin-top: 10px;
}
</style>
