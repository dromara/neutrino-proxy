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
package fun.asgc.neutrino.core.db.template;

import fun.asgc.neutrino.core.util.ArrayUtil;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/27
 */
@Slf4j
public abstract class PreparedStatementJdbcCallback<T> implements JdbcCallback<T> {

	@Override
	public T execute() {
		PreparedStatement pstm = null;
		Object[] params = this.getParams();
		Connection conn = getConnection();

		T res = null;
		try {
			log.debug("sql:" + this.getSql());
			StringBuffer sb = new StringBuffer();
			if (ArrayUtil.notEmpty(params)) {
				for(Object o : params){
					sb.append(o.toString()).append(",");
				}

				if(sb.length() > 0 && sb.charAt(sb.length() - 1) == ','){
					sb.deleteCharAt(sb.length() - 1);
				}
			}
			log.debug("params:" + sb.toString());
			pstm = conn.prepareStatement(this.getSql());
			if (ArrayUtil.notEmpty(params)) {
				for(int i = 0;i < params.length;i++){
					pstm.setObject(i + 1, params[i]);
				}
			}
			res = this.execute(pstm);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return res;
	}

	/**
	 * 获取参数
	 * @return
	 */
	abstract Object[] getParams();

	/**
	 * 获取sql语句
	 * @return
	 */
	abstract String getSql();

	/**
	 * 执行
	 * @param ps
	 * @return
	 */
	abstract T execute(PreparedStatement ps);

	/**
	 * 获取数据库连接
	 * @return
	 */
	abstract Connection getConnection();
}
