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

import java.util.List;

/**
 * @author: aoshiguchen
 * @date: 2022/11/3
 */
public interface CrispDbOperator {
    /**
     * 查询数据列表
     * @param resultType 结果类型
     * @param sql sql
     * @param params 参数列表
     * @return 查询结果列表
     * @param <T>
     */
    <T> List<T> queryList(Class<T> resultType, String sql, Object... params);

    /**
     * 查询单条记录
     * @param resultType 结果类型
     * @param sql sql
     * @param params 参数列表
     * @return 查询结果
     * @param <T>
     */
    <T> T query(Class<T> resultType, String sql, Object... params);

    /**
     * 更新
     * @param sql sql
     * @param params 参数列表
     * @return 更新结果
     */
    CrispDbUpdateResult update(String sql, Object... params);

    /**
     * 新增
     * @param sql sql
     * @param params 参数列表
     * @return 新增结果
     */
    CrispDbUpdateResult insert(String sql, Object... params);

    /**
     * 删除
     * @param sql sql
     * @param params 参数列表
     * @return 删除结果
     */
    CrispDbUpdateResult delete(String sql, Object... params);
}
