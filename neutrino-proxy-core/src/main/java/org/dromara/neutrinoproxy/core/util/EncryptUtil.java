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
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.util.encoders.Hex;
import org.dromara.neutrinoproxy.core.KeyPairRecord;

import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 *  国密算法加解密工具
 *  @author: az
 *  @date: 2023/11/07
 */
public class EncryptUtil {

    /**
     * 生成SM2密钥对
     * @return
     */
    public static KeyPairRecord generateSm2KeyPair() {

        String privateKeyHex = null;
        String publicKeyHex = null;

        KeyPair keyPair = Sm2Util.createECKeyPair();

        PrivateKey privateKey = keyPair.getPrivate();
        if (privateKey instanceof BCECPrivateKey) {
            //获取32字节十六进制私钥串
            privateKeyHex = ((BCECPrivateKey) privateKey).getD().toString(16);
        }

        PublicKey publicKey = keyPair.getPublic();
        if (publicKey instanceof BCECPublicKey) {
            //获取65字节非压缩缩的十六进制公钥串(0x04)
            publicKeyHex = Hex.toHexString(((BCECPublicKey) publicKey).getQ().getEncoded(false));
        }

        return new KeyPairRecord(privateKeyHex, publicKeyHex);
    }

    /**
     * 使用SM2算法对数据进行加密
     * @param publicKey 加密所需的公钥
     * @param data 需要加密的数据
     * @return 加密后的字节数组
     */
    public static byte[] encryptBySm2(String publicKey, byte[] data) {
        return Sm2Util.encrypt(publicKey, data);
    }

    /**
     * 使用SM2算法对数据进行解密
     * @param privateKey 解密所需私钥
     * @param data 需要解密的数据
     * @return 解密后的字节数组
     */
    public static byte[] decryptBySm2(String privateKey, byte[] data) {
        return Sm2Util.decrypt(privateKey, data);
    }

    public static byte[] generateSm4Key() {
        return SecureUtil.generateKey("AES", 128).getEncoded();
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

    public static byte[] generateAesKey() {
        return AesUtil.generateKey();
    }

    /**
     * 使用AES算法加密数据
     * @param key 密钥
     * @param data 被加密数据
     * @return 加密后的数据
     */
    public static byte[] encryptByAes(byte[] key, byte[] data) {
        return AesUtil.encrypt(key, data);
    }

    /**
     * 使用AES法解密数据
     * @param key 密钥
     * @param encryptedData 已加密数据
     * @return 解密后的数据
     */
    public static byte[] decryptByAes(byte[] key, byte[] encryptedData) {
        return AesUtil.decrypt(key, encryptedData);
    }

    /**
     * 使用SM3算法对内容生成摘要
     * @param data
     * @return
     */
    public static String digestBySm3(byte[] data) {
        return SmUtil.sm3().digestHex(data);
    }

}
