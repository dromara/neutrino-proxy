<template>
  <div class="app-container calendar-list-container">
    <div class="filter-container">
      <el-select v-model="listQuery.userId" placeholder="请选择用户" clearable>
        <el-option v-for="item in userList" :key="item.id" :label="item.name" :value="item.id"/>
      </el-select>
      <el-button type="primary" v-waves icon="el-icon-search" @click="handleFilter">{{$t('table.search')}}</el-button>
    </div>

    <el-table :key='tableKey' :data="list" v-loading="listLoading" element-loading-text="给我一点时间" border fit highlight-current-row style="width: 100%">
      <el-table-column type="index" width="100" :label="$t('table.id')"></el-table-column>
      <el-table-column align="center" :label="$t('table.userName')" min-width="120">
        <template slot-scope="scope">
          <span>{{scope.row.userName}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.upFlow')" min-width="120">
        <template slot-scope="scope">
          <span>{{scope.row.upFlowDesc}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.downFlow')" min-width="120">
        <template slot-scope="scope">
          <span>{{scope.row.downFlowDesc}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.totalFlow')" min-width="120">
        <template slot-scope="scope">
          <span>{{scope.row.totalFlowDesc}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.actions')" min-width="230" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleFlowMonthReportClick(scope.row)">流量月度明细</el-button>
          <el-button size="mini" type="text" @click="handleLicenseFlowMonthReportClick(scope.row)">License流量月度明细</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination-container">
      <el-pagination background @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-pageInfo.sync="listQuery.current"
                     :pageInfo-sizes="[10,20,30, 50]" :pageInfo-size="listQuery.size" layout="total, sizes, prev, pager, next, jumper" :total="total">
      </el-pagination>
    </div>
  </div>
</template>

<script>
import { fetchUserFlowReportList } from '@/api/report'
import { userList } from '@/api/user'
import waves from '@/directive/waves' // 水波纹指令

export default {
  name: 'userFlowReport',
  directives: {
    waves
  },
  data() {
    return {
      tableKey: 0,
      list: null,
      total: null,
      listLoading: false,
      listQuery: {
        current: 1,
        size: 10,
        userId: undefined
      },
      userList: [],
      dialogVisible: false,
      selectRow: {}
    }
  },
  filters: {
  },
  created() {
    this.getList()
    this.getUserList()
  },
  activated() {
    this.getUserList()
    if (this.$route.query.userId) {
      this.listQuery.userId = this.$route.query.userId
      this.getList()
    }
  },
  methods: {
    getList() {
      this.listLoading = true
      fetchUserFlowReportList(this.listQuery).then(response => {
        this.list = response.data.data.records
        this.total = response.data.data.total
        this.listLoading = false
      })
    },
    getUserList() {
      userList().then(response => {
        this.userList = response.data.data
      })
    },
    handleFilter() {
      this.listQuery.current = 1
      this.getList()
    },
    handleSizeChange(val) {
      this.listQuery.size = val
      this.listQuery.current = 1
      this.getList()
    },
    handleCurrentChange(val) {
      this.listQuery.current = val
      this.getList()
    },
    handleShowClick(row) {
      console.log(row)
    },
    handleLookOver(row) {
      this.selectRow = row
      this.dialogVisible = true
    },
    handleFlowMonthReportClick(row) {
      this.$router.push({ path: '/report/userFlowMonthReport', query: { userId: row.userId }})
    },
    handleLicenseFlowMonthReportClick(row) {
      this.$router.push({ path: '/report/licenseFlowMonthReport', query: { userId: row.userId }})
    }
  }
}
</script>

<style>
.job-msg-div{
  max-height: 400px;
  overflow-y: auto;
}
</style>
