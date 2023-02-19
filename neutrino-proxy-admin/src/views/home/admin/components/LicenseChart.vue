<template>
  <div>
    <div :class="className" id="license-div" :style="{height:height,width:width}"></div>
  </div>
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
      default: '250px'
    },
    data: {
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
      this.chartDom = document.getElementById('license-div')
      this.myChart = echarts.init(this.chartDom)
      const option = {
        title: {
          text: 'License统计',
          subtext: '在线数：' + this.data.onLine + '\n\n' + '离线数：' + (this.data.total - this.data.onLine),
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
          data: ['在线数', '离线数'],
          orient: 'vertical',
          left: 'right'
        },
        series: [
          {
            // 第一张圆环
            name: 'License',
            type: 'pie',
            radius: [45, 65],
            center: ['50%', '58%'],
            avoidLabelOverlap: false,
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
            emphasis: {
              itemStyle: {
                label: {
                  show: true,
                  fontSize: 40,
                  fontWeight: 'bold'
                }
              }
            },
            data: [
              // value当前进度 + 颜色
              {
                name: '在线数',
                value: this.data.onLine,
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
                name: '离线数',
                value: this.data.total - this.data.onLine,
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
