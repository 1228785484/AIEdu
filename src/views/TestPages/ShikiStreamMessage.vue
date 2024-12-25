<template>
  <el-container>
    <el-main>
      <div class="chat-container">
        <div class="messages-container" ref="messagesContainer">
          <div v-for="message in messages" :key="message.id" class="message">
            <div class="message-content" 
                 :class="{ 'user-message': message.isUser }" 
                 v-html="message.text">
            </div>
          </div>
        </div>
        <div class="input-container">
          <el-input
            v-model="userInput"
            type="textarea"
            :rows="3"
            placeholder="输入消息..."
            resize="none"
            @keydown="handleKeydown"
          />
          <el-button type="primary" @click="handleSend" :loading="loading">发送</el-button>
        </div>
      </div>
    </el-main>
  </el-container>
</template>

<script setup>
import { ref, nextTick, onUnmounted } from 'vue'
import DOMPurify from 'dompurify'
import { EventSourcePolyfill } from 'event-source-polyfill'
import { ElMessage } from 'element-plus'
import { markdownRenderer } from '@/utils/markdownRenderer'

const messages = ref([])
const userInput = ref('')
const loading = ref(false)
const messagesContainer = ref(null)
const eventSource = ref(null)
const inUserCodeBlock = ref(false)
const currentMessageId = ref(null)
const messageStates = ref(new Set()) // 用于跟踪已处理的消息

const keywords = ['int', 'void', 'return', 'public', 'private', 'protected', 'class', 'interface', 'static', 'final'];

const processKeywords = (text) => {
  let processedText = text;
  keywords.forEach(keyword => {
    // 使用正则表达式确保匹配的是完整的单词，且后面没有空格
    const regex = new RegExp(`\\b${keyword}\\b(?![\\s])`, 'g');
    processedText = processedText.replace(regex, `${keyword} `);
  });
  return processedText;
};

const renderMarkdown = async (text) => {
  try {
    console.log('Raw text to render:', {
      text,
      length: text.length,
      firstLine: text.split('\n')[0],
      charCodes: text.split('\n')[0].split('').map(char => char.charCodeAt(0))
    })

    let inCodeBlock = false
    
    const lines = text.split('\n')
    const processedLines = []
    
    for (let i = 0; i < lines.length; i++) {
      let line = lines[i]
      
      if (line.trim().startsWith('#')) {
        line = line.trimStart()
        const matches = line.match(/^(#+)(.*)/)
        if (matches) {
          const [, hashes, content] = matches
          line = `${hashes} ${content.trim()}`
        }
      }
      
      // 处理代码块外的破折号
      if (!inCodeBlock && line.trim().startsWith('-')) {
        // 确保破折号后有空格，并且保持原有缩进
        const indent = line.match(/^\s*/)[0]
        const content = line.trim().substring(1).trim()
        line = `${indent}- ${content}`
      }
      
      if (line.startsWith('```')) {
        inCodeBlock = !inCodeBlock
        processedLines.push(line)
      } else {
        processedLines.push(line + (inCodeBlock ? '' : '  '))
      }
    }
    
    if (inCodeBlock) {
      processedLines.push('```')
    }
    
    const processedText = processedLines.join('\n')
    
    if (window.__markdownRenderDebounce) {
      clearTimeout(window.__markdownRenderDebounce)
    }

    return new Promise((resolve) => {
      window.__markdownRenderDebounce = setTimeout(async () => {
        const rendered = await markdownRenderer.render(processedText)
        const sanitized = DOMPurify.sanitize(rendered)
        resolve(sanitized)
      }, 30) // 减少到30ms
    })
  } catch (error) {
    console.error('Error rendering markdown:', error)
    return text
  }
}

const processStreamMessage = (() => {
  let accumulatedText = ''
  let debounceTimer = null
  let lastRenderTime = Date.now()
  const RENDER_INTERVAL = 50 // 每50ms更新一次
  let previousMessageIndex = -1
  
  return async (event, messageIndex) => {
    // 只在消息索引变化时重置累积文本
    if (messageIndex !== previousMessageIndex) {
      accumulatedText = ''
      previousMessageIndex = messageIndex
    }

    if (event.data === '[DONE]') {
      if (debounceTimer) {
        clearTimeout(debounceTimer)
        debounceTimer = null
      }
      // 消息完成时的最后一次渲染
      const rendered = await renderMarkdown(accumulatedText)
      messages.value[messageIndex] = {
        ...messages.value[messageIndex],
        text: rendered
      }
      // 清空累积的文本，并创建新的消息占位
      accumulatedText = ''
      messages.value.push({
        id: generateId(),
        text: '',
        isUser: false
      })
      await nextTick()
      scrollToBottom()
      return true
    }

    // 处理关键字空格
    accumulatedText += processKeywords(event.data)
    const now = Date.now()
    
    // 如果距离上次渲染时间超过了间隔，立即渲染
    if (now - lastRenderTime >= RENDER_INTERVAL) {
      lastRenderTime = now
      const rendered = await renderMarkdown(accumulatedText)
      messages.value[messageIndex] = {
        ...messages.value[messageIndex],
        text: rendered
      }
      await nextTick()
      scrollToBottom()
    } else {
      // 否则使用短时间的防抖
      if (debounceTimer) {
        clearTimeout(debounceTimer)
      }
      
      debounceTimer = setTimeout(async () => {
        lastRenderTime = Date.now()
        const rendered = await renderMarkdown(accumulatedText)
        messages.value[messageIndex] = {
          ...messages.value[messageIndex],
          text: rendered
        }
        await nextTick()
        scrollToBottom()
      }, RENDER_INTERVAL - (now - lastRenderTime))
    }
    
    return false
  }
})()

const handleEventSource = (eventSource) => {
  eventSource.onmessage = async (event) => {
    const msgTimestamp = new Date().toISOString()
    try {
      const isDone = await processStreamMessage(event, messages.value.length - 1)
      if (isDone) {
        console.log(`[${msgTimestamp}] Stream completed for message:`, { messageId: currentMessageId.value })
        closeEventSource()
      }
    } catch (error) {
      console.error(`[${msgTimestamp}] Error processing stream message:`, error)
      closeEventSource()
    }
  };

  eventSource.onerror = (error) => {
    console.error('EventSource error:', error);
    loading.value = false;
    eventSource.close();
  };
};

const handleKeydown = (event) => {
  if (event.key === 'Enter' && !event.shiftKey) {
    event.preventDefault()
    if (!loading.value) {
      handleSend()
    }
  } else if (event.key === '`' && event.ctrlKey) {
    event.preventDefault()
    inUserCodeBlock.value = !inUserCodeBlock.value
    const codeBlockMarker = '```'
    if (inUserCodeBlock.value) {
      userInput.value += codeBlockMarker + '\n'
    } else {
      userInput.value += '\n' + codeBlockMarker + '\n'
    }
  }
}

const closeEventSource = () => {
  const timestamp = new Date().toISOString()
  if (eventSource.value) {
    console.log(`[${timestamp}] Closing EventSource for message:`, { 
      messageId: currentMessageId.value,
      readyState: eventSource.value.readyState
    })
    eventSource.value.close()
    eventSource.value = null
    loading.value = false
    
    if (currentMessageId.value) {
      messageStates.value.add(currentMessageId.value)
      console.log(`[${timestamp}] Marked message as completed:`, { messageId: currentMessageId.value })
    }
  }
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

const generateId = () => `msg-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`

const startStream = async () => {
  const timestamp = new Date().toISOString()
  
  if (loading.value) {
    console.log(`[${timestamp}] Already loading, ignoring request`)
    return
  }
  
  try {
    loading.value = true
    
    const userMessageIndex = messages.value.length - 2
    const userMessage = messages.value[userMessageIndex]
    if (!userMessage || !userMessage.text) {
      console.error(`[${timestamp}] No user message found`)
      loading.value = false
      return
    }

    if (messageStates.value.has(userMessage.id)) {
      console.log(`[${timestamp}] Message already processed, ignoring request for:`, { messageId: userMessage.id })
      loading.value = false
      return
    }
    
    const token = localStorage.getItem('token')
    if (!token) {
      console.error(`[${timestamp}] No authentication token found`)
      loading.value = false
      ElMessage.error('未找到认证信息，请重新登录')
      return
    }

    if (eventSource.value) {
      console.log(`[${timestamp}] Closing previous EventSource for message:`, { previousMessageId: currentMessageId.value })
    }
    closeEventSource()

    currentMessageId.value = userMessage.id
    console.log(`[${timestamp}] Starting to process message:`, { messageId: currentMessageId.value })

    const url = new URL('http://localhost:8008/api/agent/stream')
    url.searchParams.append('query', userMessage.text)
    url.searchParams.append('user', 'abc-123')
    
    console.log(`[${timestamp}] Creating new EventSource for message:`, { messageId: currentMessageId.value })
    
    eventSource.value = new EventSourcePolyfill(url.toString(), {
      headers: {
        'Authorization': `Bearer ${token}`
      },
      withCredentials: true,
      heartbeatTimeout: 60000,
      connectionTimeout: 30000
    })

    handleEventSource(eventSource.value)

    eventSource.value.onopen = () => {
      console.log(`[${timestamp}] EventSource connection opened for message:`, { 
        messageId: currentMessageId.value 
      })
    }
  } catch (error) {
    const errorTimestamp = new Date().toISOString()
    console.error(`[${errorTimestamp}] Chat error:`, { 
      messageId: currentMessageId.value,
      error
    })
    closeEventSource()
  }
}

const handleSend = async () => {
  if (!userInput.value.trim() || loading.value) {
    console.log('Invalid input or already loading')
    return
  }

  let finalInput = userInput.value.trim()
  if (inUserCodeBlock.value) {
    finalInput += '\n```'
    inUserCodeBlock.value = false
  }
  
  userInput.value = ''
  
  const messageId = generateId()
  console.log(`[${new Date().toISOString()}] User sending message:`, { 
    messageId, 
    text: finalInput 
  })
  
  messages.value.push({
    id: messageId,
    text: finalInput,
    isUser: true
  })
  
  const aiMessageId = generateId()
  console.log(`[${new Date().toISOString()}] Creating AI message placeholder:`, { 
    messageId: aiMessageId 
  })
  
  messages.value.push({
    id: aiMessageId,
    text: '',
    isUser: false
  })
  
  await nextTick()
  scrollToBottom()
  
  console.log(`[${new Date().toISOString()}] Starting stream for message:`, { 
    messageId 
  })
  await startStream()
}

onUnmounted(() => {
  closeEventSource()
  messageStates.value.clear()
})
</script>

<style scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 60px); /* 减去header高度 */
  background-color: #f5f7fa;
}

.messages-container {
  flex-grow: 1;
  overflow-y: auto;
  padding: 20px;
  background-color: #f5f7fa;
}

.message {
  margin-bottom: 12px;
  display: flex;
  flex-direction: column;
}

.message-content {
  padding: 12px 16px;
  border-radius: 8px;
  max-width: 80%;
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.user-message {
  align-self: flex-end;
  background-color: #409eff;
  color: white;
}

.input-container {
  height: 120px;
  min-height: 120px;
  padding: 20px;
  background-color: #fff;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
  display: flex;
  gap: 10px;
}

:deep(.el-input__wrapper) {
  flex: 1;
}

/* 代码块样式 */
:deep(pre) {
  margin: 16px 0;
  padding: 16px;
  overflow: auto;
  font-size: 85%;
  line-height: 1.45;
  background-color: #0d1117;
  border-radius: 6px;
}

:deep(code) {
  font-family: ui-monospace, SFMono-Regular, SF Mono, Menlo, Consolas, Liberation Mono, monospace;
  font-size: 85%;
  padding: 0.2em 0.4em;
  border-radius: 3px;
  background-color: rgba(0, 0, 0, 0.05);
}
</style>