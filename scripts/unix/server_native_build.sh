#!/bin/sh
# 中微子代理服务端编译打包脚本，基础参数请自行修改

export JAVA_HOME=/Users/yangwen/my/service/graalvm/graalvm-community-openjdk-21.0.1+12.1/Contents/Home
export MAVEN_HOME=/Users/yangwen/my/service/maven/apache-maven-3.8.1
export PATH=:$PATH:$JAVA_HOME/bin:$MAVEN_HOME/bin

deployDir="deploy"
serverDeployDir=$deployDir"/server"
machine=macos

#切到项目根目录
cd ../..
#初始化文件夹
mkdir -p $serverDeployDir

# 删除原来的编译文件
rm -rf $serverDeployDir/neutrino-proxy-server.jar
rm -rf $serverDeployDir/neutrino-proxy-server
rm -rf $serverDeployDir/neutrino-proxy-server-jar
rm -rf $serverDeployDir/neutrino-proxy-server-jar.zip
rm -rf $serverDeployDir/neutrino-proxy-server-${machine}-native
rm -rf $serverDeployDir/neutrino-proxy-server-${machine}-native.zip

# 不需要每次都重新编译一次前端代码，如果前端代码有变更，编译前需要手动执行`admin_build_docker.sh`
##前端页面打包
#rm -rf ./neutrino-proxy-server/src/main/resources/static
#cd ./scripts/unix
#sh admin_build_docker.sh
#cd ../..
#mkdir -p neutrino-proxy-server/src/main/resources/static
#cp -rf ./neutrino-proxy-admin/dist/ ./neutrino-proxy-server/src/main/resources/static

#服务端打包
mvn clean install -U -pl neutrino-proxy-server -am -Dmaven.test.skip=true
cd neutrino-proxy-server
mvn clean native:compile -P native -DskipTests
cd ..

# 给执行权限
chmod +x ./neutrino-proxy-server/target/neutrino-proxy-server

# 拷贝到deploy目录下
cp ./neutrino-proxy-server/target/neutrino-proxy-server.jar $serverDeployDir/neutrino-proxy-server.jar
cp ./neutrino-proxy-server/target/neutrino-proxy-server $serverDeployDir/neutrino-proxy-server

#打zip包，用于发版
cd $serverDeployDir
## jar
mkdir neutrino-proxy-server-jar
cp ../../neutrino-proxy-server/target/neutrino-proxy-server.jar ./neutrino-proxy-server-jar/neutrino-proxy-server.jar
cp ../../neutrino-proxy-server/src/main/resources/app.yml ./neutrino-proxy-server-jar/app.yml
zip -r neutrino-proxy-server-jar.zip ./neutrino-proxy-server-jar
rm -rf ./neutrino-proxy-server-jar

## native
mkdir neutrino-proxy-server-${machine}-native
cp ../../neutrino-proxy-server/target/neutrino-proxy-server ./neutrino-proxy-server-${machine}-native/neutrino-proxy-server
cp ../../neutrino-proxy-server/src/main/resources/app.yml ./neutrino-proxy-server-${machine}-native/app.yml
zip -r neutrino-proxy-server-${machine}-native.zip ./neutrino-proxy-server-${machine}-native
rm -rf ./neutrino-proxy-server-${machine}-native
