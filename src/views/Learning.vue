<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div id="main-container">
    <!-- 左侧模块 -->
    <div id="left-container">
      <div id="left-content">
        <div class="left-header">
          <h2>课程大纲</h2>
          <div class="header-underline"></div>
        </div>
        
        <!-- 课程进度统计 -->
        <div class="course-stats">
          <div class="stat-item">
            <div class="stat-icon">
              <img src="@/assets/task.png" alt="任务点">
            </div>
            <div class="stat-content">
              <span class="stat-label">任务点</span>
              <span class="stat-value">{{note.task}}/{{note.sumTask}}</span>
            </div>
          </div>

          <div class="stat-item">
            <div class="stat-icon">
              <img src="@/assets/test.png" alt="测试">
            </div>
            <div class="stat-content">
              <span class="stat-label">测试</span>
              <span class="stat-value">{{note.test}}/{{note.sumTest}}</span>
            </div>
          </div>

          <div class="stat-item">
            <div class="stat-icon">
              <img src="@/assets/frequency.png" alt="学习次数">
            </div>
            <div class="stat-content">
              <span class="stat-label">学习次数</span>
              <span class="stat-value">{{note.frequency}}</span>
            </div>
          </div>

          <div class="stat-item">
            <div class="stat-icon">
              <img src="@/assets/progress.png" alt="学习进度">
            </div>
            <div class="stat-content">
              <span class="stat-label">学习进度</span>
              <span class="stat-value">{{progressValue}}%</span>
            </div>
          </div>

          <div class="progress-chart-container">
            <e-charts class="progress-chart" :option="option" />
          </div>
        </div>

      </div>
    </div>

    <!-- 中间模块（学习计划） -->
    <div id="middle-container">
      <div id="middle-content">
        <div class="action-buttons">
          <span 
            class="action-btn" 
            :class="{ active: selectedAction === 'learn' }"
            @click="selectAction('learn')"
          >
            学习
          </span>
          <span 
            class="action-btn" 
            :class="{ active: selectedAction === 'test' }"
            @click="selectAction('test')"
          >
            测验
          </span>
        </div>
        <div class="divider"></div>
        
        <!-- 内容区域 -->
        <div class="content-wrapper">
          <div v-if="selectedAction === 'learn'" class="content-section learn-section">
            <div class="section-content">
              {{ sectionData ? sectionData.content : '请选择一个章节开始学习' }}
            </div>
          </div>
          <div v-if="selectedAction === 'test'" class="content-section test-section">
            <div class="section-content">
              测验内容将在这里显示
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧模块（仅保留选择日期的提示） -->
    <div id="right-container">
      <div id="right-content">
        <div id="plan-title">学习计划</div>
        <div id="plan-line"></div><!--在学习计划下面添加一条横线-->
        <div id="scrollable-area">
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
  </div>
</template>

<script setup>
import { ref,onMounted } from 'vue';
import { ElTree } from 'element-plus';
//任务点
var data1 = 12;
var data2 = 20;
var data3 = 0;
var data4 = 0;
var data5 = 3;
let note = ref({
  task:data1,
  sumTask:data2,
  test:data3,
  sumTest:data4,
  frequency:data5
});

//另一种进度图
var progressValue = 60;
var option = {
        title: { // 中间标题、数据
          text: progressValue+'%', // 标题
          textStyle: {
            color: '#28BCFE',
            fontSize: '25px'
          },
          subtext: '加载进度', // 副文本标题
          subtextStyle: {
            // 
            color: '#a9a9a9',
            fontSize: '10px',
          },
          itemGap: 20, // 标题间的距离
          left: 'center',
          top: '43%'
        },
        grid: [
          {x: '7%', y: '7%', width: '33%', height: '100%'},
        ],
        // 极坐标系的角度轴
        angleAxis: {
          // 起始角度，180 也可以是 225
          startAngle: 180,
          max: 360,
          clockwise: true, // 逆时针
          // 隐藏刻度线
          show: false
        },
        // 极坐标系的径向轴
        radiusAxis: {
          type: 'category',
          show: true,
          axisLabel: {
            show: false
          },
          axisLine: {
            show: false
 
          },
          axisTick: {
            show: false
          }
        },
        polar: {
          center: ['50%', '60%'],
          radius: '150%', //图形大小
        },
        series: [
          // 上层数据
          {
            type: 'bar',
            z: 2,
            // 数值
            data: [progressValue*180/100],
            showBackground: true,
            backgroundStyle: {
              color: 'transparent'
            },
            coordinateSystem: 'polar',  // 使用极坐标系
            roundCap: true,
            barWidth: 20,
            barGap: '-100%',
            itemStyle: {
              opacity: 1,
              color: {
                type: 'linear',
                x: 0,
                y: 0,
                x2: 0,
                y2: 1,
                colorStops: [
                 {
                    offset: 0,
                    color: '#25BFFF'
                  }, {
                    offset: 1,
                    color: '#5284DE'
                  }],
                shadowBlur: 5,
                shadowColor: '#f92a2a'
              }
            }
          },
          // 底层背景色
          {
            type: 'bar',
            z: 1,
            // 需要的圆环跨度大小，可以是180,270等
            data: [180],
            coordinateSystem: 'polar',
            roundCap: true,
            barWidth: 20,
            barGap: '-100%',
            itemStyle: {
              opacity: 1,
              color: '#093368'
            }
          }
          ,
        ],
 
      };
const selectedAction = ref(''); // 用于跟踪当前选中的动作
function selectAction(action) {
  selectedAction.value = action; // 更新选中的动作
}

// 目录树数据改为响应式
const treeData = ref([]);

// 加载课程树数据的方法
const loadCourseTree = async () => {
  try {
    const courseId = localStorage.getItem('selectedCourseId');
    if (!courseId) {
      console.error('No course selected');
      return;
    }

    const token = localStorage.getItem('token');
    const response = await fetch(`http://localhost:8008/api/course/${courseId}/tree`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });

    if (!response.ok) {
      throw new Error('Failed to fetch course tree');
    }

    const result = await response.json();
    console.log('Received course tree:', result);

    // 转换数据结构
    const transformNode = (node) => {
      return {
        id: node.id,
        label: `${node.name}`, // 显示名称
        children: Array.isArray(node.children) ? node.children.map(child => transformNode(child)) : []
      };
    };

    // 取第一个元素（课程），然后转换其children
    if (result && result.length > 0 && result[0].children) {
      treeData.value = result[0].children.map(chapter => transformNode(chapter));
    } else {
      console.error('Invalid course tree structure');
      treeData.value = [];
    }
  } catch (error) {
    console.error('Error loading course tree:', error);
    treeData.value = [];
  }
};

// 在组件挂载时加载数据
onMounted(() => {
  loadCourseTree();
});

// el-tree 需要的默认属性配置
const defaultProps = {
  children: 'children',
  label: 'label'
};

// 响应式变量，用于存储后端返回的章节内容
const sectionData = ref(null);

// 点击节点时的处理函数，发送请求给后端
const handleNodeClick = async (nodeData) => {
  const sectionId = nodeData.id;  // 使用节点的实际ID
  console.log('Clicked node ID:', sectionId);

  try {
    const response = await fetch(`http://localhost:8008/api/test/askAi?sectionId=${sectionId}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      },
    });

    if (!response.ok) {
      throw new Error('网络请求失败');
    }

    const data = await response.json();
    console.log('Received section data:', data);

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
    margin: 0 auto;
    max-width: 1400px;
    width: 100%;
    box-sizing: border-box;
    gap: 20px;
}

/* 左侧模块 */
#left-container {
    width: 25%;
    min-width: 250px;
    height: 650px;
    background-color: #f8f9fa;
    border-radius: 15px;
    box-shadow: 0 10px 10px rgb(237, 201, 237);
    flex-shrink: 0;
    overflow: hidden;
}

#left-content {
    padding: 20px;
    height: 100%;
    display: flex;
    flex-direction: column;
    box-sizing: border-box;
}

/* 左侧头部样式 */
.left-header {
    margin-bottom: 20px;
}

.left-header h2 {
    color: #333;
    font-size: 1.5em;
    margin: 0;
    padding: 0;
    font-weight: 600;
}

.header-underline {
    height: 3px;
    background: linear-gradient(to right, rgb(236, 198, 236), transparent);
    margin-top: 8px;
    border-radius: 2px;
}

/* 课程树样式 */
.course-tree {
    flex: 1;
    overflow: auto;
    padding-right: 10px;
    margin-top: 10px;
}

/* 自定义树节点样式 */
.custom-tree {
    background: transparent;
    font-size: 14px;
}

.custom-tree :deep(.el-tree-node__content) {
    height: 36px;
    border-radius: 6px;
    margin: 4px 0;
    transition: all 0.3s ease;
}

.custom-tree :deep(.el-tree-node__content:hover) {
    background-color: rgb(245, 230, 245);
}

.custom-tree :deep(.el-tree-node.is-current > .el-tree-node__content) {
    background-color: rgb(236, 198, 236);
    color: #333;
}

.custom-tree-node {
    display: flex;
    align-items: center;
    justify-content: space-between;
    width: 100%;
    padding: 0 6px;
}

.node-label {
    font-size: 14px;
    color: #333;
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.node-count {
    background-color: rgb(236, 198, 236);
    color: #fff;
    padding: 2px 6px;
    border-radius: 10px;
    font-size: 12px;
    margin-left: 8px;
}

/* 自定义滚动条 */
.course-tree::-webkit-scrollbar {
    width: 6px;
}

.course-tree::-webkit-scrollbar-track {
    background: #f1f1f1;
    border-radius: 3px;
}

.course-tree::-webkit-scrollbar-thumb {
    background: #ddd;
    border-radius: 3px;
}

.course-tree::-webkit-scrollbar-thumb:hover {
    background: #ccc;
}

/* 左侧统计项样式 */
.course-stats {
    margin: 15px 0;
    padding: 15px;
    background: white;
    border-radius: 10px;
    box-shadow: 0 2px 6px rgba(236, 198, 236, 0.2);
}

.stat-item {
    display: flex;
    align-items: center;
    padding: 12px;
    margin: 8px 0;
    background: #f8f9fa;
    border-radius: 8px;
    transition: all 0.3s ease;
}

.stat-item:hover {
    transform: translateX(5px);
    background: rgb(245, 230, 245);
}

.stat-icon {
    width: 32px;
    height: 32px;
    margin-right: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
}

.stat-icon img {
    width: 24px;
    height: 24px;
    object-fit: contain;
}

.stat-content {
    flex: 1;
    display: flex;
    justify-content: space-between;
    align-items: center;
    min-width: 0;
}

.stat-label {
    color: #666;
    font-size: 14px;
    font-weight: 500;
}

.stat-value {
    color: rgb(236, 198, 236);
    font-size: 15px;
    font-weight: 600;
    margin-left: 8px;
}

.progress-chart-container {
    margin-top: 15px;
    height: 160px;
    background: #f8f9fa;
    border-radius: 8px;
    padding: 15px;
    transition: all 0.3s ease;
    display: flex;
    justify-content: center;
    align-items: center;
}

.progress-chart-container:hover {
    transform: translateX(5px);
    background: rgb(245, 230, 245);
}

.progress-chart {
    width: 80%;
    height: 80%;
}

/* 自定义图表样式 */
:deep(.echarts-container) {
    display: flex;
    justify-content: center;
    align-items: center;
}

/* 中间模块 */
#middle-container {
    width: 50%;
    min-width: 500px;
    height: 650px;
    background-color: #f8f9fa;
    border-radius: 15px;
    box-shadow: 0 10px 10px rgb(237, 201, 237);
    flex-shrink: 0;
    overflow: hidden;
    position: relative;
}

#middle-content {
    padding: 20px;
    height: 100%;
    width: 100%;
    position: relative;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;
    max-width: 100%;
    overflow-x: hidden;
}

.action-buttons {
    display: flex;
    justify-content: flex-start;
    gap: 20px;
    margin-bottom: 15px;
    padding: 0 10px;
}

.action-btn {
    padding: 10px 30px;
    font-size: 16px;
    border: none;
    background-color: white;
    color: #666;
    border-radius: 6px;
    cursor: pointer;
    transition: all 0.3s ease;
    outline: none;
}

.action-btn:hover {
    background-color: rgb(245, 230, 245);
    transform: translateY(-1px);
}

.action-btn.active {
    background-color: rgb(236, 198, 236);
    color: white;
    transform: translateY(1px);
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.divider {
    height: 2px;
    background-color: rgb(244, 219, 245);
    margin: 0 10px 15px 10px;
}

.content-wrapper {
    flex: 1;
    overflow: hidden;
    position: relative;
    width: 100%;
}

.content-section {
    height: 100%;
    overflow: auto;
    padding: 20px;
    box-sizing: border-box;
    background-color: white;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
    width: 100%;
    max-width: 100%;
    word-wrap: break-word;
    overflow-x: hidden;
}

.section-content {
    line-height: 1.6;
    color: #333;
    width: 100%;
    max-width: 100%;
    word-wrap: break-word;
    overflow-wrap: break-word;
}

/* 右侧模块 */
#right-container {
    width: 25%;
    min-width: 280px;
    height: 650px;
    background-color: #f8f9fa;
    border-radius: 15px;
    box-shadow: 0 10px 10px rgb(237, 201, 237);
    flex-shrink: 0;
}

#right-content {
    padding: 20px;
    height: 100%;
}

#plan-title{
  font-size:1.5em;
  font-weight:bold;
  margin-bottom:20px;
}

#plan-line{
  height:1px;
  background-color:rgb(244, 219, 245);
  margin-bottom:10px;
}

/* 树形目录样式 */
.el-tree {
  margin-top: 20px;
  max-height: 400px;
  overflow-y: auto;
  color:black;
}

/* 章节数据展示部分 */
#section-content {
  margin-top: 20px;
  padding: 10px;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 2px 5px #a7caf0;
}

/* 自定义滚动条样式 */
.content-section::-webkit-scrollbar,
#scrollable-area::-webkit-scrollbar {
    width: 8px;
    height: 8px;
}

.content-section::-webkit-scrollbar-track,
#scrollable-area::-webkit-scrollbar-track {
    background: #f1f1f1;
    border-radius: 4px;
}

.content-section::-webkit-scrollbar-thumb,
#scrollable-area::-webkit-scrollbar-thumb {
    background: #ccc;
    border-radius: 4px;
}

.content-section::-webkit-scrollbar-thumb:hover,
#scrollable-area::-webkit-scrollbar-thumb:hover {
    background: #aaa;
}

/* 左侧模块 */
#left-content {
    padding: 20px;
    height: 100%;
    overflow-y: auto;
}

.chart{
    height:250px;
    width: 280px;
}

/* 学习计划模块 */
#learning-plan-container {
    margin-top: 30px;
}

.learning-plan-item.vertical {
  position:relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 25px;
  padding: 10px;
  background-color: #f0f8ff;
  border-radius: 8px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.learning-plan-item.vertical .label {
  position:absolute;
  top:30%;
  left:15%;
  font-weight: bold;
  margin-bottom: 10px;
  margin-top:5px;
}

.learning-plan-item.vertical .value {
  position: absolute;
  top:35%;
  left:80%;
  color: #007bff;
}

.image{
  position: absolute;
  top:40%;
  left:6%
}

/* 中间模块 */
#middle-content {
    padding: 20px;
    height: 100%;
    overflow-y: auto;
    position:absolute;
}

/* 按钮容器样式 */
.button-container {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

/* 按钮样式 */
.action-button {
  padding: 10px 20px;
  background-color: white;
  border: none;
  color: #666;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s;
  margin-right:20px;
}

/* 选中状态的按钮样式 */
.action-button.selected {
  background-color: rgb(236, 198, 236);
  color: white;
}

/*横线样式 */
#middle-line{
  height:1px;
  width:100%;
  background-color:rgb(244, 219, 245);
  margin-bottom:10px;
}

/*内容区域样式 */
#content-area{
  height:100%;
  overflow-y:auto;
}

/*学习内容区域样式 */
#learn-section{
  background-color: #a7caf0;
  padding:20px;
  border-radius: 8px;
}

/* 添加边框样式 */
.border-container {
  border: 15px solid transparent;
}

/* 当选中学习时，应用紫色边框 */
.purple-border {
  border-color: rgb(237, 201, 237);
  height:490px;
  width:280px;
}

/*测试内容区域样式 */
.tets-section{
  background-color:rgb(245, 205, 245) ;
  padding:20px;
  border-radius:8px;
}
</style>