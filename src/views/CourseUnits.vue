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
        <el-form-item label="单元ID">
          <el-input-number v-model="unitForm.unitId" :min="1" />
        </el-form-item>
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
      units: [
        {
          unitId: 1,
          title: '单元一',
          description: '基础知识介绍',
          sequenceNumber: 1
        },
        {
          unitId: 2,
          title: '单元二',
          description: '进阶概念学习',
          sequenceNumber: 2
        }
      ],
      searchQuery: '',
      selectedUnits: [],
      dialogVisible: false,
      unitForm: {
        unitId: 1,
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
  methods: {
    handleSearch() {
      // 搜索功能通过计算属性 filteredUnits 自动实现
    },
    handleSelectionChange(selection) {
      this.selectedUnits = selection
    },
    handleAddUnit() {
      this.dialogVisible = true
      const maxId = Math.max(...this.units.map(u => u.unitId), 0)
      this.unitForm.unitId = maxId + 1
    },
    submitAddUnit() {
      const newUnit = {
        unitId: this.unitForm.unitId,
        title: this.unitForm.title,
        description: this.unitForm.description,
        sequenceNumber: this.unitForm.sequenceNumber
      }
      
      this.units.push(newUnit)
      
      this.dialogVisible = false
      this.unitForm = {
        unitId: 1,
        title: '',
        description: '',
        sequenceNumber: 1
      }
      
      this.$message.success('添加单元成功')
    },
    handleDeleteUnits() {
      if (!this.selectedUnits.length) {
        this.$message.warning('请选择要删除的单元')
        return
      }
      
      this.$confirm('确认删除选中的单元吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const deleteIds = this.selectedUnits.map(unit => unit.unitId)
        this.units = this.units.filter(unit => !deleteIds.includes(unit.unitId))
        this.$message.success('删除单元成功')
      }).catch(() => {
        // 用户点击取消，不做任何操作
      })
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