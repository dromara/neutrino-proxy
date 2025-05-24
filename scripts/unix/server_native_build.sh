#!/bin/sh
# 中微子代理服务端编译打包脚本，基础参数请自行修改

export JAVA_HOME=/Users/yangwen/my/service/graalvm/graalvm-community-openjdk-21.0.1+12.1/Contents/Home
export MAVEN_HOME=/Users/yangwen/my/service/maven/apache-maven-3.8.1
export PATH=:$PATH:$JAVA_HOME/bin:$MAVEN_HOME/bin

MODULE_NAME=neutrino-proxy-server
deployDir="deploy"
serverDeployDir=$deployDir"/server"
machine=macos
VER=2.0.3

#切到项目根目录
cd ../..
#初始化文件夹
mkdir -p $serverDeployDir

# 删除原来的编译文件
rm -rf $serverDeployDir/$MODULE_NAME.jar
rm -rf $serverDeployDir/$MODULE_NAME
rm -rf $serverDeployDir/$MODULE_NAME-jdk21-$VER-jar
rm -rf $serverDeployDir/$MODULE_NAME-jdk21-$VER-jar.zip
rm -rf $serverDeployDir/$MODULE_NAME-$machine-$VER-native
rm -rf $serverDeployDir/$MODULE_NAME-$machine-$VER-native.zip

# 不需要每次都重新编译一次前端代码，如果前端代码有变更，编译前需要手动执行`admin_build_docker.sh`
##前端页面打包
#rm -rf ./neutrino-proxy-server/src/main/resources/static
#cd ./scripts/unix
#sh admin_build_docker.sh
#cd ../..
#mkdir -p neutrino-proxy-server/src/main/resources/static
#cp -rf ./neutrino-proxy-admin/dist/ ./neutrino-proxy-server/src/main/resources/static

#服务端打包
mvn clean install -U -pl $MODULE_NAME -am -Dmaven.test.skip=true
cd $MODULE_NAME
mvn clean native:compile -P native -DskipTests
cd ..

# 给执行权限
chmod +x ./$MODULE_NAME/target/$MODULE_NAME

# 拷贝到deploy目录下
cp ./$MODULE_NAME/target/$MODULE_NAME.jar $serverDeployDir/$MODULE_NAME.jar
cp ./$MODULE_NAME/target/$MODULE_NAME $serverDeployDir/$MODULE_NAME

#打zip包，用于发版
cd $serverDeployDir
## jar
mkdir $MODULE_NAME-jdk21-$VER-jar
cp ../../$MODULE_NAME/target/$MODULE_NAME.jar ./$MODULE_NAME-jdk21-$VER-jar/$MODULE_NAME.jar
cp ../../$MODULE_NAME/src/main/resources/app-copy.yml ./$MODULE_NAME-jdk21-$VER-jar/app.yml
zip -r $MODULE_NAME-jdk21-$VER-jar.zip ./$MODULE_NAME-jdk21-$VER-jar
rm -rf ./$MODULE_NAME-jdk21-$VER-jar

## native
mkdir $MODULE_NAME-$machine-$VER-native
cp ../../$MODULE_NAME/target/$MODULE_NAME ./$MODULE_NAME-$machine-$VER-native/$MODULE_NAME
cp ../../$MODULE_NAME/src/main/resources/app-copy.yml ./$MODULE_NAME-$machine-$VER-native/app.yml
zip -r $MODULE_NAME-$machine-$VER-native.zip ./$MODULE_NAME-$machine-$VER-native
rm -rf ./$MODULE_NAME-$machine-$VER-native
