@echo off
@rem console output code changed to UTF-8
chcp 65001
@rem  basic params
set MODULE_NAME=neutrino-proxy-server
@rem  step
cd %~dp0 && cd ../../

SET VS_HOME=C:\Program Files\Microsoft Visual Studio\2022\Community\VC\Tools\MSVC\14.39.33519
SET JAVA_HOME=C:\my\package\jdk\graalvm-community-openjdk-21.0.2
SET MAVEN_HOME=C:\my\package\maven\apache-maven-3.9.6
SET WIN_RAR_HOME=C:\Program Files\WinRAR
SET PATH=%PATH%;%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%VS_HOME%\bin;%WIN_RAR_HOME%
SET INCLUDE=%INCLUDE%;%VS_HOME%\include;
SET LIB=%LIB%;%VS_HOME%\lib;

SET deployDir=deploy
SET serverDeployDir=%deployDir%\server
SET machine=windows
SET VER=2.0.1

@rem 初始化文件夹
mkdir -p %serverDeployDir%

@rem 删除原来的编译文件
del /S /Q /F %serverDeployDir%\%MODULE_NAME%.jar
del /S /Q /F %serverDeployDir%\%MODULE_NAME%.exe
del /S /Q /F %serverDeployDir%\%MODULE_NAME%-jdk21-%VER%-jar
del /S /Q /F %serverDeployDir%\%MODULE_NAME%-jdk21-%VER%-jar.zip
del /S /Q /F %serverDeployDir%\%MODULE_NAME%-%machine%-%VER%-native
del /S /Q /F %serverDeployDir%\%MODULE_NAME%-%machine%-%VER%-native.zip
rd /S /Q %serverDeployDir%\%MODULE_NAME%-jdk21-%VER%-jar
rd /S /Q  %serverDeployDir%\%MODULE_NAME%-%machine%-%VER%-native

@rem 服务端打包
call mvn clean install -U -pl %MODULE_NAME% -am -Dmaven.test.skip=true
cd %MODULE_NAME%
call mvn clean native:compile -P native -DskipTests
cd ..

@rem 拷贝到deploy目录下
copy .\%MODULE_NAME%\target\%MODULE_NAME%.jar .\%serverDeployDir%\%MODULE_NAME%.jar
copy .\%MODULE_NAME%\target\%MODULE_NAME%.exe .\%serverDeployDir%\%MODULE_NAME%.exe

@rem 打zip包，用于发版
cd %serverDeployDir%
@rem jar
mkdir %MODULE_NAME%-jdk21-%VER%-jar
copy %MODULE_NAME%.jar %MODULE_NAME%-jdk21-%VER%-jar
copy ..\..\%MODULE_NAME%\src\main\resources\app-copy.yml %MODULE_NAME%-jdk21-%VER%-jar\app.yml
Rar a %MODULE_NAME%-jdk21-%VER%-jar.zip %MODULE_NAME%-jdk21-%VER%-jar

@rem native
mkdir %MODULE_NAME%-%machine%-%VER%-native
copy %MODULE_NAME%.exe %MODULE_NAME%-%machine%-%VER%-native
copy ..\..\%MODULE_NAME%\src\main\resources\app-copy.yml %MODULE_NAME%-%machine%-%VER%-native\app.yml
Rar a %MODULE_NAME%-%machine%-%VER%-native.zip %MODULE_NAME%-%machine%-%VER%-native

@rem 删除临时文件


@rem 回到脚本目录
cd ..\..\scripts\win