#!/bin/sh
# 中微子代理管理后台编译打包脚本，基础参数请自行修改

#环境
env=dev
nvmDir=$HOME/.nvm
nodeVersion=v13.12.0
deployDir="deploy"
serverDeployDir=$deployDir"/server"
adminDeployDir=$serverDeployDir"/neutrino-proxy-admin"

#设置nvm生效
export NVM_DIR="$HOME/.nvm"
[ -s "$NVM_DIR/nvm.sh" ] && \. "$NVM_DIR/nvm.sh"
 # This loads nvm
[ -s "$NVM_DIR/bash_completion" ] && \. "$NVM_DIR/bash_completion"

#初始化文件夹
cd ..
rm -rf $adminDeployDir
if [ ! -d "$deployDir" ];then
  mkdir $deployDir
fi
if [ ! -d "$serverDeployDir" ];then
  mkdir $serverDeployDir
fi
if [ ! -d "$adminDeployDir" ];then
  mkdir $adminDeployDir
fi
#切node版本
nvm use $nodeVersion
#进入admin项目目录
cd ./neutrino-proxy-admin
#删除之前的build
rm -rf ./dist
#安装依赖
npm i
#编译
npm run build:$env
#拷贝
cd ..
cp -rf ./neutrino-proxy-admin/dist $adminDeployDir/
cp -rf ./neutrino-proxy-admin/dist/ $giteePagesDir