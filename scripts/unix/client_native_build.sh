#!/bin/sh
# 中微子代理客户端编译打包脚本，基础参数请自行修改

export JAVA_HOME=/Users/yangwen/my/service/graalvm/graalvm-community-openjdk-17.0.8+7.1/Contents/Home
export MAVEN_HOME=/Users/yangwen/my/service/maven/apache-maven-3.8.1
export PATH=:$PATH:$JAVA_HOME/bin:$MAVEN_HOME/bin

deployDir="deploy"
clientDeployDir=$deployDir"/client"

#切到项目根目录
cd ../..
#初始化文件夹
mkdir -p $clientDeployDir

# 删除原来的编译文件
rm -rf $clientDeployDir/neutrino-proxy-client.jar
rm -rf $clientDeployDir/neutrino-proxy-client

#客户端打包
mvn clean install -U -pl neutrino-proxy-client -am -Dmaven.test.skip=true
cd neutrino-proxy-client
mvn clean native:compile -P native -DskipTests
cd ..

# 拷贝到deploy目录下
cp ./neutrino-proxy-client/target/neutrino-proxy-client.jar $clientDeployDir/neutrino-proxy-client.jar
cp ./neutrino-proxy-client/target/neutrino-proxy-client $clientDeployDir/neutrino-proxy-client

