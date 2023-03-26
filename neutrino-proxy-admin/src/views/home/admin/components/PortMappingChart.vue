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
      default: '220px'
    },
    data: {
      type: Object,
      default: () => {
        return {
          onlineCount: 0, // 进度条最大值
          totalCount: 0 // 当前进度
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
          text: '端口映射统计',
          subtext: '在线数量：' + this.data.onlineCount + '\n\n' + '离线数量：' + (this.data.totalCount - this.data.onlineCount) + '\n\n' + '汇总数量：' + this.data.totalCount,
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
            name: '端口映射',
            type: 'pie',
            radius: [45, 65],
            center: ['60%', '60%'],
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
            data: [
              // value当前进度 + 颜色
              {
                name: '在线数',
                value: this.data.onlineCount,
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
                name: '离线数',
                value: this.data.totalCount - this.data.onlineCount,
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
