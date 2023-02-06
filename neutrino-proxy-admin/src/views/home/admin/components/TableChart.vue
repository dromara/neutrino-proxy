<template>
  <div id="port-mapping-div" :style="{height:height+'px',width:width}">
    <el-table
      :data="tableChart"
      style="width: 100%"
      :default-sort = "{prop: 'date', order: 'descending'}"
      :height="height"
    >
      <el-table-column prop="date" label="日期" sortable />
      <el-table-column prop="name" label="姓名" sortable />
      <el-table-column prop="address" label="地址" sortable show-overflow-tooltip/>
    </el-table>
  </div>
</template>

<script>

export default {
  props: {
    width: {
      type: String,
      default: '100%'
    },
    height: {
      type: String,
      default: '340'
    },
    tableChart: {
      type: Array,
      default: []
    }
  },
  data() {
    return {

    }
  }
}
</script>
