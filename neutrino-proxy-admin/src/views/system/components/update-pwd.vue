<template>
  <div>
    <el-dialog title="修改密码"
               :visible.sync="visible"
               :close-on-click-modal="false"
               :before-close="onCancel"
               :width="width"
    >
      <el-form :rules="rules" ref='form' :model="formData" label-position="left" label-width="80px" style='padding:0 20px'>
        <el-form-item label="登录名" prop="loginName" v-if="row">
          <el-input v-model="formData.loginName" disabled/>
        </el-form-item>
        <el-form-item label="旧密码" prop="oldLoginPassword" v-if="!row">
          <el-input v-model="formData.oldLoginPassword" type="password" placeholder="请输入旧密码"/>
        </el-form-item>
        <el-form-item label="新密码" prop="loginPassword">
          <el-input v-model="formData.loginPassword" type="password" placeholder="请输入新密码"/>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword" v-if="!row">
          <el-input v-model="formData.confirmPassword" type="password" placeholder="请输入确认密码"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="updatePwd">确定</el-button>
        <el-button @click="onCancel">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { updatePassword, updateUserPassword } from '@/api/user'

export default {
  name: 'updatePwd',
  props: {
    row: {
      type: Object,
      default: null
    },
    visible: Boolean,
    width: {
      type: String,
      default: '500px'
    }
  },
  data() {
    const validatorValue = (rule, value, callback) => {
      if (value === '' || !value) {
        callback(new Error('确认密码必填'))
      } else if (this.formData.loginPassword !== value) {
        callback(new Error('与新密码不一致'))
      } else {
        callback()
      }
    }
    return {
      formData: {
        id: undefined,
        loginName: undefined,
        loginPassword: undefined,
        oldLoginPassword: undefined,
        confirmPassword: undefined
      },
      rules: {
        loginName: [
          { required: true, message: '用户名', trigger: 'blur' }
        ],
        loginPassword: [
          { required: true, message: '新密码必填', trigger: 'blur' },
          { min: 6, max: 20, message: '密码长度为 6 到 10 位之间', trigger: 'blur' }
        ],
        oldLoginPassword: [
          { required: true, message: '旧密码必填', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, validator: validatorValue, trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      if (val) {
        this.row && this.getRow()
      }
    }
  },
  methods: {
    getRow() {
      this.formData.id = this.row.id
      this.formData.loginName = this.row.loginName
      this.formData.loginPassword = ''
    },
    updatePwd() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          if (this.row) {
            updatePassword(this.formData).then(res => {
              if (res.data.code === 0) {
                this.$message.success('修改成功')
                this.onCancel()
              }
            })
          } else {
            updateUserPassword(this.formData).then(res => {
              if (res.data.code === 0) {
                this.$message.success('修改成功')
                this.onCancel()
              }
            })
          }
        }
      })
    },
    onCancel() {
      this.$emit('cancel')
    }
  }
}
</script>
