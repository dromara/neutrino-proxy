#!/bin/sh

# 镜像版本，每次更新版本时需要调整
ImageVer=2.0.0
ImageName=neutrino-proxy-client
DockerFilePath=$PWD/../../neutrino-proxy-client

##初始化文件夹
#deployDir=$PWD/../../"deploy"
#clientDeployDir=$deployDir"/client"
#if [ ! -d "$deployDir" ];then
#  mkdir $deployDir
#fi
#if [ ! -d "$clientDeployDir" ];then
#  mkdir $clientDeployDir
#fi
#rm -rf $clientDeployDir/$ImageName.tar

#echo '打包jar...'
#sh ./client_build.sh

# 删除老的本地镜像
docker rmi -f $(docker images | grep $ImageName | awk '{print $3}')
# 构建镜像
docker build -t $ImageName:$ImageVer -t $ImageName:latest $DockerFilePath
# 保存镜像到本地
#docker save -o $clientDeployDir/$ImageName.tar $ImageName:$ImageVer

# docker run -it -e LICENSE_KEY=b0a907332b474b25897c4dcb31fc7eb6 -e SERVER_IP=host.docker.internal --name np-client neutrino-proxy-client:2.0.0
