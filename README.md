# 1、简介
中微子代理（neutrino-proxy）是一个基于netty的、开源的java内网穿透项目。遵循MIT许可，因此您可以对它进行复制、修改、传播并用于任何个人或商业行为。

# 2、项目结构
- neutrino-proxy 
    - neutrino-core     与代理无关的基础封装
    - neutrino-proxy-core       与代理相关的公共常量、编解码器
    - neutrino-proxy-client     代理客户端项目
    - neutrino-proxy-server     代理服务端项目
    - neutrino-proxy-admin      代理监控项目（基于vue-element-admin开发）

# 3、运行
## 3.1、使用keytool工具生成ssl证书, 若不需要ssl加密可跳过
```shell
keytool -genkey -alias test1 -keyalg RSA -keysize 1024 -validity 3650 -keypass 123456 -storepass 123456 -keystore  "./test.jks"
```

## 3.2、修改服务端配置（application.yml）
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
  # license配置, 客户端连接时需要用这个进行校验
  license:
    # license数为3表示用该license连接的客户端最多可代理3个端口，-1为不限
    79419a1a8691413aa5e845b9e3e90051: 3
    9352b1c25f564c81a5677131d7769876: 2
```

## 3.3、启动服务端
> fun.asgc.neutrino.proxy.server.ProxyServer

## 3.4、修改客户端配置
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

## 3.5、准备代理信息配置文件 config.json
```
{
    "environment": "我的Mac",
    "clientKey": "79419a1a8691413aa5e845b9e3e90051",  # 对应服务端配置license中的key
    "proxy": [
        {
            "serverPort": 9100,         # 外网服务器对外暴露的端口
            "clientInfo": "127.0.0.1:3306" # 需要代理的本地端口(mysql)
        },
        {
            "serverPort": 9101, # 外网服务器对外暴露的端口
            "clientInfo": "rm-xxxx.mysql.rds.aliyuncs.com:3306" # 代理外网端口本身无意义，仅供测试
        },
        {
            "serverPort": 9102, # 外网服务器对外暴露的端口
            "clientInfo": "127.0.0.1:8080" # 需要代理的本地端口(http)
        }
    ]
}
```

## 3.6、启动客户端
> fun.asgc.neutrino.proxy.client.ProxyClient
默认情况下，客户端会加载当前目录下的config.json文件作为代理配置，可通过命令行参数指定，如：java -jar neutrino-proxy-client.jar /xxx/proxy.json

# 4、未来迭代方向
- 优化代码、增强稳定性
- 服务端增加管理页面，提供报表、授权、限流等功能
- 从项目中分离、孵化出另一个开源项目(neutrino-framework)

# 5、技术文档
- [Aop](./docs/Aop.MD)

# 6、联系我们
- 微信: yuyunshize
- Gitee(主更): https://gitee.com/asgc/neutrino-proxy
- Github: https://github.com/aoshiguchen/neutrino-proxy

# 7、特别鸣谢
* [JetBrains](https://www.jetbrains.com?from=neutrino-proxy)

![JenBrains logo](assets/jetbrains.svg)