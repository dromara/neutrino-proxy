<template>
  <div class="dashboard-editor">
    <div class="log-div">

    </div>
    <div class="container">
      <el-row class="line-chart" :gutter="16">
        <el-col :span="12">
          <el-card>
            <traffic-sum-chart :data="echartsData.trafficSumChart" :chartId="'traffic-sum-div1'"/>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card>
            <traffic-sum-chart :data="echartsData.trafficSumChart2"/>
          </el-card>
        </el-col>
      </el-row>
      <el-row class="pie-chart" :gutter="16">
        <el-col :span="6">
          <el-card class="box-card">
            <license-chart :data="echartsData.licenseChart"/>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="box-card">
            <port-mapping-chart :data="echartsData.portMappingChart"/>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="box-card">
            <daily-traffic-chart :data="echartsData.dailyTrafficChart" :chartId="'daily-traffic-div'"/>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="box-card">
            <daily-traffic-chart :data="echartsData.historyTrafficChart" :chartId="'history-traffic-div'"/>
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

const echartsData = {
  licenseChart: {
    total: 100,
    onLine: 80
  },
  portMappingChart: {
    total: 100,
    onLine: 25
  },
  dailyTrafficChart: {
    upload: 300,
    download: 680,
    text: '今日流量'
  },
  historyTrafficChart: {
    upload: 4100,
    download: 9520,
    text: '历史流量'
  },
  trafficSumChart: {
    text: '今日流量',
    subtext: '当日0-24时',
    title: ['1:00', '2:00', '3:00', '4:00', '5:00', '6:00', '7:00'],
    list: [
      {
        name: '上行',
        value: [120, 132, 101, 134, 90, 230, 210]
      },
      {
        name: '下行',
        value: [112, 333, 150, 60, 99, 50, 102]
      }
    ]
  },
  trafficSumChart2: {
    text: '历史流量',
    title: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'],
    list: [
      {
        name: '上行',
        value: [132, 101, 134, 90, 120, 132, 101, 134, 90, 230, 210, 120]
      },
      {
        name: '下行',
        value: [111, 0, 50, 101, 134, 90, 230, 120, 132, 101, 60, 20]
      }
    ]
  }
}

export default {
  name: 'dashboard-admin',
  components: { TrafficSumChart, DailyTrafficChart, PortMappingChart, LicenseChart },
  data() {
    return {
      echartsData: echartsData
    }
  },
  mounted() {
  },
  methods: {

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
      padding-top: 16px;
      .el-card{
        min-height: 300px;
      }
    }
  }
}

</style>
