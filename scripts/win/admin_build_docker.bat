@echo off
@rem  basic params
set ENV=docker
set NODE_VERSION=v13.12.0
set MODULE_NAME=neutrino-proxy-admin
@rem  判断当前node版本是否符合，如果不符合切换node版本
for /f "tokens=1" %%v in ('node -v') do set v=%%v
if not "%v%" == "%NODE_VERSION%" (
  echo Node.js version is not %NODE_VERSION%.
  @rem  判断是否安装nvm
  set "nvm_home=%NVM_HOME%"
  set "nvm_symlink=%NVM_SYMLINK%"
  if not defined nvm_home (
    echo nvm is not installed,then use nvm to install node %NODE_VERSION%.
  ) else if not defined nvm_symlink (
    echo nvm is not installed,then use nvm to install node %NODE_VERSION%.
  ) else (
    echo nvm is installed.
    nvm use %NODE_VERSION%
  )
)
cd %~dp0 && cd ../../%MODULE_NAME%
@rem 检查dist文件夹是否存在，存在就删除，/s选项表示将目录及其所有子目录一起删除，/q选项表示不需要确认操作
if exist dist (
  rmdir /s /q dist
)
@rem 等待npm执行完毕
call npm install
npm run build:%ENV% && pause