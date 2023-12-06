<template>
  <div class="app-container calendar-list-container">
    <div class="filter-container">
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
          <span>{{scope.row.defaultPassType}}</span>
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
          <el-tag :type="scope.row.enable" v-if="scope.row.enable == '启用'">{{scope.row.enable}}</el-tag>
          <el-tag :type="scope.row.disable" v-if="scope.row.enable == '禁用'">{{scope.row.enable}}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('table.actions')" width="400" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="handleUpdate(scope.row)">{{$t('table.edit')}}</el-button>
          <el-button v-if="scope.row.enable =='启用'" size="mini" type="warning" @click="handleDisableStatus(scope.row)">{{$t('table.disable')}}</el-button>
          <el-button v-if="scope.row.enable =='禁用'" size="mini" type="success" @click="handleEnableStatus(scope.row)">{{$t('table.enable')}}</el-button>
<!--          <el-button v-if="scope.row.status!='deleted'" size="mini" type="danger" @click="handleDelete(scope.row,'deleted')">{{$t('table.delete')}}</el-button>-->
          <ButtonPopover @handleCommitClick="handleDelete(scope.row)" style="margin-left: 10px"/>
          <el-button type="primary" size="mini" @click="handleGoRulePage(scope.row)">{{$t('table.ruleConfig')}}</el-button>
          <el-button type="primary" size="mini" @click="handlePortMapping(scope.row)">{{$t('table.securityGroupBindPortMapping')}}</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">
      <el-form :rules="rules" ref="dataForm" :model="temp" label-position="right" label-width="200px" style='width: 500px; margin-left:50px;'>
        <el-form-item :label="$t('table.name')" prop="name">
          <el-input :placeholder="$t('table.name')" v-model="temp.name"></el-input>
        </el-form-item>

        <el-form-item :label="$t('table.desc')" prop="desc">
          <el-input type="textarea" :autosize="{ minRows: 2, maxRows: 4}" :placeholder="$t('table.desc')" v-model="temp.description"></el-input>
        </el-form-item>

        <el-form-item  :label="$t('table.defaultPassType')" prop="defaultPassType">
          <el-select style="width: 300px" class="filter-item" v-model="temp.defaultPassType">
            <el-option v-for="item in  passTypeList" :key="item.key" :label="item.key" :value="item.value">
            </el-option>
          </el-select>
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

    <el-dialog :title="$t('table.securityGroupBindPortMapping')" :visible.sync="dialogBindPortMappingVisible">
      <el-table :key='tableKey' :data="list" v-loading="listLoading" element-loading-text="给我一点时间" border fit highlight-current-row
              style="width: 100%">
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
            <span>{{scope.row.defaultPassType}}</span>
          </template>
        </el-table-column>
        <el-table-column align="center" :label="$t('table.actions')" width="200" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button type="primary" size="mini" @click="handleBind(scope.row)">{{$t('table.bind')}}</el-button>
            <el-button type="danger" size="mini" @click="handleUnbind(scope.row)">{{$t('table.unbind')}}</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

  </div>
</template>

<script>
import {fetchGroupList, createGroup, updateGroup, deleteGroup, enableGroup, disableGroup} from '@/api/securityGroup'
import waves from '@/directive/waves' // 水波纹指令
import { parseTime } from '@/utils'
import ButtonPopover from '../../components/Button/buttonPopover'

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
        tableKey: 0,
        list: [],
        listLoading: true,
        temp: {
          id: undefined,
          name: '',
          description: '',
          defaultPassType: undefined
        },
        dialogFormVisible: false,
        dialogStatus: '',
        textMap: {
          update: '编辑',
          create: '新建'
        },
        passTypeList: [{key: 'allow', value: 1}, {key: 'deny', value: -1}],
        dialogPvVisible: false,
        pvData: [],
        rules: {
          name: [{ required: true, message: '安全组名称必填', trigger: 'blur' }],
          defaultPassType: [{ required: true, message: '默认放行类型必选', trigger: 'blur' }]
        },
        downloadLoading: false,
        checkBoxData:[], //表单勾选的行
        dialogBindPortMappingVisible: false,
        bindProtMappingSecurityGroup: {}
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
      this.getList()
    },
    methods: {
      getList() {
        this.listLoading = true
        fetchGroupList().then(response => {
          this.list = response.data.data
          this.listLoading = false
        })
      },
      handleEnableStatus(row) {
        enableGroup(row.id).then(response => {
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
        disableGroup(row.id).then(response => {
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
        deleteGroup(row.id).then(response => {
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
        this.$router.push(`/system/securityRule?groupId=${row.id}`)
      },
      handlePortMapping(row) {
        this.dialogBindPortMappingVisible = true
        this.bindProtMappingSecurityGroup = row
      }
    }
  }
</script>
