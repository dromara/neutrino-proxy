<template>
  <div class="app-container calendar-list-container">
    <div class="filter-container" style="display:flex">
      <el-select v-model="listQuery.userId" placeholder="请选择用户" filterable clearable style="margin-right:10px;width: 120px;">
        <el-option v-for="item in userList" :key="item.loginName" :label="item.name" :value="item.id" />
      </el-select>
      <el-select v-model="listQuery.licenseId" placeholder="请选择license" filterable clearable style="margin-right:10px;width: 135px;">
        <el-option v-for="item in licenseList" :key="item.key" :label="item.name" :value="item.id" />
      </el-select>
      <el-select v-model="listQuery.protocal" placeholder="请选择协议" clearable style="margin-right:10px;width: 120px;">
        <el-option v-for="item in protocalList" :key="item.name" :label="item.name" :value="item.name"
          :disabled="!item.enable" />
      </el-select>
      <el-input v-model="listQuery.serverPort" type="text" style="width:145px;margin-right:10px" class="filter-item"
        placeholder="请输入服务端端口" :maxlength="5" show-word-limit />
      <el-select v-model="listQuery.isOnline" placeholder="请选择在线状态" clearable style="width:145px;margin-right:10px">
        <el-option v-for="item in selectObj.onlineOptions" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
      <el-select v-model="listQuery.enable" placeholder="请选择启用状态" clearable style="width:145px;margin-right:10px">
        <el-option v-for="item in selectObj.statusOptions" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
      <el-input v-model="listQuery.description" type="text" style="width:145px;margin-right:10px" class="filter-item"
        placeholder="请输入描述详情" show-word-limit />
      <el-button class="filter-item" type="primary" v-waves icon="el-icon-search" @click="handleFilter">{{
        $t('table.search') }}</el-button>
      <el-button class="filter-item" style="margin-left: 10px;" @click="handleCreate" type="primary"
        icon="el-icon-edit">{{ $t('table.add') }}</el-button>
    </div>

    <el-table :key='tableKey' :data="list" v-loading="listLoading" element-loading-text="给我一点时间" border fit
      highlight-current-row style="width: 100%">
      <el-table-column align="center" :label="$t('table.id')" width="50">
        <template slot-scope="scope">
          <span>{{ scope.row.id }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.userName')" width="100">
        <template slot-scope="scope">
          <span>{{ scope.row.userName }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.licenseName')" width="120">
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
          <span>{{ scope.row.domain }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.serverPort')" width="80">
        <template slot-scope="scope">
          <span>{{ scope.row.serverPort }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.proxyClient')" width="120">
        <template slot-scope="scope">
          <span>{{ scope.row.clientIp }}:{{ scope.row.clientPort }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('限速')" width="100">
        <template slot-scope="scope">
          <span>{{scope.row.upLimitRate ? scope.row.upLimitRate : '--'}} / {{scope.row.downLimitRate ? scope.row.downLimitRate : '--'}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.desc')" width="120">
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
          <el-tag :type="scope.row.enable | statusFilter">{{ scope.row.enable | statusName }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column class-name="status-col" :label="$t('table.isOnline')" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.isOnline | statusFilter">{{ scope.row.isOnline | isOnlineName }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column class-name="status-col" :label="$t('table.access')" width="120">
        <template slot-scope="scope">
          <el-button size="mini"  v-if="(scope.row.protocal === 'HTTP' || scope.row.protocal === 'HTTP(S)')" @click="handleOpenWebPage(scope.row)">{{$t('table.openWebPage')}}</el-button>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.actions')" width="320" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="handleUpdate(scope.row)">{{ $t('table.edit') }}</el-button>
          <el-button v-if="scope.row.enable == '1'" size="mini" type="danger" @click="handleModifyStatus(scope.row, 2)">{{$t('table.disable')}}</el-button>
          <el-button v-if="scope.row.enable == '2'" size="mini" type="success" @click="handleModifyStatus(scope.row, 1)">{{ $t('table.enable') }}</el-button>
          <!--          <el-button size="mini" type="danger" @click="handleDelete(scope.row,'deleted')">{{$t('table.delete')}}</el-button>-->
          <ButtonPopover @handleCommitClick="handleDelete2(scope.row)" style="margin-left: 10px" />

        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-container">
      <el-pagination background @size-change="handleSizeChange" @current-change="handleCurrentChange"
        :current-page.sync="listQuery.current" :pageInfo-sizes="[10, 20, 30, 50]" :pageInfo-size="listQuery.size"
        layout="total, sizes, prev, pager, next, jumper" :total="total">
      </el-pagination>
    </div>

    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">
      <el-form :rules="rules" ref="dataForm" :model="temp" label-position="left" label-width="120px"
        style='width: 400px; margin-left:50px;'>
        <!--        <el-form-item :label="$t('License')" prop="licenseId">-->
        <!--          <el-select style="width: 280px;" class="filter-item" v-model="temp.licenseId" placeholder="请选择" :disabled="dialogStatus=='update'">-->
        <!--            <el-option v-for="item in licenseList" :key="item.id" :label="item.name" :value="item.id">-->
        <!--            </el-option>-->
        <!--          </el-select>-->
        <!--        </el-form-item>-->

        <el-form-item :label="$t('License')" prop="licenseId">
          <DropdownTable v-model="temp.licenseId" :name.sync="temp.licenseName" :tableData="licenseAuthList"
            @selectedData="selectedFeeItem" placeholder="请选择" :width="280" :disabled="dialogStatus === 'update'" />
          <!--          <DropdownTable
            :columns="countryColumns"
            :data="licenseList"
            v-model="temp.licenseId"
            :value="temp.licenseName"
            @rowClick="selectedFeeItem"
            disabled
            :disabled="dialogStatus==='update'"
            style="width: 280px"
          />-->
        </el-form-item>
        <el-form-item :label="$t('协议')" prop="protocal">
          <el-select style="width: 280px;" class="filter-item" v-model="temp.protocal" placeholder="请选择">
            <el-option v-for="item in protocalList" :key="item.name" :label="item.name" :value="item.name"
              :disabled="!item.enable">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('服务端端口')" prop="serverPort" >
          <load-select style="width: 280px;" class="filter-item"
            v-model="temp.serverPort"
            :data="serverPortList"
            :page="loadServerPortQuery.page"
            :hasMore="more"
            :clearable="false"
            :dictLabel="'port'"
            :dictValue="'port'"
            :request="loadServerPort"/>
        </el-form-item>
        <el-form-item :label="$t('客户端IP')" prop="clientIp">
          <el-input v-model="temp.clientIp"></el-input>
        </el-form-item>
        <el-form-item :label="$t('客户端端口')" prop="clientPort">
          <el-input v-model="temp.clientPort"></el-input>
        </el-form-item>
        <el-form-item :label="$t('域名')" prop="domainMappings" v-if="(temp.protocal === 'HTTP' || temp.protocal === 'HTTP(S)') && domainList.length > 0" >
          <div v-for="(domain, index) in temp.domainMappings" :key="index" class="domain-mapping">
            <el-input v-model="domain.subdomain">
              <template slot="append">
                <el-select v-model="domain.domain" placeholder="请选择主域名" style="width: 130px;">
                  <el-option v-for="tmp in domainList" :key="tmp.id" :label="'.'+tmp.domain" :value="tmp.domain"></el-option>
                </el-select>
                <el-button type="danger" @click="removeDomainMapping(index)" style="margin-left: 10px;">删除</el-button>
              </template>
            </el-input>
          </div>
          <el-button type="primary" @click="addDomainMapping" style="margin-top: 10px;">添加域名</el-button>
        </el-form-item>
        <el-form-item :label="$t('响应数量')" prop="proxyResponses" v-if="temp.protocal === 'UDP'">
          <el-input v-model="temp.proxyResponses"></el-input>
        </el-form-item>
        <el-form-item :label="$t('超时时间')" prop="proxyTimeoutMs" v-if="temp.protocal === 'UDP'">
          <el-input v-model="temp.proxyTimeoutMs">
            <template slot="append">毫秒</template>
          </el-input>
        </el-form-item>
        <el-form-item  :label="$t('table.securityGroup')" prop="securityGroup">
          <el-select style="width: 280px;" class="filter-item" v-model="temp.securityGroupId" clearable >
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
        <el-form-item :label="$t('描述')" prop="description">
          <el-input v-model="temp.description"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="
          dialogFormVisible = false">{{ $t('table.cancel') }}</el-button>
        <el-button v-if="dialogStatus == 'create'" type="primary" @click="createData">{{ $t('table.confirm')
        }}</el-button>
        <el-button v-else type="primary" @click="updateData">{{ $t('table.confirm') }}</el-button>
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
import { fetchList, createUserPortMapping, updateUserPortMapping, updateEnableStatus, deletePortMapping } from '@/api/portMapping'
import { fetchGroupList } from '@/api/securityGroup'
import { availablePortList, portAvailable } from '@/api/portPool'
import { licenseList, licenseAuthList } from '@/api/license'
import { protocalList } from '@/api/protocal'
import { userList } from '@/api/user'
import waves from '@/directive/waves' // 水波纹指令
import { parseTime } from '@/utils'
import ButtonPopover from '../../components/Button/buttonPopover'
import DropdownTable from '../../components/Dropdown/DropdownTable'
// 下拉选择加载组件
import loadSelect from "@/components/Select/SelectLoadMore";
import {fetchAvailableDomainList} from "../../api/domain";

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
    ButtonPopover,
    loadSelect
  },
  data() {
    const isPortAvailable = (rule, value, callback) => {
      if (value != null) {
        const param = { port: value, portMappingId: this.temp.id }
        portAvailable(param).then(res => {
          if (!res.data.data) {
            return callback(new Error('该端口被占用'))
          } else {
            callback()
          }
        })
      }
    }
    return {
      tableKey: 0,
      list: null,
      total: null,
      listLoading: true,
      listQuery: {
        current: 1,
        size: 10,
        importance: undefined,
        title: undefined,
        type: undefined,
        userId: undefined,
        license: undefined,
        port: undefined,
        isOnline: undefined,
        enable: undefined,
        description: undefined

      },
      importanceOptions: [1, 2, 3],
      calendarTypeOptions,
      sortOptions: [{ label: 'ID Ascending', key: '+id' }, { label: 'ID Descending', key: '-id' }],
      statusOptions: ['published', 'draft', 'deleted'],
      userList: [],
      licenseList: [],
      protocalList: [],
      licenseAuthList: [],
      serverPortList: [],
      domainList: [{ 'name': 'link.com', 'id': '1' }],
      showReviewer: false,
      temp: {
        id: undefined,
        licenseId: undefined,
        licenseName: undefined,
        serverPort: undefined,
        clientIp: undefined,
        clientPort: undefined,
        protocal: undefined,
        proxyResponses: undefined,
        proxyTimeoutMs: undefined,
        domainMappings: []
      },
      selectObj: {
        statusOptions: [{ label: '启用', value: 1 }, { label: '禁用', value: 2 }],
        onlineOptions: [{ label: '在线', value: 1 }, { label: '离线', value: 2 }]
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
        domainMappings: [
          {
            validator: (rule, value, callback) => {
              if (value && value.length > 0) {
                for (let i = 0; i < value.length; i++) {
                  if (!value[i].subdomain || !value[i].domain) {
                    return callback(new Error(`子域名和主域名都不能为空`));
                  }
                }
              }
              callback(); // 验证通过
            },
            trigger: ''
          }
        ],
        licenseId: [{ required: true, message: '请选择License', trigger: 'blur,change' }],
        serverPort: [{ required: true, message: '请输入服务端端口', trigger: 'blur' },
          // { validator: isPortAvailable, trigger: 'change' }
        ],
        clientIp: [{ required: true, message: '请输入客户端IP', trigger: 'blur' }],
        clientPort: [{ required: true, message: '请输入客户端端口', trigger: 'blur' }],
        protocal: [{ required: true, message: '请选择协议', trigger: 'blur' }]
      },
      downloadLoading: false,
      countryColumns: [
        { prop: 'userName', label: '用户名', align: 'center' },
        { prop: 'name', label: 'License', align: 'center' }
      ],
      loadServerPortQuery:{ //下拉框加载数据请求参数
        page:1,
        size:50,
        licenseId:null,
      },
      more: true,
      securityGroupList: []
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
    this.getDataList()
    this.getLicenseList()
    this.getLicenseAuthList()
    this.getProtocalList()
    this.fetchSecurityGroupList()
    this.getAvailableDomainList()
  },
  methods: {
    // 添加新的域名映射
    addDomainMapping() {
      this.temp.domainMappings.push({
        subdomain: '',
        domain: ''
      });
      // console.log(this.temp)
    },
    // 删除指定的域名映射
    removeDomainMapping(index) {
      this.temp.domainMappings.splice(index, 1);
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
    fetchSecurityGroupList () {
      fetchGroupList().then(res => {
        if(res.data.code == 0) {
          this.securityGroupList = res.data.data
        }
      })
    },
    getAvailablePortList(licenseId) {
      this.loadServerPortQuery.licenseId = licenseId;
      this.serverPortList = []; //清掉数据
    },
    getAvailableDomainList() {
      fetchAvailableDomainList({enable:1}).then(response => {
        this.domainList = response.data.data
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
    getProtocalList() {
      protocalList().then(response => {
        this.protocalList = response.data.data
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
        serverPort: undefined,
        clientIp: '127.0.0.1',
        clientPort: undefined,
        userId: undefined,
        proxyResponses: undefined,
        proxyTimeoutMs: undefined,
        domainMappings: []
      };
      this.serverPortList = [];
      this.loadServerPortQuery.licenseId = null;
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
    createData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          console.log(this.temp)
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
    handleOpenWebPage(row) {
      let url = location.protocol + '//' + location.hostname + ':' + row.serverPort
      if (row.domain) {
        url = location.protocol + '//' + row.domain
      }
      open(url)
    },
    handleUpdate(row) {
      console.log(row)
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
      this.getAvailablePortList(row.licenseId)
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
    selectedFeeItem(row, list) {
      if (this.temp.licenseId !== row.id) {
        this.temp.licenseId = row.id
        this.temp.licenseName = row.name
        this.getAvailablePortList(row.id)
        this.temp.serverPort = null
      }
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
      }).catch(() => { })
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
    },
    // 传入给load-select组件的函数
    loadServerPort({page = 1, more = false, keyword = ""} = {}) {
      if(this.loadServerPortQuery.licenseId==null || this.loadServerPortQuery.licenseId==''){
        this.more = false;
        this.$message({
          message: '请先选择License',
          type: 'warning'
        })
        return ;
      }
      return new Promise(resolve => {
        this.loadServerPortQuery.page = page;
        this.loadServerPortQuery.keyword = keyword;
        // 访问后端接口API
        availablePortList(this.loadServerPortQuery).then(res => {
          let result = res.data;
          if (more) {
            this.serverPortList = [...this.serverPortList, ...result.data.records];
          } else {
            this.serverPortList = result.data.records;
          }

          // this.loadServerPortQuery.page = result.data.current;
          let {total, current, size} = result.data;
          this.more = page * size < total;
          this.loadServerPortQuery.page = current;
          resolve();
        });
      });
    }
  }
}
</script>
