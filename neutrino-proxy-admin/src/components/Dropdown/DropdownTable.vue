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
      <el-input v-model="name" :placeholder="placeholder" slot="reference" :disabled="disabled"/>
    </el-popover>
  </div>
</template>
<script>
  import FBATable from '@/components/FBATable'
  export default {
    props: {
      disabled: {
        type: Boolean,
        default: false
      },
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
            prop: 'userName',
            label: '用户名',
            align: 'center'
          },
          {
            prop: 'name',
            label: 'License',
            align: 'center'
          }
        ],
        popVisible: false,
        timer: null
      }
    },
    methods: {
      handleRowClick(val) {
        this.$emit('selectedData', val)
        this.$emit('update:name', val.name)
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
