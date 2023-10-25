#!/bin/sh
# 中微子代理客户端启动脚本，基础参数请自行修改

JAVA_OPS="-server -Xms256m -Xmx512m -XX:+HeapDumpOnOutOfMemoryError  -XX:HeapDumpPath=/work/$NAME/heapError/"
export JAVA_HOME=/Users/yangwen/my/service/graalvm/graalvm-community-openjdk-17.0.8+7.1/Contents/Home
export PATH=:$PATH:$JAVA_HOME/bin
export CLASSPATH=.:$JAVA_HOME/jre/lib/rt.jar:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
mkdir -p /work/$NAME/heapError/

NAME=neutrino-proxy-client
WORK=$PWD/../../deploy/client
OUT=$WORK/$NAME.out
JAR_PATH=$WORK

#客户端配置
jksPath=classpath:/test.jks
serverIp=localhost
serverPort=9002
sslEnable=true
licenseKey=b0a907332b474b25897c4dcb31fc7eb6
startupParams="jksPath=$jksPath serverIp=$serverIp serverPort=$serverPort sslEnable=$sslEnable licenseKey=$licenseKey"

function start(){
PID=`jps -l | grep $NAME.jar | cut -d' ' -f 1`
if [ -n "$PID" ]; then
echo "kill  pid : $PID"
kill $PID
fi
sleep 3
if [ -n "$PID" ]; then
echo "kill fail & kill -9  pid : $PID"
kill -9  $PID
fi
if [ -f "$OUT" ]; then
time=$(date "+%Y%m%d-%H%M%S")
cp $OUT $JAR_PATH/logs/back_$time.out
fi
rm -f $OUT
cd $JAR_PATH
nohup java -Dfile.encoding=utf-8 $JAVA_OPS -jar $NAME.jar $startupParams > $OUT 2>&1 &
echo "sleep 15s wating service start"
sleep 15
tail -200 $OUT
echo "start ${JAR_PATH} success ;log path:$OUT"
}

start
