<template>
  <div class="app-container calendar-list-container">
    <div class="filter-container">
      <el-select v-model="listQuery.userId" placeholder="请选择用户" clearable>
        <el-option v-for="item in userList" :key="item.id" :label="item.name" :value="item.id"/>
      </el-select>
      <el-select v-model="listQuery.licenseId" placeholder="请选择License" clearable>
        <el-option v-for="item in licenseList" :key="item.id" :label="item.name" :value="item.id"/>
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
      <el-table-column align="center" :label="$t('table.licenseName')" min-width="120">
        <template slot-scope="scope">
          <span>{{scope.row.licenseName}}</span>
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
      <el-table-column width="150px" align="center" :label="$t('table.date')">
        <template slot-scope="scope">
          <span>{{scope.row.date | parseTime('{y}-{m}')}}</span>
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
import { fetchLicenseFlowMonthReportList } from '@/api/report'
import { userList } from '@/api/user'
import waves from '@/directive/waves'
import { licenseList } from '@/api/license'

export default {
  name: 'licenseFlowMonthReport',
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
        userId: undefined,
        licenseId: undefined
      },
      userList: [],
      licenseList: [],
      dialogVisible: false,
      selectRow: {}
    }
  },
  filters: {
  },
  created() {
    this.getList()
    this.getUserList()
    this.getLicenseList()
  },
  activated() {
    this.getUserList()
    this.getLicenseList()
    if (this.$route.query.userId) {
      this.listQuery.userId = this.$route.query.userId
    }
    if (this.$route.query.licenseId) {
      this.listQuery.licenseId = this.$route.query.licenseId
    }
    this.getList()
  },
  methods: {
    getList() {
      this.listLoading = true
      fetchLicenseFlowMonthReportList(this.listQuery).then(response => {
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
    getLicenseList() {
      licenseList().then(response => {
        this.licenseList = response.data.data
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
