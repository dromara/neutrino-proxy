<template>
  <div class="app-container calendar-list-container">
    <div class="filter-container" style="display:flex">
      <el-select v-model="listQuery.userId" placeholder="请选择用户" filterable clearable style="margin-right:10px;width: 120px;">
        <el-option v-for="item in userList" :key="item.loginName" :label="item.name" :value="item.id" />
      </el-select>
      <el-select v-model="listQuery.licenseId" placeholder="请选择license" filterable clearable style="margin-right:10px;width: 135px;">
        <el-option v-for="item in licenseList" :key="item.key" :label="item.name" :value="item.id" />
      </el-select>
      <!-- <el-select v-model="listQuery.protocal" placeholder="请选择协议" clearable style="margin-right:10px;width: 120px;">
        <el-option v-for="item in protocalList" :key="item.name" :label="item.name" :value="item.name" :disabled="!item.enable" />
      </el-select> -->
      <el-input v-model="listQuery.domain" type="text" style="width:145px;margin-right:10px" class="filter-item" placeholder="请输入域名关键词" />
      <el-select v-model="listQuery.isOnline" placeholder="请选择在线状态" clearable style="width:145px;margin-right:10px">
        <el-option v-for="(value, key) in onlineOptions" :key="key" :label="value" :value="key" />
      </el-select>
      <el-select v-model="listQuery.enable" placeholder="请选择启用状态" clearable style="width:145px;margin-right:10px">
        <el-option v-for="(value, key) in statusOptions" :key="key" :label="value" :value="key" />
      </el-select>
      <el-input v-model="listQuery.description" type="text" style="width:145px;margin-right:10px" class="filter-item" placeholder="请输入描述详情" show-word-limit />
      <el-button class="filter-item" type="primary" v-waves icon="el-icon-search" @click="handleFilter">{{
        $t('table.search') }}</el-button>
      <el-button class="filter-item" style="margin-left: 10px;" @click="handleCreate" type="primary" icon="el-icon-edit">{{ $t('table.add') }}</el-button>
    </div>

    <el-table :key='tableKey' :data="list" v-loading="listLoading" element-loading-text="给我一点时间" border fit highlight-current-row style="width: 100%">
      <el-table-column align="center" :label="$t('table.id')" width="50">
        <template slot-scope="scope">
          <span>{{ scope.row.id }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.userName')" width="80">
        <template slot-scope="scope">
          <span>{{ scope.row.userName }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.licenseName')" width="140">
        <template slot-scope="scope">
          <span>{{ scope.row.licenseName }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.protocalName')" width="80">
        <template slot-scope="scope">
          <span>{{ scope.row.protocal }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.domainName')" width="180">
        <template slot-scope="scope">
          <el-link target="_blank" @click="handleOpenWebPage(scope.row)">http(s)://{{ scope.row.domain }}</el-link>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('目标路径')" width="240">
        <template slot-scope="scope">
          <span v-html="scope.row.targetPath.replace(/(\n\r|\r\n|\r|\n)/g, '<br/>')"> </span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('限速')" width="160">
        <template slot-scope="scope">
          <span>{{ scope.row.upLimitRate ? scope.row.upLimitRate : '--' }} / {{ scope.row.downLimitRate ? scope.row.downLimitRate : '--' }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.desc')" width="260">
        <template slot-scope="scope">
          <span>{{ scope.row.description }}</span>
        </template>
      </el-table-column>
      <!--      <el-table-column width="150px" align="center" :label="$t('table.createTime')">-->
      <!--        <template slot-scope="scope">-->
      <!--          <span>{{ scope.row.createTime | parseTime('{y}-{m}-{d} {h}:{i}') }}</span>-->
      <!--        </template>-->
      <!--      </el-table-column>-->
      <el-table-column width="150px" align="center" :label="$t('table.updateTime')">
        <template slot-scope="scope">
          <span>{{ scope.row.updateTime | parseTime('{y}-{m}-{d} {h}:{i}') }}</span>
        </template>
      </el-table-column>
      <el-table-column class-name="status-col" :label="$t('table.enableStatus')" width="100">
        <template slot-scope="scope">
          <el-tag :type="colorOption[scope.row.enable]">{{ statusOptions[scope.row.enable] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column class-name="status-col" :label="$t('table.isOnline')" width="100">
        <template slot-scope="scope">
          <el-tag :type="colorOption[scope.row.isOnline]">{{ onlineOptions[scope.row.isOnline] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.actions')" width="320" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="toModify(scope.row)">{{ $t('table.edit') }}</el-button>
          <el-button v-if="scope.row.enable == '1'" size="mini" type="danger" @click="handleModifyStatus(scope.row, 2)">{{ $t('table.disable') }}</el-button>
          <el-button v-if="scope.row.enable == '2'" size="mini" type="success" @click="handleModifyStatus(scope.row, 1)">{{ $t('table.enable') }}</el-button>
          <ButtonPopover @handleCommitClick="handleDelete(scope.row)" style="margin-left: 10px" />
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-container">
      <el-pagination background @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-pageInfo.sync="listQuery.current" :pageInfo-sizes="[10, 20, 30, 50]"
        :pageInfo-size="listQuery.size" layout="total, sizes, prev, pager, next, jumper" :total="total">
      </el-pagination>
    </div>

    <el-dialog :title="textMap[dialogStatus]" :close-on-click-modal="false" :visible.sync="dialogFormVisible" top="20px">
      <el-form :rules="rules" ref="dataForm" :model="temp" label-position="right" label-width="110px">
        <el-form-item :label="$t('License')" prop="licenseId">
          <DropdownTable v-model="temp.licenseId" :name.sync="temp.licenseName" :tableData="licenseAuthList" @selectedData="selectedFeeItem" placeholder="请选择" :width="500"
            :disabled="dialogStatus === 'update'" />
        </el-form-item>
        <el-form-item :label="$t('域名')" prop="domain">
          <el-input v-model="temp.domain">
            <template slot="prepend">http(s)://</template>
          </el-input>
        </el-form-item>
        <el-form-item :label="$t('目标地址')" prop="targetPath">
          <el-input type="textarea" v-model="temp.targetPath" rows="3" placeholder="请输入目标地址,多个可实现负载均衡，如:
127.0.0.1:80
127.0.0.1:90"></el-input>
        </el-form-item>
        <el-form-item :label="$t('描述')" prop="description">
          <el-input v-model="temp.description" type="textarea" placeholder="请输入描述"></el-input>
        </el-form-item>
        <el-form-item :label="$t('请求头部信息')" prop="requestHeader">
          <el-input v-model="temp.requestHeader" type="textarea" placeholder="请输入请求头如：Cache-Control: no-cache"></el-input>
        </el-form-item>
        <el-form-item :label="$t('table.securityGroup')" prop="securityGroup">
          <el-select style="width: 280px;" class="filter-item" v-model="temp.securityGroupId" clearable>
            <el-option v-for="item in securityGroupList" :key="item.id" :label="item.name" :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('上传限速')" prop="upLimitRate">
          <el-input v-model="temp.upLimitRate" placeholder="如：10240B、500K、1M"></el-input>
        </el-form-item>
        <el-form-item :label="$t('下载限速')" prop="downLimitRate">
          <el-input v-model="temp.downLimitRate" placeholder="如：10240B、500K、1M"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">{{ $t('table.cancel') }}</el-button>
        <el-button v-if="dialogStatus == 'create'" type="primary" @click="modifyData">{{ $t('table.confirm')
          }}</el-button>
        <el-button v-else type="primary" @click="modifyData">{{ $t('table.confirm') }}</el-button>
      </div>
    </el-dialog>

    <el-dialog title="Reading statistics" :visible.sync="dialogPvVisible">
      <el-table :data="pvData" border fit highlight-current-row style="width: 100%">
        <el-table-column prop="key" label="Channel"> </el-table-column>
        <el-table-column prop="pv" label="Pv"> </el-table-column>
      </el-table>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="dialogPvVisible = false">{{ $t('table.confirm') }}</el-button>
      </span>
    </el-dialog>

  </div>
</template>

<script>
import { fetchList, mappingModify, updateEnableStatus, deleteMapping, domainAvailable } from '@/api/domain'
import { fetchGroupList } from '@/api/securityGroup'
import { licenseList, licenseAuthList } from '@/api/license'
import { userList } from '@/api/user'
import waves from '@/directive/waves' // 水波纹指令
import { parseTime } from '@/utils'
import ButtonPopover from '../../components/Button/buttonPopover'
import DropdownTable from '../../components/Dropdown/DropdownTable'
export default {
  name: 'DmainMaping',
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
        current: 1,
        size: 10,
        domain: null,
        title: undefined,
        type: undefined,
        userId: undefined,
        license: undefined,
        port: undefined,
        isOnline: undefined,
        enable: undefined,
        description: undefined
      },
      userList: [],
      licenseList: [],
      licenseAuthList: [],
      showReviewer: false,
      temp: {
        id: undefined,
        licenseId: undefined,
        licenseName: undefined,
        domain: undefined,
        targetPath: undefined,
        requestHeader: undefined
      },
      statusOptions: {
        1: '启用',
        2: '禁用',
      },
      onlineOptions: {
        1: '在线',
        2: '离线'
      },
      colorOption: {
        1: 'success',
        2: 'danger'
      },
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '新增'
      },
      dialogPvVisible: false,
      pvData: [],
      rules: {
        licenseId: [{ required: true, message: '请选择License', trigger: 'blur,change' }],
        domain: [{ required: true, message: '请输入域名', trigger: 'blur' },
          {pattern: '^(?=^.{3,255}$)[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+$', message: '域名格式不正确，请检查后重试', trigger: 'blur' },
          { validator: this.isDomainAvailable, trigger: 'blur' }
        ],
        targetPath: [{ required: true, message: '请输入目标地址', trigger: 'blur' }],
      },
      more: true,
      securityGroupList: []
    }
  },
  // filters: {
  // },
  created() {
    this.getDataList()
    this.getLicenseList()
    this.getLicenseAuthList()
    this.fetchSecurityGroupList()
  },
  methods: {
    isDomainAvailable(rule, value, callback) {
      if (value != null) {
        const param = { domain: value, id: this.temp.id }
        domainAvailable(param).then(res => {
          console.log(res)
          if (!res.data.data) {
            return callback(new Error('域名已被占用'))
          } else {
            callback()
          }
        })
      }
    },
    getList() {
      this.listLoading = true
      fetchList(this.listQuery).then(response => {
        this.list = response.data.data.records
        this.total = response.data.data.total
        this.listLoading = false
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
    fetchSecurityGroupList() {
      fetchGroupList().then(res => {
        if (res.data.code == 0) {
          this.securityGroupList = res.data.data
        }
      })
    },
    getAllUserList() {
      userList().then(response => {
        this.userList = response.data.data
      })
    },
    getLicenseList() {
      licenseList().then(response => {
        this.licenseList = response.data.data
      })
    },
    getLicenseAuthList() {
      licenseAuthList().then(response => {
        this.licenseAuthList = response.data.data
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
        licenseId: undefined,
        licenseName: undefined,
        clientIp: '127.0.0.1',
        clientPort: undefined,
        userId: undefined,
        proxyResponses: undefined,
        proxyTimeoutMs: undefined
      }
      this.more = true;
    },
    handleCreate() {
      this.resetTemp()
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    modifyData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          mappingModify(this.temp).then(response => {
            if (response.data.code === 0) {
              this.dialogFormVisible = false
              this.$notify({
                title: '成功',
                message: '操作成功',
                type: 'success',
                duration: 2000
              })
              this.getList()
            }
          })
        }
      })
    },
    handleOpenWebPage(row) {
      open(location.protocol + '//' + row.domain)
    },
    toModify(row) {
      this.temp = Object.assign({}, row) // copy obj
      if (row.securityGroupId === 0) {
        this.temp.securityGroupId = null
      }
      this.temp.timestamp = new Date(this.temp.timestamp)
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    selectedFeeItem(row, list) {
      if (this.temp.licenseId !== row.id) {
        this.temp.licenseId = row.id
        this.temp.licenseName = row.name
        this.temp.serverPort = null
      }
    },
    handleDelete(row) {
      this.$confirm('确定要删除吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteMapping(row.id).then(response => {
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
      }).catch(() => { })
    },
  }
}
</script>
