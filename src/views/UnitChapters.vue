<template>
    <div class="unit-chapters">
      <div class="header">
        <div class="left-section">
          <el-button @click="$router.go(-1)" type="primary" plain>
            <el-icon><ArrowLeft /></el-icon> 返回
          </el-button>
          <h2>{{ unitTitle }}</h2>
        </div>
        
        <div class="right-section">
          <el-button type="primary" @click="handleAddChapter">
            <el-icon><Plus /></el-icon> 添加章节
          </el-button>
          <el-button type="danger" @click="handleDeleteChapters" :disabled="!selectedChapters.length">
            <el-icon><Delete /></el-icon> 删除章节
          </el-button>
        </div>
      </div>
  
      <div class="chapters-container">
        <el-table 
          :data="chapters" 
          border 
          style="width: 100%"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="70" />
          <el-table-column prop="chapterId" label="章节ID" />
          <el-table-column prop="title" label="标题" />
          <el-table-column prop="sequence_number" label="章节序号" />
        </el-table>
      </div>
  
      <!-- 添加章节对话框 -->
      <el-dialog v-model="dialogVisible" title="添加章节" width="500px">
        <el-form :model="chapterForm" label-width="100px">
          <el-form-item label="标题">
            <el-input v-model="chapterForm.title" />
          </el-form-item>
          <el-form-item label="内容提示词">
            <el-input v-model="chapterForm.content_prompt" type="textarea" />
          </el-form-item>
          <el-form-item label="序号">
            <el-input-number v-model="chapterForm.sequenceNumber" :min="1" />
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="submitAddChapter">确定</el-button>
          </span>
        </template>
      </el-dialog>
    </div>
  </template>
  
  <script>
  import { ArrowLeft, Plus, Delete } from '@element-plus/icons-vue'
  
  export default {
    name: 'UnitChapters',
    components: {
      ArrowLeft,
      Plus,
      Delete
    },
    props: {
      courseId: {
        type: [String, Number],
        required: true
      },
      unitId: {
        type: [String, Number],
        required: true
      },
      unitTitle: {
        type: String,
        required: true
      }
    },
    data() {
      return {
        chapters: [],
        selectedChapters: [],
        dialogVisible: false,
        chapterForm: {
          title: '',
          content_prompt: '',
          sequenceNumber: 1
        }
      }
    },
    async created() {
      await this.fetchChapters()
    },
    methods: {
      handleSelectionChange(selection) {
        this.selectedChapters = selection
      },
      handleAddChapter() {
        this.dialogVisible = true
      },
      async fetchChapters() {
        try {
          const token = localStorage.getItem('token')
          if (!token) {
            this.$message.error('未登录或登录已过期，请重新登录')
            this.$router.push('/login')
            return
          }

          console.log('Fetching chapters for courseId:', this.courseId, 'unitId:', this.unitId)

          const response = await fetch(`http://localhost:8008/api/course/${this.courseId}/tree`, {
            method: 'GET',
            headers: {
              'Authorization': `Bearer ${token}`,
              'Content-Type': 'application/json'
            },
            credentials: 'include'
          })
          
          const result = await response.json()
          console.log('API response:', result)

          if (response.ok) {
            const courseData = result[0]
            const unit = this.findUnitInTree(courseData, this.unitId)
            console.log('Found unit:', unit)

            if (unit && Array.isArray(unit.children)) {
              const sortedChapters = unit.children.sort((a, b) => a.id - b.id);
              
              this.chapters = sortedChapters.map((chapter, index) => {
                return {
                  chapterId: chapter.id,
                  title: chapter.name,
                  sequence_number: index + 1
                };
              });
              console.log('Final processed chapters:', this.chapters);
            } else {
              console.log('No chapters found or unit.children is not an array');
              this.chapters = [];
            }
          } else {
            throw new Error(result.msg || '获取章节数据失败')
          }
        } catch (error) {
          console.error('获取章节数据失败:', error)
          this.$message.error('获取章节数据失败，请稍后重试')
          this.chapters = []
        }
      },
      findUnitInTree(courseData, targetUnitId) {
        console.log('Finding unit in tree:', { courseData, targetUnitId })
        if (!courseData || !Array.isArray(courseData.children)) {
          console.log('Invalid course data structure')
          return null
        }

        for (const unit of courseData.children) {
          console.log('Checking unit:', unit)
          if (unit.id === parseInt(targetUnitId)) {
            console.log('Found matching unit')
            return unit
          }
        }
        console.log('No matching unit found')
        return null
      },
      async submitAddChapter() {
        try {
          const token = localStorage.getItem('token')
          if (!token) {
            this.$message.error('未登录或登录已过期，请重新登录')
            this.$router.push('/login')
            return
          }

          const response = await fetch(`http://localhost:8008/api/unit/${this.unitId}/chapter`, {
            method: 'POST',
            headers: {
              'Authorization': `Bearer ${token}`,
              'Content-Type': 'application/json'
            },
            body: JSON.stringify(this.chapterForm),
            credentials: 'include'
          })

          if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`)
          }

          const result = await response.json()
          if (result.code === 200) {
            this.$message.success('添加章节成功')
            this.dialogVisible = false
            await this.fetchChapters()
            this.chapterForm = { title: '', content_prompt: '', sequenceNumber: 1 }
          } else {
            throw new Error(result.msg || '添加章节失败')
          }
        } catch (error) {
          console.error('添加章节失败:', error)
          this.$message.error('添加章节失败，请稍后重试')
        }
      },
      async handleDeleteChapters() {
        try {
          if (!this.selectedChapters.length) {
            this.$message.warning('请选择删除的章节')
            return
          }

          await this.$confirm('确认删除选中的章节？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          })

          const token = localStorage.getItem('token')
          if (!token) {
            this.$message.error('未登录或登录已过期，请重新登录')
            this.$router.push('/login')
            return
          }

          const chapterIds = this.selectedChapters.map(chapter => chapter.chapterId)
          const response = await fetch(`http://localhost:8008/api/unit/chapter/batch`, {
            method: 'DELETE',
            headers: {
              'Authorization': `Bearer ${token}`,
              'Content-Type': 'application/json'
            },
            body: JSON.stringify(chapterIds),
            credentials: 'include'
          })

          if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`)
          }

          const result = await response.json()
          if (result.code === 200) {
            this.$message.success('删除章节成功')
            await this.fetchChapters()
          } else {
            throw new Error(result.msg || '删除章节失败')
          }
        } catch (error) {
          if (error.toString().includes('cancel')) return
          console.error('删除章节失败:', error)
          this.$message.error('删除章节失败，请稍后重试')
        }
      }
    }
  }
  </script>
  
  <style scoped>
  .unit-chapters {
    padding: 20px;
  }
  
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
  }
  
  .left-section {
    display: flex;
    align-items: center;
    gap: 20px;
  }
  
  .right-section {
    display: flex;
    gap: 10px;
  }
  
  .chapters-container {
    background: white;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
  }
  
  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
  }
  </style> 