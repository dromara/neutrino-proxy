package fun.asgc.neutrino.core.base;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author: aoshiguchen
 * @date: 2023/3/1
 */
public class CfgTest {
    @Test
    public void test1() {
        Object key1 = new Object();
        Cfg cfg0 = Cfg.of()
                .setAlias("app.server.http.http-port", "aaa")
                ;

        Cfg cfg1 = Cfg.of(cfg0)
            .set("app.server.http.http-port", 8080)
            .set("app.server.http.jks_Path", "/123/456")
            .set("app.server.websocket.port", "ws-port", 8888) // 设值的同时设置别名
            .set(key1, 1)
            .setAlias("app.server.http.http-port", "http-port")
            ;


        Cfg cfg2 = Cfg.of(cfg1)
            .set("app.server.http.httpPort", 8081)
            .set("app.server.http.context-path", "/")
                ;

        Cfg cfg3 = Cfg.of(cfg2)
                .setAlias("app.server.http.http-port", "port")
                .setAlias("http-port", "p");

        Assert.assertTrue(!cfg1.isEmpty());
        Assert.assertTrue(cfg1.size() == 4);
        Assert.assertTrue(cfg1.stackSize() == 4);
        Assert.assertTrue(cfg1.get("app.server.http.http-port").equals(8080));
        Assert.assertTrue(cfg1.get("app.server.http.httpPort").equals(8080));
        Assert.assertTrue(cfg1.idx(0).equals(8080));
        Assert.assertTrue(cfg1.get("app.server.http.jks_Path").equals("/123/456"));
        Assert.assertTrue(cfg1.get("app.server.http.jksPath").equals("/123/456"));
        Assert.assertTrue(cfg1.idx(1).equals("/123/456"));
        Assert.assertTrue(cfg1.containsKey("app.server.http.http-port"));
        Assert.assertTrue(cfg1.containsKey(key1));
        Assert.assertFalse(cfg1.containsKey(new Object()));

        Assert.assertTrue(!cfg2.isEmpty());
        Assert.assertTrue(cfg2.size() == 2);
        Assert.assertTrue(cfg2.stackSize() == 6);
        Assert.assertTrue(cfg2.get("app.server.http.http-port").equals(8081));
        Assert.assertTrue(cfg2.get("app.server.http.httpPort").equals(8081));
        Assert.assertTrue(cfg2.get("app.server.http.jks-path") == null);
        Assert.assertTrue(cfg2.get("app.server.http.jksPath") == null);
        Assert.assertTrue(cfg2.get("app.server.http.context-path").equals("/"));

        Assert.assertTrue(cfg2.stackGet("app.server.http.jks-path").equals("/123/456"));
        Assert.assertTrue(cfg2.stackGet("app.server.http.jksPath").equals("/123/456"));
        Assert.assertTrue(cfg2.stackGet("app.server.http.context-path").equals("/"));

        Assert.assertTrue(cfg3.isEmpty());
        Assert.assertTrue(!cfg3.isStackEmpty());
        Assert.assertTrue(cfg3.size() == 0);
        Assert.assertTrue(cfg3.stackSize() == 6);
        Assert.assertTrue(cfg3.get("app.server.http.jks-path") == null);
        Assert.assertTrue(cfg3.get("app.server.http.jksPath") == null);
        Assert.assertTrue(cfg3.get("app.server.http.context-path") == null);
        Assert.assertTrue(cfg3.stackGet("app.server.http.jks-path").equals("/123/456"));
        Assert.assertTrue(cfg3.stackGet("app.server.http.jksPath").equals("/123/456"));
        Assert.assertTrue(cfg3.stackGet("app.server.http.context-path").equals("/"));

        Assert.assertTrue(cfg3.stackGetOrDefault("app.server.http.timeout", 60000).equals(60000));

        Assert.assertTrue(cfg3.takeStr("app.server.http.http-port").equals("8081"));
        Assert.assertNull(cfg3.take(null));
        Assert.assertTrue(cfg3.takeStr("httpPort").equals("8081"));
        Assert.assertTrue(cfg3.takeInt("http-port").equals(8081));
        Assert.assertTrue(cfg3.takeInt("port").equals(8081));
        // 注意：第3层的别名p，指向了第1层的别名http-port
        Assert.assertTrue(cfg3.takeInt("p").equals(8080));
        Assert.assertTrue(cfg3.takeStr("aaa").equals("8081"));
        Assert.assertTrue(cfg3.takeInt("wsPort").equals(8888));
    }
}
