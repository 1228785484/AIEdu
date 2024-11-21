<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div id="main-container">
    <!-- 左侧模块 -->
    <div id="left-container">
      <div id="left-content">
        <div class="learning-plan-item vertical">
          <span class="label">任务点</span>
          <span class="value">11/20</span>
        </div>
        <div class="learning-plan-item vertical">
          <span class="label">测试</span>
          <span class="value">0/0</span>
        </div>
        <div class="learning-plan-item vertical">
          <span class="label">学习次数</span>
          <span class="value">10</span>
        </div>
        <div class="learning-plan-item vertical">
          <span class="label">学习进度</span>
          
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
          <ul class="chapter-list">
            <li v-for="chapter in chapters" :key="chapter.id" class="chapter-item">
              <span @click="toggleChapter(chapter.id)" :class="{ 'chapter-name': true, 'expanded': chapter.expanded }">
                {{ chapter.name }}
                <span class="arrow" v-if="chapter.sections.length > 0">{{ chapter.expanded ? '▼' : '►' }}</span>
              </span>
              <ul v-if="chapter.expanded" class="section-list">
                <li v-for="section in chapter.sections" :key="section.id" class="section-item">
                  <span @click="selectSection(section.id)">{{ section.name }}</span>
                </li>
              </ul>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

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

const chapters = ref([
  {
    id: 1,
    name: '第一章',
    expanded: false,
    sections: [
      { id: 1.1, name: '第一节' },
      { id: 1.2, name: '第二节' },
    ],
  },
  {
    id: 2,
    name: '第二章',
    expanded: false,
    sections: [
      { id: 2.1, name: '第一节' },
      { id: 2.2, name: '第二节' },
    ],
  }
]);
const selectedAction = ref(''); // 用于跟踪当前选中的动作
function toggleChapter(chapterId) {
  const chapter = chapters.value.find(c => c.id === chapterId);
  if (chapter) {
    chapter.expanded = !chapter.expanded;
  }
}

function selectSection(sectionId) {
  // 处理小节点击事件
  console.log(`小节 ${sectionId} 被点击`);
}

function selectAction(action) {
  selectedAction.value = action; // 更新选中的动作
}
</script>

<style scoped>
/* 主容器 */
#main-container {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    padding: 20px;
    margin:0 20px;/*添加外边距*/
  }
  #learning-plan-container {
    margin-top: 30px;
  }
  /* 左侧模块 */
  #left-container {
    width: 450px;
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

  /* 学习计划模块 */
  #learning-plan-container {
    margin-top: 30px;
  }

.learning-plan-item.vertical {
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
  font-weight: bold;
  margin-bottom: 10px;
  margin-top:5px;
}

.learning-plan-item.vertical .value {
  color: #007bff;
}

/* 中间模块 */
#middle-container {
    width: 500px;
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
  width:290px;
}
/*测试内容区域样式 */
.tets-section{
  background-color:rgb(245, 205, 245) ;
  padding:20px;
  border-radius:8px;
}
  /* 右侧模块 */
  #right-container {
    width: 350px;
    height: 650px;
    background-color: #f8f9fa;
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
  }
  #right-content {
    padding: 20px;
    height: 100%;
  }
  .chapter-list {
    margin-top:28px;
    list-style: none;
    padding: 0;
}

.chapter-item {
  margin-bottom: 10px;
}

.chapter-name{
  font-size: 1.2em;/*设置一级目录比二级目录大 */
  cursor:pointer;
}

.expanded .arrow{
  transform: rotate(90deg);
}

.arrow{
  display: inline-block;
  transition: transform 0.3s;
  color:rgb(237,201,237);
}
.section-list {
  list-style: none;
  padding-left: 20px;
}

.section-item {
  margin-bottom: 5px;
}
.section-name{
  font-size:1em;/*设置二级目录字体大小 */
  cursor:pointer;
}
.chart{
  height:250px;
  width: 400px;
}
</style>