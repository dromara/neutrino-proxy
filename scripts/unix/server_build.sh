#!/bin/sh
# 中微子代理服务端编译打包脚本，基础参数请自行修改

export JAVA_HOME=/Users/yangwen/my/service/graalvm/graalvm-community-openjdk-21.0.1+12.1/Contents/Home
export MAVEN_HOME=/Users/yangwen/my/service/maven/apache-maven-3.8.1
export PATH=:$PATH:$JAVA_HOME/bin:$MAVEN_HOME/bin

deployDir="deploy"
serverDeployDir=$deployDir"/server"

#切到项目根目录
cd ../..
#初始化文件夹
mkdir -p $serverDeployDir
rm -rf $serverDeployDir/neutrino-proxy-server.jar
rm -rf $serverDeployDir/neutrino-proxy-server-jar
rm -rf $serverDeployDir/neutrino-proxy-server-jar.zip

#服务端打包
mvn clean install -U -pl neutrino-proxy-server -am -Dmaven.test.skip=true
# 拷贝到deploy目录下
cp ./neutrino-proxy-server/target/neutrino-proxy-server.jar $serverDeployDir/neutrino-proxy-server.jar

#打zip包，用于发版
cd $serverDeployDir
mkdir neutrino-proxy-server-jar
cp ../../neutrino-proxy-server/target/neutrino-proxy-server.jar ./neutrino-proxy-server-jar/neutrino-proxy-server.jar
cp ../../neutrino-proxy-server/src/main/resources/app.yml ./neutrino-proxy-server-jar/app.yml
zip -r neutrino-proxy-server-jar.zip ./neutrino-proxy-server-jar
rm -rf $serverDeployDir/neutrino-proxy-server-jar
