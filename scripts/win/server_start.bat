@echo off
@rem console output code changed to UTF-8
chcp 65001
@rem  basic params
set MODULE_NAME="neutrino-proxy-server"
set JAR_NAME="neutrino-proxy-server.jar"
set JVM_OPTS="-Xmx256m" "-Xms256m"
@rem  step
cd %~dp0 && cd ../../%MODULE_NAME%/target
java -jar %JVM_OPTS% %JAR_NAME%