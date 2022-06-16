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

package fun.asgc.neutrino.core.config.impl;

import fun.asgc.neutrino.core.config.AbstractConfigurationParser;
import fun.asgc.neutrino.core.constant.MetaDataConstant;
import fun.asgc.neutrino.core.exception.ConfigurationParserException;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
public class YmlConfigurationParser extends AbstractConfigurationParser {

	@Override
	public Map<String, Object> parse2Map(InputStream in) throws ConfigurationParserException {
		try {
			return new Yaml().load(in);
		} catch (Exception e) {
			throw new ConfigurationParserException("yml配置解析异常！");
		}
	}

	@Override
	protected String defaultFileName() {
		return MetaDataConstant.DEFAULT_YML_CONFIG_FILE_NAME;
	}
}
