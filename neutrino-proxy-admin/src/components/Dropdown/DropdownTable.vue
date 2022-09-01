<template>
  <div>
    <el-popover v-model="popVisible" width="700" trigger="click" placement="bottom">
      <div>
        <FBATable
          ref="countryTableRef"
          :data="tableData"
          height="300"
          :rowHeader="countryColumns"
          @rowClick="handleRowClick"
        ></FBATable>
      </div>
      <el-input v-model="keyWords" :placeholder="placeholder" slot="reference"/>
    </el-popover>
  </div>
</template>
<script>
  import FBATable from '@/components/FBATable'
  export default {
    props: {
      name: {
        type: String,
        default: ''
      },
      placeholder: {
        type: String,
        default: '请选择'
      },
      tableData: {
        type: Array,
        default: []
      }
    },
    components: {
      FBATable
    },
    data() {
      return {
        pagination: {
          TotalCount: 0,
          PageIndex: 1,
          PageSize: 10
        },
        countryColumns: [
          {
            prop: 'NameCn',
            label: '中文名',
            align: 'center'
          },
          {
            prop: 'NameEn',
            label: '英文名',
            align: 'center'
          },
          {
            prop: 'FeeItemCode',
            label: '费用项编码',
            align: 'center'
          },
          {
            prop: 'FeeItemTypeStr',
            label: '费用类型',
            align: 'center'
          }
        ],
        popVisible: false,
        selected: [],
        timer: null
      }
    },
    created() {},
    computed: {
      keyWords: {
        get() {
          return this.name
        },
        set(val) {
          this.$emit('update:name', val)
        }
      }
    },
    methods: {
      handleRowClick(val, column) {
        this.$emit('selectedData', { list: val, index: column })
        this.timer = setTimeout(() => {
          this.popVisible = false
        }, 200)
      },
      handleSizeChange(val) {
        this.pagination.PageSize = val
        this.getList()
      },
      handleCurrentChange(val) {
        this.pagination.PageIndex = val
        this.getList()
      }
    },
    destroyed() {
      clearTimeout(this.timer)
    }
  }
</script>
<style lang="scss" scoped>
  .demo-form-inline {
    position: relative;
    margin: 15px 10px;
  }
  .demo-footer {
    display: flex;
    justify-content: flex-end;
  }
  .country-footer {
    text-align: center;
    position: relative;
    margin: 10px auto;
  }
</style>
