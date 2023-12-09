<template>
  <div class="app-container calendar-list-container">

    <div>
      <div style="text-align: center;line-height:48px;font-size:24px">{{group.name}}安全组</div>
      <div style="text-align: center;font-size:14px; color: #606266">{{group.description}}</div>
    </div>

    <div class="filter-container" align="right">
      <el-button class="filter-item" @click="handleCreate" type="primary" icon="el-icon-edit">{{$t('table.add')}}</el-button>
    </div>

    <el-table :key='tableKey' :data="list" v-loading="listLoading" element-loading-text="给我一点时间" border fit highlight-current-row
              style="width: 100%">
      <!-- <el-table-column align="center" width="40" type="selection" /> -->
      <el-table-column align="center" :label="$t('table.id')" width="60">
        <template slot-scope="scope">
          <span>{{scope.row.id}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.ruleName')">
        <template slot-scope="scope">
          <span>{{scope.row.name}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.desc')">
        <template slot-scope="scope">
          <span>{{scope.row.description}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.rule')">
        <template slot-scope="scope">
          <span>{{scope.row.rule}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.passType')">
        <template slot-scope="scope">
          <el-tag type="success" v-if="scope.row.passType == 'allow'" effect="dark">允许</el-tag>
          <el-tag type="info" v-if="scope.row.passType == 'deny'" effect="dark">拒绝</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.priority')">
        <template slot-scope="scope">
          <span>{{scope.row.priority}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.createTime')">
        <template slot-scope="scope">
          <span>{{scope.row.createTime}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.updateTime')">
        <template slot-scope="scope">
          <span>{{scope.row.updateTime}}</span>
        </template>
      </el-table-column>
      <el-table-column class-name="status-col" :label="$t('table.enableStatus')" width="150">
        <template slot-scope="scope">
          <el-tag type="success" v-if="scope.row.enable == '启用'">{{scope.row.enable}}</el-tag>
          <el-tag type="warning" v-if="scope.row.enable == '禁用'">{{scope.row.enable}}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.actions')" width="250" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-link type="primary" :underline="false" size="mini" @click="handleUpdate(scope.row)" style="font-size:12px">{{$t('table.edit')}}</el-link>
          <el-link :underline="false" v-if="scope.row.enable =='启用'" size="mini" type="warning" @click="handleDisableStatus(scope.row)" style="font-size:12px">{{$t('table.disable')}}</el-link>
          <el-link :underline="false" v-if="scope.row.enable =='禁用'" size="mini" type="success" @click="handleEnableStatus(scope.row)" style="font-size:12px">{{$t('table.enable')}}</el-link>
          <LinkPopover @handleCommitClick="handleDelete(scope.row)"/>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" top="4vh">
      <el-form :rules="rules" ref="dataForm" :model="temp" label-position="right" label-width="100px" style='margin-left:50px;margin-right: 150px'>
        <el-form-item :label="$t('table.name')" prop="name">
          <el-input :placeholder="$t('table.name')" v-model="temp.name"></el-input>
        </el-form-item>

        <el-form-item :label="$t('table.desc')" prop="desc">
          <el-input type="textarea" :autosize="{ minRows: 2, maxRows: 4}" :placeholder="$t('table.desc')" v-model="temp.description"></el-input>
        </el-form-item>

        <el-form-item :label="$t('table.rule')" prop="rule">
          <el-input type="textarea" :autosize="{ minRows: 4, maxRows: 10}" :placeholder="$t('table.rule')" v-model="temp.rule"></el-input>
        </el-form-item>
        <el-form-item>
          <div style="line-height: 28px; color: cornflowerblue">
            <div>规则描述:</div>
            <div>单个ip：192.168.1.1, AA22:BB11:1122:CDEF:1234:AA99:7654:7410, ipv6只支持单个ip判断</div>
            <div>范围类型：192.168.1.0-192.168.1.255</div>
            <div>掩码类型：192.168.1.0/24</div>
            <div>泛型：0.0.0.0/ALL</div>
            <div>每个类型中间以英文逗号分隔,形如 192.168.1.1,192.168.3.0/24 是正确的 </div>
          </div>
        </el-form-item>
        

        <el-form-item  :label="$t('table.passType')" prop="passType">
          <el-tooltip class="item" effect="dark" :content="temp.passTypeTooltip" placement="right">
            <!-- <el-button>右边</el-button> -->
            <el-select class="filter-item" v-model="temp.passType" disabled>
              <el-option v-for="item in  passTypeList" :key="item.key" :label="item.key" :value="item.value">
              </el-option>
            </el-select>
          </el-tooltip>
         
        </el-form-item>

        <!-- <el-form-item :label="$t('table.priority')" prop="priority">
          <el-input-number v-model="temp.priority" :min="1" :max="1000" :placeholder="$t('table.priority')"></el-input-number>
        </el-form-item> -->

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
import {fetchGroupOne, fetchRuleList, createRule, updateRule, deleteRule, enableRule, disableRule} from '@/api/securityGroup'
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
        groupId: 1,
        group: {},
        tableKey: 0,
        list: [],
        listLoading: true,
        temp: {
          id: undefined,
          groupId: undefined,
          name: '',
          description: '',
          rule: '',
          passType: undefined,
          passTypeTooltip: '',
          priority: 1
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
          rule: [{ required: true, message: '规则内容必填', trigger: 'blur' }],
          // passType: [{ required: true, message: '放行类型必选', trigger: 'blur' }],
          // priority: [{ required: true, message: '优先级必填', trigger: 'blur' }]
        },
        downloadLoading: false,
        checkBoxData:[], //表单勾选的行
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
      const queryParam = this.$route.query
      if (queryParam && typeof queryParam === 'object' && queryParam.groupId) {
        this.groupId = queryParam.groupId
        localStorage.setItem('groupId', this.groupId)
        this.getGroupOne()
        this.getList()
        return
      }

      const groupId = localStorage.getItem('groupId')
      if (groupId) {
        this.groupId = parseInt(groupId)
        this.getGroupOne()
        this.getList()
        return
      } 
      
      this.$notify({
          title: '错误',
          message: '没有获取到安全组信息',
          type: 'error',
          duration: 3000
        })
      this.$router.push(`/system/securityGroup`)
    },
    methods: {
      getGroupOne () {
        fetchGroupOne(this.groupId).then(response => {
          this.group = response.data.data
        })
      },
      getList() {
        if (!this.groupId) {
          this.$notify({
              title: '错误',
              message: '没有获取到安全组信息',
              type: 'error',
              duration: 3000
            })
          return
        }
        this.listLoading = true
        fetchRuleList(this.groupId).then(response => {
          this.list = response.data.data
          this.listLoading = false
        })
      },
      handleEnableStatus(row) {
        enableRule(row.id).then(response => {
          if (response.data.code === 0) {
            this.$message({
              message: '操作成功',
              type: 'success'
            })
            this.getList()
          }
        })
      },
      handleDisableStatus(row) {
        disableRule(row.id).then(response => {
          if (response.data.code === 0) {
            this.$message({
              message: '操作成功',
              type: 'success'
            })
            this.getList()
          }
        })
      },
      resetTemp() {
        this.temp = {
          id: undefined,
          groupId: this.groupId,
          name: '',
          description: '',
          rule: '',
          passType: this.group.defaultPassType == 'allow' ? 0 : 1,
          passTypeTooltip: `安全组已设置默认${(this.group.defaultPassType == 'allow' ? '允许' : '拒绝')}`,
          priority: 1
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
            this.temp.groupId = this.groupId
            createRule(this.temp).then(response => {
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
        this.temp.passType = row.passType == 'allow' ? 1 : 0
        this.temp.passTypeTooltip = `安全组已设置默认${(this.group.defaultPassType == 'allow' ? '允许' : '拒绝')}`,
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
            tempData.groupId = this.groupId
            updateRule(tempData).then(response => {
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
        deleteRule(row.id).then(response => {
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
      }
    }
  }
</script>
