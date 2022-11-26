<template>
  <div class="app-container calendar-list-container">
    <div class="filter-container">
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
      <el-table-column align="center" :label="$t('table.ip')" min-width="120">
        <template slot-scope="scope">
          <span>{{scope.row.ip}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.type')" min-width="90">
        <template slot-scope="scope">
          <el-tag :type="scope.row.type | statusFilter">{{scope.row.type | statusName}}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.msg')" min-width="200" show-overflow-tooltip>
        <template slot-scope="scope">
          <span>{{scope.row.msg}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.outcome')" min-width="90">
        <template slot-scope="scope">
          <el-tag :type="scope.row.code | statusFilter">{{scope.row.code | outcomeName}}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.err')" min-width="200" show-overflow-tooltip>
        <template slot-scope="scope">
          <span>{{scope.row.err}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.happendTime')" min-width="150">
        <template slot-scope="scope">
          <span>{{scope.row.createTime | parseTime('{y}-{m}-{d} {h}:{i}')}}</span>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination-container">
      <el-pagination background @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page.sync="listQuery.currentPage"
                     :page-sizes="[10,20,30, 50]" :page-size="listQuery.pageSize" layout="total, sizes, prev, pager, next, jumper" :total="total">
      </el-pagination>
    </div>

    <el-dialog
      title="调度日志"
      :visible.sync="dialogVisible"
      width="700px"
      :before-close="() => this.dialogVisible = false">
      <div class="job-msg-div">{{selectRow.msg}}</div>
      <div slot="footer" class="dialog-footer"></div>
    </el-dialog>
  </div>
</template>

<script>
import { fetchList } from '@/api/clientConnectLog'
import waves from '@/directive/waves' // 水波纹指令

export default {
  name: 'clientConnectLog',
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
        pageSize: 10,
        jobId: undefined
      },
      dialogVisible: false,
      selectRow: {}
    }
  },
  filters: {
    statusName(status) {
      const statusMap = {
        1: '连接',
        2: '断开'
      }
      return statusMap[status]
    },
    statusFilter(status) {
      const statusMap = {
        1: 'success',
        2: 'danger'
      }
      return statusMap[status]
    },
    outcomeName(status) {
      const statusMap = {
        1: '成功',
        2: '失败'
      }
      return statusMap[status]
    }
  },
  created() {
    this.getList()
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
      this.listQuery.currentPage = 1
      this.getList()
    },
    handleCurrentChange(val) {
      this.listQuery.currentPage = val
      this.getList()
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
