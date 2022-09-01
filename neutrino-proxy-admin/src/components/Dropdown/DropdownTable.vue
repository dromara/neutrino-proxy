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
        <div class="demo-footer">
          <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="pagination.PageIndex"
            :page-sizes="[5, 10, 20, 50, 100]"
            :page-size="pagination.PageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="pagination.TotalCount"
          >
          </el-pagination>
        </div>
      </div>
      <el-input v-model="keyWords" @input="changeKey" :placeholder="placeholder" slot="reference" :CatId="CatId"> </el-input>
    </el-popover>
  </div>
</template>
<script>
  import { delay } from '@/utils/index'
  import { wfinFeeItemGetPageList } from '@/api/OverseasWarehouse/Cost'
  import FBATable from '@/components/FBATable'
  export default {
    props: {
      name: {
        type: String,
        default: ''
      },
      index: {
        type: Number,
        default: () => 0
      },
      placeholder: {
        type: String,
        default: '请选择'
      },
      CatId: {
        type: Number
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
        tableData: [],
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
    watch: {
      CatId: {
        handler(newVal, oldVal) {
          this.getList(newVal)
        },
        deep: true,
        immediate: true
      }
    },
    methods: {
      getList(CatId) {
        const { PageSize, PageIndex } = this.pagination
        wfinFeeItemGetPageList({
          PageSize,
          PageIndex,
          NameCn: this.keyWords,
          CatId: CatId ? CatId : 0
        })
          .then(res => {
            if (res.ErrorCode === 0) {
              this.tableData = res.Body.Items
              this.pagination.TotalCount = res.Body.TotalCount
            }
          })
          .catch(err => {
            console.log(err)
          })
      },
      handleSelectionChange(val) {
        if (val.length >= 2) {
          let arrays = val.splice(0, val.length - 1)
          arrays.forEach(row => {
            this.$refs.countryTableRef.$refs.FBATable.toggleRowSelection(row) //除了当前点击的，其他的全部取消选中
          })
        }
        this.$emit('selectedData', { list: val, index: this.index })
        this.timer = setTimeout(() => {
          this.popVisible = false
        }, 500)
      },
      handleRowClick(val) {
        this.$emit('selectedData', { list: val, index: this.index })
        this.timer = setTimeout(() => {
          this.popVisible = false
        }, 200)
      },
      // 搜索
      changeKey() {
        delay(() => {
          this.pagination.PageIndex = 1
          this.getList()
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
