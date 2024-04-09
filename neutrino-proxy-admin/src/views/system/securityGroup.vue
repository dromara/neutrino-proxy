<template>
  <div class="app-container calendar-list-container">
    <div class="filter-container">
      <el-input v-model="listQuery.name" style="width:145px;margin-right:10px" placeholder="请输入名称" />
      <el-input v-model="listQuery.description" style="width:145px;margin-right:10px" placeholder="请输入描述" />
      <el-select v-model="listQuery.defaultPassType" placeholder="请选择默认放行类型" clearable style="width:145px;margin-right:10px">
        <el-option v-for="item in selectObj.passType" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
      <el-select v-model="listQuery.enable" placeholder="请选择启用状态" clearable style="width:145px;margin-right:10px">
        <el-option v-for="item in selectObj.statusOptions" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
      <el-button class="filter-item" type="primary" v-waves icon="el-icon-search" @click="handleFilter">{{
          $t('table.search') }}
      </el-button>
      <el-button class="filter-item" style="margin-left: 10px;" @click="handleCreate" type="primary" icon="el-icon-edit">{{$t('table.add')}}</el-button>
    </div>

    <el-table :key='tableKey' :data="list" v-loading="listLoading" element-loading-text="给我一点时间" border fit highlight-current-row
              style="width: 100%">
      <!-- <el-table-column align="center" width="40" type="selection" /> -->
      <el-table-column align="center" :label="$t('table.id')" width="60">
        <template slot-scope="scope">
          <span>{{scope.row.id}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.name')">
        <template slot-scope="scope">
          <span>{{scope.row.name}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.desc')">
        <template slot-scope="scope">
          <span>{{scope.row.description}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.defaultPassType')">
        <template slot-scope="scope">
          <el-tag :type="scope.row.defaultPassType | passTypeFilter">{{ scope.row.defaultPassType | passTypeName }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.createTime')">
        <template slot-scope="scope">
          <span>{{ scope.row.createTime | parseTime('{y}-{m}-{d} {h}:{i}') }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.updateTime')">
        <template slot-scope="scope">
          <span>{{ scope.row.updateTime | parseTime('{y}-{m}-{d} {h}:{i}') }}</span>
        </template>
      </el-table-column>
      <el-table-column class-name="status-col" :label="$t('table.enableStatus')">
        <template slot-scope="scope">
          <el-tag :type="scope.row.enable | statusFilter">{{ scope.row.enable | statusName }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.actions')"  class-name="small-padding fixed-width" style="display:flex;justify-content:center">
        <template slot-scope="scope">
          <div >
            <el-link :underline="false" type="primary" size="mini" @click="handleGoRulePage(scope.row)" style="font-size: 12px">{{$t('table.ruleConfig')}}</el-link>
            <el-link :underline="false" type="primary" size="mini" @click="handleUpdate(scope.row)" style="font-size: 12px">{{$t('table.edit')}}</el-link>
            <el-link :underline="false" v-if="scope.row.enable =='1'" size="mini" type="warning" @click="handleModifyStatus(scope.row, 2)" style="font-size: 12px">{{$t('table.disable')}}</el-link>
            <el-link :underline="false" v-if="scope.row.enable =='2'" size="mini" type="success" @click="handleModifyStatus(scope.row, 1)" style="font-size: 12px">{{$t('table.enable')}}</el-link>
          </div>
          <el-dropdown>
            <span class="el-dropdown-link" style="font-size: 12px">
              更多操作<i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item>
                <LinkPopover @handleCommitClick="handleDelete(scope.row)" style="width: 100%"/>
              </el-dropdown-item>
              <el-dropdown-item>
                <el-link :underline="false" type="primary" size="mini" @click="handlePortMapping(scope.row)" style="font-size: 12px">{{$t('table.securityGroupBindPortMapping')}}</el-link>
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
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
      <el-form :rules="rules" ref="dataForm" :model="temp" label-position="right" label-width="120px" style='width: 500px; margin-left:10px;'>
        <el-form-item :label="$t('table.name')" prop="name">
          <el-input :placeholder="$t('table.name')" v-model="temp.name"></el-input>
        </el-form-item>

        <el-form-item :label="$t('table.desc')" prop="desc">
          <el-input type="textarea" :autosize="{ minRows: 2, maxRows: 4}" :placeholder="$t('table.desc')" v-model="temp.description"></el-input>
        </el-form-item>

        <el-form-item  :label="$t('table.defaultPassType')" prop="defaultPassType">
          <el-tooltip class="item" effect="dark" content="当IP地址不能匹配任何规则时，默认执行的放行类型" placement="bottom">
            <el-select style="width: 380px" class="filter-item" v-model="temp.defaultPassType" :disabled="dialogStatus === 'update'">
              <el-option v-for="item in  passTypeList" :key="item.key" :label="item.key" :value="item.value">
              </el-option>
            </el-select>
          </el-tooltip>
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

    <el-dialog :title="$t('table.securityGroupBindPortMapping')+'---'+forBindProtMappingSecurityGroup.name+'安全组'" :visible.sync="dialogBindPortMappingVisible" width="90%">
      <el-table :key='tableKey' :data="portMappingList" v-loading="listLoading" element-loading-text="给我一点时间" border width="100%"
        highlight-current-row style="width: 100%">
        <el-table-column align="center" :label="$t('table.id')" width="50">
          <template slot-scope="scope">
            <span>{{ scope.row.id }}</span>
          </template>
        </el-table-column>
        <el-table-column align="center" :label="$t('table.licenseName')">
          <template slot-scope="scope">
            <span>{{ scope.row.licenseName }}</span>
          </template>
        </el-table-column>
        <el-table-column align="center" :label="$t('table.protocalName')" width="120">
          <template slot-scope="scope">
            <span>{{ scope.row.protocal }}</span>
          </template>
        </el-table-column>
        <el-table-column align="center" :label="$t('table.domainName')">
          <template slot-scope="scope">
            <span>{{ scope.row.domain }}</span>
          </template>
        </el-table-column>
        <el-table-column align="center" :label="$t('table.serverPort')" width="100">
          <template slot-scope="scope">
            <span>{{ scope.row.serverPort }}</span>
          </template>
        </el-table-column>
        <el-table-column align="center" :label="$t('table.proxyClient')" width="200">
          <template slot-scope="scope">
            <span>{{ scope.row.clientIp }}:{{ scope.row.clientPort }}</span>
          </template>
        </el-table-column>
        <el-table-column align="center" :label="$t('table.desc')" width="120">
          <template slot-scope="scope">
            <span>{{ scope.row.description }}</span>
          </template>
        </el-table-column>
        <el-table-column class-name="status-col" :label="$t('table.enableStatus')" width="120">
          <template slot-scope="scope">
            <el-tag :type="scope.row.enable | statusFilter">{{ scope.row.enable | statusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" :label="$t('table.actions')" width="150" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button type="primary" size="mini" v-if="!scope.row.securityGroupId" @click="handleBind(scope.row)">{{$t('table.bind')}}</el-button>
            <el-button type="danger" size="mini" v-if="scope.row.securityGroupId && scope.row.securityGroupId == forBindProtMappingSecurityGroup.id" @click="handleUnbind(scope.row)">{{$t('table.unbind')}}</el-button>
            <span v-if="scope.row.securityGroupId && scope.row.securityGroupId != forBindProtMappingSecurityGroup.id" style="font-size: 12px;">{{$t('table.bindOtherSecurityGroup')}}</span>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination background @size-change="handlePortMappingSizeChange" @current-change="handlePortMappingCurrentChange"
          :current-page.sync="portMappingListQuery.current" :pageInfo-sizes="[10, 20, 30, 50]" :pageInfo-size="portMappingListQuery.size"
          layout="total, sizes, prev, pager, next, jumper" :total="portMappingTotal">
        </el-pagination>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import {fetchGroupPage, createGroup, updateGroup, deleteGroup, updateGroupEnableStatus} from '@/api/securityGroup'
import { fetchList as fetchPortMappingList, portMappingBindSecurityGroup, portMappingUnbindSecurityGroup} from '@/api/portMapping'
import waves from '@/directive/waves' // 水波纹指令
import { parseTime } from '@/utils'
import LinkPopover from '../../components/Link/linkPopover'

  export default {
    name: 'complexTable',
    directives: {
      waves
    },
    components: {
      LinkPopover
    },
    data() {
      return {
        tableKey: 0,
        list: [],
        total: null,
        listQuery: {
          current: 1,
          size: 10,
          name: undefined,
          description: undefined,
          defaultPassType: undefined,
          enable: undefined
        },
        listLoading: true,
        temp: {
          id: undefined,
          name: '',
          description: '',
          defaultPassType: undefined
        },
        selectObj: {
          statusOptions: [{ label: '启用', value: 1 }, { label: '禁用', value: 2 }],
          onlineOptions: [{ label: '在线', value: 1 }, { label: '离线', value: 2 }],
          passType: [{ label: '允许', value: 1 }, { label: '拒绝', value: 0 }]

        },
        dialogFormVisible: false,
        dialogStatus: '',
        textMap: {
          update: '编辑',
          create: '新建'
        },
        passTypeList: [{key: '允许', value: 1}, {key: '拒绝', value: 0}],
        dialogPvVisible: false,
        pvData: [],
        rules: {
          name: [{ required: true, message: '安全组名称必填', trigger: 'blur' }],
          defaultPassType: [{ required: true, message: '默认放行类型必选', trigger: 'blur' }]
        },
        downloadLoading: false,
        checkBoxData:[], //表单勾选的行
        dialogBindPortMappingVisible: false,
        forBindProtMappingSecurityGroup: {},
        portMappingList: [],
        portMappingTotal: null,
        portMappingListLoading: false,
        portMappingListQuery: {
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
      }
    },
    filters: {
      passTypeName(type) {
        const statusMap = {
          1: '允许',
          0: '拒绝'
        }
        return statusMap[type]
      },
      passTypeFilter(type) {
        const statusMap = {
          1: 'success',
          0: 'danger'
        }
        return statusMap[type]
      },
      statusName(status) {
        const statusMap = {
          1: '启用',
          2: '禁用'
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
      // eslint-disable-next-line no-sequences
      this.getList()
      this.getPortMappingList()
    },
    methods: {
      getList() {
        this.listLoading = true
        fetchGroupPage(this.listQuery).then(response => {
          this.list = response.data.data.records
          this.total = response.data.data.total
          this.listLoading = false
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
      getPortMappingList() {
        this.portMappingListLoading = true
        fetchPortMappingList(this.portMappingListQuery).then(response => {
          this.portMappingList = response.data.data.records
          this.portMappingTotal = response.data.data.total
          this.portMappingListQuery.current = response.data.data.current
          this.portMappingListLoading = false
        })
      },
      handleModifyStatus(row, enable) {
        updateGroupEnableStatus(row.id, enable).then(response => {
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
          name: '',
          description: '',
          defaultPassType: undefined
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
            createGroup(this.temp).then(response => {
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
        // this.temp.timestamp = new Date(this.temp.timestamp)
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
            updateGroup(tempData).then(response => {
              if (response.data.code === 0) {
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
      handleDelete(row) {
        deleteGroup({groupId: row.id}).then(response => {
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
      handleGoRulePage (row) {
        this.$router.push({ path: '/system/securityRule', query: { groupId: row.id }})

        // this.$router.push(`/system/securityRule?groupId=${row.id}`)
      },
      handlePortMapping(row) {
        this.dialogBindPortMappingVisible = true
        this.forBindProtMappingSecurityGroup = row
      },
      handlePortMappingSizeChange(val) {
        this.portMappingListQuery.size = val
        this.getPortMappingList()
      },
      handlePortMappingCurrentChange(val) {
        this.portMappingListQuery.current = val
        this.getPortMappingList()
      },
      handleBind(portMapping) {
        portMappingBindSecurityGroup(portMapping.id, this.forBindProtMappingSecurityGroup.id).then(response => {
          if (response.data.code === 0) {
            this.$notify({
              title: '成功',
              message: '绑定成功',
              type: 'success',
              duration: 2000
            })
            this.getPortMappingList()
          }
        })
      },
      handleUnbind(portMapping) {
        portMappingUnbindSecurityGroup(portMapping.id).then(response => {
          if (response.data.code === 0) {
            this.$notify({
              title: '成功',
              message: '解绑成功',
              type: 'success',
              duration: 2000
            })
            this.getPortMappingList()
          }
        })
      }
    }
  }
</script>
