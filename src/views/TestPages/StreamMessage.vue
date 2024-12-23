<template>
  <div class="chat-container">
    <div ref="messagesContainer" class="messages-container">
      <div v-for="message in messages" :key="message.id" class="message">
        <div class="message-content" 
             :class="{ 'user-message': message.isUser }" 
             v-html="sanitizeAndFormat(message.text)">
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
        @keydown.enter.prevent="handleSend"
      />
      <el-button type="primary" @click="handleSend" :loading="loading">发送</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { marked } from 'marked'
import DOMPurify from 'dompurify'
import hljs from 'highlight.js'
import 'highlight.js/styles/vs2015.css'

// 配置 marked
marked.setOptions({
  highlight: function(code, lang) {
    if (lang && hljs.getLanguage(lang)) {
      try {
        return hljs.highlight(code, { language: lang }).value
      } catch (err) {
        console.error('Highlight error:', err)
      }
    }
    return hljs.highlightAuto(code).value
  },
  langPrefix: 'hljs language-',
  breaks: true,
  gfm: true
})

const userInput = ref('')
const messages = ref([])
const loading = ref(false)
const messagesContainer = ref(null)

const handleSend = async () => {
  const input = userInput.value.trim()
  if (!input || loading.value) return

  messages.value.push({
    id: generateId(),
    text: input,
    isUser: true
  })

  userInput.value = ''
  scrollToBottom()

  let aiMessageId = generateId()
  messages.value.push({
    id: aiMessageId,
    text: '',
    isUser: false
  })

  try {
    loading.value = true
    
    // 创建 EventSource 实例，添加 withCredentials
    const url = new URL('http://localhost:8008/api/agent/stream')
    url.searchParams.append('query', input)
    url.searchParams.append('user', 'abc-123')

    const eventSource = new EventSource(url.toString(), {
      withCredentials: true  // 添加这个选项
    })

    // 监听消息事件
    eventSource.onmessage = (event) => {
      const messageToUpdate = messages.value.find(m => m.id === aiMessageId)
      if (messageToUpdate) {
        // 在接收数据时就处理关键字
        let processedData = event.data
        const keywords = [
          'void', 'int', 'char', 'float', 'double',
          'long', 'short', 'unsigned', 'signed',
          'struct', 'enum', 'const', 'static',
          'return', 'sizeof'
        ]
        
        // 处理括号前的关键字
        keywords.forEach(keyword => {
          const regex = new RegExp(`${keyword}(?=\\()`, 'g')
          processedData = processedData.replace(regex, `${keyword} `)
        })
        
        // 处理其他位置的关键字
        keywords.forEach(keyword => {
          const regex = new RegExp(`\\b${keyword}\\b(?!\\s|["'])`, 'g')
          processedData = processedData.replace(regex, `${keyword} `)
        })
        
        messageToUpdate.text += processedData
        messageToUpdate.formattedText = sanitizeAndFormat(messageToUpdate.text)
        messages.value = [...messages.value]
        nextTick(() => scrollToBottom())
      }
    }

    // 监听错误事件
    eventSource.onerror = (error) => {
      console.error('EventSource error:', error)
      const messageToUpdate = messages.value.find(m => m.id === aiMessageId)
      if (messageToUpdate) {
        messageToUpdate.text += '\n\n读取响应时发生错误'
        messageToUpdate.formattedText = sanitizeAndFormat(messageToUpdate.text)
      }
      eventSource.close()
      loading.value = false
    }

    // 监听连接关闭事件
    eventSource.addEventListener('complete', () => {
      eventSource.close()
      loading.value = false
      scrollToBottom()
    })

  } catch (error) {
    console.error('Stream connection error:', error)
    const messageToUpdate = messages.value.find(m => m.id === aiMessageId)
    if (messageToUpdate) {
      messageToUpdate.text += '\n\n连接发生错误'
      messageToUpdate.formattedText = sanitizeAndFormat(messageToUpdate.text)
    }
    loading.value = false
  }
}

const generateId = () => `msg_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

// 格式化和净化消息内容
const sanitizeAndFormat = (message) => {
  try {
    let buffer = ''
    let isInCodeBlock = false
    let currentCodeBlock = null
    let codeBlockId = 0
    
    // 需要在后面添加空格的C语言关键字
    const typeKeywords = [
      // 类型关键字
      'void', 'int', 'char', 'float', 'double',
      'long', 'short', 'unsigned', 'signed',
      'struct', 'enum', 'const', 'static',
      // 函数相关关键字
      'return', 'sizeof'
    ]
    
    // 处理代码缩进的函数
    const processIndentation = (lines) => {
      let indentLevel = 0
      const indentSize = 2 // 缩进空格数
      
      return lines.map(line => {
        const trimmedLine = line.trim()
        
        // 处理大括号的缩进
        if (trimmedLine.includes('}')) {
          indentLevel = Math.max(0, indentLevel - 1)
        }
        
        // 计算当前行的缩进
        const indent = ' '.repeat(indentLevel * indentSize)
        
        // 处理关键字后面的空格
        let processedLine = trimmedLine
        for (const keyword of typeKeywords) {
          // 使用预格式化的HTML空格
          const regex = new RegExp(`\\b${keyword}\\b(?![\\s"'])`, 'g')
          processedLine = processedLine.replace(regex, `${keyword} `)
        }
        
        const formattedLine = indent + processedLine
        
        // 处理下一行的缩进级别
        if (trimmedLine.includes('{')) {
          indentLevel++
        }
        
        return formattedLine
      })
    }
    
    // 按行处理内容
    const lines = message.split('\n')
    
    for (const line of lines) {
      if (line.startsWith('```')) {
        if (!isInCodeBlock) {
          isInCodeBlock = true
          codeBlockId++
          const lang = line.slice(3).trim()
          // 使用 pre 标签的 white-space: pre 属性来保持空格
          buffer += `\n<pre style="white-space: pre"><code class="language-${lang}" id="code-block-${codeBlockId}">\n`
          currentCodeBlock = {
            id: codeBlockId,
            lang: lang,
            content: ''
          }
        } else {
          isInCodeBlock = false
          if (currentCodeBlock) {
            // 按行处理代码内容，保留换行
            const formattedLines = currentCodeBlock.content.trim().split('\n')
            
            // 应用缩进和关键字处理
            const indentedLines = processIndentation(formattedLines)
            
            // 应用语法高亮，保持换行和空格
            const highlightedCode = hljs.highlight(
              indentedLines.join('\n'),
              { language: currentCodeBlock.lang || 'plaintext' }
            ).value
            
            buffer += highlightedCode
          }
          buffer += `\n</code></pre>\n`
          currentCodeBlock = null
        }
        continue
      } else if (line.startsWith('###')) {
        // 确保标题格式正确
        const titleText = line.slice(3).trim()
        buffer += `\n<h3>${titleText}</h3>\n`
      } else if (isInCodeBlock) {
        if (currentCodeBlock) {
          currentCodeBlock.content += line + '\n'
          const codeElement = document.getElementById(`code-block-${currentCodeBlock.id}`)
          if (codeElement) {
            codeElement.textContent = currentCodeBlock.content
          }
        }
      } else {
        // 处理普通文本、列表和加粗
        if (line.trim().startsWith('-') || line.trim().startsWith('1.')) {
          // 处理列表项
          if (!buffer.endsWith('\n\n')) {
            buffer += '\n'
          }
          // 处理列表项中的加粗
          const processedLine = line.replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
          buffer += processedLine + '\n\n'  // 确保列表项之间有空行
        } else {
          // 处理普通文本中的加粗
          const processedLine = line.replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
          buffer += processedLine + '\n'
        }
      }
    }
    
    const htmlContent = marked(buffer, {
      gfm: true,
      breaks: true,
      highlight: null,
      listItemIndent: 'one',
      bulletListMarker: '-'
    })
    
    return `<div class="markdown-content">${DOMPurify.sanitize(htmlContent, {
      ALLOWED_TAGS: [
        'h1', 'h2', 'h3', 'h4', 'h5', 'h6', 'p', 'br', 'hr',
        'code', 'pre', 'blockquote', 'strong', 'em', 'ul', 'ol', 'li',
        'span', 'div'
      ],
      ALLOWED_ATTR: ['class', 'id'],
      KEEP_CONTENT: true
    })}</div>`
  } catch (error) {
    console.error('Format message error:', error)
    return `<div class="markdown-content"><p>${message}</p></div>`
  }
}
</script>

<style scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 40px);
  max-width: 800px;
  margin: 20px auto;
  padding: 0 20px;
  box-sizing: border-box;
}

.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  margin-bottom: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.message {
  margin-bottom: 20px;
  clear: both;
  width: 100%;
}

.message-content {
  padding: 15px;
  border-radius: 8px;
  max-width: 80%;
  word-wrap: break-word;
}

/* 用户消息靠右 */
.message .user-message {
  float: right;
  background: #e3f2fd;
  margin-left: 20%;
}

/* AI 消息���左 */
.message .ai-message {
  float: left;
  background: #f5f5f5;
  margin-right: 20%;
}

.input-container {
  display: flex;
  gap: 10px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.input-container .el-textarea {
  flex: 1;
}

/* Markdown 样式 */
:deep(.markdown-content) {
  line-height: 1.6;
}

:deep(.markdown-content pre) {
  margin: 1em 0;
  padding: 1em;
  overflow-x: auto;
  background: #f6f8fa;
  border-radius: 4px;
}

:deep(.markdown-content code) {
  font-family: Consolas, Monaco, 'Andale Mono', monospace;
  font-size: 0.9em;
}

:deep(.markdown-content p) {
  margin: 0.5em 0;
}

/* 确保消息容器新消息后正确清除浮动 */
.message::after {
  content: '';
  display: table;
  clear: both;
}

/* 更新样式以支持新的格式化 */
.markdown-content {
  line-height: 1.6;
  font-size: 16px;
}

.markdown-content h3 {
  margin: 1.5em 0 1em;
  padding-bottom: 0.3em;
  border-bottom: 1px solid #eaecef;
  font-size: 1.3em;
  font-weight: 600;
  color: #24292e;
}

.markdown-content pre {
  margin: 1em 0;
  padding: 16px;
  overflow: auto;
  background-color: #1e1e1e !important;
  border-radius: 8px;
  color: #d4d4d4 !important;
  font-family: 'Consolas', 'Monaco', 'Andale Mono', monospace;
  line-height: 1.5;
  tab-size: 4;
}

.markdown-content code {
  padding: 0.2em 0.4em;
  margin: 0;
  font-size: 85%;
  background-color: rgba(27,31,35,0.05);
  border-radius: 6px;
  font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
}

.markdown-content pre code {
  display: block;
  padding: 0;
  background: transparent;
  white-space: pre;
  word-break: normal;
  overflow-wrap: normal;
}

.markdown-content p {
  margin: 1em 0;
}

.markdown-content ul, .markdown-content ol {
  margin: 1em 0;
  padding-left: 2em;
}

.markdown-content li {
  margin: 0.5em 0;
}

.markdown-content blockquote {
  margin: 1em 0;
  padding: 0 1em;
  color: #6a737d;
  border-left: 0.25em solid #dfe2e5;
}

/* 代码高亮样式 */
.hljs {
  background: #1e1e1e !important;
  color: #d4d4d4 !important;
}

/* 关键字高亮 */
.hljs-keyword {
  color: #569cd6 !important; /* 蓝色 */
  font-weight: bold;
}

.hljs-function {
  color: #dcdcaa !important; /* 黄色 */
}

.hljs-number {
  color: #b5cea8 !important; /* 浅绿色 */
}

.hljs-comment {
  color: #6a9955 !important; /* 绿色 */
  font-style: italic;
}

.hljs-string {
  color: #ce9178 !important; /* 橙色 */
}

.hljs-type {
  color: #4ec9b0 !important; /* 青色 */
}

/* 列表项样式 */
.markdown-content ul {
  list-style-type: disc;
  margin-left: 1.5em;
  margin-bottom: 1em;
}

.markdown-content li {
  margin: 0.5em 0;
}

/* Markdown 标样式 */
.markdown-content h3 {
  margin: 1.5em 0 1em;
  padding-bottom: 0.3em;
  border-bottom: 1px solid #eaecef;
  font-size: 1.3em;
  font-weight: 600;
  color: #24292e;
}

/* 确保标题前有足够的空间 */
.markdown-content h3:first-child {
  margin-top: 0;
}

.markdown-content {
  /* 添加样式 */
  pre {
    background-color: #1e1e1e;
    padding: 1rem;
    border-radius: 4px;
    overflow-x: auto;
  }
  
  code {
    font-family: 'Consolas', 'Monaco', monospace;
    font-size: 0.9em;
  }
  
  /* 代码高亮样式 */
  .hljs {
    background: transparent;
  }
}

.markdown-content {
  line-height: 1.6;
}

.markdown-content h3 {
  margin: 1.5em 0 1em;
  padding-bottom: 0.3em;
  border-bottom: 1px solid #eaecef;
  font-size: 1.3em;
  font-weight: 600;
  color: #24292e;
}

.markdown-content ul,
.markdown-content ol {
  margin-left: 1.5em;
  margin-bottom: 1em;
}

.markdown-content li {
  margin: 0.5em 0;
}

.markdown-content strong {
  font-weight: 600;
  color: #24292e;
}
</style>