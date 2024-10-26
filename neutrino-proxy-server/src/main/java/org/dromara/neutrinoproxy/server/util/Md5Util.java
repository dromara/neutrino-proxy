package org.dromara.neutrinoproxy.server.util;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/1
 */
public class Md5Util {

	/**
	 * md5加密
	 * @param data
	 * @return
	 */
	public static String encode(String data) {
		byte[] digest = null;
		try {
			MessageDigest md5 = MessageDigest.getInstance("md5");
			digest  = md5.digest(data.getBytes("utf-8"));
			return new BigInteger(1, digest).toString(16);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
