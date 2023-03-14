---
home: true

heroImage: /img/logo.png
heroText: Neutrino-Proxy1
tagline: 🚀一个基于 netty 的、开源的 java 内网穿透项目
actionText: 开始使用 →
actionLink: /pages/a2f161/
bannerBg: none # auto => 网格纹背景(有bodyBgImg时无背景)，默认 | none => 无 | '大图地址' | background: 自定义背景样式       提示：如发现文本颜色不适应你的背景时可以到palette.styl修改$bannerTextColor变量

features: # 可选的
  - title: 简介
    details: 包含三种典型的知识管理形态：结构化、碎片化、体系化。轻松打造属于你自己的知识管理平台
  - title: 简洁高效
    details: 以 Markdown 为中心的项目结构，内置自动化工具，以更少的配置完成更多的事。配合多维索引快速定位每个知识点
  - title: 名称由来
    details: 中微子，是轻子的一种，是组成自然界的最基本的粒子之一。它十分微小、不带电，可自由穿过地球，以接近光速运动，与其他物质的相互作用十分微弱，号称宇宙间的“隐身人”。中微子是宇宙中穿透能力最强的物质,只有粒子之间的间隙小于10的负19次方米时,才能够阻挡住中微子。 因此以中微子命名，寓意着中微子代理拥有中微子"安全"、"快速"、"穿透力强"的特点。

# 文章列表显示方式: detailed 默认，显示详细版文章列表（包括作者、分类、标签、摘要、分页等）| simple => 显示简约版文章列表（仅标题和日期）| none 不显示文章列表
postList: none
---
<p align="center">
  <a class="become-sponsor" href="/pages/1b12ed/">支持这个项目</a>
</p>

<style>
.become-sponsor {
  padding: 8px 20px;
  display: inline-block;
  color: #11a8cd;
  border-radius: 30px;
  box-sizing: border-box;
  border: 1px solid #11a8cd;
}
</style>

<br/>
<p align="center">
  <a href="https://www.npmjs.com/package/vuepress-theme-vdoing" target="_blank"><img src="https://img.shields.io/npm/v/vuepress-theme-vdoing" alt="npm" class="no-zoom"></a>
  <a href="https://www.npmjs.com/package/vuepress-theme-vdoing" target="_blank"><img src="https://img.shields.io/npm/dt/vuepress-theme-vdoing" alt="npm" class="no-zoom"></a>
  <a href="https://gitee.com/dromara/neutrino-proxy" target="_blank"><img src='https://gitee.com/dromara/neutrino-proxy/badge/star.svg?theme=dark' alt='star' class="no-zoom"></img></a>
  <a href="https://gitee.com/dromara/neutrino-proxy" target="_blank"><img src='https://gitee.com/dromara/neutrino-proxy/badge/fork.svg?theme=dark' alt='forks' class="no-zoom"></a>
</p>

<br/>
<!-- 注释掉
<p align="center" style="color: #999;">
  赞助商 (进入注册为主题作者充电)
</p>

<p align="center">
  <a href="http://apifox.cn/a103xugaoyi" target="_blank"><img src="https://cdn.staticaly.com/gh/xugaoyi/blog-gitalk-comment@master/img/441669861566_.2bedplbm21hc.jpg" alt="npm" class="no-zoom" style="width: 300px;border-radius: 2px;"></a>
</p>-->
<!-- 
## 🎖特别用户
::: cardList 3
```yaml
# - name: OpenHarmony
#   desc: 开放原子开源基金会
#   link: https://docs.openharmony.cn/pages/000000/
#   bgColor: '#f1f1f1'
#   textColor: '#2A3344'
- name: MyBatis-Plus官网
  desc: 🚀为简化开发而生
  link: https://baomidou.com/
  bgColor: '#f1f1f1'
  textColor: '#2A3344'
- name: Deepin 社区
  desc: Deepin 应用开发技术分享、DTK开发经验等
  link: https://docs.deepin.org
  bgColor: '#f1f1f1'
  textColor: '#2A3344'
- name: VForm官网
  desc: 低代码表单优选方案，拖拽式设计，一键生成源码
  link: http://www.vform666.com
  bgColor: '#f1f1f1'
  textColor: '#2A3344'
```
:::
-->
<br/>

## 🎉上新推荐
* `v1.12.x`
  - 新增配置项`pageStyle`，用于切换页面的风格样式，可选`卡片`、 `线条`风格。[详情](/pages/a20ce8/#pagestyle)
  - 新增配置项`bodyBgImgInterval`，用于在设置了多张背景大图时修改大图切换的时间间隔。[详情](/pages/a20ce8/#bodybgimginterval)
  - 新增配置项`defaultMode`，用于修改默认外观模式(v1.12.3)。[详情](/pages/a20ce8/#defaultmode)
* `v1.11.x`：新增配置项`extendFrontmatter`，用于扩展自动生成front matter。[详情](/pages/a20ce8/#extendfrontmatter)
* `v1.10.x`：新增右侧目录栏对h2~h6标题的适配，并优化了UI，[详情](/pages/8dfab5/)。
* `v1.9.x`：新增配置文件对TypeScript的支持，参考[config.ts](https://github.com/xugaoyi/vuepress-theme-vdoing/blob/master/docs/.vuepress/config.ts)。新增[标题标记](/pages/3216b0/#titletag)。
* `v1.8.x`：新增 Markdown中使用的组件：[代码块选项卡](/pages/197691/#代码块选项卡) 。
* `v1.7.x`：新增 [自定义html模块](/pages/a20ce8/#自定义html模块) 配置，可用于插入广告模块。
* `v1.6.x`：支持[`四级目录`](/pages/33d574/#级别说明)，提高[站点结构](/pages/33d574/#级别说明)可塑性。
* `v1.5.x`：新增[`笔记`容器](/pages/d0d7eb/)，轻松插入笔记框。
* `v1.4.x`：新增了文章内容区块的 [背景底纹配置](/pages/a20ce8/#文章内容块的背景底纹)，让你的文章看起来像笔记本的风格~
* `v1.2.x`：这个版本对整体的UI细节做了很多优化，比如标签栏和分类栏等
* `v1.1.x`：从这个版本开始主题新增`超好用`、`高颜值`的Markdown容器，快去 [体验](/pages/d0d7eb/) 吧~

更多上新请查阅：[**更新日志**](https://github.com/xugaoyi/vuepress-theme-vdoing/releases)

<br/>

<!-- ## ⚡️未来...

::: tip
期待 [VuePress v2.0](https://github.com/vuepress/vuepress-next) 以及 [VitePress](https://github.com/vuejs/vitepress) 的正式发布...

届时，VuePress 1.x 编译慢的缺点将得到极大的改善。我将会视情况把主题升级至 VuePress v2.0 或 VitePress。还希望大家多多 [:sparkling_heart:支持](/pages/1b12ed/) 哟，持续关注吧~
::: -->

<br/>

<!-- ## 💎 公众号
`有趣研究社`是本人对各种有趣的、好玩的、沙雕的创意和想法以在线小网站或者文章的形式表达出来，比如：
- [小霸王游戏机](https://game.xugaoyi.com)
- [爱国头像生成器](https://avatar.xugaoyi.com/)
- [到账语音生成器](https://zfb.xugaoyi.com/)

还有更多好玩的等你去探索吧~

::: center
<img src="https://fastly.jsdelivr.net/gh/xugaoyi/image_store@master/blog/qrcode.zdqv9mlfc0g.jpg"  style="width:190px;" />
:::

<br/> -->

## ⚡ 反馈与交流

在使用过程中有任何问题和想法，请给我提 [Issue](https://github.com/xugaoyi/vuepress-theme-vdoing/issues)。
你也可以在Issue查看别人提的问题和给出解决方案。

或者加入我们的交流群：

<table>
  <tbody>
    <tr>
      <td align="center" valign="middle">
        <img src="https://cdn.staticaly.com/gh/xugaoyi/blog-gitalk-comment@master/img/0.4pp7r95mdai0.jpeg" class="no-zoom" style="width:120px;margin: 10px;">
        <p>vdoing微信群(添加我微信备注"进群")</p>
      </td>
      <td align="center" valign="middle">
        <img :src="$withBase('/img/qrcode/qqq.webp')" alt="群号: 694387113" class="no-zoom" style="width:120px;margin: 10px;">
        <p>vdoing QQ群: 694387113</p>
      </td>
    </tr>
  </tbody>
</table>
<br/> 

# 📚 Dromara 成员项目
<p align="center">
<a href="https://gitee.com/dromara/TLog" target="_blank">
<img src="https://oss.dev33.cn/sa-token/link/tlog2.png" alt="一个轻量级的分布式日志标记追踪神器，10分钟即可接入，自动对日志打标签完成微服务的链路追踪" width="15%">
</a>
<a href="https://gitee.com/dromara/liteFlow" target="_blank">
<img src="https://oss.dev33.cn/sa-token/link/liteflow.png" alt="轻量，快速，稳定，可编排的组件式流程引擎" width="15%">
</a>
<a href="https://hutool.cn/" target="_blank">
<img src="https://oss.dev33.cn/sa-token/link/hutool.jpg" alt="小而全的Java工具类库，使Java拥有函数式语言般的优雅，让Java语言也可以“甜甜的”。" width="15%">
</a>
<a href="https://sa-token.dev33.cn/" target="_blank">
<img src="https://oss.dev33.cn/sa-token/link/sa-token.png" alt="一个轻量级 java 权限认证框架，让鉴权变得简单、优雅！" width="15%">
</a>
<a href="https://gitee.com/dromara/hmily" target="_blank">
<img src="https://oss.dev33.cn/sa-token/link/hmily.png" alt="高性能一站式分布式事务解决方案。" width="15%">
</a>
<a href="https://gitee.com/dromara/Raincat" target="_blank">
<img src="https://oss.dev33.cn/sa-token/link/raincat.png" alt="强一致性分布式事务解决方案。" width="15%">
</a>
</p>
<p align="center">
<a href="https://gitee.com/dromara/myth" target="_blank">
<img src="https://oss.dev33.cn/sa-token/link/myth.png" alt="可靠消息分布式事务解决方案。" width="15%">
</a>
<a href="https://cubic.jiagoujishu.com/" target="_blank">
<img src="https://oss.dev33.cn/sa-token/link/cubic.png" alt="一站式问题定位平台，以agent的方式无侵入接入应用，完整集成arthas功能模块，致力于应用级监控，帮助开发人员快速定位问题" width="15%">
</a>
<a href="https://maxkey.top/" target="_blank">
<img src="https://oss.dev33.cn/sa-token/link/maxkey.png" alt="业界领先的身份管理和认证产品" width="15%">
</a>
<a href="http://forest.dtflyx.com/" target="_blank">
<img src="https://oss.dev33.cn/sa-token/link/forest-logo.png" alt="Forest能够帮助您使用更简单的方式编写Java的HTTP客户端" width="15%">
</a>
<a href="https://jpom.io/" target="_blank">
<img src="https://oss.dev33.cn/sa-token/link/jpom.png" alt="一款简而轻的低侵入式在线构建、自动部署、日常运维、项目监控软件" width="15%">
</a>
<a href="https://su.usthe.com/" target="_blank">
<img src="https://oss.dev33.cn/sa-token/link/sureness.png" alt="面向 REST API 的高性能认证鉴权框架" width="15%">
</a>
</p>
<p align="center">
<a href="https://easy-es.cn/" target="_blank">
<img src="https://oss.dev33.cn/sa-token/link/easy-es2.png" alt="傻瓜级ElasticSearch搜索引擎ORM框架" width="15%">
</a>
<a href="https://gitee.com/dromara/northstar" target="_blank">
<img src="https://oss.dev33.cn/sa-token/link/northstar_logo.png" alt="Northstar盈富量化交易平台" width="15%">
</a>
<a href="https://hertzbeat.com/" target="_blank">
<img src="https://oss.dev33.cn/sa-token/link/hertzbeat_brand.jpg" alt="易用友好的云监控系统" width="15%">
</a>
<a href="https://plugins.sheng90.wang/fast-request/" target="_blank">
<img src="https://oss.dev33.cn/sa-token/link/fast-request.gif" alt="Idea 版 Postman，为简化调试API而生" width="15%">
</a>
<a href="https://www.jeesuite.com/" target="_blank">
<img src="https://oss.dev33.cn/sa-token/link/mendmix.png" alt="开源分布式云原生架构一站式解决方案" width="15%">
</a>
<a href="https://gitee.com/dromara/koalas-rpc" target="_blank">
<img src="https://oss.dev33.cn/sa-token/link/koalas-rpc2.png" alt="企业生产级百亿日PV高可用可拓展的RPC框架。" width="15%">
</a>
</p>
<p align="center">
<a href="https://async.sizegang.cn/" target="_blank">
<img src="https://oss.dev33.cn/sa-token/link/gobrs-async.png" alt="配置极简功能强大的异步任务动态编排框架" width="15%">
</a>
<a href="https://dynamictp.cn/" target="_blank">
<img src="https://oss.dev33.cn/sa-token/link/dynamic-tp.png" alt="基于配置中心的轻量级动态可监控线程池" width="15%">
</a>
<a href="https://www.x-easypdf.cn" target="_blank">
<img src="https://oss.dev33.cn/sa-token/link/x-easypdf.png" alt="一个用搭积木的方式构建pdf的框架（基于pdfbox）" width="15%">
</a>
<a href="http://dromara.gitee.io/image-combiner" target="_blank">
<img src="https://oss.dev33.cn/sa-token/link/image-combiner.png" alt="一个专门用于图片合成的工具，没有很复杂的功能，简单实用，却不失强大" width="15%">
</a>
<a href="https://www.herodotus.cn/" target="_blank">
<img src="https://oss.dev33.cn/sa-token/link/dante-cloud2.png" alt="Dante-Cloud 是一款企业级微服务架构和服务能力开发平台。" width="15%">
</a>
<a href="https://dromara.org/zh/projects/" target="_blank">
<img src="https://oss.dev33.cn/sa-token/link/dromara.png" alt="让每一位开源爱好者，体会到开源的快乐。" width="15%">
</a>
</p>
<!-- AD -->
<div class="wwads-cn wwads-horizontal page-wwads" data-id="136"></div>
<style>
  .page-wwads{
    width:100%!important;
    min-height: 0;
    margin: 0;
  }
  .page-wwads .wwads-img img{
    width:80px!important;
  }
  .page-wwads .wwads-poweredby{
    width: 40px;
    position: absolute;
    right: 25px;
    bottom: 3px;
  }
  .wwads-content .wwads-text, .page-wwads .wwads-text{
    height: 100%;
    padding-top: 5px;
    display: block;
  }
</style>
