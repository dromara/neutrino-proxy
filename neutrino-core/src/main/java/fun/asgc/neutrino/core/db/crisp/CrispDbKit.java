/**
 * Copyright (c) 2022 aoshiguchen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package fun.asgc.neutrino.core.db.crisp;

import fun.asgc.neutrino.core.util.LockUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 清爽的数据库工具
 * @author: aoshiguchen
 * @date: 2022/11/3
 */
public class CrispDbKit {
    /**
     * 数据源缓存
     */
    private static Map<String, CrispDbConfig> crispDbConfigMap = new HashMap<>();
    /**
     * 数据库操作实例
     */
    private static Map<String, CrispDbExecutor> crispDbExecutorMap = new HashMap<>();
    /**
     * 默认数据库的key
     */
    private static final String DEFAULT_KEY = "default";

    /**
     * 设置默认数据库配置
     * @param config 数据库配置
     */
    public static void setConfig(CrispDbConfig config) {
        setConfig(DEFAULT_KEY, config);
    }

    /**
     * 设置数据库配置
     * @param key 键
     * @param config 数据库配置
     */
    public static void setConfig(String key, CrispDbConfig config) {
        crispDbConfigMap.put(key, config);
    }

    /**
     * 根据名称获取数据库配置
     * @param key 键
     * @return 数据库配置
     */
    private static CrispDbConfig getConfig(String key) {
        return crispDbConfigMap.get(key);
    }

    /**
     * 获取默认数据库操作实例
     * @return
     */
    private static CrispDbExecutor getDbExecutor() {
        return getDbExecutor(DEFAULT_KEY);
    }

    /**
     * 获取数据库操作实例
     * @param key 键
     * @return 数据库操作实例
     */
    private static CrispDbExecutor getDbExecutor(String key) {
        return LockUtil.doubleCheckProcessForNoException(
            () -> !crispDbExecutorMap.containsKey(key),
                key,
            () -> {
                CrispDbConfig config = getConfig(key);
                if (null != config) {
                    crispDbExecutorMap.put(key, new CrispDbExecutor(config));
                }
            },
            () -> crispDbExecutorMap.get(crispDbExecutorMap)
        );
    }

    /**
     * 切换数据源
     * @param name 数据源名称
     * @return 数据库操作实例
     */
    public static CrispDbExecutor use(String name) {
        return getDbExecutor(name);
    }
}
