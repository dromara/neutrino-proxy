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

import java.util.ArrayList;
import java.util.List;

/**
 * @author: aoshiguchen
 * @date: 2022/11/4
 */
public class BatchResult {

    private final MappedStatement mappedStatement;
    private final String sql;
    private final List<Object> parameterObjects;

    private int[] updateCounts;

    public BatchResult(MappedStatement mappedStatement, String sql) {
        super();
        this.mappedStatement = mappedStatement;
        this.sql = sql;
        this.parameterObjects = new ArrayList<>();
    }

    public BatchResult(MappedStatement mappedStatement, String sql, Object parameterObject) {
        this(mappedStatement, sql);
        addParameterObject(parameterObject);
    }

    public MappedStatement getMappedStatement() {
        return mappedStatement;
    }

    public String getSql() {
        return sql;
    }

    @Deprecated
    public Object getParameterObject() {
        return parameterObjects.get(0);
    }

    public List<Object> getParameterObjects() {
        return parameterObjects;
    }

    public int[] getUpdateCounts() {
        return updateCounts;
    }

    public void setUpdateCounts(int[] updateCounts) {
        this.updateCounts = updateCounts;
    }

    public void addParameterObject(Object parameterObject) {
        this.parameterObjects.add(parameterObject);
    }

}
