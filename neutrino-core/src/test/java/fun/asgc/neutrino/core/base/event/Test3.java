/**
 * Copyright (c) 2022 aoshiguchen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package fun.asgc.neutrino.core.base.event;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Sets;
import fun.asgc.neutrino.core.util.SystemUtil;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * 应用事件测试
 * 1、异步执行
 * 2、业务解耦
 * 3、topic订阅
 * 4、支持多种模式无缝切换（本地模式、redis模式、rocketMQ模式、MQTT模式等）
 * 5、不支持事务消息
 * @author: aoshiguchen
 * @date: 2022/10/3
 */
@Slf4j
public class Test3 {
    private SimpleApplicationEventManager<Student> simpleApplicationEventManager = new SimpleApplicationEventManager<>(this);

    @Test
    public void test1() {
        ApplicationEventReceiver<Student> receiver1 = new ApplicationEventReceiver<Student>() {
            @Override
            public void receive(ApplicationEvent<Student> msg) {
                log.info("receiver1 data:{}", JSONObject.toJSONString(msg.data()));
            }
        };
        receiver1.setTopic("/*");
        receiver1.setTags(Sets.newHashSet("tag1"));
        ApplicationEventReceiver<Student> receiver2 = new ApplicationEventReceiver<Student>() {
            @Override
            public void receive(ApplicationEvent<Student> msg) {
                log.info("receiver2 data:{}", JSONObject.toJSONString(msg.data()));
            }
        };
        receiver2.setTags(Sets.newHashSet("tag2"));
        simpleApplicationEventManager.registerReceiver(receiver1);
        simpleApplicationEventManager.registerReceiver(receiver2);

        simpleApplicationEventManager.publish("/aaa", Sets.newHashSet("tag1"), new Student().setId("1").setName("张三").setAge(28).setSex("男"));

        SystemUtil.waitProcessDestroy().sync();
    }

    @Accessors(chain = true)
    @Data
    public static class Student {
        private String id;
        private String name;
        private Integer age;
        private String sex;
    }

    public static class MyTestChannel extends ApplicationEventChannel<Student> {

    }
}
