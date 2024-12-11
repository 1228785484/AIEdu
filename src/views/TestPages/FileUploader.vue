<template>
  <div class="file-uploader">
    <el-card class="upload-card">
      <template #header>
        <div class="card-header">
          <span>文件管理</span>
        </div>
      </template>
      
      <!-- 文件上传区域 -->
      <el-upload
        class="upload-demo"
        :action="`${baseUrl}/api/file/upload`"
        :headers="headers"
        :data="uploadData"
        :on-success="handleUploadSuccess"
        :on-error="handleUploadError"
        multiple>
        <el-button type="primary">选择文件</el-button>
        <template #tip>
          <div class="el-upload__tip">支持任意类型文件上传</div>
        </template>
      </el-upload>

      <!-- 文件列表 -->
      <div class="file-list" v-if="fileList.length > 0">
        <h3>已上传文件列表：</h3>
        <el-table :data="fileList" style="width: 100%">
          <el-table-column prop="fileName" label="文件名" />
          <el-table-column label="预览" width="300">
            <template #default="scope">
              <!-- 视频预览 -->
              <video v-if="isVideo(scope.row.fileName)" 
                     :src="scope.row.fileUrl"
                     controls
                     style="max-width: 200px; max-height: 150px;">
                您的浏览器不支持视频标签
              </video>
              <!-- 图片预览 -->
              <img v-else-if="isImage(scope.row.fileName)"
                   :src="scope.row.fileUrl"
                   style="max-width: 200px; max-height: 150px;" />
              <!-- 其他文件类型显示图标 -->
              <el-icon v-else><Document /></el-icon>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template #default="scope">
              <el-button 
                type="primary" 
                link 
                @click="handleDownload(scope.row)">
                下载
              </el-button>
              <el-button 
                type="danger" 
                link 
                @click="handleDelete(scope.row)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Document } from '@element-plus/icons-vue'

export default {
  name: 'FileUploader',
  components: {
    Document
  },
  setup() {
    const baseUrl = 'http://localhost:8008'
    const userId = ref(localStorage.getItem('userid'))
    const bucketName = ref('notes')
    const fileList = ref([])

    // 上传参数
    const uploadData = {
      userId: userId.value,
      bucketName: bucketName.value
    }

    // 请求头
    const headers = {
      'Authorization': `Bearer ${localStorage.getItem('token')}`
    }

    // 判断是否为视频文件
    const isVideo = (fileName) => {
      const videoExtensions = ['.mp4', '.webm', '.ogg', '.mov']
      return videoExtensions.some(ext => fileName.toLowerCase().endsWith(ext))
    }

    // 判断是否为图片文件
    const isImage = (fileName) => {
      const imageExtensions = ['.jpg', '.jpeg', '.png', '.gif', '.webp']
      return imageExtensions.some(ext => fileName.toLowerCase().endsWith(ext))
    }

    // 上传成功的处理
    const handleUploadSuccess = (response, uploadFile) => {
      if (response.code === 200) {
        ElMessage.success('文件上传成功')
        fileList.value.push({
          fileName: uploadFile.name,
          fileUrl: response.data
        })
      } else {
        ElMessage.error(response.msg || '文件上传失败')
      }
    }

    // 上���失败的处理
    const handleUploadError = (error) => {
      ElMessage.error('文件上传失败：' + error.message)
    }

    // 下载文件
    const handleDownload = (file) => {
      window.open(file.fileUrl, '_blank')
    }

    // 删除文件
    const handleDelete = async (file) => {
      try {
        const response = await fetch(`${baseUrl}/api/file/delete`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          },
          body: JSON.stringify({
            userId: userId.value,
            bucketName: bucketName.value,
            objectName: file.fileName
          })
        })

        if (response.ok) {
          const result = await response.json()
          if (result.code === 200) {
            ElMessage.success('文件删除成功')
            fileList.value = fileList.value.filter(item => item.fileName !== file.fileName)
          } else {
            throw new Error(result.msg || '删除失败')
          }
        } else {
          throw new Error('删除失败')
        }
      } catch (error) {
        ElMessage.error('文件删除失败：' + error.message)
      }
    }

    return {
      baseUrl,
      headers,
      uploadData,
      fileList,
      handleUploadSuccess,
      handleUploadError,
      handleDownload,
      handleDelete,
      isVideo,
      isImage
    }
  }
}
</script>

<style scoped>
.file-uploader {
  padding: 20px;
}

.upload-card {
  max-width: 800px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.file-list {
  margin-top: 20px;
}

.el-upload__tip {
  margin-top: 8px;
  color: #666;
}
</style>