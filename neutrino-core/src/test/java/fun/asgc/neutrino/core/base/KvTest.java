package fun.asgc.neutrino.core.base;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author: aoshiguchen
 * @date: 2023/3/1
 */
public class KvTest {
    @Test
    public void test1() {
        Object key1 = new Object();
        Kv kv1 = Kv.of()
            .set("app.server.http.http-port", 8080)
            .set("app.server.http.jks_Path", "/123/456")
            .set(key1, 1)
            .setAlias("app.server.http.http-port", "http-port")
            ;


        Kv kv2 = Kv.of(kv1)
            .set("app.server.http.httpPort", 8081)
            .set("app.server.http.context-path", "/")
                ;

        Kv kv3 = Kv.of(kv2)
                .setAlias("app.server.http.http-port", "port");

        Assert.assertTrue(!kv1.isEmpty());
        Assert.assertTrue(kv1.size() == 3);
        Assert.assertTrue(kv1.stackSize() == 3);
        Assert.assertTrue(kv1.get("app.server.http.http-port").equals(8080));
        Assert.assertTrue(kv1.get("app.server.http.httpPort").equals(8080));
        Assert.assertTrue(kv1.idx(0).equals(8080));
        Assert.assertTrue(kv1.get("app.server.http.jks_Path").equals("/123/456"));
        Assert.assertTrue(kv1.get("app.server.http.jksPath").equals("/123/456"));
        Assert.assertTrue(kv1.idx(1).equals("/123/456"));
        Assert.assertTrue(kv1.containsKey("app.server.http.http-port"));
        Assert.assertTrue(kv1.containsKey(key1));
        Assert.assertFalse(kv1.containsKey(new Object()));

        Assert.assertTrue(!kv2.isEmpty());
        Assert.assertTrue(kv2.size() == 2);
        Assert.assertTrue(kv2.stackSize() == 5);
        Assert.assertTrue(kv2.get("app.server.http.http-port").equals(8081));
        Assert.assertTrue(kv2.get("app.server.http.httpPort").equals(8081));
        Assert.assertTrue(kv2.get("app.server.http.jks-path") == null);
        Assert.assertTrue(kv2.get("app.server.http.jksPath") == null);
        Assert.assertTrue(kv2.get("app.server.http.context-path").equals("/"));

        Assert.assertTrue(kv2.stackGet("app.server.http.jks-path").equals("/123/456"));
        Assert.assertTrue(kv2.stackGet("app.server.http.jksPath").equals("/123/456"));
        Assert.assertTrue(kv2.stackGet("app.server.http.context-path").equals("/"));

        Assert.assertTrue(kv3.isEmpty());
        Assert.assertTrue(!kv3.isStackEmpty());
        Assert.assertTrue(kv3.size() == 0);
        Assert.assertTrue(kv3.stackSize() == 5);
        Assert.assertTrue(kv3.get("app.server.http.jks-path") == null);
        Assert.assertTrue(kv3.get("app.server.http.jksPath") == null);
        Assert.assertTrue(kv3.get("app.server.http.context-path") == null);
        Assert.assertTrue(kv3.stackGet("app.server.http.jks-path").equals("/123/456"));
        Assert.assertTrue(kv3.stackGet("app.server.http.jksPath").equals("/123/456"));
        Assert.assertTrue(kv3.stackGet("app.server.http.context-path").equals("/"));

        Assert.assertTrue(kv3.stackGetOrDefault("app.server.http.timeout", 60000).equals(60000));

        Assert.assertTrue(kv3.takeStr("app.server.http.http-port").equals("8081"));
        Assert.assertNull(kv3.take(null));
        // 注意：http-port是第1层设置的别名，因此用这个别名对2、3层不可见，不会指向第二层的8081（除非第2、3层定义了同样的别名）
        Assert.assertTrue(kv3.takeStr("httpPort").equals("8080"));
        Assert.assertTrue(kv3.takeInt("http-port").equals(8080));
        // 注意：第3层设置的别名port，找到第二层的配置，不再往上找
        Assert.assertTrue(kv3.takeInt("port").equals(8081));
    }
}
