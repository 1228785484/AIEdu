<template>

    <div class="course-units">
  
      <div class="header">
  
        <div class="left-section">
  
          <el-button @click="$router.go(-1)" type="primary" plain>
  
            <el-icon><ArrowLeft /></el-icon> 返回
  
          </el-button>
  
          <el-input
  
            v-model="searchQuery"
  
            placeholder="搜索单元"
  
            class="search-input"
  
            clearable
  
            @input="handleSearch"
  
          >
  
            <template #prefix>
  
              <el-icon><Search /></el-icon>
  
            </template>
  
          </el-input>
  
        </div>
  
        
  
        <div class="right-section">
  
          <el-button type="primary" @click="handleAddUnit">
  
            <el-icon><Plus /></el-icon> 添加单元
  
          </el-button>
  
          <el-button type="danger" @click="handleDeleteUnits" :disabled="!selectedUnits.length">
  
            <el-icon><Delete /></el-icon> 删除单元
  
          </el-button>
  
        </div>
  
      </div>
  
      
  
      <div class="units-container">
  
        <el-table 
  
          :data="filteredUnits" 
  
          border 
  
          style="width: 100%"
  
          @selection-change="handleSelectionChange"
  
        >
  
          <el-table-column type="selection" width="55" />
  
          <el-table-column prop="unitId" label="单元ID" width="180" />
  
          <el-table-column prop="title" label="标题" width="200">
  
            <template #default="scope">
  
              <span style="color: black; cursor: default;">
  
                {{ scope.row.title }}
  
              </span>
  
            </template>
  
          </el-table-column>
  
          <el-table-column prop="description" label="描述" />
  
          <el-table-column prop="sequenceNumber" label="序号" width="250" />
  
          <el-table-column label="查看" width="200">
  
            <template #default="scope">
  
              <el-button type="text" @click="handleViewUnit(scope.row)">查看</el-button>
  
            </template>
  
          </el-table-column>
  
        </el-table>
  
      </div>
  
  
  
      <!-- 添加单元对话框 -->
  
      <el-dialog v-model="dialogVisible" title="添加单元" width="500px">
  
        <el-form :model="unitForm" label-width="100px">
  
          <el-form-item label="标题">
  
            <el-input v-model="unitForm.title" />
  
          </el-form-item>
  
          <el-form-item label="描述">
  
            <el-input v-model="unitForm.description" type="textarea" />
  
          </el-form-item>
  
          <el-form-item label="序号">
  
            <el-input-number v-model="unitForm.sequenceNumber" :min="1" />
  
          </el-form-item>
  
        </el-form>
  
        <template #footer>
  
          <span class="dialog-footer">
  
            <el-button @click="dialogVisible = false">取消</el-button>
  
            <el-button type="primary" @click="submitAddUnit">确定</el-button>
  
          </span>
  
        </template>
  
      </el-dialog>
  
    </div>
  
  </template>
  
  
  
  <script>
  
  import { ArrowLeft, Search, Plus, Delete } from '@element-plus/icons-vue'
  
  
  
  export default {
  
    name: 'CourseUnits',
  
    components: {
  
      ArrowLeft,
  
      Search,
  
      Plus,
  
      Delete
  
    },
  
    data() {
  
      return {
  
        units: [],
  
        searchQuery: '',
  
        selectedUnits: [],
  
        dialogVisible: false,
  
        unitForm: {
  
          title: '',
  
          description: '',
  
          sequenceNumber: 1
  
        }
  
      }
  
    },
  
    computed: {
  
      filteredUnits() {
  
        if (!this.searchQuery) return this.units
  
        const query = this.searchQuery.toLowerCase()
  
        return this.units.filter(unit => 
  
          unit.title.toLowerCase().includes(query) ||
  
          unit.description.toLowerCase().includes(query)
  
        )
  
      }
  
    },
  
    async created() {
  
      const courseId = this.$route.params.courseId
  
      await this.fetchCourseUnits(courseId)
  
    },
  
    methods: {
  
      async fetchCourseUnits(courseId) {
  
        try {
  
          const token = localStorage.getItem('token')
  
          if (!token) {
  
            this.$message.error('未登录或登录已过期，请重新登录')
  
            this.$router.push('/login')
  
            return
  
          }
  
  
  
          const params = new URLSearchParams({
  
            courseId: courseId
  
          })
  
          
  
          const response = await fetch(`http://localhost:8008/api/course/findUnits?${params.toString()}`, {
  
            method: 'GET',
  
            headers: {
  
              'Authorization': `Bearer ${token}`,
  
              'Content-Type': 'application/json'
  
            },
  
            credentials: 'include'
  
          })
  
          
  
          const result = await response.json()
  
  
  
          if (response.ok) {
  
            this.units = result.map(unit => ({
  
              unitId: unit.unitId,
  
              title: unit.title,
  
              description: unit.description,
  
              sequenceNumber: unit.sequenceNumber
  
            }))
  
          } else {
  
            throw new Error(result.msg || '获取课程单元数据失败')
  
          }
  
        } catch (error) {
  
          console.error('获取课程单元数据失败:', error)
  
          this.$message.error('获取课程单元数据失败，请稍后重试')
  
          this.units = []
  
        }
  
      },
  
      handleSearch() {
  
        // 搜索功能通过计算属性 filteredUnits 自动实现
  
      },
  
      handleSelectionChange(selection) {
  
        this.selectedUnits = selection
  
      },
  
      handleAddUnit() {
  
        this.dialogVisible = true
  
      },
  
      async submitAddUnit() {
  
        try {
  
          const token = localStorage.getItem('token')
  
          if (!token) {
  
            this.$message.error('未登录或登录已过期，请重新登录')
  
            this.$router.push('/login')
  
            return
  
          }
  
  
  
          const response = await fetch(`http://localhost:8008/api/course/${this.$route.params.courseId}/unit`, {
  
            method: 'POST',
  
            headers: {
  
              'Authorization': `Bearer ${token}`,
  
              'Content-Type': 'application/json'
  
            },
  
            body: JSON.stringify(this.unitForm),
  
            credentials: 'include'
  
          })
  
  
  
          if (!response.ok) {
  
            throw new Error(`HTTP error! status: ${response.status}`)
  
          }
  
  
  
          const result = await response.json()
  
          if (result.code === 200) {
  
            this.$message.success('添加单元成功')
  
            this.dialogVisible = false
  
            await this.fetchCourseUnits(this.$route.params.courseId)
  
          } else {
  
            throw new Error(result.msg || '添加单元失败')
  
          }
  
        } catch (error) {
  
          console.error('添加单元失败:', error)
  
          this.$message.error('添加单元失败，请稍后重试')
  
        }
  
      },
  
      async handleDeleteUnits() {
  
        try {
  
          const unitIds = this.selectedUnits.map(unit => unit.unitId)
  
          
  
          await this.$confirm('确认删除选中的单元吗？', '提示', {
  
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
  
  
  
          const response = await fetch(`http://localhost:8008/api/course/unit/batch`, {
  
            method: 'DELETE',
  
            headers: {
  
              'Authorization': `Bearer ${token}`,
  
              'Content-Type': 'application/json'
  
            },
  
            body: JSON.stringify(unitIds),
  
            credentials: 'include'
  
          })
  
  
  
          if (!response.ok) {
  
            throw new Error(`HTTP error! status: ${response.status}`)
  
          }
  
  
  
          const result = await response.json()
  
          if (result.code === 200) {
  
            this.$message.success('删除单元成功')
  
            await this.fetchCourseUnits(this.$route.params.courseId)
  
          } else {
  
            throw new Error(result.msg || '删除单元失败')
  
          }
  
        } catch (error) {
  
          if (error.toString().includes('cancel')) return
  
          console.error('删除单元失败:', error)
  
          this.$message.error('删除单元失败，请稍后重试')
  
        }
  
      },
  
      handleTitleClick(unit) {
  
        this.$router.push({
  
          name: 'UnitChapters',
  
          params: {
  
            courseId: this.$route.params.courseId,
  
            unitId: unit.unitId,
  
            unitTitle: unit.title
  
          }
  
        })
  
      },
  
      goToCourseDetails(courseId) {
  
        this.$router.push({ name: 'UserQuizScores', params: { userId: this.userId, courseId } });
  
      },
  
      handleViewUnit(unit) {
  
        console.log('查看单元:', unit); // 调试信息
  
        this.$router.push({
  
          name: 'UnitChapters',
  
          params: {
  
            courseId: this.$route.params.courseId,
  
            unitId: unit.unitId,
  
            unitTitle: unit.title
  
          }
  
        });
  
      }
  
    }
  
  }
  
  </script>
  
  
  
  <style scoped>
  
  .course-units {
  
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
  
  
  
  .search-input {
  
    width: 300px;
  
  }
  
  
  
  .units-container {
  
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
  
  
  
  .course-units .el-link {
  
    font-weight: normal;
  
    color: inherit;
  
    text-decoration: none;
  
  }
  
  
  
  .course-units .el-link:hover {
  
    text-decoration: none;
  
  }
  
  </style> 