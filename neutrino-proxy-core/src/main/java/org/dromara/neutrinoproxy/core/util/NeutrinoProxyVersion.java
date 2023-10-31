package org.dromara.neutrinoproxy.core.util;

import org.noear.solon.Solon;

/**
 * @author songyinyin
 * @since 2023/10/31 11:48
 */
public class NeutrinoProxyVersion {

    /**
     * 获取 NeutrinoProxy 版本号
     */
    public static String getVersion() {
        return Solon.cfg().get("solon.app.version");
    }


}
