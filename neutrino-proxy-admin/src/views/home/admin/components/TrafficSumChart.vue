<template>
  <div :class="className" id="traffic-sum-div" :style="{height:height,width:width}"></div>
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
      default: '380px'
    },
    licenseChart: {
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
      this.chartDom = document.getElementById('traffic-sum-div')
      this.myChart = echarts.init(this.chartDom)
      const option = {
        title: {
          text: '今日流量折线图',
          subtext: '当日0-24时'
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['上行', '下行'],
          left: 'right'
        },
        grid: {
          left: '2%',
          right: '2%',
          bottom: '2%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: ['1:00', '2:00', '3:00', '4:00', '5:00', '6:00', '7:00']
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '上行',
            type: 'line',
            stack: 'Total',
            data: [120, 132, 101, 134, 90, 230, 210]
          },
          {
            name: '下行',
            type: 'line',
            stack: 'Total',
            data: [220, 182, 191, 234, 290, 330, 310]
          }
        ]
      }
      option && this.myChart.setOption(option)
    }
  }
}
</script>
