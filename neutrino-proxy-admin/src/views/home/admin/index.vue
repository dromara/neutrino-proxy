<template>
  <div class="dashboard-editor">
    <div class="container">
      <el-row class="pie-chart" :gutter="16">
        <el-col :span="6">
          <el-card class="box-card">
            <license-chart :data="licenseChart" v-if="licenseChart"/>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="box-card">
            <port-mapping-chart :data="portMappingChart" v-if="portMappingChart"/>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="box-card">
            <daily-traffic-chart :data="dailyTrafficChart" :chartId="'daily-traffic-div'" v-if="dailyTrafficChart"/>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="box-card">
            <daily-traffic-chart :data="historyTrafficChart" :chartId="'history-traffic-div'" v-if="historyTrafficChart"/>
          </el-card>
        </el-col>
      </el-row>
      <el-row class="line-chart" :gutter="16">
        <el-col :span="24">
          <el-card>
            <traffic-sum-chart :data="trafficSumChart" :chartId="'traffic-sum-div1'" v-if="trafficSumChart"/>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>

import LicenseChart from './components/LicenseChart'
import PortMappingChart from './components/PortMappingChart'
import DailyTrafficChart from './components/DailyTrafficChart'
import TrafficSumChart from './components/TrafficSumChart'
import { homeData } from '@/api/report'

export default {
  name: 'dashboard-admin',
  components: { TrafficSumChart, DailyTrafficChart, PortMappingChart, LicenseChart },
  data() {
    return {
      licenseChart: undefined,
      portMappingChart: undefined,
      dailyTrafficChart: undefined,
      historyTrafficChart: undefined,
      trafficSumChart: undefined
    }
  },
  created() {
    this.getHomeData()
  },
  mounted() {
  },
  methods: {
    getHomeData() {
      homeData().then(res => {
        this.licenseChart = res.data.data.license
        this.portMappingChart = res.data.data.portMapping
        this.dailyTrafficChart = Object.assign(res.data.data.todayFlow, { text: '今日流量' })
        this.historyTrafficChart = Object.assign(res.data.data.totalFlow, { text: '流量汇总' })
        this.trafficSumChart = this.getChartData(res.data.data.last7dFlow)
      })
    },
    getChartData(last7dFlow) {
      const list = []
      last7dFlow.seriesList.forEach(item => {
        list.push({
          name: item.seriesName,
          value: item.seriesData
        })
      })
      return {
        text: '流量监控',
        subtext: `最近${last7dFlow.dataList.length || 0}天流量监控`,
        title: last7dFlow.xDate,
        list: list,
        legendList: last7dFlow.legendData
      }
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.dashboard-editor{
  min-height: calc(100vh - 85px);
  padding: 16px;
  background-color: #f5f7fa;
  .container {
    display: flex;
    flex-direction: column;
    .line-chart, .pie-chart{
      flex: 0 0 auto;
    }
    .pie-chart{
      padding-bottom: 16px;
      .el-card{
        min-height: 250px;
      }
    }
  }
}

</style>
