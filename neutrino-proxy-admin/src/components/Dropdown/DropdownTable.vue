<template>
  <div>
    <el-popover v-model="popVisible" :width="width" trigger="click" placement="bottom">
      <div>
        <FBATable
          ref="countryTableRef"
          :data="tableData"
          height="300"
          :rowHeader="countryColumns"
          @rowClick="handleRowClick"
        ></FBATable>
      </div>
      <el-input v-model="name" :placeholder="placeholder" slot="reference"/>
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
      width: {
        type: Number,
        default: 700
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
        timer: null
      }
    },
    watch: {
      name(val) {
        console.log(val, this.name)
      }
    },
    methods: {
      handleRowClick(val) {
        this.$emit('selectedData', val)
        this.timer = setTimeout(() => {
          this.popVisible = false
        }, 200)
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
