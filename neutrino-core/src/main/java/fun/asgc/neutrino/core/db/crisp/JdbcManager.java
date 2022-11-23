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

import java.util.HashMap;
import java.util.Map;

/**
 * @author: aoshiguchen
 * @date: 2022/11/4
 */
public class JdbcManager {
    /**
     * 数据源缓存
     */
    private Map<String, DbConfig> dbConfigMap = new HashMap<>();
    /**
     * 默认数据库的key
     */
    private final String DEFAULT_KEY = "default";

    /**
     * 设置默认数据库配置
     * @param config 数据库配置
     */
    public void setConfig(DbConfig config) {
        setConfig(DEFAULT_KEY, config);
    }

    /**
     * 设置数据库配置
     * @param key 键
     * @param config 数据库配置
     */
    public void setConfig(String key, DbConfig config) {
        dbConfigMap.put(key, config);
    }

    /**
     * 根据名称获取数据库配置
     * @param key 键
     * @return 数据库配置
     */
    public DbConfig getConfig(String key) {
        return dbConfigMap.get(key);
    }

    /**
     * 获取默认数据库配置
     * @return 数据库配置
     */
    public DbConfig getConfig() {
        return getConfig(DEFAULT_KEY);
    }

    /**
     * 获取默认数据库操作实例
     * @return
     */
    private DbExecutor getDbExecutor(String key) {
        DbConfig dbConfig = getConfig(key);
        return null == dbConfig ? null : dbConfig.getDbExecutor();
    }

    /**
     * 切换数据源
     * @param name 数据源名称
     * @return 数据库操作实例
     */
    public DbExecutor use(String name) {
        return getDbExecutor(name);
    }

    /**
     * 切换为默认数据源
     * @return 默认数据库操作实例
     */
    public DbExecutor useDefault() {
        return getDbExecutor(DEFAULT_KEY);
    }
}
