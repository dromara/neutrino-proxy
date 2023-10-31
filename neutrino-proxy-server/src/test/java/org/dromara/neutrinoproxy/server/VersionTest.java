package org.dromara.neutrinoproxy.server;

import org.dromara.neutrinoproxy.core.util.NeutrinoProxyVersion;
import org.dromara.neutrinoproxy.server.app.AppBaseTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author songyinyin
 * @since 2023/10/31 11:51
 */
public class VersionTest extends AppBaseTest {

    @Test
    public void testVersion() {
        Assert.assertNotNull(NeutrinoProxyVersion.getVersion());
        System.out.println("version: " + NeutrinoProxyVersion.getVersion());
    }
}
