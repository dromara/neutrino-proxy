<template>
  <div class="app-container calendar-list-container">

    <div>
      <div style="text-align: center;line-height:48px;font-size:24px">{{group.name}}安全组</div>
      <div style="text-align: center;font-size:14px; color: #606266">{{group.description}}</div>
    </div>

    <div class="filter-container">
      <el-input v-model="listQuery.name" style="width:145px;margin-right:10px" placeholder="请输入名称" />
      <el-input v-model="listQuery.description" style="width:145px;margin-right:10px" placeholder="请输入描述" />
<!--      <el-select v-model="listQuery.passType" placeholder="请选择默认放行类型" clearable style="width:145px;margin-right:10px">-->
<!--          <el-option v-for="item in selectObj.passType" :key="item.value" :label="item.label" :value="item.value" />-->
<!--      </el-select>-->
      <el-select v-model="listQuery.enable" placeholder="请选择启用状态" clearable style="width:145px;margin-right:10px">
          <el-option v-for="item in selectObj.statusOptions" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
      <el-button class="filter-item" type="primary" v-waves icon="el-icon-search" @click="handleFilter">{{
          $t('table.search') }}
      </el-button>
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
          <el-tag :type="scope.row.passType | passTypeFilter">{{ scope.row.passType | passTypeName }}</el-tag>
        </template>
      </el-table-column>
<!--      <el-table-column align="center" :label="$t('table.priority')">-->
<!--        <template slot-scope="scope">-->
<!--          <span>{{scope.row.priority}}</span>-->
<!--        </template>-->
<!--      </el-table-column>-->
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
      <el-table-column class-name="status-col" :label="$t('table.enableStatus')" width="150">
        <template slot-scope="scope">
          <el-tag :type="scope.row.enable | statusFilter">{{ scope.row.enable | statusName }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.actions')" width="250" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-link type="primary" :underline="false" size="mini" @click="handleUpdate(scope.row)" style="font-size:12px">{{$t('table.edit')}}</el-link>
          <el-link :underline="false" v-if="scope.row.enable =='1'" size="mini" type="warning" @click="handleModifyStatus(scope.row, 2)" style="font-size:12px">{{$t('table.disable')}}</el-link>
          <el-link :underline="false" v-if="scope.row.enable =='2'" size="mini" type="success" @click="handleModifyStatus(scope.row, 1)" style="font-size:12px">{{$t('table.enable')}}</el-link>
          <LinkPopover @handleCommitClick="handleDelete(scope.row)"/>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-container">
      <el-pagination background @size-change="handleSizeChange" @current-change="handleCurrentChange"
                     :current-page.sync="listQuery.current" :pageInfo-sizes="[10, 20, 30, 50]" :pageInfo-size="listQuery.size"
                     layout="total, sizes, prev, pager, next, jumper" :total="total">
      </el-pagination>
    </div>

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
import {fetchGroupDetail, fetchRulePage, createRule, updateRule, deleteRule, updateRuleEnableStatus} from '@/api/securityGroup'
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
        total: null,
        listQuery: {
          current: 1,
          size: 10,
          groupId: undefined,
          name: undefined,
          description: undefined,
          passType: undefined,
          enable: undefined
        },
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
        selectObj: {
            statusOptions: [{ label: '启用', value: 1 }, { label: '禁用', value: 2 }],
            onlineOptions: [{ label: '在线', value: 1 }, { label: '离线', value: 2 }],
            passType: [{ label: '允许', value: 1 }, { label: '拒绝', value: 2 }]

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
      if (this.$route.query.groupId) {
          this.listQuery.groupId = this.$route.query.groupId
      } else {
          this.$notify({
              title: '错误',
              message: '没有获取到安全组信息',
              type: 'error',
              duration: 3000
          })
          this.$router.push(`/system/securityGroup`)
          return
      }
      this.getGroupDetail()
      this.getList()
    },
    methods: {
      getGroupDetail () {
        fetchGroupDetail({id: this.listQuery.groupId}).then(response => {
          this.group = response.data.data
        })
      },
      getList() {
        if (!this.listQuery.groupId) {
          this.$notify({
              title: '错误',
              message: '没有获取到安全组信息',
              type: 'error',
              duration: 3000
            })
          return
        }
        this.listLoading = true
        fetchRulePage(this.listQuery).then(response => {
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
      handleModifyStatus(row, enable) {
        updateRuleEnableStatus(row.id, enable).then(response => {
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
          groupId: this.listQuery.groupId,
          name: '',
          description: '',
          rule: '',
          passType: this.group.defaultPassType == 1 ? 0 : 1,
          passTypeTooltip: `安全组已设置默认${(this.group.defaultPassType == 1 ? '允许' : '拒绝')}`,
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
            this.temp.groupId = this.listQuery.groupId
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
        this.temp.passType = row.passType
        this.temp.passTypeTooltip = `安全组已设置默认${(this.group.defaultPassType == 1 ? '允许' : '拒绝')}`,
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
            tempData.groupId = this.listQuery.groupId
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
        deleteRule({ruleId: row.id}).then(response => {
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
