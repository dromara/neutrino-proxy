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
package fun.asgc.neutrino.core.db.mapper;

import fun.asgc.neutrino.core.cache.Cache;
import fun.asgc.neutrino.core.cache.MemoryCache;
import fun.asgc.neutrino.core.db.annotation.*;
import fun.asgc.neutrino.core.util.LockUtil;
import fun.asgc.neutrino.core.util.StringUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Collection;

/**
 * sql解析器
 * @author: aoshiguchen
 * @date: 2022/8/3
 */
@Data
@Slf4j
public class SqlParser {
	/**
	 * sqlParser实例缓存
	 */
	private static final Cache<Method, SqlParser> sqlParserCache = new MemoryCache<>();
	/**
	 * 目标执行方法
	 */
	private Method targetMethod;
	/**
	 * 操作类型
	 */
	private SqlOperatorType operatorType;
	/**
	 * 结果类型
	 */
	private Class<?> resultType;
	/**
	 * 返回结果组成类型（集合类型的子类型）
	 */
	private Class<?> resultComponentType;
	/**
	 * 目标方法签名
	 */
	private String targetMethodSign;
	/**
	 * 返回数据是否是集合类型
	 */
	private boolean isReturnCollection;
	/**
	 * sql语句
	 */
	private String sql;

	private SqlParser(Method targetMethod) {
		this.targetMethod = targetMethod;
		this.resultType = targetMethod.getReturnType();
		this.targetMethodSign = String.format("%s#%s", targetMethod.getDeclaringClass().getName(), targetMethod.getName());
		this.initByAnnotation();
		this.initByXml();
	}

	/**
	 * 根据注解进行初始化
	 */
	private void initByAnnotation() {
		if (!StringUtil.isEmpty(this.sql)) {
			return;
		}
		ResultType resultType = targetMethod.getAnnotation(ResultType.class);
		Class<?> resultClass = (null == resultType) ? null : resultType.value();
		this.resultComponentType = resultClass;

		if (targetMethod.isAnnotationPresent(Select.class)) {
			// 查询
			this.operatorType = SqlOperatorType.SELECT;
			Select select = targetMethod.getAnnotation(Select.class);
			this.sql = select.value();
			if (StringUtil.isEmpty(sql)) {
				throw new RuntimeException(String.format("%s sql不能为空!", targetMethod));
			}
			this.isReturnCollection = Collection.class.isAssignableFrom(targetMethod.getReturnType());
			if (isReturnCollection && null == resultClass) {
				throw new RuntimeException(String.format("%s 请指名实体类型!", targetMethodSign));
			}
		} else if (targetMethod.isAnnotationPresent(Insert.class)) {
			// 新增
			this.operatorType = SqlOperatorType.INSERT;
			Insert insert = targetMethod.getAnnotation(Insert.class);
			this.sql = insert.value();
			if (StringUtil.isEmpty(sql)) {
				throw new RuntimeException(String.format("%s sql不能为空!", targetMethodSign));
			}
		} else if (targetMethod.isAnnotationPresent(Delete.class)) {
			// 删除
			this.operatorType = SqlOperatorType.DELETE;
			Delete delete = targetMethod.getAnnotation(Delete.class);
			this.sql = delete.value();
			if (StringUtil.isEmpty(sql)) {
				throw new RuntimeException(String.format("%s sql不能为空!", targetMethodSign));
			}
		} else if (targetMethod.isAnnotationPresent(Update.class)) {
			// 更新
			this.operatorType = SqlOperatorType.UPDATE;
			Update update = targetMethod.getAnnotation(Update.class);
			this.sql = update.value();
			if (StringUtil.isEmpty(sql)) {
				throw new RuntimeException(String.format("%s sql不能为空!", targetMethodSign));
			}
		}
	}

	/**
	 * 根据xml文件进行初始化
	 */
	private void initByXml() {
		if (!StringUtil.isEmpty(this.sql)) {
			return;
		}
		// TODO
	}

	public static SqlParser getInstance(Method targetMethod) throws Exception {
		return LockUtil.doubleCheckProcess(
			() -> !sqlParserCache.containsKey(targetMethod),
			targetMethod,
			() -> sqlParserCache.set(targetMethod, new SqlParser(targetMethod)),
			() -> sqlParserCache.get(targetMethod)
		);
	}

	public boolean isSelect() {
		return this.operatorType == SqlOperatorType.SELECT;
	}

	public boolean isUpdate() {
		return this.operatorType == SqlOperatorType.UPDATE;
	}

	public boolean isDelete() {
		return this.operatorType == SqlOperatorType.DELETE;
	}

	public boolean isInsert() {
		return this.operatorType == SqlOperatorType.INSERT;
	}
}
