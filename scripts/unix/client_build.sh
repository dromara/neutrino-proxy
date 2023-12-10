#!/bin/sh
# 中微子代理客户端编译打包脚本，基础参数请自行修改

#export JAVA_HOME=/Users/yangwen/my/service/graalvm/graalvm-community-openjdk-21.0.1+12.1/Contents/Home
export JAVA_HOME=/Users/yangwen/my/service/jdk/jdk17/jdk-17.0.4.1.jdk/Contents/Home
export MAVEN_HOME=/Users/yangwen/my/service/maven/apache-maven-3.8.1
export PATH=:$PATH:$JAVA_HOME/bin:$MAVEN_HOME/bin

deployDir="deploy"
clientDeployDir=$deployDir"/client"

#切到项目根目录
cd ../..
#初始化文件夹
mkdir -p $clientDeployDir
rm -rf $clientDeployDir/neutrino-proxy-client.jar
rm -rf $clientDeployDir/neutrino-proxy-client-jar
rm -rf $clientDeployDir/neutrino-proxy-client-jar.zip

#客户端打包
mvn clean install -U -pl neutrino-proxy-client -am -Dmaven.test.skip=true
# 拷贝到deploy目录下
cp ./neutrino-proxy-client/target/neutrino-proxy-client.jar $clientDeployDir/neutrino-proxy-client.jar

#打zip包，用于发版
cd $clientDeployDir
mkdir neutrino-proxy-client-jar
cp ../../neutrino-proxy-client/target/neutrino-proxy-client.jar ./neutrino-proxy-client-jar/neutrino-proxy-client.jar
cp ../../neutrino-proxy-client/src/main/resources/app-copy.yml ./neutrino-proxy-client-jar/app.yml
zip -r neutrino-proxy-client-jar.zip ./neutrino-proxy-client-jar
rm -rf $clientDeployDir/neutrino-proxy-client-jar
