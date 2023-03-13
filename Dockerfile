FROM openjdk:8-jdk-alpine
#同步时间
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories && \
    apk update && apk add wget unzip vim && apk add -U tzdata && \
    ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone
RUN mkdir -p /root/neutrino-proxy/config
RUN mkdir -p /root/neutrino-proxy/neutrino-proxy-admin
WORKDIR /root/neutrino-proxy
COPY neutrino-proxy-server/target/neutrino-proxy-server.jar /root/neutrino-proxy/neutrino-proxy-server.jar
COPY neutrino-proxy-server/src/main/resources/app.yml /root/neutrino-proxy/config
COPY neutrino-proxy-admin/dist /root/neutrino-proxy/neutrino-proxy-admin/dist
#VOLUME ["/root/neutrino-proxy"]
ENTRYPOINT ["java","-jar","neutrino-proxy-server.jar","config=./config/app.yml"]

#docker run -it -p 9000-9200:9000-9200/tcp -p 8888:8888 -v /root/neutrino-proxy/config:/root/neutrino-proxy/config -d --restart=always --name neutrino registry.cn-hangzhou.aliyuncs.com/asgc/aoshiguchen-docker-images:1.7