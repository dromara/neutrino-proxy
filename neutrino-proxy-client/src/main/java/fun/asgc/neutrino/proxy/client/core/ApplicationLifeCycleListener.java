///**
// * Copyright (c) 2022 aoshiguchen
// *
// * Permission is hereby granted, free of charge, to any person obtaining a copy
// * of this software and associated documentation files (the "Software"), to deal
// * in the Software without restriction, including without limitation the rights
// * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the Software is
// * furnished to do so, subject to the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in all
// * copies or substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// * SOFTWARE.
// */
//package fun.asgc.neutrino.proxy.client.core;
//
//import fun.asgc.neutrino.core.annotation.Autowired;
//import fun.asgc.neutrino.core.annotation.Component;
//import fun.asgc.neutrino.core.annotation.NonIntercept;
//import fun.asgc.neutrino.core.annotation.Subscribe;
//import fun.asgc.neutrino.core.base.event.ApplicationEvent;
//import fun.asgc.neutrino.core.base.event.ApplicationEventReceiver;
//import fun.asgc.neutrino.core.constant.AppLifeCycleStatusEnum;
//import fun.asgc.neutrino.core.constant.MetaDataConstant;
//import lombok.extern.slf4j.Slf4j;
//
///**
// * 应用生命周期事件监听器
// * @author: aoshiguchen
// * @date: 2022/10/10
// */
//@Slf4j
//@NonIntercept
//@Component
//@Subscribe(topic = MetaDataConstant.TOPIC_APP_LIFE_CYCLE)
//public class ApplicationLifeCycleListener extends ApplicationEventReceiver<AppLifeCycleStatusEnum> {
//    @Autowired
//    private LicenseObtainService licenseObtainService;
//
//    @Override
//    public void receive(ApplicationEvent<AppLifeCycleStatusEnum> msg) {
//        log.debug("ApplicationLifeCycleListener:{}", msg.data().getDesc());
//        if (msg.data() == AppLifeCycleStatusEnum.APP_STARTUP) {
//            licenseObtainService.start();
//        }
//    }
//
//}
