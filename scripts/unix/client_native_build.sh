#!/bin/sh
# 中微子代理客户端编译打包脚本，基础参数请自行修改

export JAVA_HOME=/Users/yangwen/my/service/graalvm/graalvm-community-openjdk-17.0.8+7.1/Contents/Home
export MAVEN_HOME=/Users/yangwen/my/service/maven/apache-maven-3.8.1
export PATH=:$PATH:$JAVA_HOME/bin:$MAVEN_HOME/bin

deployDir="deploy"
clientDeployDir=$deployDir"/client"
machine=macos

#切到项目根目录
cd ../..
#初始化文件夹
mkdir -p $clientDeployDir

# 删除原来的编译文件
rm -rf $clientDeployDir/neutrino-proxy-client.jar
rm -rf $clientDeployDir/neutrino-proxy-client
rm -rf $clientDeployDir/neutrino-proxy-client-jar
rm -rf $clientDeployDir/neutrino-proxy-client-jar.zip
rm -rf $clientDeployDir/neutrino-proxy-client-${machine}-native
rm -rf $clientDeployDir/neutrino-proxy-client-${machine}-native.zip

#客户端打包
mvn clean install -U -pl neutrino-proxy-client -am -Dmaven.test.skip=true
cd neutrino-proxy-client
mvn clean native:compile -P native -DskipTests
cd ..

# 给执行权限
chmod +x ./neutrino-proxy-client/target/neutrino-proxy-client

# 拷贝到deploy目录下
cp ./neutrino-proxy-client/target/neutrino-proxy-client.jar $clientDeployDir/neutrino-proxy-client.jar
cp ./neutrino-proxy-client/target/neutrino-proxy-client $clientDeployDir/neutrino-proxy-client

#打zip包，用于发版
cd $clientDeployDir
## jar
mkdir neutrino-proxy-client-jar
cp ../../neutrino-proxy-client/target/neutrino-proxy-client.jar ./neutrino-proxy-client-jar/neutrino-proxy-client.jar
cp ../../neutrino-proxy-client/src/main/resources/app.yml ./neutrino-proxy-client-jar/app.yml
zip -r neutrino-proxy-client-jar.zip ./neutrino-proxy-client-jar
rm -rf ./neutrino-proxy-client-jar

## native
mkdir neutrino-proxy-client-${machine}-native
cp ../../neutrino-proxy-client/target/neutrino-proxy-client ./neutrino-proxy-client-${machine}-native/neutrino-proxy-client
cp ../../neutrino-proxy-client/src/main/resources/app.yml ./neutrino-proxy-client-${machine}-native/app.yml
zip -r neutrino-proxy-client-${machine}-native.zip ./neutrino-proxy-client-${machine}-native
rm -rf ./neutrino-proxy-client-${machine}-native
