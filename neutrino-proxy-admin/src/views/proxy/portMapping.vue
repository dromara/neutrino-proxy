<template>
  <div class="app-container calendar-list-container">
    <div class="filter-container">
      <el-button class="filter-item" style="margin-left: 10px;" @click="handleCreate" type="primary" icon="el-icon-edit">{{$t('table.add')}}</el-button>
    </div>

    <el-table :key='tableKey' :data="list" v-loading="listLoading" element-loading-text="给我一点时间" border fit highlight-current-row
              style="width: 100%">
      <el-table-column align="center" :label="$t('table.id')" width="50">
        <template slot-scope="scope">
          <span>{{scope.row.id}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.userName')" width="100">
        <template slot-scope="scope">
          <span>{{scope.row.userName}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.licenseName')" width="150">
        <template slot-scope="scope">
          <span>{{scope.row.licenseName}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.serverPort')" width="200">
        <template slot-scope="scope">
          <span>{{scope.row.serverPort}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.proxyClient')" width="200">
        <template slot-scope="scope">
          <span>{{scope.row.clientIp}}:{{scope.row.clientPort}}</span>
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
      <el-table-column class-name="status-col" :label="$t('table.isOnline')" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.isOnline | statusFilter">{{scope.row.isOnline | isOnlineName}}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.actions')" width="230" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="handleUpdate(scope.row)">{{$t('table.edit')}}</el-button>
          <el-button v-if="scope.row.enable =='1'" size="mini" type="danger" @click="handleModifyStatus(scope.row,2)">{{$t('table.disable')}}</el-button>
          <el-button v-if="scope.row.enable =='2'" size="mini" type="success" @click="handleModifyStatus(scope.row,1)">{{$t('table.enable')}}</el-button>
          <!--          <el-button size="mini" type="danger" @click="handleDelete(scope.row,'deleted')">{{$t('table.delete')}}</el-button>-->
          <ButtonPopover @handleCommitClick="handleDelete2(scope.row)" style="margin-left: 10px"/>

        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-container">
      <el-pagination background @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page.sync="listQuery.currentPage"
                     :page-sizes="[10,20,30, 50]" :page-size="listQuery.pageSize" layout="total, sizes, prev, pager, next, jumper" :total="total">
      </el-pagination>
    </div>

    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">
      <el-form :rules="rules" ref="dataForm" :model="temp" label-position="left" label-width="120px" style='width: 400px; margin-left:50px;'>
        <el-form-item :label="$t('License')" prop="licenseId">
          <el-select style="width: 280px;" class="filter-item" v-model="temp.licenseId" placeholder="请选择" :disabled="dialogStatus=='update'">
            <el-option v-for="item in licenseList" :key="item.id" :label="item.name" :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item :label="$t('License')" prop="licenseId">
          <DropdownTable
            v-model="temp.licenseId"
            :name.sync="temp.licenseId"
            :tableData= "licenseList"
            @selectedData="selectedFeeItem"
            placeholder="请选择"
          />

        </el-form-item>
        <el-form-item :label="$t('服务端端口')" prop="serverPort">
          <el-select style="width: 280px;" class="filter-item" v-model="temp.serverPort" placeholder="请选择">
            <el-option v-for="item in serverPortList" :key="item.port" :label="item.port" :value="item.port">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('客户端IP')" prop="clientIp">
          <el-input v-model="temp.clientIp"></el-input>
        </el-form-item>
        <el-form-item :label="$t('客户端端口')" prop="clientPort">
          <el-input v-model="temp.clientPort"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">{{$t('table.cancel')}}</el-button>
        <el-button v-if="dialogStatus=='create'" type="primary" @click="createData">{{$t('table.confirm')}}</el-button>
        <el-button v-else type="primary" @click="updateData">{{$t('table.confirm')}}</el-button>
      </div>
    </el-dialog>

    <el-dialog title="Reading statistics" :visible.sync="dialogPvVisible">
      <el-table :data="pvData" border fit highlight-current-row style="width: 100%">
        <el-table-column prop="key" label="Channel"> </el-table-column>
        <el-table-column prop="pv" label="Pv"> </el-table-column>
      </el-table>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="dialogPvVisible = false">{{$t('table.confirm')}}</el-button>
      </span>
    </el-dialog>

  </div>
</template>

<script>
  import { fetchList, createUserPortMapping, updateUserPortMapping, updateEnableStatus, deletePortMapping } from '@/api/portMapping'
  import { portPoolList } from '@/api/portPool'
  import waves from '@/directive/waves' // 水波纹指令
  import { parseTime } from '@/utils'
  import ButtonPopover from '../../components/Button/buttonPopover'
  import DropdownTable from '../../components/Dropdown/DropdownTable'

  const calendarTypeOptions = [
    { key: 'CN', display_name: 'China' },
    { key: 'US', display_name: 'USA' },
    { key: 'JP', display_name: 'Japan' },
    { key: 'EU', display_name: 'Eurozone' }
  ]

  // arr to obj ,such as { CN : "China", US : "USA" }
  const calendarTypeKeyValue = calendarTypeOptions.reduce((acc, cur) => {
    acc[cur.key] = cur.display_name
    return acc
  }, {})

  export default {
    name: 'complexTable',
    directives: {
      waves
    },
    components: {
      DropdownTable,
      ButtonPopover
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
        importanceOptions: [1, 2, 3],
        calendarTypeOptions,
        sortOptions: [{ label: 'ID Ascending', key: '+id' }, { label: 'ID Descending', key: '-id' }],
        statusOptions: ['published', 'draft', 'deleted'],
        licenseList: [
          { id: 1, name: '我的Mac' },
          { id: 2, name: '我的台式机' }
        ],
        serverPortList: [],
        showReviewer: false,
        temp: {
          id: undefined,
          licenseId: undefined,
          serverPort: undefined,
          clientIp: undefined,
          clientPort: undefined
        },
        dialogFormVisible: false,
        dialogStatus: '',
        textMap: {
          update: '编辑',
          create: '新建'
        },
        dialogPvVisible: false,
        pvData: [],
        rules: {
          licenseId: [{ required: true, message: '请选择License', trigger: 'blur' }],
          serverPort: [{ required: true, message: '请输入服务端端口', trigger: 'blur' }],
          clientIp: [{ required: true, message: '请输入客户端IP', trigger: 'blur' }],
          clientPort: [{ required: true, message: '请输入客户端端口', trigger: 'blur' }]
        },
        downloadLoading: false
      }
    },
    filters: {
      statusName(status) {
        const statusMap = {
          1: '启用',
          2: '禁用'
        }
        return statusMap[status]
      },
      isOnlineName(isOnline) {
        const isOnlineMap = {
          1: '在线',
          2: '离线'
        }
        return isOnlineMap[isOnline]
      },
      statusFilter(status) {
        const statusMap = {
          1: 'success',
          2: 'danger'
        }
        return statusMap[status]
      },
      typeFilter(type) {
        return calendarTypeKeyValue[type]
      }
    },
    created() {
      this.getList()
      this.getPortPoolList()
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
      getPortPoolList() {
        portPoolList().then(response => {
          this.serverPortList = response.data.data
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
        console.log('route', this.$route)
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
      resetTemp() {
        this.temp = {
          id: undefined,
          importance: 1,
          remark: '',
          timestamp: new Date(),
          title: '',
          status: 'published',
          type: ''
        }
      },
      handleCreate() {
        this.resetTemp()
        this.dialogStatus = 'create'
        this.dialogFormVisible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].clearValidate()
        })
      },
      createData() {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            createUserPortMapping(this.temp).then(response => {
              if (response.data.code === 0) {
                this.dialogFormVisible = false
                this.$notify({
                  title: '成功',
                  message: '创建成功',
                  type: 'success',
                  duration: 2000
                })
                this.getList()
              }
            })
          }
        })
      },
      handleUpdate(row) {
        this.temp = Object.assign({}, row) // copy obj
        this.temp.timestamp = new Date(this.temp.timestamp)
        this.dialogStatus = 'update'
        this.dialogFormVisible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].clearValidate()
        })
      },
      updateData() {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            const tempData = Object.assign({}, this.temp)
            updateUserPortMapping(tempData).then(response => {
              if (response.data.code === 0) {
                // this.$message({
                //   message: '操作成功',
                //   type: 'success'
                // })
                this.$notify({
                  title: '成功',
                  message: '更新成功',
                  type: 'success',
                  duration: 2000
                })
                this.dialogFormVisible = false
                this.getList()
              }
            })
          }
        })
      },
      selectedFeeItem(list, index) {
        console.log(list, index)
      },
      handleDelete(row) {
        this.$confirm('确定要删除吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          deletePortMapping(row.id).then(response => {
            if (response.data.code === 0) {
              this.$notify({
                title: '成功',
                message: '删除成功',
                type: 'success',
                duration: 2000
              })
              this.getList()
            }
          })
        }).catch(() => {})
      },
      handleDelete2(row) {
        deletePortMapping(row.id).then(response => {
          if (response.data.code === 0) {
            this.$notify({
              title: '成功',
              message: '删除成功',
              type: 'success',
              duration: 2000
            })
            this.getList()
          }
        })
      },
      handleDownload() {
        this.downloadLoading = true
        import('@/vendor/Export2Excel').then(excel => {
          const tHeader = ['timestamp', 'title', 'type', 'importance', 'status']
          const filterVal = ['timestamp', 'title', 'type', 'importance', 'status']
          const data = this.formatJson(filterVal, this.list)
          excel.export_json_to_excel(tHeader, data, 'table-list')
          this.downloadLoading = false
        })
      },
      formatJson(filterVal, jsonData) {
        return jsonData.map(v => filterVal.map(j => {
          if (j === 'timestamp') {
            return parseTime(v[j])
          } else {
            return v[j]
          }
        }))
      }
    }
  }
</script>
