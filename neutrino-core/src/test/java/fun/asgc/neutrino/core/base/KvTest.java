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
        Kv kv1 = Kv.of()
            .set("app.server.http.http-port", 8080)
            .set("app.server.http.jks_Path", "/123/456")
            ;


        Kv kv2 = Kv.of(kv1)
            .set("app.server.http.httpPort", 8081)
            .set("app.server.http.context-path", "/")
                ;

        Kv kv3 = Kv.of(kv2);

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
        Assert.assertTrue(kv3.get("app.server.http.jks-path") == null);
        Assert.assertTrue(kv3.get("app.server.http.jksPath") == null);
        Assert.assertTrue(kv3.get("app.server.http.context-path") == null);
        Assert.assertTrue(kv3.stackGet("app.server.http.jks-path").equals("/123/456"));
        Assert.assertTrue(kv3.stackGet("app.server.http.jksPath").equals("/123/456"));
        Assert.assertTrue(kv3.stackGet("app.server.http.context-path").equals("/"));

        Assert.assertTrue(kv3.stackGetOrDefault("app.server.http.timeout", 60000).equals(60000));

        Assert.assertTrue(kv3.takeStr("app.server.http.http-port").equals("8081"));
    }
}
