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
package fun.asgc.neutrino.core.base;

import fun.asgc.neutrino.core.constant.MetaDataConstant;

/**
 * 全局配置
 * @author: aoshiguchen
 * @date: 2022/7/7
 */
public class GlobalConfig {
	/**
	 * 是否打印自动生成的代码
	 */
	private static volatile boolean isPrintGeneratorCode = false;
	/**
	 * 是否是容器启动
	 */
	private static volatile boolean isContainerStartup = false;
	/**
	 * 是否保存生成的代码
	 * 只是一个建议。
	 * 当程序是普通运行时，采纳建议。
	 * 当程序是以jar方式运行时，强制保存生成的代码
	 */
	private static volatile boolean isSaveGeneratorCode = false;
	/**
	 * 自动生成代码保存路径
	 */
	private static volatile String generatorCodeSavePath = "./lib/";
	/**
	 * mapper文件存放基础路径
	 */
	private static volatile String mapperXmlFileBasePath = MetaDataConstant.CLASSPATH_RESOURCE_IDENTIFIER.concat("/mapper");

	public static boolean isPrintGeneratorCode() {
		return isPrintGeneratorCode;
	}

	public static void setIsPrintGeneratorCode(boolean isPrintGeneratorCode) {
		GlobalConfig.isPrintGeneratorCode = isPrintGeneratorCode;
	}

	public static boolean isContainerStartup() {
		return isContainerStartup;
	}

	public static void setIsContainerStartup(boolean isContainerStartup) {
		GlobalConfig.isContainerStartup = isContainerStartup;
	}

	public static boolean isSaveGeneratorCode() {
		return isSaveGeneratorCode;
	}

	public static void setIsSaveGeneratorCode(boolean isSaveGeneratorCode) {
		GlobalConfig.isSaveGeneratorCode = isSaveGeneratorCode;
	}

	public static String getGeneratorCodeSavePath() {
		return generatorCodeSavePath;
	}

	public static void setGeneratorCodeSavePath(String generatorCodeSavePath) {
		GlobalConfig.generatorCodeSavePath = generatorCodeSavePath;
	}

	public static String getMapperXmlFileBasePath() {
		return mapperXmlFileBasePath;
	}
}
