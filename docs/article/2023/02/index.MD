# 内网穿透神器NeutrinoProxy 1.8.0版本发布

## 项目简介
- [中微子代理(neutrino-proxy)](https://gitee.com/dromara/neutrino-proxy) 是一款基于netty的内网穿透神器。该项目采用最为宽松的MIT协议，因此您可以对它进行复制、修改、传播并用于任何个人或商业行为。
- Gitee地址：https://gitee.com/dromara/neutrino-proxy
- 官网地址：http://neutrino-proxy.dromara.org
- 服务端管理后台截图：
<img src="./home.png" width="100%"/>

## 主要特点：
- 1、流量监控：首页图表、报表管理多维度流量监控。全方位掌握实时、历史代理数据。
- 2、用户/License：支持多用户、多客户端使用。后台禁用实时生效。
- 3、端口池：对外端口统一管理，支持用户、License独占端口。
- 4、端口映射：新增、编辑、删除、禁用实时生效。
- 5、Docker：服务端支持Docker一键部署。
- 6、SSL证书：支持SSL，保护您的信息安全。
- 7、域名映射：支持绑定子域名，方便本地调试三方回调
- 8、采用最为宽松的MIT协议，免去你的后顾之忧

## 快速使用
> 更多使用姿势、细节请通过官网或结尾微信二维码加我备注"中微子代理"入群交流。

### 1、 部署服务端
#### 1.1、 Docker一键部署
> 当前最新版本为1.8.0，下面的脚本中，可以使用：`registry.cn-hangzhou.aliyuncs.com/asgc/neutrino-proxy:1.8.0` 指定版本安装，推荐使用`latest`直接安装最新版。

##### 使用默认sqlite数据库
```shell
docker run -it -p 9000-9200:9000-9200/tcp -p 8888:8888 \
-d --restart=always --name neutrino-proxy \
registry.cn-hangzhou.aliyuncs.com/asgc/neutrino-proxy:latest
```

##### 指定自己的mysql数据库
- 在服务器上创建目录：/root/neutrino-proxy/config
- 在该目录下创建`app.yml`文本文件，并配置如下内容：
```yml
neutrino:
  data:
    db:
      type: mysql
      # 自己的数据库实例，创建一个空的名为'neutrino-proxy'的数据库即可，首次启动服务端会自动初始化
      url: jdbc:mysql://xxxx:3306/neutrino-proxy?useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true&useAffectedRows=true&useSSL=false
      driver-class: com.mysql.jdbc.Driver
      # 数据库帐号
      username: xxx
      # 数据库密码
      password: xxx
```
- 然后执行如下命令：
```shell
docker run -it -p 9000-9200:9000-9200/tcp -p 8888:8888 \
-v /root/neutrino-proxy/config:/root/neutrino-proxy/config \
-d --restart=always --name neutrino \
registry.cn-hangzhou.aliyuncs.com/asgc/neutrino-proxy:latest
```

#### 1.2、使用jar包自行部署
- 首先确保服务器上已安装java8运行环境
- 打开[发行版页面](https://gitee.com/dromara/neutrino-proxy/releases)，下载最新的release包：`neutrino-proxy-server.jar`、`neutrino-proxy-admin.zip`
- 在服务器上新建部署目录：`/work/projects/neutrino-proxy-server`
- 将` neutrino-proxy-server.jar`、`neutrino-proxy-admin.zip`上传至服务器部署目录。
- 解压`neutrino-proxy-admin.zip`文件
- 执行命令`java -jar neutrino-proxy-server.jar`启动服务端完成部署，默认使用sqlite数据库。
- 若需要指定自己的mysql数据库，同样的需要在当前目录下新建`app.yml`文件，文件内容同上。执行命令`java -jar neutrino-proxy-server.jar config=app.yml`启动服务端完成部署
- 可参照 https://gitee.com/dromara/neutrino-proxy/blob/master/bin/server_start.sh 使用shell脚本启动服务端。

### 2、管理后台配置
- 服务端部署成功后，访问`http://{服务端IP}:8888`打开后台管理页面。
- 使用默认的管理员帐号登录：admin/123456
- 打开`代理配置>License管理`页面，可以看到系统已经自动为管理员初始化了一条License记录，复制该`LicenseKey`备用，后续客户端配置需要。
- 打开`代理配置>端口映射`页面，可以看到系统已经自动为初始化了几条端口映射。可根据需要自行添加、修改。这里我们以`9101 -> 127.0.0.1:8080`映射为例

### 3、启动客户端
- 首先确保本地已安装java8运行环境
- 打开[发行版页面](https://gitee.com/dromara/neutrino-proxy/releases)，下载最新的release包：`neutrino-proxy-client.jar`
- 在本地`neutrino-proxy-client.jar`同级别目录下新建`app.yml`文件，并配置如下内容：
```yml
neutrino:
  proxy:
    client:
      # ssl证书密钥（使用jjar包内自带的证书，则此处无需修改）
      key-store-password: 123456
      # ssl证书管理密钥（使用jjar包内自带的证书，则此处无需修改。自定义证书，则此处配置对应的路径）
      jks-path: classpath:/test.jks
      # 代理服务端IP
      server-ip: localhost
      # 代理服务端IP, 若是非ssl端口，则ssl-enable需要配置为false
      server-port: 9002
      # 是否启用ssl
      ssl-enable: true
      # licenseKey，客户端凭证。此处需要配置刚刚从管理后台复制的LicenseKey
      license-key: xxxx
```
- 执行命令`java -jar neutrino-proxy-client.jar`启动客户端
- 查看服务端License管理，刷新页面，对应的License在线状态为`在线`，则表明客户端已正常连接。

### 4、代理验证
- 本地启动被代理服务，如：redis、本地web项目、本地mysql等等
- 先确保本地能正常访问被代理服务，如果本地都不能访问，不用想代理更不可能！！！
- 通过服务端IP+9101（上面License配置的端口映射重的服务端端口）访问本地被代理服务

## 联系我们
笔者时间、能力有限，且开源项目非一朝一夕之事，存在众多问题亦在所难免。使用、学习过程中有任何问题欢迎大家与我联系。

对项目有什么想法或者建议，可以加我微信拉交流群，或者创建[issues](https://gitee.com/dromara/neutrino-proxy/issues)，一起完善项目

- 微信号：yuyunshize
- Email: aoshiguchen@dromara.org
- 中微子代理官网：http://neutrino-proxy.dromara.org
- 中微子代理仓库：https://gitee.com/dromara/neutrino-proxy
- 微信二维码：
<img src="MyWeChatQRCode.jpeg" width="100%"/>