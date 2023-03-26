<template>
  <div class="app-container calendar-list-container">
    <div class="filter-container">
      <el-select v-model="listQuery.userId" placeholder="请选择用户" clearable>
        <el-option v-for="item in userList" :key="item.id" :label="item.name" :value="item.id"/>
      </el-select>
      <el-button class="filter-item" type="primary" v-waves icon="el-icon-search" @click="handleFilter">{{$t('table.search')}}</el-button>
    </div>

    <el-table :key='tableKey' :data="list" v-loading="listLoading" element-loading-text="给我一点时间" border fit highlight-current-row style="width: 100%">
      <el-table-column type="index" width="100" :label="$t('table.id')"></el-table-column>
      <el-table-column align="center" :label="$t('table.userName')" min-width="200">
        <template slot-scope="scope">
          <span>{{scope.row.userName}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.ip')" min-width="200">
        <template slot-scope="scope">
          <span>{{scope.row.ip}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.type')" min-width="120">
        <template slot-scope="scope">
          <el-tag :type="scope.row.type | typeFilter">{{scope.row.type | typeName}}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.happendTime')" min-width="150">
        <template slot-scope="scope">
          <span>{{scope.row.createTime | parseTime('{y}-{m}-{d} {h}:{i}')}}</span>
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
import { fetchList } from '@/api/loginLog'
import { userList } from '@/api/user'
import waves from '@/directive/waves' // 水波纹指令

export default {
  name: 'loginLog',
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
        userId: null
      },
      userList: []
    }
  },
  filters: {
    statusName(status) {
      const statusMap = {
        0: '成功',
        1: '失败'
      }
      return statusMap[status]
    },
    statusFilter(status) {
      const statusMap = {
        0: 'success',
        1: 'danger'
      }
      return statusMap[status]
    },
    typeName(type) {
      const typeMap = {
        1: '登录',
        2: '登出'
      }
      return typeMap[type]
    },
    typeFilter(type) {
      const typeMap = {
        1: 'success',
        2: 'danger'
      }
      return typeMap[type]
    },
    alarmStatusFilter(status) {
      const statusMap = {
        0: 'success',
        1: 'warning',
        2: 'success',
        3: 'danger'
      }
      return statusMap[status]
    }
  },
  created() {
    this.getList()
    this.getDataList()
  },
  activated() {
    this.getUserList()
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
    getUserList() {
      userList().then(response => {
        this.userList = response.data.data
      })
    },
    getDataList() {
      const loginName = this.$store.state.user.loginName
      userList().then(response => {
        this.userList = response.data.data
        const curUser = this.userList.find((val) => val.loginName === loginName)
        if (curUser) {
          this.listQuery.userId = curUser.id
        }
        this.getList()
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
    }
  }
}
</script>
