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

package fun.asgc.neutrino.core.context;

import fun.asgc.neutrino.core.annotation.Configuration;
import fun.asgc.neutrino.core.annotation.Value;
import lombok.Data;

import java.util.Set;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@Data
@Configuration(prefix = "neutrino")
public class ApplicationConfig {
	private Application application;
	private Http http;

	@Data
	public static class Application {
		/**
		 * 应用名称
		 */
		private String name;
	}

	@Data
	public static class Http {
		private Boolean enable;
		@Value("${port:8080}")
		private Integer port;
		@Value("context-path")
		private String contextPath;
		@Value("${max-content-length-desc:64KB}")
		private String maxContentLengthDesc;
		@Value("max-content-length")
		private Long maxContentLength;
		@Value("static-resource")
		private StaticResource staticResource;
	}

	@Data
	public static class StaticResource{
		private Set<String> locations;
	}
}
