<template>
    <div>
      <h1>Chat with AI</h1>
  
      <div id="chat-box"></div>
  
      <input type="text" v-model="userInput" placeholder="Type your message..." />
      <button @click="sendMessage">Send</button>
    </div>
  </template>
  
  <script>
  export default {
    data() {
      return {
        userInput: '', // 绑定用户输入
      };
    },
    methods: {
      sendMessage() {
        if (this.userInput.trim()) {
          // 获取聊天框元素
          const chatBox = document.getElementById('chat-box');
          
          // 显示用户消息
          chatBox.innerHTML += `<p><b>You:</b> ${this.userInput}</p>`;
          
          // 清空输入框
          this.userInput = '';
          
          // 发送请求到 Flask 后端
          fetch('http://127.0.0.1:5000/send_message', {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
            },
            body: JSON.stringify({
              query: this.userInput,
              user_id: 'abc-123', // 替换为动态用户ID（如有需要）
              conversation_id: '', // 可选
            }),
          })
          .then((response) => response.json())
          .then((data) => {
            // 显示 AI 回复
            chatBox.innerHTML += `<p><b>AI:</b> ${data.result}</p>`;
            // 滚动到底部
            chatBox.scrollTop = chatBox.scrollHeight;
          })
          .catch((error) => {
            chatBox.innerHTML += `<p><b>Error:</b> ${error.message}</p>`;
          });
        }
      },
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
  #user-input {
    width: 80%;
    padding: 10px;
  }
  #send-btn {
    padding: 10px;
  }
  </style>
  