package org.dromara.neutrinoproxy.client.starter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author: gc.x
 * @date: 2024/1/21
 */
@SpringBootApplication
@Slf4j
public class SpringProxyClient {

    public static void main(String[] args) {
        SpringApplication.run(SpringProxyClient.class, args);
    }

}
