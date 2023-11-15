package org.dromara.neutrinoproxy.core.util;

import cn.hutool.core.util.RandomUtil;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Random;

/**
 * AES工具
 */
public class AesUtil {

    public static byte[] generateKey() {
        byte[] keyBytes = new byte[16];
        Random random = RandomUtil.getRandom(true);
        random.nextBytes(keyBytes);
        return keyBytes;
    }

    /**
     * AES解密
     * @param decryptKey 秘钥，16位
     * @param encryptBytes 密文
     * @return 明文
     * @throws Exception
     */
    public static byte[] decrypt(byte[] decryptKey, byte[] encryptBytes) {
        try{
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decryptKey, "AES"));
            return cipher.doFinal(encryptBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * AES加密
     * @param encryptKey 秘钥，必须为16个字符组成
     * @param data 明文
     * @return 密文
     * @throws Exception
     */
    public static byte[] encrypt(byte[] encryptKey, byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptKey, "AES"));
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
