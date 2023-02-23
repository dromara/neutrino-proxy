package fun.asgc.neutrino.core.aop.compiler.internal;

import fun.asgc.neutrino.core.aop.compiler.internal.jar.JarFile;
import fun.asgc.neutrino.core.aop.compiler.internal.jar.JarURLConnection;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

/**
 * @author: aoshiguchen
 * @date: 2023/2/23
 */
public final class SpringBootJarParser {

    /**
     * 根据一个URI从SpringBoot jar包中解析出输入流
     * 1、先尝试直接获取流，若能获取则到此结束
     * 2、若不能获取，且URI标识定位的是一个jar包，则进行处理（只处理SpringBoot情况的jar）
     * @param uri
     * @return
     */
    public static InputStream getInputStream(URI uri) {
        try {
            if (uri.getScheme().equals("jar")) {
                URL url = uri.toURL();
                String path = url.getPath();
                int index = path.indexOf("!");
                if (index >= 0) {
                    path = path.substring(0, index);
                }
                if (path.startsWith("file:")) {
                    path = path.substring(5);
                }
                JarFile jarFile = new JarFile(new File(path));
                return JarURLConnection.get(url, jarFile).getInputStream();
            }
        } catch (Exception e1) {
            // ignore
        }
        return null;
    }
}
