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

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/28
 */
@Getter
@AllArgsConstructor
public enum DBType {
	MYSQL(1, "mysql"),
	SQL_SERVER(2, "sqserver"),
	ORACLE(3, "oracle"),
	SQL_LITE(4, "sqllite"),
	MONGO(5, "mongodb");
	private static final Map<Integer, DBType> typeMap = Stream.of(DBType.values()).collect(Collectors.toMap(DBType::getType, Function.identity()));
	private static final Map<String, DBType> nameMap = Stream.of(DBType.values()).collect(Collectors.toMap(DBType::getName, Function.identity()));

	private Integer type;
	private String name;

	public static DBType byType(Integer type) {
		return typeMap.get(type);
	}

	public static DBType byName(String name) {
		return nameMap.get(name);
	}
}
