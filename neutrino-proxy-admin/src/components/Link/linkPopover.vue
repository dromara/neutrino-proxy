<template>
  <el-popover
    placement="top"
    :width="width"
    v-model="visible">
    <p class="popper-p-css"><i class="el-icon-warning" style="color: #e6a23c"/>{{title}}</p>
    <div style="text-align: center; margin: 0">
      <el-button size="mini" @click="handleCancelClick">{{cancelText}}</el-button>
      <el-button type="primary" size="mini" @click="handleCommitClick">{{okText}}</el-button>
    </div>
    <el-link slot="reference" :underline="false" :type="type" :size="size" :icon="icon" :disabled="disabled" style="text-align: left; font-size: 12px">{{buttonText}}</el-link>
  </el-popover>
</template>

<script>

  export default {
    name: 'deleteLink',
    props: {
      width: {
        type: Number,
        default: 160
      },
      buttonText: {
        type: String,
        default: '删除'
      },
      type: {
        type: String,
        default: 'danger'
      },
      size: {
        type: String,
        default: 'mini'
      },
      icon: {
        type: String,
        default: ''
      },
      disabled: {
        type: Boolean,
        default: false
      },
      title: {
        type: String,
        default: '确定删除吗？'
      },
      okText: {
        type: String,
        default: '确定'
      },
      cancelText: {
        type: String,
        default: '取消'
      }
    },
    data() {
      return {
        visible: false
      }
    },
    methods: {
      handleCancelClick() {
        this.visible = false
        this.$emit('handleCancelClick')
      },
      handleCommitClick() {
        this.visible = false
        this.$emit('handleCommitClick')
      }
    }
  }
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  .popper-p-css{
    margin-top: 0px !important;
    margin-bottom: 5px !important;
    .el-icon-warning{
      margin-right: 5px;
    }
  }
</style>
