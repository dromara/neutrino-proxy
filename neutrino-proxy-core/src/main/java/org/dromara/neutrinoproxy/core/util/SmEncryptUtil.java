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

package org.dromara.neutrinoproxy.core.util;

import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.SmUtil;
import lombok.Data;

import javax.crypto.SecretKey;
import java.security.KeyPair;

/**
 *  国密算法加解密工具
 *  @author: az
 *  @date: 2023/11/07
 */
public class SmEncryptUtil {

    record Sm2KeyPairRecord(String privateKey, String publicKey) {
    }

    /**
     * 生成SM2密钥对
     * @return
     */
    public static Sm2KeyPairRecord generateSm2KeyPair() {
        KeyPair keyPair = SecureUtil.generateKeyPair("SM2");
        byte[] privateKeyBytes = keyPair.getPrivate().getEncoded();
        byte[] publicKeyBytes = keyPair.getPublic().getEncoded();

        String privateKey = HexUtil.encodeHexStr(privateKeyBytes);
        String publicKey = HexUtil.encodeHexStr(publicKeyBytes);

        return new Sm2KeyPairRecord(privateKey, publicKey);
    }

    /**
     * 使用SM2算法对数据进行加密
     * @param publicKey 加密所需的公钥
     * @param data 需要加密的数据
     * @return 加密后的字节数组
     */
    public static byte[] encryptBySm2(String publicKey, byte[] data) {
        return SmUtil.sm2(null, publicKey).encrypt(data);
    }

    /**
     * 使用SM2算法对数据进行解密
     * @param privateKey 解密所需私钥
     * @param data 需要解密的数据
     * @return 解密后的字节数组
     */
    public static byte[] decryptBySm2(String privateKey, byte[] data) {
        return SmUtil.sm2(privateKey, null).decrypt(data);
    }

    public static byte[] generateSm4Key() {
        SecretKey key = SecureUtil.generateKey("AES", 128);
        return key.getEncoded();
    }

    /**
     * 使用SM4算法加密数据
     * @param key 密钥
     * @param data 待加密的数据
     * @return 已加密的数据
     */
    public static byte[] encryptBySm4(byte[] key, byte[] data) {
        return SmUtil.sm4(key).encrypt(data);
    }

    /**
     * 使用SM4算法解密数据
     * @param key 密钥
     * @param encryptedData 已加密数据
     * @return 解密后的数据
     */
    public static byte[] decryptBySm4(byte[] key, byte[] encryptedData) {
        return SmUtil.sm4(key).decrypt(encryptedData);
    }

}
