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
      <div id="course-list">
        <div class="course-item" @click="selectCourse('c')">C语言</div>
        <div class="course-item" @click="selectCourse('python')">Python</div>
        <div class="course-item" @click="selectCourse('network')">网络安全</div>
        <div class="course-item" @click="selectCourse('computer_network')">计算机网络</div>
        <div class="course-item" @click="selectCourse('computer_organization')">计算机组成原理</div>
        <div class="course-item" @click="selectCourse('operating_system')">操作系统</div>
        <div class="course-item" @click="selectCourse('java')">Java</div>
        <div class="course-item" @click="selectCourse('database')">数据库</div>
      </div>
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
function selectCourse(course) {
  if (course === 'c') {
    router.push("/learning"); // Redirect to /learning when C语言 is clicked
  } else {
    console.log(`Selected course: ${course}`);
  }
}
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
  width: 300px;
  height: 650px;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: rgba(248, 249, 250, 0.9);
  border-radius: 15px;
  padding: 20px;
  box-shadow: 0 4px 20px rgb(237,201,237);
}

#logo {
  width: 300px;
  height: auto; /* 设置logo的高度自适应 */
  max-height: 600px; /* 设置最大高度为容器的高度 */
}

/* 中间聊天窗口 */
#chat-container {
  width: 600px;
  height: 700px;
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-left: 5px;
  margin-right: 10px;
}

#chat-box {
  width: 100%;
  height: 100%;
  overflow-y: auto;
  border-radius: 15px;
  padding: 20px;
  background:rgba(248, 249, 250, 0.9);
  box-shadow: inset 0 0 10px rgb(237,201,237);
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
  max-width: 500px;
  padding: 12px;
  border-radius: 12px;
  margin-bottom: 12px;
  word-wrap: break-word;
  background-color: #f0f0f0;
  box-shadow: 0 2px 5px rgb(249, 198, 198);
}

.user-message {
  background-color: rgb(245, 249, 194);
  align-self: flex-end;
}

.ai-message {
  background-color: rgb(248, 227, 248);
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
  color: rgb(240, 189, 189);
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
  width: 500px;
  padding: 12px;
  border-radius: 25px;
  border: 1px solid rgb(237,201,237);
  font-size: 16px;
}

#send-btn {
  width: 15%;
  padding: 12px;
  margin-left: 10px;
  border-radius: 25px;
  background-color: rgb(237,201,237);
  color: white;
  border:yellow;
}

#send-btn:hover {
  background-color: rgb(229, 188, 229);
}
#dropdown-container{
  margin-left: 10px;
  width: 100px;
  height: 650px;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: rgba(248, 249, 250, 0.9);
  padding: 5px;
  border-radius: 15px;
  padding: 20px;
  box-shadow: 0 4px 20px rgb(237,201,237);
  color:rgb(129, 129, 231);
}

.course-item {
  margin-bottom: 10px;
  padding: 5px;
  border-bottom: 1px solid rgb(247, 237, 142);
}
.course-item:last-child {
  border-bottom: none; /* Remove the bottom border from the last item */
}
.course-item.active {
  background-color: rgb(248, 190, 190); /* 点击后的背景颜色 */
  color: white; /* 点击后的文字颜色 */
}
.course-list{
  margin-top:10px;
}
</style>
