package fun.asgc.neutrino.core.base;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author: aoshiguchen
 * @date: 2023/3/1
 */
public class NvMapTest {
    @Test
    public void test1() {
        Object key1 = new Object();
        NvMap nvMap0 = NvMap.of()
                .setAlias("app.server.http.http-port", "aaa")
                ;

        NvMap nvMap1 = NvMap.of(nvMap0)
            .set("app.server.http.http-port", 8080)
            .set("app.server.http.jks_Path", "/123/456")
            .set("app.server.websocket.port", "ws-port", 8888) // 设值的同时设置别名
            .set(key1, 1)
            .setAlias("app.server.http.http-port", "http-port")
            ;


        NvMap nvMap2 = NvMap.of(nvMap1)
            .set("app.server.http.httpPort", 8081)
            .set("app.server.http.context-path", "/")
                ;

        NvMap nvMap3 = NvMap.of(nvMap2)
                .setAlias("app.server.http.http-port", "port")
                .setAlias("http-port", "p");

        Assert.assertTrue(!nvMap1.isEmpty());
        Assert.assertTrue(nvMap1.size() == 4);
        Assert.assertTrue(nvMap1.stackSize() == 4);
        Assert.assertTrue(nvMap1.get("app.server.http.http-port").equals(8080));
        Assert.assertTrue(nvMap1.get("app.server.http.httpPort").equals(8080));
        Assert.assertTrue(nvMap1.idx(0).equals(8080));
        Assert.assertTrue(nvMap1.get("app.server.http.jks_Path").equals("/123/456"));
        Assert.assertTrue(nvMap1.get("app.server.http.jksPath").equals("/123/456"));
        Assert.assertTrue(nvMap1.idx(1).equals("/123/456"));
        Assert.assertTrue(nvMap1.containsKey("app.server.http.http-port"));
        Assert.assertTrue(nvMap1.containsKey(key1));
        Assert.assertFalse(nvMap1.containsKey(new Object()));

        Assert.assertTrue(!nvMap2.isEmpty());
        Assert.assertTrue(nvMap2.size() == 2);
        Assert.assertTrue(nvMap2.stackSize() == 6);
        Assert.assertTrue(nvMap2.get("app.server.http.http-port").equals(8081));
        Assert.assertTrue(nvMap2.get("app.server.http.httpPort").equals(8081));
        Assert.assertTrue(nvMap2.get("app.server.http.jks-path") == null);
        Assert.assertTrue(nvMap2.get("app.server.http.jksPath") == null);
        Assert.assertTrue(nvMap2.get("app.server.http.context-path").equals("/"));

        Assert.assertTrue(nvMap2.stackGet("app.server.http.jks-path").equals("/123/456"));
        Assert.assertTrue(nvMap2.stackGet("app.server.http.jksPath").equals("/123/456"));
        Assert.assertTrue(nvMap2.stackGet("app.server.http.context-path").equals("/"));

        Assert.assertTrue(nvMap3.isEmpty());
        Assert.assertTrue(!nvMap3.isStackEmpty());
        Assert.assertTrue(nvMap3.size() == 0);
        Assert.assertTrue(nvMap3.stackSize() == 6);
        Assert.assertTrue(nvMap3.get("app.server.http.jks-path") == null);
        Assert.assertTrue(nvMap3.get("app.server.http.jksPath") == null);
        Assert.assertTrue(nvMap3.get("app.server.http.context-path") == null);
        Assert.assertTrue(nvMap3.stackGet("app.server.http.jks-path").equals("/123/456"));
        Assert.assertTrue(nvMap3.stackGet("app.server.http.jksPath").equals("/123/456"));
        Assert.assertTrue(nvMap3.stackGet("app.server.http.context-path").equals("/"));

        Assert.assertTrue(nvMap3.stackGetOrDefault("app.server.http.timeout", 60000).equals(60000));

        Assert.assertTrue(nvMap3.takeStr("app.server.http.http-port").equals("8081"));
        Assert.assertNull(nvMap3.take(null));
        Assert.assertTrue(nvMap3.takeStr("httpPort").equals("8081"));
        Assert.assertTrue(nvMap3.takeInt("http-port").equals(8081));
        Assert.assertTrue(nvMap3.takeInt("port").equals(8081));
        // 注意：第3层的别名p，指向了第1层的别名http-port
        Assert.assertTrue(nvMap3.takeInt("p").equals(8081));
        Assert.assertTrue(nvMap3.takeStr("aaa").equals("8081"));
        Assert.assertTrue(nvMap3.takeInt("wsPort").equals(8888));
    }
}
