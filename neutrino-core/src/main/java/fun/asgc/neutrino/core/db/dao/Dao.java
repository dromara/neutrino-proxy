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
package fun.asgc.neutrino.core.db.dao;

import java.io.Serializable;
import java.util.List;

/**
 * @author: aoshiguchen
 * @date: 2022/6/28
 */
public interface Dao<T> {

	//==============================================新增=====================

	/**
	 * 新增单条记录
	 * @param po
	 * @return
	 */
	T add(T po);

	//==============================================修改=====================

	/**
	 * 更新单条记录
	 * @param po
	 * @return
	 */
	int updateById(T po);

	/**
	 * 更新单条记录
	 * @param po
	 * @param field
	 * @return
	 */
	int updateById(T po, String ...field);

	//==============================================删除=====================

	/**
	 * 根据id删除
	 * @param id
	 * @return
	 */
	int deleteById(Serializable id);

	int delete(T po, String ...field);

	int delete();

	//==============================================查询=====================
	Long count();
	Long count(T po, String ...field);

	T findOneById(Serializable id);
	T findOne(T po, String ...field);
	List<T> find();
	List<T> find(T po, String ...field);

	List<T> findPage(int beginNo, int pageSize);
	List<T> findPage(T po,int beginNo, int pageSize, String ...field);
}

