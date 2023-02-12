<template>
  <scroll-bar>
    <div class="log-div">
      <img :src="logCross"/>
    </div>
    <el-menu mode="vertical" :default-active="$route.path" :collapse="isCollapse" background-color="#304156" text-color="#bfcbd9" active-text-color="#409EFF">
      <sidebar-item :routes="permission_routers"></sidebar-item>
    </el-menu>
  </scroll-bar>
</template>

<script>
import { mapGetters } from 'vuex'
import SidebarItem from './SidebarItem'
import ScrollBar from '@/components/ScrollBar'
import logCross from '@/assets/logo/logo-text-cross.svg'

export default {
  components: { SidebarItem, ScrollBar },
  computed: {
    ...mapGetters([
      'permission_routers',
      'sidebar'
    ]),
    isCollapse() {
      return !this.sidebar.opened
    }
  },
  data() {
    return {
      logCross
    }
  }
}
</script>

<style lang="scss" scoped>
  .log-div{
    padding: 10px 0 5px 15px;
    img{
      height: 40px;
    }
  }
</style>
