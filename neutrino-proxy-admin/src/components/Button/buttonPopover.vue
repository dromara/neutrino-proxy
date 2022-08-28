<template>
  <el-popover
    placement="top"
    :width="width"
    v-model="visible">
    <p>{{title}}</p>
    <div style="text-align: right; margin: 0">
      <el-button size="mini" type="text" @click="handleCancelClick">{{cancelText}}</el-button>
      <el-button type="primary" size="mini" @click="handleCommitClick">{{okText}}</el-button>
    </div>
    <el-button slot="reference">{{buttonText}}</el-button>
  </el-popover>
</template>

<script>

export default {
  name: 'deleteButton',
  props: {
    width: {
      type: Number,
      default: 160
    },
    visible: {
      type: Boolean,
      default: false
    },
    buttonText: {
      type: String,
      default: '删除'
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
