<template>
  <div>
    <h1>欢迎来到项目广场</h1>
    <!-- 假设你已经安装并引入了 ECharts -->
    <div id="gauge-chart" style="width: 100%; height: 400px;"></div>
  </div>
</template>

<script>
import * as echarts from 'echarts';

export default {
  name: 'ProjectSquare',
  mounted() {
    this.initChart();
  },
  methods: {
    initChart() {
      const chartDom = document.getElementById('gauge-chart');
      const myChart = echarts.init(chartDom);
      const option = {
        series: [
          {
            type: 'gauge',
            startAngle: 180,
            endAngle: 0,
            center: ['50%', '75%'],
            radius: '90%',
            min: 0,
            max: 1,
            splitNumber: 10,
            axisLine: {
              lineStyle: {
                width: 6,
                color: [
                  [0.6, '#FF6E76'], // 不及格
                  [0.8, '#FDDD60'], // 及格
                  [0.9, '#58D9F9'], // 良好
                  [1, '#7CFFB2'] // 优秀
                ]
              }
            },
            pointer: {
              icon: 'path://M12.8,0.7l12,40.1H0.7L12.8,0.7z',
              length: '12%',
              width: 20,
              offsetCenter: [0, '-60%'],
              itemStyle: {
                color: 'auto'
              }
            },
            axisTick: {
              show: true,
              splitNumber: 4,
              length: 8,
              lineStyle: {
                color: '#999',
                width: 1
              }
            },
            splitLine: {
              show: true,
              length: 20,
              lineStyle: {
                color: '#000', // 分界线颜色
                width: 3
              }
            },
            axisLabel: {
              color: '#464646',
              fontSize: 18,
              distance: -60,
              rotate: 'tangential',
              formatter: function (value) {
                if (value === 0.3) {
                  return '不及格';
                } else if (value === 0.6) {
                  return '及格';
                } else if (value === 0.8) {
                  return '良好';
                } else if (value === 0.9) {
                  return '优秀';
                }
              }
            },
            title: {
              offsetCenter: [0, '-10%'],
              fontSize: 20
            },
            detail: {
              fontSize: 30,
              offsetCenter: [0, '-35%'],
              valueAnimation: true,
              formatter: function (value) {
                return Math.round(value * 100) + '';
              },
              color: 'inherit'
            },
            data: [
              {
                value: 0.7, // 当前分数
                name: '分数评定'
              }
            ]
          }
        ],
        graphic: [
          {
            type: 'line',
            shape: { x1: 0, y1: 0, x2: 0, y2: -300 },
            style: { stroke: '#FF6E76', lineWidth: 2 },
            position: ['50%', '75%'],
            rotation: Math.PI * 0.6 // 分界线角度 (0.6)
          },
          {
            type: 'line',
            shape: { x1: 0, y1: 0, x2: 0, y2: -300 },
            style: { stroke: '#FDDD60', lineWidth: 2 },
            position: ['50%', '75%'],
            rotation: Math.PI * 0.75 // 分界线角度 (0.75)
          },
          {
            type: 'line',
            shape: { x1: 0, y1: 0, x2: 0, y2: -300 },
            style: { stroke: '#58D9F9', lineWidth: 2 },
            position: ['50%', '75%'],
            rotation: Math.PI * 0.9 // 分界线角度 (0.9)
          }
        ]
      };

      myChart.setOption(option);
    }
  }
};
</script>

<style scoped>
/* 你可以在这里添加样式 */
</style>
