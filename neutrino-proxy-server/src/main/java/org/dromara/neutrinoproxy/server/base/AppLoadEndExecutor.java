package org.dromara.neutrinoproxy.server.base;

import lombok.extern.slf4j.Slf4j;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Component;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.core.event.EventListener;
import org.noear.solon.core.runtime.NativeDetector;

/**
 * 初始化数据库
 * @author: aoshiguchen
 * @date: 2024/10/16
 */
@Slf4j
@Component
public class AppLoadEndExecutor implements EventListener<AppLoadEndEvent> {

    @Override
    public void onEvent(AppLoadEndEvent appLoadEndEvent) throws Throwable {
        // aot 阶段，不执行
        if (NativeDetector.isAotRuntime()) {
            return;
        }
        System.out.printf("""
            ---------------------------------------------------------------
                    Neutrino Proxy
            version: %s
            admin: http://localhost:8888
            account: admin/123456
            Gitee: https://gitee.com/dromara/neutrino-proxy
            GitHub: https://github.com/dromara/neutrino-proxy
            GitCode: https://gitcode.com/dromara/neutrino-proxy
            ---------------------------------------------------------------
            %n""", Solon.app().cfg().get("solon.app.version"));
    }

}
