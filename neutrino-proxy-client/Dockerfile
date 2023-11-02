FROM openjdk:21-jdk-oracle
#同步时间
#RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories && \
#    apk update && apk add wget unzip vim && apk add -U tzdata && \
#    ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone
# 设置时区为北京时间
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
RUN mkdir -p /root/neutrino-proxy/config
WORKDIR /root/neutrino-proxy
COPY ./target/neutrino-proxy-client.jar /root/neutrino-proxy/neutrino-proxy-client.jar
COPY ./src/main/resources/app-copy.yml /root/neutrino-proxy/config/app.yml
#VOLUME ["/root/neutrino-proxy"]
ENTRYPOINT ["java","-jar","neutrino-proxy-client.jar","config=./config/app.yml"]

#docker run -it -d --restart=always --name np_client -e SERVER_IP=127.0.0.1
