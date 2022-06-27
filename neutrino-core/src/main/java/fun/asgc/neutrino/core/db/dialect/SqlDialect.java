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
package fun.asgc.neutrino.core.db.dialect;

import java.util.Map;
import java.util.Set;

/**
 * sql方言，用于屏蔽各种不同的数据库的sql差异
 * @author: aoshiguchen
 * @date: 2022/6/27
 */
public interface SqlDialect {
	/**
	 * 获取记录数
	 * @return
	 */
	String getRecordCount();

	/**
	 * 查询所有数据
	 * @return
	 */
	String findAll();

	/**
	 * 根据id查询单条数据
	 * @param id
	 * @return
	 */
	String findById(String id);

	/**
	 * 查询
	 * @param filterField
	 * @param params
	 * @return
	 */
	String find(Set<String> filterField, Map<String,Object> params);

	/**
	 * 根据id删除
	 * @param id
	 * @return
	 */
	String deleteById(String id);

	/**
	 * 删除
	 * @param filterField
	 * @param params
	 * @return
	 */
	String delete(Set<String> filterField,Map<String,Object> params);

	/**
	 * 删除所有数据
	 * @return
	 */
	String deleteAll();

	/**
	 * 更新
	 * @param filterField
	 * @param params
	 * @return
	 */
	String update(Set<String> filterField,Map<String,Object> params);

	/**
	 * 新增
	 * @param filterField
	 * @param params
	 * @return
	 */
	String create(Set<String> filterField,Map<String,Object> params);

	/**
	 * 分页查询
	 * @return
	 */
	String findPage();

	/**
	 * 分页查询
	 * @param filterField
	 * @param params
	 * @return
	 */
	String findPage(Set<String> filterField,Map<String,Object> params);

}
