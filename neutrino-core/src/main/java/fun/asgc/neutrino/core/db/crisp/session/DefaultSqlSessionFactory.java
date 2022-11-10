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
package fun.asgc.neutrino.core.db.crisp.session;

import fun.asgc.neutrino.core.db.crisp.DbConfig;
import fun.asgc.neutrino.core.db.crisp.base.SqlExecutorType;
import fun.asgc.neutrino.core.db.crisp.tx.TransactionIsolationLevel;

import java.sql.Connection;

/**
 * @author: aoshiguchen
 * @date: 2022/11/4
 */
public class DefaultSqlSessionFactory implements SqlSessionFactory {

    private final DbConfig config;

    public DefaultSqlSessionFactory(DbConfig config) {
        this.config = config;
    }

    @Override
    public SqlSession openSession() {
        // TODO
        return null;
    }

    @Override
    public SqlSession openSession(boolean autoCommit) {
        // TODO
        return null;
    }

    @Override
    public SqlSession openSession(Connection connection) {
        // TODO
        return null;
    }

    @Override
    public SqlSession openSession(TransactionIsolationLevel level) {
        // TODO
        return null;
    }

    @Override
    public SqlSession openSession(SqlExecutorType execType) {
        // TODO
        return null;
    }

    @Override
    public SqlSession openSession(SqlExecutorType execType, boolean autoCommit) {
        // TODO
        return null;
    }

    @Override
    public SqlSession openSession(SqlExecutorType execType, TransactionIsolationLevel level) {
        // TODO
        return null;
    }

    @Override
    public SqlSession openSession(SqlExecutorType execType, Connection connection) {
        // TODO
        return null;
    }

    @Override
    public DbConfig getDbConfig() {
        // TODO
        return null;
    }
}
