<template>
  <div :class="className" :id="chartId" :style="{height:height,width:width}"></div>
</template>

<script>
import echarts from 'echarts'
require('echarts/theme/macarons') // echarts theme
import { getSizeDescByByteCount } from '@/utils/utils'
export default {
  props: {
    chartId: {
      type: String,
      default: 'traffic-sum-div'
    },
    data: {
      type: Object,
      default: {}
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
      default: '500px'
    }
  },
  data() {
    return {
      chartDom: null,
      colorList: ['#63b2ee', '#76da91', '#f8cb7f', '#f89588', '#7cd6cf', '#9192ab', '#7898e1', '#7898e1']
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
      const seriesList = []
      this.data.list && this.data.list.forEach((item, index) => {
        seriesList.push({
          name: item.name,
          type: 'line',
          // stack: 'Total',
          data: item.value,
          areaStyle: {
            normal: {
              color: this.colorList[index] + '1f'
            }
          },
          lineStyle: {
            normal: {
              color: this.colorList[index]
            }
          },
          itemStyle: {
            normal: {
              color: this.colorList[index]
            }
          },
          smooth: true
        })
      })

      const option = {
        title: {
          text: this.data.text,
          subtext: this.data.subtext || ''
        },
        tooltip: {
          trigger: 'axis',
          formatter: (value) => {
            let title = this.data.text + '<br/>'
            value.forEach(item => {
              title = title + item.marker + item.seriesName + ' : ' + getSizeDescByByteCount(item.data) + '<br/>'
            })
            return title
          }
        },
        legend: {
          data: this.data.legendList,
          left: 'right'
        },
        grid: {
          left: '0',
          right: '3%',
          bottom: '2%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: this.data.title
        },
        yAxis: {
          type: 'value',
          axisLabel: {
            formatter: (value) => {
              return getSizeDescByByteCount(value)
            }
          }
        },
        series: seriesList
      }
      option && this.myChart.setOption(option)
    }
  }
}
</script>
