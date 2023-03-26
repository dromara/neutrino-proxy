<template>
  <div :class="className" :id="chartId" :style="{height:height,width:width}"></div>
</template>

<script>
import echarts from 'echarts'
require('echarts/theme/macarons') // echarts theme
let that = null

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
      default: '220px'
    },
    data: {
      type: Object,
      default: () => {
        return {
          upFlowBytes: 0, // 上行
          downFlowBytes: 0, // 下行
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
    that = this
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
          subtext: '上行：' + this.data.upFlowDesc + '\n\n' + '下行：' + this.data.downFlowDesc + '\n\n' + '汇总：' + this.data.totalFlowDesc,
          textStyle: {
            fontSize: 18,
            fontWeight: 800,
            align: 'center'
          }
        },
        tooltip: {
          trigger: 'item',
          formatter: function(value) {
            return `${value.seriesName} <br/>${value.marker} : ${value.name} ${value.name === '上行' ? that.data.upFlowDesc : that.data.downFlowDesc}`
          }
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
            center: ['60%', '60%'],
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
                value: this.data.upFlowBytes,
                name: '上行',
                lineStyle: {
                  normal: {
                    color: '#63b2ee'
                  }
                },
                itemStyle: {
                  normal: {
                    color: '#63b2ee'
                  }
                }
              },
              {
                value: this.data.downFlowBytes,
                name: '下行',
                lineStyle: {
                  normal: {
                    color: '#76da91'
                  }
                },
                itemStyle: {
                  normal: {
                    color: '#76da91'
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
