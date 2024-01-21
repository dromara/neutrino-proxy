package org.dromara.neutrinoproxy.client.starter.util;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

/**
 * ApplicaitonContext工具类
 * @author: gc.x
 * @date: 2024/1/21
 */
public class ApplicationContextUtils {

    private static ApplicationContext context;

    public static void setContext(ApplicationContext applicationContext) {
        context = applicationContext;
    }

    public static Object getBean(String beanName) {
        return context.getBean(beanName);
    }
    public static void stop(){
        SpringApplication.exit(context);
    }
    public static <T> T getBean(String beanName,Class<T> t) {
        return context.getBean(beanName,t);
    }
    public static <T> T getBean(Class<T> t) {
        return context.getBean(t);
    }

    public static void addBean(String beanName,Object bean){
        // 获取BeanFactory
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getAutowireCapableBeanFactory();
        // 创建Bean信息
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(bean.getClass());
        beanDefinition.setInstanceSupplier(() -> bean);
        // 注入Bean
        beanFactory.registerBeanDefinition(beanName, beanDefinition);
    }
}