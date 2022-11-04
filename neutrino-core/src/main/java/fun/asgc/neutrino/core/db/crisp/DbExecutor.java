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

import javax.sql.DataSource;
import java.util.List;

/**
 * @author: aoshiguchen
 * @date: 2022/11/3
 */
class DbExecutor implements DbOperator {
    /**
     * 配置
     */
    private DbConfig config;


    public DbExecutor(DbConfig config) {
        this.config = config;
    }

    public DbExecutor(IDataSourceProvider dataSourceProvider) {
        this(new DbConfig(dataSourceProvider));
    }

    public DbExecutor(String name, IDataSourceProvider dataSourceProvider) {
        this(new DbConfig(name, dataSourceProvider));
    }

    public DbExecutor(DataSource dataSource) {
        this(new DbConfig(dataSource));
    }

    public DbExecutor(String name, DataSource dataSource) {
        this(new DbConfig(name, dataSource));
    }

    @Override
    public <T> List<T> queryList(Class<T> resultType, String sql, Object... params) {
        return null;
    }

    @Override
    public <T> T query(Class<T> resultType, String sql, Object... params) {
        return null;
    }

    @Override
    public DbUpdateResult update(String sql, Object... params) {
        return null;
    }

    @Override
    public DbUpdateResult insert(String sql, Object... params) {
        return null;
    }

    @Override
    public DbUpdateResult delete(String sql, Object... params) {
        return null;
    }
}
