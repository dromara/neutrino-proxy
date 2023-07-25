#!/bin/sh

# 镜像版本，每次更新版本时需要调整
ImageVer=1.8.5
ImageName=neutrino-proxy-client
DockerFilePath=$PWD/../../neutrino-proxy-client/Dockerfile

#echo '打包jar...'
sh ./client_build.sh

# 删除老的本地镜像
docker rmi -f $(docker images | grep $ImageName | awk '{print $3}')
# 构建镜像
docker build -t $ImageName:$ImageVer -t $ImageName:latest -f $DockerFilePath $PWD/../..

