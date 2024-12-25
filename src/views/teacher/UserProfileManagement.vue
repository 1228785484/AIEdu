<template>
  <div class="profile-info" style="margin-top: 20px;">
    <h2>用户信息/个人资料</h2>
    <div class="search-area">
      <el-select v-model="searchField" placeholder="选择搜索字段" style="width: 150px;">
        <el-option label="用户ID" value="userId"></el-option>
        <el-option label="用户名" value="username"></el-option>
        <el-option label="邮箱" value="email"></el-option>
      </el-select>
      <el-input
        v-model="localSearchQuery"
        placeholder="输入搜索内容"
        class="search-input"
      />
      <el-button 
        type="primary"
        size="medium"
        @click="searchStudents"
        class="search-button">
        查询
      </el-button>
      <div class="total-users">
        <span>总人数: {{ filteredUsers.length }}</span>
      </div>
    </div>
    <table class="info-table">
      <thead>
        <tr>
          <th style="width: 15%">用户ID</th>
          <th style="width: 20%">用户名</th>
          <th style="width: 15%">邮箱</th>
          <th style="width: 20%">身份角色</th>
          <th style="width: 30%">操作</th> <!-- 新增操作列 -->
        </tr>
      </thead>
      <tbody>
        <tr v-for="user in filteredUsers" :key="user.userId">
          <td>{{ user.userId || '-' }}</td>
          <td>{{ user.username || '-' }}</td>
          <td>{{ user.email || '-' }}</td>
          <td>{{ user.role || '-' }}</td>
          <td>
            <el-button size="mini" type="danger" @click="deleteUser(user.userId)">删除</el-button>
            <el-button size="mini" type="primary" @click="assignRole(user, 'teacher')">分配教师</el-button>
            <el-button size="mini" type="success" @click="assignRole(user, 'student')">分配学生</el-button>
          </td> <!-- 操作列按钮 -->
        </tr>
        <tr v-if="filteredUsers.length === 0">
          <td colspan="5" style="text-align: center;">暂无数据</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
export default {
  name: 'UserProfileManagement',
  data() {
    return {
      allUsers: [],
      localSearchQuery: '',
      searchField: 'userId', // 默认搜索字段
      filteredUsers: [], // 用于存储过滤后的用户
    };
  },
  created() {
    this.fetchUserProfile(); // 调用获取用户资料的方法
  },
  methods: {
    // 获取用户资料
    async fetchUserProfile() {
      try {
        const response = await fetch(`http://localhost:8008/user/list?pageNum=1&pageSize=15`, {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
            'Content-Type': 'application/json'
          }
        });

        if (!response.ok) {
          throw new Error('获取用户资料失败');
        }

        const data = await response.json();
        console.log('获取的用户数据:', data); 

        // 确保将正确的段赋值给 allUsers
        this.allUsers = data.data.records; 
        this.filteredUsers = this.allUsers; // 初始化过滤后的用户为所有用户

        // 获取每个用户的角色
        await this.fetchRolesForUsers();

        // 查用户资是否成功加载
        console.log('所有用户:', this.allUsers);

      } catch (error) {
        console.error('获取用户资料出错:', error.message); // 打印错误信息
        alert('获取用户资料失败，请稍后试');
      }
    },
    
    // 获取每个用户的角色
    async fetchRolesForUsers() {
      for (let user of this.allUsers) {
        try {
          const roleResponse = await fetch(`http://localhost:8008/api/roles/${user.userId}`, {
            method: 'GET',
            headers: {
              'Authorization': `Bearer ${localStorage.getItem('token')}`,
              'Content-Type': 'application/json'
            }
          });

          if (roleResponse.ok) {
            const roleData = await roleResponse.json();
            user.role = roleData.data?.roleName || '-'; // 获取角色名称，如果没有角色则为 '-'
          } else {
            user.role = '-'; // 如果获取角色失败，显示默认值
          }
        } catch (error) {
          console.error(`获取用户 ${user.userId} 角色失败:`, error.message);
          user.role = '-'; // 角色获取失败时，显示默认值
        }
      }

      // 更新过滤后的用户列表
      this.filteredUsers = [...this.allUsers];
    },

    // 删除用户
    deleteUser(userId) {
      // 在前端删除用户
      this.filteredUsers = this.filteredUsers.filter(user => user.userId !== userId);
      this.allUsers = this.allUsers.filter(user => user.userId !== userId);
      alert(`用户ID: ${userId} 已删除`);
    },

    // 分配角色
    async assignRole(user, role) {
      let apiUrl = '';
      if (role === 'teacher') {
        apiUrl = `http://localhost:8008/api/roles/teacher/${user.userId}`;
      } else if (role === 'student') {
        apiUrl = `http://localhost:8008/api/roles/student/${user.userId}`;
      } else {
        return;
      }

      try {
        const response = await fetch(apiUrl, {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
            'Content-Type': 'application/json'
          }
        });

        if (response.ok) {
          user.role = role === 'teacher' ? 'teacher' : 'student'; // 更新角色
          alert(`成功分配角色：${role} 给 用户ID: ${user.userId}`);
        } else {
          alert('分配角色失败: 请求失败');
        }
      } catch (error) {
        console.error('分配角色失败:', error);
        alert('分配角色失败，请稍后重试');
      }
    },
    // 搜索用户
    searchStudents() {
      if (!this.localSearchQuery) {
        this.filteredUsers = this.allUsers; // 如果没有搜索内容，返回所有用户
      } else {
        this.filteredUsers = this.allUsers.filter(user => {
          const value = user[this.searchField] ? user[this.searchField].toString() : '';
          return value.includes(this.localSearchQuery);
        });
      }
    },
  },
};
</script>

<style scoped>
  .profile-info {
    padding: 24px;
    background: #ffffff;
    border-radius: 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  }
  
  .search-area {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 24px;
    background: #f8f9fc;
    padding: 16px;
    border-radius: 8px;
  }
  
  .search-input {
    flex: 1;
    max-width: 300px;
  }

  .search-button {
    margin-left: 0 !important;
    height: 40px;
    padding: 0 20px;
    font-weight: 500;
    border-radius: 6px;
  }

  .total-users {
    margin-left: auto;
    padding: 0 16px;
    border-left: 2px solid #e0e7ff;
  }
  
  .total-users span {
    color: #2196f3;
    font-weight: 500;
    font-size: 15px;
  }
  
  .info-table {
    width: 100%;
    border-collapse: separate;
    border-spacing: 0;
    background: white;
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 2px 12px rgba(33, 150, 243, 0.08);
  }
  
  .info-table th {
    background: #f5f7fa;
    color: #2196f3;
    font-weight: 600;
    padding: 14px 16px;
    text-align: left;
    border-bottom: 2px solid #e8f4fe;
  }
  
  .info-table td {
    padding: 14px 16px;
    border-bottom: 1px solid #f0f7ff;
    color: #333;
  }
  
  .info-table tr:hover {
    background-color: #f8fbff;
  }
  
  .info-table tr:last-child td {
    border-bottom: none;
  }
  
  /* 按钮样式优化 */
  :deep(.el-button) {
    transition: all 0.3s ease;
    border-radius: 6px;
    font-weight: 500;
  }
  
  :deep(.el-button--danger) {
    background: #f44336;
    border-color: #f44336;
    color: white;
  }
  
  :deep(.el-button--danger:hover) {
    background: #ef5350;
    border-color: #ef5350;
    box-shadow: 0 2px 8px rgba(244, 67, 54, 0.2);
    transform: translateY(-1px);
  }
  
  :deep(.el-button--primary) {
    background: #2196f3;
    border-color: #2196f3;
    color: white;
  }
  
  :deep(.el-button--primary:hover) {
    background: #42a5f5;
    border-color: #42a5f5;
    box-shadow: 0 2px 8px rgba(33, 150, 243, 0.2);
    transform: translateY(-1px);
  }
  
  :deep(.el-button--success) {
    background: #4caf50;
    border-color: #4caf50;
    color: white;
  }
  
  :deep(.el-button--success:hover) {
    background: #66bb6a;
    border-color: #66bb6a;
    box-shadow: 0 2px 8px rgba(76, 175, 80, 0.2);
    transform: translateY(-1px);
  }
  
  /* 输入框样式优化 */
  :deep(.el-input__inner) {
    height: 40px;
    line-height: 40px;
    border-color: #e0e7ff;
    border-radius: 6px;
    transition: all 0.3s ease;
    background: #ffffff;
  }
  
  :deep(.el-input__inner:hover) {
    border-color: #2196f3;
  }
  
  :deep(.el-input__inner:focus) {
    border-color: #2196f3;
    box-shadow: 0 0 0 2px rgba(33, 150, 243, 0.1);
  }
  
  /* 标题样式 */
  h2 {
    color: #2196f3;
    font-size: 24px;
    margin-bottom: 24px;
    font-weight: 600;
  }
</style>