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

import fun.asgc.neutrino.core.db.crisp.ds.DefaultDataSourceProvider;
import fun.asgc.neutrino.core.db.crisp.ds.IDataSourceProvider;

import javax.sql.DataSource;

/**
 * @author: aoshiguchen
 * @date: 2022/11/3
 */
public class DbConfig {
    /**
     * 数据库名称
     */
    private String name;
    /**
     * 数据源提供者
     */
    private IDataSourceProvider dataSourceProvider;
    /**
     * 是否显示sql
     */
    private Boolean showSql;
    /**
     * 是否开启调试模式
     */
    private Boolean debugMode;
    /**
     * 上下文
     */
    private DbContext context;
    /**
     * 数据库执行器
     */
    private DbExecutor dbExecutor;

    public DbConfig(String name, IDataSourceProvider dataSourceProvider) {
        this.name = name;
        this.dataSourceProvider = dataSourceProvider;
        this.showSql = Boolean.FALSE;
        this.debugMode = Boolean.FALSE;
        this.context = new DbContext(this);
        this.dbExecutor = new DbExecutor(this);
    }

    public DbConfig(String name, DataSource dataSource) {
        this(name, new DefaultDataSourceProvider(dataSource));
    }

    public DbConfig(IDataSourceProvider dataSourceProvider) {
        this("unknown", dataSourceProvider);
    }

    public DbConfig(DataSource dataSource) {
        this(new DefaultDataSourceProvider(dataSource));
    }

    /**
     * 获取数据库操作实例
     * @return 数据库操作实例
     */
    public DbExecutor getDbExecutor() {
        return dbExecutor;
    }
}
