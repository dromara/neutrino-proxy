<template>
  <div :class="className" :id="chartId" :style="{height:height,width:width}"></div>
</template>

<script>
import echarts from 'echarts'
require('echarts/theme/macarons') // echarts theme

export default {
  props: {
    chartId: {
      type: String,
      default: 'daily-traffic-div'
    },
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
      default: '250px'
    },
    data: {
      type: Object,
      default: () => {
        return {
          upload: 90, // 上行
          download: 100, // 下行
          text: '今日流量'
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
      this.chartDom = document.getElementById(this.chartId)
      this.myChart = echarts.init(this.chartDom)
      const option = {
        title: {
          text: this.data.text,
          subtext: '上行：' + this.data.upload + '\n\n' + '下行：' + this.data.download,
          textStyle: {
            fontSize: 18,
            fontWeight: 800,
            align: 'center'
          }
        },
        tooltip: {
          trigger: 'item'
        },
        legend: {
          data: ['上行', '下行'],
          orient: 'vertical',
          left: 'right'
        },
        series: [
          {
            name: this.data.text,
            type: 'pie',
            radius: [45, 65],
            center: ['50%', '58%'],
            // 隐藏指示线
            labelLine: {
              normal: {
                show: false
              }
            },
            // 隐藏圆环上文字
            label: {
              normal: {
                show: false,
                position: 'center'
              }
            },
            data: [
              {
                value: this.data.upload,
                name: '上行',
                lineStyle: {
                  normal: {
                    color: '#2B81B1'
                  }
                },
                itemStyle: {
                  normal: {
                    color: '#2B81B1'
                  }
                }
              },
              {
                value: this.data.download,
                name: '下行',
                lineStyle: {
                  normal: {
                    color: '#dbebf7'
                  }
                },
                itemStyle: {
                  normal: {
                    color: '#dbebf7'
                  }
                }
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
