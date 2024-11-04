package org.dromara.neutrinoproxy.client.config;

import lombok.extern.slf4j.Slf4j;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Component;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.core.event.EventListener;
import org.noear.solon.core.runtime.NativeDetector;

/**
 * 启动成功后信息输出
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
                    Neutrino Proxy Client %s
            Gitee: https://gitee.com/dromara/neutrino-proxy
            GitHub: https://github.com/dromara/neutrino-proxy
            GitCode: https://gitcode.com/dromara/neutrino-proxy
            ---------------------------------------------------------------
            %n""", Solon.app().cfg().get("solon.app.version"));
    }

}
