<template>
  <div>
    <h1>Chat with AI</h1>

    <!-- 显示聊天记录 -->
    <div id="chat-box"></div>

    <!-- 用户输入框 -->
    <input
      type="text"
      v-model="userInput"
      placeholder="Type your message..."
      @keyup.enter="sendMessage"
    />
    <button @click="sendMessage">Send</button>

    <!-- 假设这里添加一个隐藏的输入框用于存储动态获取的用户ID，可根据实际情况修改获取方式 -->
    <input type="hidden" id="dynamic-user-id" v-model="dynamicUserId">
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      userInput: '', // 绑定用户输入
      dynamicUserId: '', // 用于存储动态获取的用户ID
    };
  },
  methods: {
    // 发送消息的函数
    sendMessage() {
      if (this.userInput.trim()) {
        // 获取聊天框元素
        const chatBox = document.getElementById('chat-box');

        // 显示用户消息
        chatBox.innerHTML += `<p><b>You:</b> ${this.userInput}</p>`;

        // 清空输入框
        this.userInput = '';

        // 获取动态用户ID，这里只是示例，实际可能需要从登录状态等获取
        const userId = this.dynamicUserId || 'abc-123';

        // 使用axios发送请求到 Flask 后端
        axios.post('http://127.0.0.1:5000/api/chat', {
          query: this.userInput,
          user_id: userId,
          conversation_id: '', // 可选，可根据后续业务逻辑完善此参数的使用
        })
      .then((response) => {
            // 如果 AI 返回的结果有效，显示 AI 回复
            if (response.data.response) {
              chatBox.innerHTML += `<p><b>AI:</b> ${response.data.response}</p>`;
            } else {
              chatBox.innerHTML += `<p><b>AI:</b> Sorry, no response received.</p>`;
            }

            // 滚动到底部
            chatBox.scrollTop = chatBox.scrollHeight;
          })
      .catch((error) => {
            // 处理请求错误
            chatBox.innerHTML += `<p><b>Error:</b> ${error.message}</p>`;
            chatBox.scrollTop = chatBox.scrollHeight;
          });
      }
    },
  },
  mounted() {
    // 这里可以添加在页面加载时获取动态用户ID的逻辑，比如从本地存储、Cookie或者通过向其他服务请求获取等
    // 以下是一个简单示例，假设从本地存储获取用户ID
    const storedUserId = localStorage.getItem('user_id');
    if (storedUserId) {
      this.dynamicUserId = storedUserId;
    }
  },
};
</script>

<style scoped>
/* CSS 样式 */
body {
  font-family: Arial, sans-serif;
  padding: 20px;
}

#chat-box {
  width: 100%;
  height: 300px;
  overflow-y: scroll;
  border: 1px solid #ccc;
  padding: 10px;
}

input[type="text"] {
  width: 80%;
  padding: 10px;
  margin-right: 10px;
}

button {
  padding: 10px;
  cursor: pointer;
}
</style>