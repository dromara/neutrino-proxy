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
      <el-table-column align="center" :label="$t('table.desc')" width="200">
        <template slot-scope="scope">
          <span>{{scope.row.desc}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.handler')" width="200">
        <template slot-scope="scope">
          <span>{{scope.row.handler}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.cron')" width="200">
        <template slot-scope="scope">
          <span>{{scope.row.cron}}</span>
        </template>
      </el-table-column>
      <el-table-column width="150px" align="center" :label="$t('table.createTime')">
        <template slot-scope="scope">
          <span>{{scope.row.createTime | parseTime('{y}-{m}-{d} {h}:{i}')}}</span>
        </template>
      </el-table-column>
      <el-table-column width="150px" align="center" :label="$t('table.updateTime')">
        <template slot-scope="scope">
          <span>{{scope.row.updateTime | parseTime('{y}-{m}-{d} {h}:{i}')}}</span>
        </template>
      </el-table-column>
      <el-table-column class-name="status-col" :label="$t('table.enableStatus')" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.enable | statusFilter">{{scope.row.enable | statusName}}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.actions')" width="230" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="primary" @click="handleExecuteClick(scope.row)">执行</el-button>
          <el-button v-if="scope.row.enable =='1'" size="mini" type="danger" @click="handleModifyStatus(scope.row,2)">{{'停止'}}</el-button>
          <el-button v-if="scope.row.enable =='2'" size="mini" type="success" @click="handleModifyStatus(scope.row,1)">{{'启动'}}</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination-container">
      <el-pagination background @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page.sync="listQuery.currentPage"
                     :page-sizes="[10,20,30, 50]" :page-size="listQuery.pageSize" layout="total, sizes, prev, pager, next, jumper" :total="total">
      </el-pagination>
    </div>

    <el-dialog title="执行" :visible.sync="executeVisible">
      <el-form ref="dataForm" :model="temp" label-position="left" label-width="70px">
        <el-form-item :label="$t('table.jobParam')" prop="param">
          <el-input v-model="temp.param" type="textarea" :rows="4" placeholder="请输入任务执行参数" :maxlength="200" show-word-limit style="padding-right: 20px"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="executeVisible = false">{{$t('table.cancel')}}</el-button>
        <el-button type="primary" @click="commitExecute">{{$t('table.confirm')}}</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
  import { fetchList, updateEnableStatus, execute } from '@/api/jobInfo'
  import waves from '@/directive/waves' // 水波纹指令

  export default {
    name: 'jobManager',
    directives: {
      waves
    },
    data() {
      return {
        tableKey: 0,
        list: null,
        total: null,
        listLoading: true,
        listQuery: {
          currentPage: 1,
          pageSize: 20,
          importance: undefined,
          title: undefined,
          type: undefined
        },
        temp: {
          id: '',
          param: ''
        },
        executeVisible: false
      }
    },
    filters: {
      statusName(status) {
        const statusMap = {
          1: '启动',
          2: '停止'
        }
        return statusMap[status]
      },
      statusFilter(status) {
        const statusMap = {
          1: 'success',
          2: 'danger'
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
        this.getList()
      },
      handleCurrentChange(val) {
        this.listQuery.currentPage = val
        this.getList()
      },
      handleModifyStatus(row, enable) {
        updateEnableStatus(row.id, enable).then(response => {
          if (response.data.data.code === 0) {
            this.$message({
              message: '操作成功',
              type: 'success'
            })
          }
          this.getList()
        })
      },
      handleExecuteClick(row) {
        this.temp.id = row.id
        this.temp.param = row.param
        this.executeVisible = true
      },
      commitExecute() {
        execute(this.temp).then(response => {
          if (response.data.code === 0) {
            this.executeVisible = false
            this.$message({
              message: '操作成功',
              type: 'success'
            })
          }
        })
      }
    }
  }
</script>
