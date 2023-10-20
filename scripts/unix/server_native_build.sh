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
mkdir -p $serverDeployDir

# 删除原来的编译文件
rm -rf $serverDeployDir/neutrino-proxy-server.jar
rm -rf $serverDeployDir/neutrino-proxy-server

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

# 拷贝到deploy目录下
cp ./neutrino-proxy-server/target/neutrino-proxy-server.jar $serverDeployDir/neutrino-proxy-server.jar
cp ./neutrino-proxy-server/target/neutrino-proxy-server $serverDeployDir/neutrino-proxy-server

