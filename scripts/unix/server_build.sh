#!/bin/sh
# 中微子代理服务端编译打包脚本，基础参数请自行修改

export JAVA_HOME=/Users/yangwen/my/service/graalvm/graalvm-community-openjdk-17.0.8+7.1/Contents/Home
export MAVEN_HOME=/Users/yangwen/my/service/maven/apache-maven-3.8.1
export PATH=:$PATH:$JAVA_HOME/bin:$MAVEN_HOME/bin

deployDir="deploy"
serverDeployDir=$deployDir"/server"

#切到项目根目录
cd ../..
#初始化文件夹
#if [ ! -d "$deployDir" ];then
#  mkdir $deployDir
#fi
#if [ ! -d "$serverDeployDir" ];then
#  mkdir $serverDeployDir
#fi
mkdir -p $serverDeployDir
rm -rf $serverDeployDir/neutrino-proxy-server.jar

#服务端打包
mvn clean install -U -pl neutrino-proxy-server -am -Dmaven.test.skip=true
# 拷贝到deploy目录下
cp ./neutrino-proxy-server/target/neutrino-proxy-server.jar $serverDeployDir/neutrino-proxy-server.jar
