<template>
  <div class="app-container calendar-list-container">
    <div class="filter-container" style="display:flex">
      <el-select v-model="listQuery.userId" placeholder="请选择用户" filterable clearable style="margin-right:10px;width: 120px;">
        <el-option v-for="item in userList" :key="item.id" :label="item.name" :value="item.id"/>
      </el-select>
      <el-select v-model="listQuery.enable" placeholder="请选择启用状态" clearable style="width:145px;margin-right:10px">
        <el-option v-for="item in selectObj.statusOptions" :key="item.value" :label="item.label" :value="item.value"/>
      </el-select>
      <el-button class="filter-item" type="primary" v-waves icon="el-icon-search" @click="handleFilter">{{$t('table.search')}}</el-button>
      <el-button class="filter-item" style="margin-left: 10px;" @click="handleCreate" type="primary" icon="el-icon-edit">{{$t('table.add')}}</el-button>
    </div>

    <el-table :key='tableKey' :data="list" v-loading="listLoading" element-loading-text="给我一点时间" border fit highlight-current-row
              style="width: 100%">
      <el-table-column align="center" :label="$t('table.id')" width="50">
        <template slot-scope="scope">
          <span>{{scope.row.id}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.userName')" width="150">
        <template slot-scope="scope">
          <span>{{scope.row.userName}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.domain')" width="300">
        <template slot-scope="scope">
          <span>{{scope.row.domain}}</span>
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
      <el-table-column class-name="status-col" :label="$t('SSL证书状态')" width="110">
        <template slot-scope="scope">
          <el-tag :type="scope.row.sslStatus | statusFilter">{{scope.row.sslStatus | sslStatusName}}</el-tag>
        </template>
      </el-table-column>
      <el-table-column class-name="status-col" :label="$t('默认域名')" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.isDefault | statusFilter">{{scope.row.isDefault | httpStatusName}}</el-tag>
        </template>
      </el-table-column>
      <el-table-column class-name="status-col" :label="$t('强制HTTPS')" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.forceHttps | statusFilter">{{scope.row.forceHttps | httpStatusName}}</el-tag>
        </template>
      </el-table-column>
      <el-table-column class-name="status-col" :label="$t('table.enableStatus')" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.enable | statusFilter">{{scope.row.enable | statusName}}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.actions')" width="330" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button v-if="scope.row.isDefault !='1' && scope.row.enable == '1'" size="mini" type="primary" @click="handleDefaultStatus(scope.row)">{{$t('默认')}}</el-button>

          <el-button type="primary" size="mini" @click="handleUpdate(scope.row)">{{$t('table.edit')}}</el-button>
          <el-button v-if="scope.row.enable =='1'" size="mini" type="danger" @click="handleModifyStatus(scope.row,2)">{{$t('table.disable')}}</el-button>
          <el-button v-if="scope.row.enable =='2'" size="mini" type="success" @click="handleModifyStatus(scope.row,1)">{{$t('table.enable')}}</el-button>
          <!--          <el-button size="mini" type="danger" @click="handleDelete(scope.row,'deleted')">{{$t('table.delete')}}</el-button>-->
          <ButtonPopover @handleCommitClick="handleDelete2(scope.row)" style="margin-left: 10px"/>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-container">
      <el-pagination background @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page.sync="listQuery.current"
                     :pageInfo-sizes="[10,20,30, 50]" :pageInfo-size="listQuery.size" layout="total, sizes, prev, pager, next, jumper" :total="total">
      </el-pagination>
    </div>

    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">
      <el-form :rules="rules" ref="dataForm" :model="temp" label-position="left" label-width="120px" style='width: 400px; margin-left:50px;'>
        <el-form-item :label="$t('主域名')" prop="domain">
          <el-input v-model="temp.domain"></el-input>
        </el-form-item>
        <el-form-item :label="$t('强制HTTPS')" prop="userId">
          <el-select style="width: 280px" class="filter-item" v-model="temp.forceHttps" placeholder="请选择">
            <el-option label="开启" :value="1"></el-option>
            <el-option label="关闭" :value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('JKS文件上传')" prop="file">
          <el-upload
            ref="upload"
            class="upload-demo"
            action=""
            :http-request="httpRequest"
            :file-list="this.fileList"
            :auto-upload="false"
            :multiple="false"
            accept=".jks"
            :limit="1"
            :on-change="handleChange"
            >
            <el-button slot="trigger" size="small" type="primary">选择文件</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item v-if="passWordInputStatus" :label="$t('JKS密码')" prop="filePassword">
          <el-input type="password" v-model="temp.keyStorePassword"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">{{$t('table.cancel')}}</el-button>
        <el-button v-if="dialogStatus=='create'" type="primary" @click="createData">{{$t('table.confirm')}}</el-button>
        <el-button v-else type="primary" @click="updateData">{{$t('table.confirm')}}</el-button>
      </div>
    </el-dialog>

<!--    <el-dialog title="Reading statistics" :visible.sync="dialogPvVisible">-->
<!--      <el-table :data="pvData" border fit highlight-current-row style="width: 100%">-->
<!--        <el-table-column prop="key" label="Channel"> </el-table-column>-->
<!--        <el-table-column prop="pv" label="Pv"> </el-table-column>-->
<!--      </el-table>-->
<!--      <span slot="footer" class="dialog-footer">-->
<!--        <el-button type="primary" @click="dialogPvVisible = false">{{$t('table.confirm')}}</el-button>-->
<!--      </span>-->
<!--    </el-dialog>-->
  </div>
</template>

<script>
  import { fetchList, updateEnableStatus, createDomain, updateDomain, deleteDomain, updateDefaultStatus } from '@/api/domain'
  import { userList } from '@/api/user'
  import waves from '@/directive/waves' // 水波纹指令
  import { parseTime } from '@/utils'
  import ButtonPopover from '../../components/Button/buttonPopover'

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
      ButtonPopover
    },
    data() {
      return {
        fileList: [],
        tableKey: 0,
        list: null,
        total: null,
        listLoading: true,
        listQuery: {
          current: 1,
          size: 10,
          enable: undefined,
          userId: null
        },
        importanceOptions: [1, 2, 3],
        calendarTypeOptions,
        userList: [],
        sortOptions: [{ label: 'ID Ascending', key: '+id' }, { label: 'ID Descending', key: '-id' }],
        statusOptions: ['published', 'draft', 'deleted'],
        showReviewer: false,
        temp: {
          id: undefined,
          importance: 1,
          remark: '',
          timestamp: new Date(),
          title: '',
          type: '',
          status: 'published',
          domain: '',
          jks: undefined,
          keyStorePassword: '',
          forceHttps: ''
        },
        dialogFormVisible: false,
        dialogStatus: '',
        passWordInputStatus: false,
        textMap: {
          update: '编辑',
          create: '新建'
        },
        dialogPvVisible: false,
        pvData: [],
        rules: {
          domain: [
            { required: true, message: '主域名不能为空', trigger: 'blur' },
            { validator: this.validateDomain, trigger: 'blur' }
          ],
          file: [
            { required: false, message: '请选择文件', trigger: 'blur' }
          ],
          filePassword: [
            { required: false, message: '密码不能为空', trigger: 'blur' }
          ]// TODO required改为true后，密码不为空，仍不能通过
        },
        downloadLoading: false,
        selectObj: {
          statusOptions: [{ label: '启用', value: 1 }, { label: '禁用', value: 2 }],
          onlineOptions: [{ label: '在线', value: 1 }, { label: '离线', value: 2 }]
        }
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
      sslStatusName(status) {
        const statusMap = {
          1: '已上传',
          2: '未上传',
          3: '已验证'
        }
        return statusMap[status]
      },
      httpStatusName(status) {
        const statusMap = {
          1: '开启',
          2: '关闭'
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
      typeFilter(type) {
        return calendarTypeKeyValue[type]
      }
    },
    created() {
      this.getDataList()
    },
    methods: {
      httpRequest(param) {
        this.temp.jks = param.file
      },
      validateDomain(rule, value, callback) {
        // 匹配多级域名，例如：sub.example.com, sub.sub.example.com
        const domainPattern = /^(?:[a-zA-Z0-9][a-zA-Z0-9-]{0,61}[a-zA-Z0-9]\.)+[a-zA-Z]{2,}$/

        if (!value) {
          callback(new Error('主域名不能为空'));
        } else if (!domainPattern.test(value)) {
          callback(new Error('请输入正确的域名格式'));
        } else {
          callback();
        }
      },
      handleChange(file) {
        this.fileList = [file]
        this.passWordInputStatus = true
      },
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
        this.getList()
      },
      handleCurrentChange(val) {
        this.listQuery.current = val
        this.getList()
      },
      handleDefaultStatus(row) {
        updateDefaultStatus(row.id, 1).then(response => {
          if (response.data.code === 0) {
            this.$notify({
              title: '成功',
              message: '操作成功',
              type: 'success'
            })
            this.getList()
          }
          this.getList()
        })
      },
      handleModifyStatus(row, enable) {
        console.log('route', this.$route)
        updateEnableStatus(row.id, enable, row.domain).then(response => {
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
        this.passWordInputStatus = false
        this.fileList = []
        this.temp = {
          domain: undefined,
          jks: undefined,
          keyStorePassword: undefined,
          forceHttps: undefined
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
            this.$refs.upload.submit()
            const formData = this.objectToFormData(this.temp)
            createDomain(formData).then(response => {
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
        this.resetTemp()
        this.temp = Object.assign({}, row) // copy obj
        this.dialogStatus = 'update'
        this.dialogFormVisible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].clearValidate()
        })
      },
      updateData() {
        this.$refs['dataForm'].validate((valid) => {
          console.log(this.temp)
          if (valid) {
            this.$refs.upload.submit()
            const tempData = Object.assign({}, this.temp)
            const formData = this.objectToFormData(tempData)
            updateDomain(formData).then(response => {
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
      objectToFormData(obj) {
        const formData = new FormData()
        Object.keys(obj).forEach(key => {
          if (obj[key] !== undefined) {
            formData.append(key, obj[key])
          }
        })
        return formData
      },
      handleDelete(row) {
        this.$confirm('确定要删除吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          deleteDomain(row.id).then(response => {
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
        deleteDomain(row.id).then(response => {
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
