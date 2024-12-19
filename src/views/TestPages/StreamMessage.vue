<template>
  <div class="stream-message-container">
    <div class="chat-window">
      <div class="messages" ref="messagesContainer">
        <div v-for="(message, index) in messages" :key="index" class="message" :class="{ 'user-message': message.type === 'user', 'ai-message': message.type === 'ai' }">
          <div class="message-content">
            <div class="message-avatar">
              <el-avatar :size="40" :src="message.type === 'user' ? userAvatar : aiAvatar"></el-avatar>
            </div>
            <div class="message-text" v-html="formatMessage(message.content)"></div>
          </div>
        </div>
        <div v-if="loading" class="message ai-message">
          <div class="message-content">
            <div class="message-avatar">
              <el-avatar :size="40" :src="aiAvatar"></el-avatar>
            </div>
            <div class="message-text">
              <span class="loading-dots">思考中</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="input-area">
      <el-input
        v-model="userInput"
        type="textarea"
        :rows="3"
        placeholder="请在这输入消息..."
        @keydown.enter="handleEnterKey"
      />
      <el-button type="primary" @click="sendMessage" :loading="loading">发送</el-button>
    </div>
  </div>
</template>

<script>
import { ref, nextTick } from 'vue'

export default {
  name: 'StreamMessage',
  setup() {
    const userInput = ref('')
    const messages = ref([])
    const loading = ref(false)
    const messagesContainer = ref(null)
    const userAvatar = 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
    const aiAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
    const currentAIMessage = ref('')
    let responseComplete = false
    let isStreaming = ref(true)

    const formatMessage = (message) => {
      return message.replace(/\n/g, '<br>')
    }

    const scrollToBottom = () => {
      nextTick(() => {
        if (messagesContainer.value) {
          messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
        }
      })
    }

    const handleEnterKey = (e) => {
      if (e.shiftKey) return
      e.preventDefault()
      sendMessage()
    }

    const addMessage = (type, content) => {
      messages.value.push({
        type,
        content: content || ''
      })
      scrollToBottom()
    }

    const handleSSEData = (data) => {
      try {
        console.log('收到SSE数据:', data);
        if (!data || !data.trim()) {
          console.log('收到空数据');
          return;
        }

        // 正常消息处理
        if (currentAIMessage.value === '') {
          console.log('收到第一条消息块，创建新的AI消息');
          addMessage('ai', '')
          currentAIMessage.value = data
        } else {
          console.log('追加到现有消息');
          currentAIMessage.value += data
        }
        
        // 更新最后一条消息的内容
        if (messages.value.length > 0) {
          console.log('更新最后一条消息内容');
          messages.value[messages.value.length - 1].content = currentAIMessage.value
          scrollToBottom()
        }
      } catch (error) {
        console.error('处理SSE数据出错:', error)
        addMessage('ai', `处理消息时出错: ${error.message}`)
      }
    }

    const processStreamResponse = async (response) => {
      const reader = response.body.getReader()
      const decoder = new TextDecoder()
      let buffer = ''
      console.log('开始处理流式响应...')

      try {
        while (isStreaming.value) {
          const { value, done } = await reader.read()
          
          if (done) {
            console.log('流处理完成')
            responseComplete = true
            isStreaming.value = false
            break
          }
          
          const chunk = decoder.decode(value, { stream: true })
          buffer += chunk
          
          // 使用正则表达式匹配 data: 开头的行
          const regex = /data: (.*?)(?:\r\n|\n|\r|$)/g
          let match
          
          while ((match = regex.exec(buffer)) !== null) {
            const data = match[1].trim()
            if (data) {
              handleSSEData(data)
            }
          }
          
          // 保留最后一个不完整的行
          const lastNewlineIndex = buffer.lastIndexOf('\n')
          if (lastNewlineIndex > -1) {
            buffer = buffer.slice(lastNewlineIndex + 1)
          }
        }
      } catch (error) {
        console.error('处理流出错:', error)
        isStreaming.value = false
        throw error
      }
    }

    const sendMessage = async () => {
      const message = userInput.value.trim()
      if (!message || loading.value) return

      try {
        loading.value = true
        responseComplete = false
        addMessage('user', message)
        currentAIMessage.value = ''
        isStreaming.value = true

        const response = await fetch('http://localhost:8008/test/stream', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'text/event-stream'
          },
          body: JSON.stringify({
            inputs: {},
            query: message,
            response_mode: 'streaming',
            user: 'abc-123'
          })
        })
        console.log('Response:', response)

        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`)
        }

        await processStreamResponse(response)
        userInput.value = ''
      } catch (error) {
        console.error('Error:', error)
        addMessage('ai', `发送消息时出错: ${error.message}`)
        responseComplete = true
      } finally {
        if (responseComplete) {
          loading.value = false
        }
      }
    }

    return {
      userInput,
      messages,
      loading,
      messagesContainer,
      userAvatar,
      aiAvatar,
      formatMessage,
      handleEnterKey,
      sendMessage
    }
  }
}
</script>

<style scoped>
.stream-message-container {
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: #f5f5f5;
}

.chat-window {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.messages {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.message {
  display: flex;
  margin-bottom: 10px;
}

.message-content {
  display: flex;
  gap: 10px;
  max-width: 70%;
}

.message-avatar {
  flex-shrink: 0;
}

.message-text {
  padding: 10px 15px;
  border-radius: 10px;
  background-color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  word-break: break-word;
}

.user-message {
  justify-content: flex-end;
}

.user-message .message-text {
  background-color: #95ec69;
}

.ai-message {
  justify-content: flex-start;
}

.loading-dots::after {
  content: '...';
  animation: dots 1.5s steps(4, end) infinite;
}

@keyframes dots {
  0%, 20% { content: ''; }
  40% { content: '.'; }
  60% { content: '..'; }
  80%, 100% { content: '...'; }
}

.input-area {
  padding: 20px;
  background-color: white;
  border-top: 1px solid #eee;
  display: flex;
  gap: 10px;
}

.input-area .el-textarea {
  flex: 1;
}

.input-area .el-button {
  align-self: flex-end;
}
</style>