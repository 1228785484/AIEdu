<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div id="main-container">
    <!-- 左侧模块 -->
    <div id="left-container">
      <div id="left-content">
        <div class="learning-plan-item vertical" style="width: 93%; height: 60px;">
          <img class='image' src="@/assets/task.png" alt="描述文本">
          <span class="label">任务点</span>
          <!-- <span class="value">{{note.task}}/{{note.sumTask}}</span> -->
           <button class="value" @click="changeTask" style="background: rgba(0, 0, 0, 0);border: none;font-size: 18px;">{{note.task}}/{{note.sumTask}}</button>
        </div>
        <div class="learning-plan-item vertical" style="width: 93%; height: 60px;">
          <img class='image' src="@/assets/test.png" alt="描述文本">
          <span class="label">测试</span>
          <span class="value">{{note.test}}/{{note.sumTest}}</span>
        </div>
        <div class="learning-plan-item vertical" style="width: 93%; height: 60px;">
          <!-- <span class="label">学习次数</span>
          <span class="value">10</span> -->
          <img class='image' src="@/assets/frequency.png" alt="描述文本">
          <div class="label">学习次数</div>
          <div class="value">{{note.frequency}}</div>
          
        </div>
        <div class="learning-plan-item vertical" style="width: 93%; height: 220px;">
          <img class='image' src="@/assets/progress.png" alt="描述文本" style="top: 10%;">
          <span class="label" style="top:10%;">学习进度</span>
          
          <e-charts class="chart" :option="option" />
        </div>
      </div>
    </div>

    <!-- 中间模块（学习计划） -->
    <div id="middle-container">
      <div id="middle-content">
        <!--添加学习和测验按钮-->
        <div class="button-container">
          <button class="action-button" :class="{'selected':selectedAction === 'learn'}" @click="selectAction('learn')">学习</button>
          <button class="action-button" :class="{'selected':selectedAction === 'test'}" @click="selectAction('test')">测验</button>
        </div>
        <div id="middle-line"></div><!--在按钮下面添加一条横线-->
        <!--学习内容-->
        <div v-if="selectedAction === 'learn'" class="content-section learn-section">
          <!--学习内容区域-->
          <div class="border-container" :class="{'purple-border': selectedAction === 'learn'}"></div>
            <!-- 学习内容 -->
        </div>
        <!-- 测验内容 -->
        <div v-if="selectedAction === 'test'" class="content-section test-section">
          <!-- 测验内容区域 -->
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
import { ref } from 'vue';
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
function changeTask() {
  if (note.value.task < note.value.sumTask){
    note.value.task += 1
  }
}
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
  {
    label: '第三章：控制结构',
    id: 'chapter3',
    children: [
      { label: '第一节：顺序结构和选择结构', id: 'section3-1' },
      { label: '第二节：循环结构', id: 'section3-2' },
      { label: '第三节：break和continue', id: 'section3-3' },
    ],
  },
  {
    label: '第四章：函数',
    id: 'chapter4',
    children: [
      { label: '第一节：函数的定义、声明和调用', id: 'section4-1' },
      { label: '第二节：函数的参数传递方式', id: 'section4-2' },
      { label: '第三节：局部变量和全局变量', id: 'section4-3' },
    ],
  },
  {
    label: '第五章：数组和字符串',
    id: 'chapter5',
    children: [
      { label: '第一节：一维数组和二维数组的定义、初始化和遍历', id: 'section5-1' },
      { label: '第二节：字符串的基本操作', id: 'section5-2' },
      { label: '第三节：字符数组与字符串', id: 'section5-3' },
    ],
  },
  {
    label: '第六章：指针',
    id: 'chapter6',
    children: [
      { label: '第一节：指针的基本概念和用法', id: 'section6-1' },
      { label: '第二节：指针与数组的关系', id: 'section6-2' },
      { label: '第三节：指针与函数的参数传递', id: 'section6-3' },
    ],
  },
  {
    label: '第七章：结构体和共用体',
    id: 'chapter7',
    children: [
      { label: '第一节：结构体的定义和初始化', id: 'section7-1' },
      { label: '第二节：结构体数组、结构体指针和结构体嵌套', id: 'section7-2' },
      { label: '第三节：共用体的概念和用法', id: 'section7-3' },
    ],
  },
  {
    label: '第八章：文件操作',
    id: 'chapter8',
    children: [
      { label: '第一节：文件的基本概念和分类', id: 'section8-1' },
      { label: '第二节：文件的基本操作', id: 'section8-2' },
      { label: '第三节：文件定位函数', id: 'section8-3' },
    ],
  },
  {
    label: '第九章：预处理器和宏定义',
    id: 'chapter9',
    children: [
      { label: '第一节：宏定义的基本用法', id: 'section9-1' },
      { label: '第二节：文件包含指令', id: 'section9-2' },
      { label: '第三节：条件编译指令', id: 'section9-3' },
    ],
  },
  {
    label: '第十章：动态内存分配',
    id: 'chapter10',
    children: [
      { label: '第一节：动态内存分配的函数', id: 'section10-1' },
      { label: '第二节：动态内存分配的原理和注意事项', id: 'section10-2' },
      { label: '第三节：链表的基本操作', id: 'section10-3' },
    ],
  },
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
    margin:0 20px;/*添加外边距*/
    width:100%;
  }
  #learning-plan-container {
    margin-top: 30px;
  }
  /* 左侧模块 */
  #left-container {
    width: 400px;
    height: 650px;
    background-color: #f8f9fa;
    border-radius: 15px;
    box-shadow: 0 10px 10px rgb(237,201,237);
    margin-right:20px;
  }
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
#middle-container {
    width: 530px;
    height: 650px;
    background-color: #f8f9fa;
    border-radius: 15px;
    box-shadow: 0 10px 10px rgb(237, 201, 237);
    margin:0 30px;/*添加外边距*/
  }
  
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
  margin-bottom: 20px; /* 添加底部外边距 */
}

/* 按钮样式 */
.action-button {
  padding: 10px 20px;
  background-color: white;
  border: 1px solid rgb(247, 208, 247);
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
  height:100%;/*设置内容区域高度为100% */
  overflow-y:auto;/*允许垂直滚动 */
}
/*学习内容区域样式 */
#learn-section{
  background-color: #a7caf0;
  padding:20px;
  border-radius: 8px;
}
/* 添加边框样式 */
.border-container {
  border: 15px solid transparent; /* 默认无边框 */
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
  /* 右侧模块 */
  #right-container {
    width: 370px;
    height: 650px;
    background-color: #f8f9fa;
    border: rgb(255, 206, 255);
    border-radius: 15px;
    box-shadow: 0 10px 10px rgb(237, 201, 237);
    margin:0 30px;

  }
  #plan-title{
    font-size:1.5em;
    font-weight:bold;
    margin-bottom:20px;
  }
  
  #plan-line{
    height:1px;/*设置横线的高度*/
    background-color:rgb(244, 219, 245);
    margin-bottom:10px;
  }
  #scrollable-area {
    height: 100%; /* 设置滚动区域高度为100% */
    overflow-y: auto; /* 允许垂直滚动 */
    color:rgb(237, 201, 237);
  }
  #right-content {
    padding: 20px;
    height: 100%;
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
</style>