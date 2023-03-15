<p align="center">
	<img src="https://i.imgtg.com/2023/02/10/crIMY.png" width="45%">
</p>
<p align="center">
  <a href='https://gitee.com/dromara/neutrino-proxy/stargazers'><img src='https://gitee.com/dromara/neutrino-proxy/badge/star.svg?theme=dark' alt='star'></img></a>
<a href='https://gitee.com/dromara/neutrino-proxy/members'><img src='https://gitee.com/dromara/neutrino-proxy/badge/fork.svg?theme=dark' alt='fork'></img></a>
</p>

# 1、简介
- 中微子代理（neutrino-proxy）是一个基于netty的、开源的java内网穿透项目。
- 技术栈：Solon、MybatisPlus、Netty
- 遵循MIT许可，因此您可以对它进行复制、修改、传播并用于任何个人或商业行为。

# 2、名称由来
中微子，是轻子的一种，是组成自然界的最基本的粒子之一。它十分微小、不带电，可自由穿过地球，以接近光速运动，与其他物质的相互作用十分微弱，号称宇宙间的“隐身人”。

中微子是宇宙中穿透能力最强的物质,只有粒子之间的间隙小于10的负19次方米时,才能够阻挡住中微子。

因此以中微子命名，寓意着中微子代理拥有中微子"安全"、"快速"、"穿透力强"的特点。

# 3、运行示例
![用户管理](assets/user-manager1.png)
![端口池管理](assets/port-pool1.png)
![License管理](assets/license1.png)
![端口映射管理](assets/port-mapping1.png)
![客户端启动示例](assets/client-run1.png)

# 4、项目结构
- neutrino-proxy 
    - neutrino-proxy-core       与代理相关的公共常量、编解码器
    - neutrino-proxy-client     代理客户端项目
    - neutrino-proxy-server     代理服务端项目
    - neutrino-proxy-admin      代理监控项目（基于vue-element-admin开发）

# 5、运行
## 5.1、使用keytool工具生成ssl证书, 若不需要ssl加密可跳过
```shell
keytool -genkey -alias test1 -keyalg RSA -keysize 1024 -validity 3650 -keypass 123456 -storepass 123456 -keystore  "./test.jks"
```

## 5.2、修改服务端配置（application.yml）
```yml
application:
  name: neutrino-proxy-server

proxy:
  protocol:
    max-frame-length: 2097152
    length-field-offset: 0
    length-field-length: 4
    initial-bytes-to-strip: 0
    length-adjustment: 0
    read-idle-time: 60
    write-idle-time: 40
    all-idle-time-seconds: 0
  server:
    # 服务端端口，用于保持与客户端的连接，非SSL
    port: 9000    
    # 服务端端口，用于保持与客户端的连接，SSL,需要jks证书文件，若不需要ssl支持，可不配置
    ssl-port: 9002
    # 证书密码
    key-store-password: 123456
    key-manager-password: 123456
    # 证书存放路径，若不想打进jar包，可不带classpath:前缀
    jks-path: classpath:/test.jks 
  data:
    # 数据库配置（支持mysql）
      type: sqlite
      url: jdbc:sqlite:data.db
      driver-class: org.sqlite.JDBC
      username:
      password:
```

## 5.3、启动服务端
> java -jar neutrino-proxy-server.jar

## 5.4、修改客户端配置
```yml
application:
  name: neutrino-proxy-client

proxy:
  protocol:
    max-frame-length: 2097152
    length-field-offset: 0
    length-field-length: 4
    initial-bytes-to-strip: 0
    length-adjustment: 0
    read-idle-time: 60
    write-idle-time: 30
    all-idle-time-seconds: 0
  client:
    # ssl证书密码
    key-store-password: 123456
    # ssl证书存放位置
    jks-path: classpath:/test.jks
    # 服务端ip，若部署到服务器，则配置服务器的ip
    server-ip: localhost
    # 服务端端口，若使用ssl，则需要配置为服务端的"ssl-port"
    server-port: 9000
    # 是否启用ssl，启用则必须配置ssl相关参数
    ssl-enable: false
```
## 5.5、代理示意图
![代理流程](assets/neutrino-proxy-process.jpg)

## 5.6、启动客户端
### 5.6.1、启动参数直接指定配置
> java -jar neutrino-proxy-client.jar serverIp=localhost serverPort=9000 licenseKey=b0a907332b474b25897c4dcb31fc7eb6

### 5.6.2、启动参数指定外部配置文件
> java -jar neutrino-proxy-client.jar config=app.properties

配置文件格式如下：
```
neutrino.proxy.client.server-ip=localhost
neutrino.proxy.client.server-port=9002
neutrino.proxy.client.ssl-enable=true
neutrino.proxy.client.key-store-password=123456
neutrino.proxy.client.jks-path=classpath:/test.jks
neutrino.proxy.client.license-key=b0a907332b474b25897c4dcb31fc7eb6
```

## 5.7、Docker快速启动
```shell script
    # 一键部署前端和后端，客户端按上方部署即可
    docker run -it -p 9000-9200:9000-9200/tcp -p 8888:8888 -d --name neutrino registry.cn-hangzhou.aliyuncs.com/asgc/aoshiguchen-docker-images:1.64
    # 如没docker环境，可以一键部署docker环境（centos 7.8）系统
    curl http://www.wangke666.cn/static/createDocker.sh | bash    
```

# 6、演示环境
> 可使用分配好的游客license试用。服务器带宽较低，仅供学习使用！
- 管理后台地址：http://103.163.47.16:9527
- 游客账号：visitor/123456

# 7、未来迭代方向
- 优化代码、增强稳定性
- 服务端增加管理页面，提供报表、授权、限流等功能
- 从项目中分离、孵化出另一个开源项目(neutrino-framework)

# 8、技术文档
- [Aop](./docs/Aop.MD)
- [Channel](./docs/Channel.MD)

# 9、联系我们
- 微信: yuyunshize
- Gitee: https://gitee.com/asgc/neutrino-proxy

#  ❤️ 感谢
* [Solon](https://gitee.com/noear/solon?from=NeutrinoProxy)
* [Hutool](https://hutool.cn?from=NeutrinoProxy)
* [JetBrains](https://www.jetbrains.com?from=NeutrinoProxy)

![JenBrains logo](assets/jetbrains.svg)

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
