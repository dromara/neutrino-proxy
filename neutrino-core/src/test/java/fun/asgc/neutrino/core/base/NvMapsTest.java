package fun.asgc.neutrino.core.base;

import org.junit.Test;

import java.io.IOException;

/**
 * @author: aoshiguchen
 * @date: 2023/3/6
 */
public class NvMapsTest {

    /**
     * 先给电脑设置环境变量，然后重启idea
     * EUTRINO_PROXY=http-port=6000:app-name=neutrino-proxy-client
     * @throws IOException
     */
    @Test
    public void test1() throws IOException {
        // 创建NvMaps实例
        NvMaps nvMaps = NvMaps.of();

        // 阶段1：设置内部默认值
        nvMaps.setKv("neutrino.application.name", "app-name",null);
        nvMaps.setKv("neutrino.http.enable", true);
        nvMaps.setKv("neutrino.http.port",  "httpPort",null);
        nvMaps.setKv("neutrino.http.context-path", "context-path", "/");
        nvMaps.stageDone();

        // 阶段2：内部配置文件
        nvMaps.loadFile(NvMapsTest.class.getResource("/application.yml").getPath());
        nvMaps.stageDone();

        // 阶段3：环境变量
        nvMaps.loadEnvironmentVariable("NEUTRINO_PROXY");
        nvMaps.stageDone();

        // 结算4：外部配置文件
//        nvMaps.loadFile(NvMapsTest.class.getResource("/aaa.json").getPath());
        nvMaps.loadFile(NvMapsTest.class.getResource("/aaa.properties").getPath());
        nvMaps.stageDone();

        // 阶段5：启动参数
        String[] mainArgs = new String[]{
                "httpPort=8100"
        };
        nvMaps.loadMainArgs(mainArgs);
        nvMaps.stageDone();

        // 获取NvMap实例
        NvMap nvMap = nvMaps.getNvMap();

        System.out.println(nvMap.takeInt("httpPort"));
        System.out.println(nvMap.takeInt("neutrino.http.port"));

        System.out.println(nvMap.takeStr("appName"));
        System.out.println(nvMap.takeStr("neutrino.application.name"));
    }

}
