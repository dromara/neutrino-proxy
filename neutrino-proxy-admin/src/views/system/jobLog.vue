<template>
  <div class="app-container calendar-list-container">
    <div class="filter-container">
      <el-button class="filter-item" type="primary" v-waves icon="el-icon-search" @click="handleFilter">{{$t('table.search')}}</el-button>
    </div>

    <el-table :key='tableKey' :data="list" v-loading="listLoading" element-loading-text="给我一点时间" border fit highlight-current-row style="width: 100%">
      <el-table-column align="center" :label="$t('table.id')" width="100">
        <template slot-scope="scope">
          <span>{{scope.row.id}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.handler')" width="200">
        <template slot-scope="scope">
          <span>{{scope.row.handler}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.jobParam')" width="200">
        <template slot-scope="scope">
          <span>{{scope.row.param}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.jobLogCode')" width="200">
        <template slot-scope="scope">
          <span>{{scope.row.code}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.jobLogMsg')" width="400">
        <template slot-scope="scope">
          <span>{{scope.row.msg}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.alarmStatus')" width="200">
        <template slot-scope="scope">
          <span>{{scope.row.alarmStatus}}</span>
        </template>
      </el-table-column>
      <el-table-column width="150px" align="center" :label="$t('table.createTime')">
        <template slot-scope="scope">
          <span>{{scope.row.createTime | parseTime('{y}-{m}-{d} {h}:{i}')}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.actions')" min-width="120">
        <template slot-scope="scope">
          <el-button size="mini" type="primary" @click="handleShowClick(scope.row)">查看日志</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination-container">
      <el-pagination background @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page.sync="listQuery.currentPage"
                     :page-sizes="[10,20,30, 50]" :page-size="listQuery.pageSize" layout="total, sizes, prev, pager, next, jumper" :total="total">
      </el-pagination>
    </div>
  </div>
</template>

<script>
import { fetchList } from '@/api/jobLog'
import waves from '@/directive/waves' // 水波纹指令

export default {
  name: 'jobLog',
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
        currentPage: 1,
        pageSize: 20,
        jobId: undefined
      }
    }
  },
  filters: {
    statusFilter(status) {
      const statusMap = {
        1: 'success',
        2: 'danger'
      }
      return statusMap[status]
    }
  },
  created() {
    // this.getList()
  },
  activated() {
    this.listQuery.jobId = this.$route.query.jobId
    // this.getList()
  },
  methods: {
    getList() {
      this.listLoading = true
      fetchList(this.listQuery).then(response => {
        this.list = response.data.data.records
        this.total = response.data.data.total
        this.listLoading = false
      })
    },
    handleFilter() {
      this.listQuery.currentPage = 1
      this.getList()
    },
    handleSizeChange(val) {
      this.listQuery.pageSize = val
      this.getList()
    },
    handleCurrentChange(val) {
      this.listQuery.currentPage = val
      this.getList()
    },
    handleShowClick(row) {
      console.log(row)
    }
  }
}
</script>
