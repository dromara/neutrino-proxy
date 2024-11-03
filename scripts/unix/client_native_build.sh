#!/bin/sh
# 中微子代理客户端编译打包脚本，基础参数请自行修改

export JAVA_HOME=/Users/yangwen/my/service/graalvm/graalvm-community-openjdk-21.0.1+12.1/Contents/Home
export MAVEN_HOME=/Users/yangwen/my/service/maven/apache-maven-3.8.1
export PATH=:$PATH:$JAVA_HOME/bin:$MAVEN_HOME/bin

MODULE_NAME=neutrino-proxy-client
deployDir="deploy"
clientDeployDir=$deployDir"/client"
machine=macos
VER=2.0.2

#切到项目根目录
cd ../..
#初始化文件夹
mkdir -p $clientDeployDir

# 删除原来的编译文件
rm -rf $clientDeployDir/$MODULE_NAME.jar
rm -rf $clientDeployDir/$MODULE_NAME
rm -rf $clientDeployDir/$MODULE_NAME-jdk21-$VER-jar
rm -rf $clientDeployDir/$MODULE_NAME-jdk21-$VER-jar.zip
rm -rf $clientDeployDir/$MODULE_NAME-$machine-$VER-native
rm -rf $clientDeployDir/$MODULE_NAME-$machine-$VER-native.zip

#客户端打包
mvn clean install -U -pl $MODULE_NAME -am -Dmaven.test.skip=true
cd $MODULE_NAME
mvn clean native:compile -P native -DskipTests
cd ..

# 给执行权限
chmod +x ./$MODULE_NAME/target/$MODULE_NAME

# 拷贝到deploy目录下
cp ./$MODULE_NAME/target/$MODULE_NAME.jar $clientDeployDir/$MODULE_NAME.jar
cp ./$MODULE_NAME/target/$MODULE_NAME $clientDeployDir/$MODULE_NAME

#打zip包，用于发版
cd $clientDeployDir
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
