<template>
  <div :class="className" id="port-mapping-div" :style="{height:height,width:width}"></div>
</template>

<script>
import echarts from 'echarts'
require('echarts/theme/macarons') // echarts theme

export default {
  props: {
    className: {
      type: String,
      default: 'chart'
    },
    width: {
      type: String,
      default: '100%'
    },
    height: {
      type: String,
      default: '160px'
    },
    portMappingChart: {
      type: Object,
      default: () => {
        return {
          onLine: 90, // 进度条最大值
          total: 100 // 当前进度
        }
      }
    }
  },
  data() {
    return {
      chartDom: null
    }
  },
  mounted() {
    this.initChart()
  },
  beforeDestroy() {
    if (!this.chart) {
      return
    }
    this.chartDom.dispose()
    this.chartDom = null
  },
  methods: {
    initChart() {
      this.chartDom = document.getElementById('port-mapping-div')
      this.myChart = echarts.init(this.chartDom)
      const option = {
        title: {
          text: this.portMappingChart.onLine,
          subtext: '端口映射在线数',
          left: 'center',
          top: '32%',
          textStyle: {
            fontSize: 22,
            fontWeight: 800,
            color: '#c23531',
            align: 'center'
          },
          subtextStyle: {
            fontSize: 10,
            fontWeight: 800,
            color: '#6c7a89'
          }
          // bottom:'0'
        },
        tooltip: {
          trigger: 'item'
        },
        series: [
          {
            // 第一张圆环
            name: '端口映射',
            type: 'pie',
            radius: ['50%', '70%'],
            center: ['50%', '50%'],
            // 隐藏指示线
            labelLine: {
              normal: {
                show: false
              }
            },
            // 隐藏圆环上文字
            label: {
              normal: {
                show: false
              }
            },
            data: [
              // value当前进度 + 颜色
              {
                name: '在线数',
                value: this.portMappingChart.onLine
              },
              {
                name: '离线数',
                value: this.portMappingChart.total - this.portMappingChart.onLine
              }
            ]
          }
        ]
      }
      option && this.myChart.setOption(option)
    }
  }
}
</script>
