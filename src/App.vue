<template>
  <div class="app-container">
    <el-container class="main-container">
      <el-header>
        <div class="header-content">
          <img src="@/assets/2.png" alt="Logo" class="logo">
          <div class="nav-menu">
            <el-menu
              :default-active="activeIndex"
              mode="horizontal"
              router
              @select="handleSelect"
            >
              <el-menu-item index="/">首页</el-menu-item>
              <el-menu-item v-if="isLoggedIn" index="/project-square">项目广场</el-menu-item>
              <el-menu-item v-if="isLoggedIn" index="/ai-assistant">AI助手</el-menu-item>
              <el-menu-item v-if="isLoggedIn" index="/ai-learning">AI学习</el-menu-item>
            </el-menu>
          </div>
          <div class="user-info">
            <template v-if="isLoggedIn">
              <el-dropdown @command="handleCommand" trigger="click">
                <img 
                  src="@/assets/客户头像.png" 
                  alt="用户头像" 
                  class="user-avatar"
                >
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="personal">个人信息</el-dropdown-item>
                    <el-dropdown-item v-if="roleName === 'STUDENT'" command="authenticateTeacher">教师认证</el-dropdown-item>
                    <el-dropdown-item v-if="roleName === 'TEACHER' || roleName === 'ADMIN'" command="teacher">教师管理</el-dropdown-item>
                    <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
          </div>
        </div>
      </el-header>
      <el-main>
        <keep-alive>
          <router-view v-if="$route.meta.keepAlive" />
        </keep-alive>
        <router-view v-if="!$route.meta.keepAlive" />
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { ElMessage } from 'element-plus'

export default {
  name: 'App',
  data() {
    return {
      activeIndex: this.$route.path,
      username: localStorage.getItem('username') || '未登录'
    }
  },
  computed: {
    isLoggedIn() {
      return localStorage.getItem('token') !== null
    },
    roleName() {
      return localStorage.getItem('roleName'); // 获取角色名称
    }
  },
  watch: {
    '$route'(to) {
      this.activeIndex = to.path;
      // 检查登录状态和路由权限
      if (!this.isLoggedIn && to.path !== '/') {
        this.$router.push('/');
        ElMessage.warning('请先登录');
      }
    }
  },
  methods: {
    handleSelect(key) {
      if (!this.isLoggedIn && key !== '/') {
        ElMessage.warning('请先登录');
        this.$router.push('/');
        return;
      }
      this.activeIndex = key;
    },
    handleCommand(command) {
      if (command === 'logout') {
        localStorage.removeItem('token');
        localStorage.removeItem('username');
        localStorage.removeItem('email');
        localStorage.removeItem('userid');
        localStorage.removeItem('roleName'); // 清除角色名称
        this.username = '未登录';
        ElMessage.success('已退出登录');
        this.$router.push('/');
      } else if (command === 'personal') {
        this.$router.push('/personal-info');
      } else if (command === 'teacher') {
        this.$router.push('/teacher');
      } else if (command === 'authenticateTeacher') {
        this.authenticateTeacher();
      }
    },
    async authenticateTeacher() {
      const userId = localStorage.getItem('userid'); // 确保 userId 已正确存储
      if (!userId) {
        ElMessage.error('用户ID未找到，请重新登录');
        return;
      }
      try {
        const response = await fetch(`http://localhost:8008/api/roles/teacher/${userId}`, {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          }
        });
        if (response.ok) {
          ElMessage.success('教师认证成功');
        } else {
          const errorData = await response.json();
          ElMessage.error(errorData.message || '教师认证失败');
        }
      } catch (error) {
        console.error('Error during teacher authentication:', error);
        ElMessage.error('教师认证过程中出现错误');
      }
    }
  },
  created() {
    // 初始化时检查登录状态
    if (!this.isLoggedIn && this.$route.path !== '/') {
      this.$router.push('/');
    }
  }
}
</script>

<style>
html, body {
  margin: 0;
  padding: 0;
  overflow: hidden;
  height: 100%;
  width: 100%;
}

#app {
  height: 100%;
  width: 100%;
  overflow: hidden;
}

.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.el-header {
  padding: 0;
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, .12);
  z-index: 1000;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  height: 60px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  justify-content: space-between;
}

.logo {
  height: 40px;
  width: auto;
  margin-right: 20px;
}

.nav-menu {
  flex-grow: 1;
  margin-right: auto;
}

.nav-menu .el-menu {
  border-bottom: none;
}

.user-info {
  margin-left: auto;
  display: flex;
  align-items: center;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  cursor: pointer;
  object-fit: cover;
}

.el-main {
  flex: 1;
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px); /* 减去header高度 */
  box-sizing: border-box;
}

/* 确保内容区域最小宽度 */
@media screen and (min-width: 1200px) {
  .el-main {
    padding: 20px calc((100% - 1200px) / 2);
  }
}

/* 响应式布局 */
@media screen and (max-width: 768px) {
  .header-content {
    padding: 0 20px;
  }
  
  .el-main {
    padding: 10px;
  }
}
</style>
