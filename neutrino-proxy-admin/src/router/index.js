import Vue from 'vue'
import Router from 'vue-router'
const _import = require('./_import_' + process.env.NODE_ENV)
// in development-env not use lazy-loading, because lazy-loading too many pages will cause webpack hot update too slow. so only in production use lazy-loading;
// detail: https://panjiachen.github.io/vue-element-admin-site/#/lazy-loading

Vue.use(Router)

/* Layout */
import Layout from '../views/layout/Layout'

/** note: submenu only apppear when children.length>=1
*   detail see  https://panjiachen.github.io/vue-element-admin-site/#/router-and-nav?id=sidebar
**/

/**
* hidden: true                   if `hidden:true` will not show in the sidebar(default is false)
* redirect: noredirect           if `redirect:noredirect` will no redirct in the breadcrumb
* name:'router-name'             the name is used by <keep-alive> (must set!!!)
* meta : {
    roles: ['admin','editor']     will control the page roles (you can set multiple roles)
    title: 'title'               the name show in submenu and breadcrumb (recommend set)
    icon: 'svg-name'             the icon show in the sidebar,
    noCache: true                if fasle ,the page will no be cached(default is false)
  }
**/
export const constantRouterMap = [
  { path: '/login', component: _import('login/index'), hidden: true },
  { path: '/authredirect', component: _import('login/authredirect'), hidden: true },
  { path: '/404', component: _import('errorPage/404'), hidden: true },
  { path: '/401', component: _import('errorPage/401'), hidden: true },
  {
    path: '',
    component: Layout,
    redirect: 'home',
    children: [{
      path: 'home',
      component: _import('home/index'),
      name: 'home',
      meta: { title: 'home', icon: 'dashboard', noCache: true }
    }]
  }
]

export default new Router({
  // mode: 'history', // require service support
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRouterMap
})

export const asyncRouterMap = [
  { path: '*', redirect: '/404', hidden: true },
  {
    path: '/proxy',
    component: Layout,
    redirect: 'noredirect',
    name: 'proxy',
    meta: {
      title: 'proxy',
      icon: 'component'
    },
    children: [
      { path: 'license', component: _import('proxy/license'), name: 'license', meta: { title: 'license' }},
      { path: 'portMapping', component: _import('proxy/portMapping'), name: 'portMapping', meta: { title: 'portMapping' }}
    ]
  },
  {
    path: '/system',
    component: Layout,
    redirect: 'noredirect',
    name: 'system',
    meta: {
      title: 'system',
      icon: 'component'
    },
    children: [
      { path: 'user', component: _import('system/user'), name: 'user', meta: { title: 'user' }},
      { path: 'portGroup', component: _import('system/portGroup'), name: 'portGroup', meta: { title: 'portGroup' }},
      { path: 'portPool', component: _import('system/portPool'), name: 'portPool', meta: { title: 'portPool' }},
      { path: 'securityGroup', component: _import('system/securityGroup'), name: 'securityGroup', meta: { title: 'securityGroup' }},
      { path: 'securityRule', component: _import('system/securityRule'), name: 'securityRule', meta: { title: 'securityRule' }, hidden: true},
      { path: 'protocal', component: _import('system/protocal'), name: 'protocal', meta: { title: 'protocal' }},
      { path: 'jobManager', component: _import('system/jobManager'), name: 'jobManager', meta: { title: 'jobManager' }}
    ]
  },
  {
    path: '/report',
    component: Layout,
    redirect: 'noredirect',
    name: 'report',
    meta: {
      title: 'report',
      icon: 'component'
    },
    children: [
      { path: 'userFlowReport', component: _import('report/userFlowReport'), name: 'userFlowReport', meta: { title: 'userFlowReport' }},
      { path: 'licenseFlowReport', component: _import('report/licenseFlowReport'), name: 'licenseFlowReport', meta: { title: 'licenseFlowReport' }},
      { path: 'userFlowMonthReport', component: _import('report/userFlowMonthReport'), name: 'userFlowMonthReport', meta: { title: 'userFlowMonthReport' }},
      { path: 'licenseFlowMonthReport', component: _import('report/licenseFlowMonthReport'), name: 'licenseFlowMonthReport', meta: { title: 'licenseFlowMonthReport' }}
    ]
  },
  {
    path: '/log',
    component: Layout,
    redirect: 'noredirect',
    name: 'log',
    meta: {
      title: 'log',
      icon: 'component'
    },
    children: [
      { path: 'jobLog', component: _import('log/jobLog'), name: 'jobLog', meta: { title: 'jobLog' }},
      { path: 'loginLog', component: _import('log/loginLog'), name: 'loginLog', meta: { title: 'loginLog' }},
      { path: 'clientConnectLog', component: _import('log/clientConnectLog'), name: 'clientConnectLog', meta: { title: 'clientConnectLog' }}
    ]
  }
]
