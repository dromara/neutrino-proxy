<template>
  <div>
    <el-dialog title="修改密码"
               :visible.sync="visible"
               :close-on-click-modal="false"
               :before-close="onCancel"
    >
      <el-form :rules="rules" ref='form' :model="formData" label-position="left" label-width="70px" style='width: 400px margin-left:50px'>
        <el-form-item label="登录名" prop="loginName">
          <el-input v-model="formData.loginName" disabled/>
        </el-form-item>
        <el-form-item label="新密码" prop="loginPassword">
          <el-input v-model="formData.loginPassword"/>
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
import { updatePassword } from '@/api/user'

export default {
  name: 'updatePwd',
  props: {
    row: Object,
    visible: Boolean
  },
  data() {
    return {
      formData: {
        id: undefined,
        loginName: undefined,
        loginPassword: undefined
      },
      rules: {
        loginName: [
          { required: true, message: '用户名', trigger: 'blur' }
        ],
        loginPassword: [
          { required: true, message: '密码必填', trigger: 'blur' },
          { min: 6, max: 10, message: '密码长度为 6 到 10 位之间', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      if (val) {
        this.getRow()
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
          updatePassword(this.formData).then(res => {
            console.log(res)
            if (res.data.code === 0) {
              this.$message.success('修改成功')
              this.onCancel()
            }
          })
        }
      })
    },
    onCancel() {
      this.$emit('cancel')
    }
  }
}
</script>
