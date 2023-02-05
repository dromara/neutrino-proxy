<template>
  <div :class="className" :id="dailyTrafficChart.isHistory ? 'history-traffic-div' : 'daily-traffic-div'" :style="{height:height,width:width}"></div>
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
    dailyTrafficChart: {
      type: Object,
      default: () => {
        return {
          upload: 90, // 上行
          download: 100, // 下行
          isHistory: false
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
      const isHistory = this.dailyTrafficChart.isHistory
      this.chartDom = document.getElementById(isHistory ? 'history-traffic-div' : 'daily-traffic-div')
      this.myChart = echarts.init(this.chartDom)
      const option = {
        title: {
          text: isHistory ? '历史流量' : '今日流量',
          left: 'center',
          bottom: '0',
          textStyle: {
            fontSize: 12,
            fontWeight: 800,
            color: '#6c7a89'
          }
        },
        tooltip: {
          trigger: 'item'
        },
        legend: {
          orient: 'vertical',
          left: 'left'
        },
        series: [
          {
            name: isHistory ? '历史流量' : '今日流量',
            type: 'pie',
            radius: '68%',
            // 隐藏指示线
            labelLine: {
              normal: {
                show: false
              }
            },
            label: {
              normal: {
                show: true,
                position: 'inner',
                fontSize: 10,
                color: '#fff',
                formatter: (data) => {
                  return `${data.name}${data.value > 1000 ? '：\n\n' : '：'}${data.value}`
                }
              }
            },
            data: [
              {
                value: this.dailyTrafficChart.upload,
                name: '上行'
              },
              {
                value: this.dailyTrafficChart.download,
                name: '下行'
              }
            ],
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      }
      option && this.myChart.setOption(option)
    }
  }
}
</script>
