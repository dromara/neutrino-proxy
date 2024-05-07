@echo off
@rem console output code changed to UTF-8
chcp 65001
@rem  basic params
set MODULE_NAME=neutrino-proxy-client
@rem  step
cd %~dp0 && cd ../../

SET VS_HOME=C:\Program Files\Microsoft Visual Studio\2022\Community\VC\Tools\MSVC\14.39.33519
SET JAVA_HOME=C:\my\package\jdk\graalvm-community-openjdk-21.0.2
SET MAVEN_HOME=C:\my\package\maven\apache-maven-3.9.6
SET PATH=%PATH%;%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%VS_HOME%\bin
SET INCLUDE=%INCLUDE%;%VS_HOME%\include;
SET LIB=%LIB%;%VS_HOME%\lib;

SET deployDir=deploy
SET clientDeployDir=%deployDir%\client
SET machine=windows

@rem 初始化文件夹
mkdir -p $clientDeployDir

@rem 删除原来的编译文件
del /Q /F %clientDeployDir%\%MODULE_NAME%.jar
del /Q /F %clientDeployDir%\%MODULE_NAME%
del /Q /F %clientDeployDir%\%MODULE_NAME%-jar
del /Q /F %clientDeployDir%\%MODULE_NAME%-jar.zip
del /Q /F %clientDeployDir%\%MODULE_NAME%-${machine}-native
del /Q /F %clientDeployDir%\%MODULE_NAME%-${machine}-native.zip

@rem 客户端打包
call mvn clean install -U -pl %MODULE_NAME% -am -Dmaven.test.skip=true
cd %MODULE_NAME%
call mvn clean native:compile -P native -DskipTests
cd ..

@rem 拷贝到deploy目录下
copy .\%MODULE_NAME%\target\%MODULE_NAME%.jar .\%clientDeployDir%\%MODULE_NAME%.jar
copy .\%MODULE_NAME%\target\%MODULE_NAME%.exe .\%clientDeployDir%\%MODULE_NAME%.exe

@rem 打zip包，用于发版
