<template>
  <div id="main-container">
    <!-- 左侧Logo容器 -->
    <div id="logo-container">
      <img src="@/assets/AI老师.png" alt="Logo" id="logo" />
    </div>

    <!-- 右侧聊天容器 -->
    <div id="chat-container">
      <!-- 用于显示消息的聊天框 -->
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
          <div class="text" v-html="message.text"></div>
        </div>

        <!-- 加载指示器，当AI正在处理消息时显示 -->
        <div v-if="isLoading" class="loading-indicator">
          <span>AI正在输入...</span>
        </div>
      </div>

      <!-- 用户输入区域 -->
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
  </div>
</template>

<script setup>
import { ref } from "vue";

// 消息数据和状态
const messages = ref([
  { text: "你好，我是你的C语言老师，下面由我带领你学习C语言", isUser: false },
  { text: "您好", isUser: true },
  { text: "正在生成学习计划......", isUser: false },
  {
    text:
      "第一周：C语言基础<br>第二周：C语言进阶<br>第三周：C语言项目<br>第四周：C语言复习；考试",
    isUser: false,
  },
  { text: "是否满意此学习计划", isUser: false },
  { text: "是", isUser: true },
  { text: "进入学习？", isUser: false },
]);

const userInput = ref("");
const isLoading = ref(false);
const userAvatar = require("@/assets/客户头像.png");
const botAvatar = require("@/assets/AI老师.png");

const sendMessage = async () => {
  if (userInput.value.trim()) {
    messages.value.push({ text: userInput.value, isUser: true });
    const inputText = userInput.value;
    userInput.value = "";

    scrollToBottom();
    isLoading.value = true;

    try {
      const response = await fetch("http://127.0.0.1:8000/send_message", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          query: inputText,
          user_id: "abc-123",
        }),
      });

      if (!response.ok) {
        throw new Error('从服务器获取响应失败');
      }

      const data = await response.json();
      messages.value.push({ text: data.result, isUser: false });
      scrollToBottom();
    } catch (error) {
      messages.value.push({
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
</script>

<style scoped>
/* 容器的整体布局 */
#main-container {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 20px;
}

/* 左侧Logo部分 */
#logo-container {
  width: 450px;
  height: 650px;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f0f0f0;
  border-radius: 15px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

#logo {
  width: 100%;
  height: auto;  /* 设置logo的高度自适应 */
  max-height: 100%;  /* 设置最大高度为容器的高度 */
}

/* 右侧聊天窗口 */
#chat-container {
  width: 928px;
  height: 755px;
  margin-left: 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;
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
  transition: all 0.3s ease;
}

#user-input:focus {
  border-color: #007bff;
  box-shadow: 0 0 8px rgba(0, 123, 255, 0.5);
}

#send-btn {
  width: 15%;
  padding: 12px;
  margin-left: 10px;
  border-radius: 25px;
  background-color: #007bff;
  color: white;
  font-size: 16px;
  border: none;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

#send-btn:hover {
  background-color: #0056b3;
}

/* 加载指示器的样式 */
.loading-indicator {
  text-align: center;
  color: #007bff;
  font-style: italic;
  margin-top: 10px;
}
</style>
