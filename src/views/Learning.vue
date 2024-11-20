<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div id="main-container">
    <!-- 左侧模块 -->
    <div id="left-container">
      <div id="left-content">
        <img src="@/assets/AI老师.png" alt="Avatar" id="logo" />
      </div>
    </div>

    <!-- 右侧模块（目录树） -->
    <div id="right-container">
      <div id="right-content">
        <!-- 目录树 -->
        <el-tree
          style="max-width: 600px"
          :data="treeData"
          :props="defaultProps"
          @node-click="handleNodeClick"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { ElTree } from 'element-plus';

// 目录树数据，添加了id字段
const treeData = [
  {
    label: '第一章：C语言基础',
    id: 'chapter1',
    children: [
      { label: '第一节：C语言的发展历程、特点及应用领域', id: 'section1-1' },
      { label: '第二节：基本语法规则', id: 'section1-2' },
      { label: '第三节：变量、数据类型和常量', id: 'section1-3' },
      { label: '第四节：输入输出函数', id: 'section1-4' },
    ],
  },
  {
    label: '第二章：运算符与表达式',
    id: 'chapter2',
    children: [
      { label: '第一节：算术运算符、关系运算符和逻辑运算符', id: 'section2-1' },
      { label: '第二节：赋值运算符和复合赋值运算符', id: 'section2-2' },
      { label: '第三节：表达式的概念及运算规则', id: 'section2-3' },
    ],
  },
  // 其他章节...
];

// el-tree 需要的默认属性配置
const defaultProps = {
  children: 'children',
  label: 'label',
};

// 响应式变量，用于存储后端返回的章节内容
const sectionData = ref(null);

// 点击节点时的处理函数，发送请求给后端
const handleNodeClick = async (nodeData) => {
  const sectionId = nodeData.id;
  console.log('Clicked node ID:', sectionId);

  // 使用 fetch 请求后端接口
  try {
    // 拼接 URL，假设后端接受类似于这个格式的请求
    const response = await fetch(`http://localhost:8008/api/test/askAi?sectionId=${sectionId}`, {
      method: 'GET', // GET 请求方式
      headers: {
        'Content-Type': 'application/json',
      },
    });

    // 检查响应状态
    if (!response.ok) {
      throw new Error('网络请求失败');
    }

    // 获取响应的 JSON 数据
    const data = await response.json();
    console.log('Received section data:', data);

    // 更新响应式变量，将章节数据存储并展示
    sectionData.value = data;
  } catch (error) {
    console.error('Error fetching section data:', error);
  }
};
</script>

<style scoped>
/* 主容器 */
#main-container {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 20px;
}

/* 左侧模块 */
#left-container {
  width: 450px;
  height: 650px;
  background-color: #f0f0f0;
  border-radius: 15px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

#left-content {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}

#logo {
  width: 90%;
  height: auto;
  max-width: 400px;
}

/* 右侧模块 */
#right-container {
  width: 350px;
  height: 650px;
  background-color: #f8f9fa;
  border-radius: 15px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

#right-content {
  padding: 20px;
  height: 100%;
  overflow-y: auto;
}

/* 树形目录样式 */
.el-tree {
  margin-top: 20px;
  max-height: 400px;
  overflow-y: auto;
}

/* 章节数据展示部分 */
#section-content {
  margin-top: 20px;
  padding: 10px;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}
</style>
