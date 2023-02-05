<template>
  <div class="dashboard-editor-container">
    <el-row :gutter="8">
      <el-col :span="12">
        <el-card class="box-card">
          <el-row>
            <el-col :span="24">
              <el-row>
                <el-col :span="12">
                  <license-chart :licenseChart="echartsData.licenseChart"/>
                </el-col>
                <el-col :span="12">
                  <port-mapping-chart :portMappingChart="echartsData.portMappingChart"/>
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="12">
                  <daily-traffic-chart :dailyTrafficChart="echartsData.dailyTrafficChart"/>
                </el-col>
                <el-col :span="12">
                  <daily-traffic-chart :dailyTrafficChart="echartsData.historyTrafficChart"/>
                </el-col>
              </el-row>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="box-card">
          <el-col :span="24">
            <table-chart :tableChart="echartsData.tableChart"/>
          </el-col>
        </el-card>
      </el-col>
    </el-row>
    <el-row class="line-chart">
      <el-col :span="24">
        <el-card>
          <traffic-sum-chart/>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>

import LicenseChart from './components/LicenseChart'
import PortMappingChart from './components/PortMappingChart'
import DailyTrafficChart from './components/DailyTrafficChart'
import TableChart from './components/TableChart'
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
    isHistory: false
  },
  historyTrafficChart: {
    upload: 4100,
    download: 9520,
    isHistory: true
  },
  tableChart: [
    { date: '2016-05-02', name: '王小虎', address: '上海市普陀区金沙江路 1518 弄' },
    { date: '2016-05-04', name: '王小虎', address: '上海市普陀区金沙江路 1517 弄' },
    { date: '2016-05-01', name: '王小虎', address: '上海市普陀区金沙江路 1519 弄' },
    { date: '2016-05-03', name: '王小虎', address: '上海市普陀区金沙江路 1516 弄' },
    { date: '2016-05-04', name: '王小虎', address: '上海市普陀区金沙江路 1517 弄' }
  ]
}

export default {
  name: 'dashboard-admin',
  components: { TrafficSumChart, TableChart, DailyTrafficChart, PortMappingChart, LicenseChart },
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
.dashboard-editor-container {
  min-height: calc(100vh - 85px);
  padding: 16px;
  background-color: rgb(240, 242, 245);
  .line-chart {
    margin-top: 16px;
  }
}
</style>
