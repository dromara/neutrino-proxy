#!/bin/sh

# 镜像版本，每次更新版本时需要调整
ImageVer=2.0.2
ImageName=neutrino-proxy
DockerFilePath=$PWD/../../neutrino-proxy-server

#echo '打包管理后台...'
sh ./admin_build_docker.sh
#echo '打包jar...'
sh ./server_build.sh

# 删除老的本地镜像
docker rmi -f $(docker images | grep $ImageName | awk '{print $3}')
# 构建镜像
docker build -t $ImageName:$ImageVer -t $ImageName:latest $DockerFilePath

# docker run -it -p 9000-9200:9000-9200/tcp -p 8888:8888 --name np-server neutrino-proxy:2.0.0
