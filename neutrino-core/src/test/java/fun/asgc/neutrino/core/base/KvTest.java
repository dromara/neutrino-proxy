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
        Kv<String, Object> kv1 = Kv.of();
        kv1.put("app.server.http.http-port", 8080);
        kv1.put("app.server.http.jks_Path", "/123/456");

        Kv<String, Object> kv2 = Kv.of(kv1);
        kv2.put("app.server.http.httpPort", 8081);
        kv2.put("app.server.http.context-path", "/");

        Kv<String, Object> kv3 = Kv.of(kv2);

        Assert.assertTrue(!kv1.isEmpty());
        Assert.assertTrue(kv1.size() == 2);
        Assert.assertTrue(kv1.stackSize() == 2);
        Assert.assertTrue(kv1.get("app.server.http.http-port").equals(8080));
        Assert.assertTrue(kv1.get("app.server.http.httpPort").equals(8080));
        Assert.assertTrue(kv1.get("app.server.http.jks_Path").equals("/123/456"));
        Assert.assertTrue(kv1.get("app.server.http.jksPath").equals("/123/456"));

        Assert.assertTrue(!kv2.isEmpty());
        Assert.assertTrue(kv2.size() == 2);
        Assert.assertTrue(kv2.stackSize() == 4);
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
        Assert.assertTrue(kv3.stackSize() == 4);
    }
}
