@echo off
@rem console output code changed to UTF-8
chcp 65001
@rem  basic params
set MODULE_NAME="neutrino-proxy-server"
@rem  step
cd %~dp0 && cd ../
call mvn clean package -pl %MODULE_NAME% -am -Dmaven.test.skip=true