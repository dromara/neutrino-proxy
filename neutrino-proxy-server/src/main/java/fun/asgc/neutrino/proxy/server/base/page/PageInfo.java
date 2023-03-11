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
package fun.asgc.neutrino.proxy.server.base.page;

import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/6
 */
@Data
public class PageInfo<T> implements Serializable {
	private static final long serialVersionUID = 8545996863226528797L;
	private List<T> records;
	private Long total;
	private Integer size;
	private Integer current;

	public PageInfo() {
		this.records = Collections.emptyList();
		this.total = 0L;
		this.size = 10;
		this.current = 1;
	}

	public PageInfo(Integer current, Integer size) {
		this(current, size, 0L);
	}

	public PageInfo(Integer current, Integer size, Long total) {
		this.records = Collections.emptyList();
		this.total = 0L;
		this.size = 10;
		this.current = 1;
		if ((long)current > 1L) {
			this.current = current;
		}

		this.size = size;
		this.total = total;
	}

	public Long getPages() {
		if ((long)this.getSize() == 0L) {
			return 0L;
		} else {
			Long pages = this.getTotal() / (long)this.getSize();
			if (this.getTotal() % (long)this.getSize() != 0L) {
				pages = pages + 1L;
			}

			return pages;
		}
	}

	public Long getTotal() {
		return this.total;
	}

	public Integer getSize() {
		return this.size;
	}

	public boolean hasPrevious() {
		return (long)this.current > 1L;
	}

	public boolean hasNext() {
		return (long)this.current < this.getPages();
	}

	public List<T> getRecords() {
		return this.records;
	}

	public PageInfo<T> setRecords(List<T> records) {
		this.records = records;
		return this;
	}

	public PageInfo<T> setTotal(Long total) {
		this.total = total;
		return this;
	}

	public PageInfo<T> setSize(Integer size) {
		this.size = size;
		return this;
	}

	public Integer getCurrent() {
		return this.current;
	}

	public PageInfo<T> setCurrent(Integer current) {
		this.current = current;
		return this;
	}

	public static <T> PageInfo<T> of(List<T> records, Long total, Integer current, Integer size) {
		PageInfo<T> pageInfo = new PageInfo();
		pageInfo.setRecords(records);
		pageInfo.setTotal(total);
		pageInfo.setCurrent(current);
		pageInfo.setSize(size);
		return pageInfo;
	}
}
