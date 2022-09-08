<template>
  <div class="drop-down-table">
    <div class="input-tag-box">
      <div id="tag-list-box">
        <el-tag class="elTag" v-show="tags.length > 0" v-for="(tag,index) in tags" closable type="info" size="mini" @close="handeCloseTag(index,tag)">{{tag.name}}</el-tag>
        <el-input v-model="inputValue"
                  :placeholder="tags.length > 0 ? '':'请选择'"
                  @focus="editor = true"
                  @keyup.backspace.native="keyup"
                  :style="{width:inputWidth}"/>
      </div>
      <i :class="editor ? iconUp : iconDown" @click="editor = !editor" style="color: #C0C4CC;"/>
    </div>
    <div class="popup-class" :style="{'width':width}" v-show="editor">
      <el-table
        border
        :columns="columns"
        :data="getData"
        @row-click="handeRowClick"
        max-height="500"
        :row-class-name="tableRowClassName"
        header-cell-class-name="table_header_class"
      >
        <el-table-column v-for="item in columns" align="center" :prop="item.key" :label="item.title" :min-width="item.minWidth"/>
      </el-table>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    value: {
      type: String,
      default: ''
    },
    height: {
      type: String,
      default: ''
    },
    width: {
      type: String,
      default: '400px'
    },
    data: {
      type: Array,
      default: []
      // required: true
    },
    columns: {
      type: Array,
      default: []
      // required: true
    },
    multiple: {
      type: Boolean,
      default: false
    }
  },
  computed: {
    getData() {
      if (!this.multiple) {
        return this.data
      } else {
        return this.data.filter(item => !this.inputValue || item.name.toLowerCase().includes(this.inputValue.toLowerCase()))
      }
    }
  },
  data() {
    return {
      editor: false,
      inputValue: '',
      iconUp: 'el-select__caret el-input__icon el-icon-arrow-up',
      iconDown: 'el-select__caret el-input__icon el-icon-arrow-down',
      tags: [],
      inputWidth: '100%'
    }
  },
  created() {
    this.inputValue = this.value
  },
  watch: {
    editor() {
      if (this.editor) {
        document.getElementsByClassName('input-tag-box')[0].style.borderColor = '#409EFF'
      } else {
        this.inputValue = ''
        document.getElementsByClassName('input-tag-box')[0].style.borderColor = '#909399'
      }
    }
  },
  mounted() {
  },
  methods: {
    tableRowClassName({ row, rowIndex }) {
      row.index = rowIndex
      let className = 'success-row'
      if (row.disabled) {
        className = 'disabled-row'
      }
      const _index = this.tags.findIndex(item => item === row)
      if (_index > -1) {
        return className + ' select-row'
      }
      return className
    },
    handeRowClick(row, column, event) {
      if (row.disabled) {
        return
      }
      if (this.multiple) {
        const _index = this.tags.findIndex(item => item === row)
        if (_index > -1) {
          this.tags.splice(_index, 1)
        } else {
          this.tags.push(row)
        }
        setTimeout(() => {
          this.getInputWidth()
        })
      } else {
        this.editor = false
        this.inputValue = row.name
        this.tags = [row]
      }
      this.$emit('rowClick', row, this.tags, column, event)
    },
    handeCloseTag(index, tag) {
      this.tags.splice(index, 1)
      this.inputValue = ''
    },
    keyup() {
      if (this.inputValue.length <= 0 && this.tags.length > 0) {
        const list = document.getElementsByClassName('elTag')
        if (list[list.length - 1].style.borderColor !== '') {
          this.handeCloseTag(this.tags.length - 1)
        } else {
          list[list.length - 1].style.borderColor = '#909399'
        }
      }
    },
    getInputWidth() {
      let tagMaxWidth = 0
      const offsetWidth = document.getElementById('tag-list-box').offsetWidth
      const elTagName = document.getElementsByClassName('elTag')
      elTagName.forEach(item => {
        const width = item.offsetWidth + 5.5
        if (width + tagMaxWidth < offsetWidth) {
          tagMaxWidth = width + tagMaxWidth
        } else {
          tagMaxWidth = width
        }
      })
      this.inputWidth = (offsetWidth - tagMaxWidth) + 'px'
    }
  }
}
</script>

<style lang="less" scoped>
.drop-down-table{
  .input-tag-box{
    display: flex;
    background: #fff;
    align-items: center;
    border-radius: 4px;
    border: 1px solid #DCDFE6;
    #tag-list-box{
      padding: 0;
      width: ~"calc(100% - 30px)";;
      .elTag{
        margin-left: 5px;
        margin-top: 5px;
      }
    }
    /deep/.el-input{
      display: inline-block;
    }
    /deep/.el-input__inner{
      border: 0px!important;
      padding-right: 0;
    }
    /deep/.el-input__icon{
      line-height: 34px!important;
    }
  }
  .popup-class{
    padding: 8px;
    margin-top: 5px;
    width: 100%;
    background: white;
    border: 1px solid #f0f2f5;
    box-shadow: 1px 1px 10px #f0f2f5;
    position: absolute;
    z-index: 9;
    /deep/.ivu-table-tip{
      overflow-x: hidden!important;
    }
  }
  /deep/.table_header_class{
    font-weight: bolder;
    color: black;
    background-color: #ebeef5!important;
  }
  /deep/.gutter{
    background-color: #ebeef5!important;
  }
  /deep/.el-table .success-row {
    &:hover{
      cursor: pointer!important;
    }
  }
  /deep/.el-table .disabled-row {
    color: #c0c4cc !important;
    &:hover{
      cursor: not-allowed!important;
    }
    &:hover>td{
      background-color: #FFF!important;
    }
  }
  /deep/.el-table .select-row {
    color: #409eff;
    font-weight: 700;
  }
}
</style>
