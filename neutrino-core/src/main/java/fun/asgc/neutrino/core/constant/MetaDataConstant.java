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

package fun.asgc.neutrino.core.constant;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
public interface MetaDataConstant {

	String DEFAULT_BANNER = "\n          _____                    _____                    _____                _____                    _____                    _____                    _____                   _______         \n" +
		"         /\\    \\                  /\\    \\                  /\\    \\              /\\    \\                  /\\    \\                  /\\    \\                  /\\    \\                 /::\\    \\        \n" +
		"        /::\\____\\                /::\\    \\                /::\\____\\            /::\\    \\                /::\\    \\                /::\\    \\                /::\\____\\               /::::\\    \\       \n" +
		"       /::::|   |               /::::\\    \\              /:::/    /            \\:::\\    \\              /::::\\    \\               \\:::\\    \\              /::::|   |              /::::::\\    \\      \n" +
		"      /:::::|   |              /::::::\\    \\            /:::/    /              \\:::\\    \\            /::::::\\    \\               \\:::\\    \\            /:::::|   |             /::::::::\\    \\     \n" +
		"     /::::::|   |             /:::/\\:::\\    \\          /:::/    /                \\:::\\    \\          /:::/\\:::\\    \\               \\:::\\    \\          /::::::|   |            /:::/~~\\:::\\    \\    \n" +
		"    /:::/|::|   |            /:::/__\\:::\\    \\        /:::/    /                  \\:::\\    \\        /:::/__\\:::\\    \\               \\:::\\    \\        /:::/|::|   |           /:::/    \\:::\\    \\   \n" +
		"   /:::/ |::|   |           /::::\\   \\:::\\    \\      /:::/    /                   /::::\\    \\      /::::\\   \\:::\\    \\              /::::\\    \\      /:::/ |::|   |          /:::/    / \\:::\\    \\  \n" +
		"  /:::/  |::|   | _____    /::::::\\   \\:::\\    \\    /:::/    /      _____        /::::::\\    \\    /::::::\\   \\:::\\    \\    ____    /::::::\\    \\    /:::/  |::|   | _____   /:::/____/   \\:::\\____\\ \n" +
		" /:::/   |::|   |/\\    \\  /:::/\\:::\\   \\:::\\    \\  /:::/____/      /\\    \\      /:::/\\:::\\    \\  /:::/\\:::\\   \\:::\\____\\  /\\   \\  /:::/\\:::\\    \\  /:::/   |::|   |/\\    \\ |:::|    |     |:::|    |\n" +
		"/:: /    |::|   /::\\____\\/:::/__\\:::\\   \\:::\\____\\|:::|    /      /::\\____\\    /:::/  \\:::\\____\\/:::/  \\:::\\   \\:::|    |/::\\   \\/:::/  \\:::\\____\\/:: /    |::|   /::\\____\\|:::|____|     |:::|    |\n" +
		"\\::/    /|::|  /:::/    /\\:::\\   \\:::\\   \\::/    /|:::|____\\     /:::/    /   /:::/    \\::/    /\\::/   |::::\\  /:::|____|\\:::\\  /:::/    \\::/    /\\::/    /|::|  /:::/    / \\:::\\    \\   /:::/    / \n" +
		" \\/____/ |::| /:::/    /  \\:::\\   \\:::\\   \\/____/  \\:::\\    \\   /:::/    /   /:::/    / \\/____/  \\/____|:::::\\/:::/    /  \\:::\\/:::/    / \\/____/  \\/____/ |::| /:::/    /   \\:::\\    \\ /:::/    /  \n" +
		"         |::|/:::/    /    \\:::\\   \\:::\\    \\       \\:::\\    \\ /:::/    /   /:::/    /                 |:::::::::/    /    \\::::::/    /                   |::|/:::/    /     \\:::\\    /:::/    /   \n" +
		"         |::::::/    /      \\:::\\   \\:::\\____\\       \\:::\\    /:::/    /   /:::/    /                  |::|\\::::/    /      \\::::/____/                    |::::::/    /       \\:::\\__/:::/    /    \n" +
		"         |:::::/    /        \\:::\\   \\::/    /        \\:::\\__/:::/    /    \\::/    /                   |::| \\::/____/        \\:::\\    \\                    |:::::/    /         \\::::::::/    /     \n" +
		"         |::::/    /          \\:::\\   \\/____/          \\::::::::/    /      \\/____/                    |::|  ~|               \\:::\\    \\                   |::::/    /           \\::::::/    /      \n" +
		"         /:::/    /            \\:::\\    \\               \\::::::/    /                                  |::|   |                \\:::\\    \\                  /:::/    /             \\::::/    /       \n" +
		"        /:::/    /              \\:::\\____\\               \\::::/    /                                   \\::|   |                 \\:::\\____\\                /:::/    /               \\::/____/        \n" +
		"        \\::/    /                \\::/    /                \\::/____/                                     \\:|   |                  \\::/    /                \\::/    /                 ~~              \n" +
		"         \\/____/                  \\/____/                  ~~                                            \\|___|                   \\/____/                  \\/____/                                  \n" +
		"                                                                                                                                                                                                    ";

	/**
	 * classpath标识符
	 */
	String CLASSPATH_IDENTIFIER = "classpath";

	/**
	 * classpath资源标识
	 */
	String CLASSPATH_RESOURCE_IDENTIFIER = CLASSPATH_IDENTIFIER + ":";

	/**
	 * 默认的yml配置文件名
	 */
	String DEFAULT_YML_CONFIG_FILE_NAME = "application.yml";

	/**
	 * 应用banner路径
	 */
	String APP_BANNER_FILE_PATH = CLASSPATH_RESOURCE_IDENTIFIER.concat("/banner.txt");

	/**
	 * 默认的http页面路径
	 */
	String DEFAULT_HTTP_PAGE_PATH = CLASSPATH_RESOURCE_IDENTIFIER.concat("/webapp/");
	/**
	 * 服务版本
	 */
	String SERVER_VS = "Neutrino-1.0";
	/**
	 * app生命周期主题
	 */
	String TOPIC_APP_LIFE_CYCLE = "TP_APP_LIFE_CYCLE";
	/**
	 * 环境变量key
	 */
	String ENVIRONMENT_VARIABLE_KEY = "NEUTRINO_PROXY";

	interface Config {
		String NEUTRINO_APPLICATION_NAME = "neutrino.application.name";
		String NEUTRINO_HTTP_ENABLE = "neutrino.http.enable";
		String NEUTRINO_HTTP_PORT = "neutrino.http.port";
		String NEUTRINO_HTTP_CONTEXT_PATH = "neutrino.http.context-path";
		String NEUTRINO_HTTP_MAX_CONTENT_LENGTH_DESC = "neutrino.http.max-content-length-desc";
		String NEUTRINO_HTTP_STATIC_RESOURCE_LOCATIONS = "neutrino.http.static-resource.locations";
		String NEUTRINO_PROXY_PROTOCOL_MAX_FRAME_LENGTH = "neutrino.proxy.protocol.max-frame-length";
		String NEUTRINO_PROXY_PROTOCOL_LENGTH_FIELD_OFFSET = "neutrino.proxy.protocol.length-field-offset";
		String NEUTRINO_PROXY_PROTOCOL_LENGTH_FIELD_LENGTH = "neutrino.proxy.protocol.length-field-length";
		String NEUTRINO_PROXY_PROTOCOL_INITIAL_BYTES_TO_STRIP = "neutrino.proxy.protocol.initial-bytes-to-strip";
		String NEUTRINO_PROXY_PROTOCOL_LENGTH_ADJUSTMENT = "neutrino.proxy.protocol.length-adjustment";
		String NEUTRINO_PROXY_PROTOCOL_READ_IDLE_TIME = "neutrino.proxy.protocol.read-idle-time";
		String NEUTRINO_PROXY_PROTOCOL_WRITE_IDLE_TIME = "neutrino.proxy.protocol.write-idle-time";
		String NEUTRINO_PROXY_PROTOCOL_ALL_IDLE_TIME_SECONDS = "neutrino.proxy.protocol.all-idle-time-seconds";
		String NEUTRINO_PROXY_SERVER_PORT = "neutrino.proxy.server.port";
		String NEUTRINO_PROXY_SERVER_SSL_PORT = "neutrino.proxy.server.ssl-port";
		String NEUTRINO_PROXY_SERVER_KEY_STORE_PASSWORD = "neutrino.proxy.server.key-store-password";
		String NEUTRINO_PROXY_SERVER_KEY_MANAGER_PASSWORD = "neutrino.proxy.server.key-manager-password";
		String NEUTRINO_PROXY_SERVER_JKS_PATH = "neutrino.proxy.server.jks-path";
		String NEUTRINO_PROXY_CLIENT_KEY_STORE_PASSWORD = "neutrino.proxy.client.key-store-password";
		String NEUTRINO_PROXY_CLIENT_JKS_PATH = "neutrino.proxy.client.jks-path";
		String NEUTRINO_PROXY_CLIENT_SERVER_IP = "neutrino.proxy.client.server-ip";
		String NEUTRINO_PROXY_CLIENT_SERVER_PORT = "neutrino.proxy.client.server-port";
		String NEUTRINO_PROXY_CLIENT_SSL_ENABLE = "neutrino.proxy.client.ssl-enable";
		String NEUTRINO_PROXY_CLIENT_OBTAIN_LICENSE_INTERVAL = "neutrino.proxy.client.obtain-license-interval";
		String NEUTRINO_DATA_DB_TYPE = "neutrino.data.db.type";
		String NEUTRINO_DATA_DB_URL = "neutrino.data.db.url";
		String NEUTRINO_DATA_DB_DRIVER_CLASS = "neutrino.data.db.driver-class";
		String NEUTRINO_DATA_DB_USERNAME = "neutrino.data.db.username";
		String NEUTRINO_DATA_DB_PASSWORD = "neutrino.data.db.password";
	}
}
