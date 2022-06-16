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

package fun.asgc.neutrino.core.config;

import fun.asgc.neutrino.core.exception.ConfigurationParserException;

import java.io.InputStream;
import java.util.Map;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
public interface ConfigurationParser {

	/**
	 * 解析
	 * @param in
	 * @param clazz
	 * @param <T>
	 * @return
	 */
	<T> T parse(InputStream in, Class<T> clazz) throws ConfigurationParserException;

	/**
	 * 解析
	 * @param clazz
	 * @param <T>
	 * @return
	 */
	<T> T parse(Class<T> clazz) throws ConfigurationParserException;

	/**
	 * 解析
	 * @param config
	 * @param clazz
	 * @param <T>
	 * @return
	 * @throws ConfigurationParserException
	 */
	<T> T parse(Map<String, Object> config, Class<T> clazz) throws ConfigurationParserException;
}
