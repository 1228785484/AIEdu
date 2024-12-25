<template>
  <div class="ai-teaching-container">
    <div class="header">
      <h2>AI智能教学助手</h2>
    </div>
    
    <el-card class="lesson-plan-card">
      <template #header>
        <div class="card-header">
          <h3>AI教案生成</h3>
          <el-button type="primary" @click="generateLessonPlan" :loading="loading">
            生成教案
          </el-button>
        </div>
      </template>
      
      <el-form :model="formData" label-width="120px" class="lesson-plan-form">
        <el-form-item label="平均学习时长">
          <el-input-number 
            v-model="formData.averageStudyDuration"
            :min="0"
            :max="1000"
            :step="1"
            controls-position="right"
          >
            <template #suffix>分钟</template>
          </el-input-number>
        </el-form-item>
        
        <el-form-item label="平均得分">
          <el-input-number 
            v-model="formData.averageScore"
            :min="0"
            :max="100"
            :step="1"
            controls-position="right"
          >
            <template #suffix>分</template>
          </el-input-number>
        </el-form-item>
        
        <el-form-item label="选择章节">
          <el-cascader
            v-model="formData.chapterId"
            :options="chapterTree"
            :props="{
              checkStrictly: true,
              label: 'name',
              value: 'id',
              children: 'children',
              emitPath: false
            }"
            placeholder="请选择章节"
            clearable
            class="chapter-select"
          />
        </el-form-item>
      </el-form>

      <div v-if="lessonPlan" class="lesson-plan-result">
        <h4>生成的教案：</h4>
        <div class="lesson-plan-content markdown-body" v-html="renderedLessonPlan"></div>
      </div>
    </el-card>
  </div>
</template>

<script>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { marked } from 'marked'
import 'github-markdown-css/github-markdown.css'

// 配置marked选项
marked.setOptions({
  gfm: true,
  breaks: true,
  sanitize: false
})

export default {
  name: 'AiTeaching',
  
  setup() {
    const loading = ref(false)
    const lessonPlan = ref('')
    const chapterTree = ref([])
    
    const formData = reactive({
      averageStudyDuration: 0,
      averageScore: 0,
      chapterId: ''
    })

    // 将Markdown转换为HTML
    const renderedLessonPlan = computed(() => {
      if (!lessonPlan.value) return ''
      return marked(lessonPlan.value)
    })
    
    // 获取章节树数据
    const fetchChapterTree = async () => {
      try {
        const token = localStorage.getItem('token')
        if (!token) {
          ElMessage.error('未登录或登录已过期，请重新登录')
          return
        }

        const response = await fetch('http://localhost:8008/api/course/1/tree', {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        })

        if (!response.ok) {
          throw new Error('获取章节树失败')
        }

        const result = await response.json()
        // 检查返回的数据是否是数组且有内容
        if (Array.isArray(result) && result.length > 0) {
          chapterTree.value = result
          console.log('章节树加载成功:', chapterTree.value)
        } else {
          console.error('章节树数据格式不正确:', result)
          throw new Error('章节树数据格式不正确')
        }
      } catch (error) {
        console.error('获取章节树失败:', error)
        ElMessage.error('获取章节树失败，请稍后重试')
      }
    }
    
    const generateLessonPlan = async () => {
      if (!formData.chapterId) {
        ElMessage.warning('请选择章节')
        return
      }
      
      try {
        loading.value = true
        const token = localStorage.getItem('token')
        if (!token) {
          ElMessage.error('未登录或登录已过期，请重新登录')
          return
        }
        
        const response = await fetch('http://localhost:8008/api/teacher/tools/generate-lesson-plan', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          body: JSON.stringify({
            ...formData,
            chapterId: formData.chapterId
          })
        })
        
        if (!response.ok) {
          throw new Error('生成教案失败')
        }
        
        const result = await response.json()
        if (result.code === 200) {
          lessonPlan.value = result.data.reply
          ElMessage.success('教案生成成功')
        } else {
          throw new Error(result.msg || '生成教案失败')
        }
      } catch (error) {
        console.error('生成教案失败:', error)
        ElMessage.error('生成教案失败，请稍后重试')
      } finally {
        loading.value = false
      }
    }
    
    onMounted(() => {
      fetchChapterTree()
    })
    
    return {
      loading,
      formData,
      chapterTree,
      lessonPlan,
      renderedLessonPlan,
      generateLessonPlan
    }
  }
}
</script>

<style scoped>
.ai-teaching-container {
  padding: 24px;
}

.header {
  margin-bottom: 24px;
}

.header h2 {
  margin: 0;
  color: #303133;
  font-size: 24px;
}

.lesson-plan-card {
  margin-bottom: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  font-size: 18px;
  color: #303133;
}

.lesson-plan-form {
  margin-top: 24px;
}

.chapter-select {
  width: 100%;
}

.lesson-plan-result {
  margin-top: 24px;
  padding: 16px;
  background-color: #ffffff;
  border-radius: 4px;
}

.lesson-plan-result h4 {
  margin: 0 0 16px;
  color: #303133;
  font-size: 16px;
}

/* Markdown样式覆盖 */
:deep(.markdown-body) {
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", "Noto Sans", Helvetica, Arial, sans-serif;
  font-size: 14px;
  line-height: 1.6;
  padding: 20px;
  background-color: #ffffff;
  border-radius: 4px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  color: #000000 !important; /* 强制设置文本颜色为黑色 */
}

:deep(.markdown-body h1, 
      .markdown-body h2,
      .markdown-body h3,
      .markdown-body h4) {
  margin-top: 24px;
  margin-bottom: 16px;
  font-weight: 600;
  line-height: 1.25;
  color: #000000 !important; /* 强制设置标题颜色为黑色 */
}

:deep(.markdown-body h1) {
  font-size: 2em;
}

:deep(.markdown-body h2) {
  font-size: 1.5em;
}

:deep(.markdown-body h3) {
  font-size: 1.25em;
}

:deep(.markdown-body h4) {
  font-size: 1em;
}

:deep(.markdown-body ul,
      .markdown-body ol) {
  padding-left: 2em;
  color: #000000 !important; /* 强制设置列表颜色为黑色 */
}

:deep(.markdown-body li) {
  margin: 0.25em 0;
  color: #000000 !important; /* 强制设置列表项颜色为黑色 */
}

:deep(.markdown-body p) {
  margin-top: 0;
  margin-bottom: 16px;
  color: #000000 !important; /* 强制设置段落颜色为黑色 */
}

:deep(.markdown-body code) {
  padding: 0.2em 0.4em;
  background-color: rgba(175, 184, 193, 0.2);
  border-radius: 6px;
  font-family: ui-monospace, SFMono-Regular, SF Mono, Menlo, Consolas, Liberation Mono, monospace;
  color: #000000 !important; /* 强制设置代码颜色为黑色 */
}

:deep(.markdown-body pre) {
  padding: 16px;
  overflow: auto;
  font-size: 85%;
  line-height: 1.45;
  background-color: #f6f8fa;
  border-radius: 6px;
}

/* 确保所有文本都是黑色 */
:deep(.markdown-body *) {
  color: #000000 !important;
}
</style>
