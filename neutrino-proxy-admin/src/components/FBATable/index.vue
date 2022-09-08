<template>
  <div>
    <el-table
      ref="FBATable"
      border
      :data="data"
      :span-method="spanMethod"
      :max-height="height"
      :header-cell-style="
        showHeaderStyle
          ? { background: '#f1f2f7', color: '#333' }
          : { background: 'none' }
      "
      @selection-change="handleSelectionChange"
      @row-click="handleRowClick"
    >
      <el-table-column
        type="selection"
        v-if="showCheckBox"
        width="55"
      ></el-table-column>
      <el-table-column type="index" v-if="showIndex" width="55" label="序号">
      </el-table-column>
      <el-table-column
        v-for="(col, index) in rowHeader"
        :key="index"
        :prop="col.prop"
        :label="col.label"
        :width="col.width"
        :fixed="col.fixed"
        :align="col.align"
      >
        <template slot-scope="scope">
          <ex-slot
            v-if="col.render"
            :render="col.render"
            :row="scope.row"
            :index="scope.$index"
            :column="col"
          >
          </ex-slot>
          <span v-else>
            {{ scope.row[col.prop] }}
          </span>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination-end" v-if="isPagination">
      <el-pagination
        :hide-on-single-page="false"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="PaginationData.currentPage"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="PaginationData.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="PaginationData.total"
      >
      </el-pagination>
    </div>
  </div>
</template>

<script>
// 自定义内容的组件
var exSlot = {
  functional: true,
  props: {
    row: Object,
    render: Function,
    index: Number,
    column: {
      type: Object,
      default: null
    }
  },

  render: (h, data) => {
    const params = {
      row: data.props.row,
      index: data.props.index
    }

    if (data.props.column) params.column = data.props.column
    return data.props.render(h, params)
  }
}
export default {
  name: 'FBATable',
  components: {
    'ex-slot': exSlot
  },
  props: {
    // 表格数据
    data: {
      type: Array,
      default: () => {
        return []
      }
    },
    // 表头数据
    rowHeader: {
      type: Array,
      default: () => {
        return []
      }
    },
    showCheckBox: {
      type: Boolean,
      default: () => {
        return false
      }
    },
    showIndex: {
      type: Boolean,
      default: () => {
        return false
      }
    },
    height: {
      type: String,
      default: () => {
        return 'auto'
      }
    },
    spanMethod: {
      type: Function,
      default: () => {
        return ''
      }
    },
    PaginationData: {
      type: Object,
      default: () => ({
        currentPage: 1,
        pageSize: 10,
        total: 0
      })
    },
    isPagination: {
      type: Boolean,
      default: () => {
        return false
      }
    },
    showHeaderStyle: {
      type: Boolean,
      default: () => {
        return true
      }
    }
  },
  methods: {
    handleSelectionChange(val) {
      this.$emit('getSelection', val)
    },
    handleRowClick(row) {
      this.$emit('rowClick', row)
    },
    // 	pageSize 改变时会触发
    handleSizeChange(val) {
      this.$emit('pageSizeChange', val)
    },
    // currentPage 改变时会触发
    handleCurrentChange(val) {
      this.$emit('pageCurrentChange', val)
    }
  }
}
</script>
<style lang="scss" scoped>
.tableHeader {
  background: #1890ff;
}
.pagination-end {
  width: 100%;
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
