<template>
  <scroll-bar>
    <div class="log-max-div" v-if="sidebar.opened">
      <img :src="logCross"/>
    </div>
    <div class="log-mini-div" v-else>
      <img :src="logMini">
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
import logMini from '@/assets/logo/logo.svg'

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
      logCross,
      logMini
    }
  }
}
</script>

<style lang="scss" scoped>
  .log-max-div{
    padding: 10px 0 5px 15px;
    img{
      height: 40px;
    }
  }
  .log-mini-div{
    text-align: center;
    margin-top: 30px;
    margin-bottom: 10px;
    img{
      height: 20px;
    }
  }
</style>
