package cn.kankancloud.jbp.core.util.encrypt;

import cn.kankancloud.jbp.core.exception.BizEncryptException;
import cn.kankancloud.jbp.core.util.HexUtil;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;

/**
 * {@link Mac}
 *
 * @see <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#Mac">Standard Algorithm Name Documentation</a>
 * @see <a href="https://github.com/EncryptUtil/EncryptUtil/blob/master/encrypt-util/src/main/java/com/encrypt/util/MacUtils.java">MacUtils</a>
 */
public class MacUtil {
    //-------------------- algorithm --------------------//
    public static final String ALG_HMAC_MD5 = "HmacMD5";
    public static final String ALG_HMAC_SHA1 = "HmacSHA1";
    public static final String ALG_HMAC_SHA224 = "HmacSHA224";
    public static final String ALG_HMAC_SHA256 = "HmacSHA256";
    public static final String ALG_HMAC_SHA384 = "HmacSHA384";
    public static final String ALG_HMAC_SHA512 = "HmacSHA512";

    private MacUtil() {
    }

    /**
     * HmacMD5 加密
     *
     * @param data 需加密的数据
     * @param key  秘钥
     */
    public static byte[] hMacMd5(byte[] data, byte[] key) {
        return encrypt(data, ALG_HMAC_MD5, key);
    }

    /**
     * HmacMD5 加密
     *
     * @param data 需加密的数据
     * @param key  秘钥
     */
    public static String hMacMd5(String data, byte[] key) {
        return encryptToHexString(data, ALG_HMAC_MD5, key);
    }

    /**
     * HmacSHA1 加密
     *
     * @param data 需加密的数据
     * @param key  秘钥
     */
    public static byte[] hMacSha1(byte[] data, byte[] key) {
        return encrypt(data, ALG_HMAC_SHA1, key);
    }

    /**
     * HmacSHA1 加密
     *
     * @param data 需加密的数据
     * @param key  秘钥
     */
    public static String hMacSha1(String data, byte[] key) {
        return encryptToHexString(data, ALG_HMAC_SHA1, key);
    }

    /**
     * HmacSHA224 加密
     *
     * @param data 需加密的数据
     * @param key  秘钥
     */
    public static byte[] hMacSha224(byte[] data, byte[] key) {
        return encrypt(data, ALG_HMAC_SHA224, key);
    }

    /**
     * HmacSHA224 加密
     *
     * @param data 需加密的数据
     * @param key  秘钥
     */
    public static String hMacSha224(String data, byte[] key) {
        return encryptToHexString(data, ALG_HMAC_SHA224, key);
    }

    /**
     * HmacSHA256 加密
     *
     * @param data 需加密的数据
     * @param key  秘钥
     */
    public static byte[] hMacSha256(byte[] data, byte[] key) {
        return encrypt(data, ALG_HMAC_SHA256, key);
    }

    /**
     * HmacSHA256 加密
     *
     * @param data 需加密的数据
     * @param key  秘钥
     */
    public static String hMacSha256(String data, byte[] key) {
        return encryptToHexString(data, ALG_HMAC_SHA256, key);
    }

    /**
     * HmacSHA384 加密
     *
     * @param data 需加密的数据
     * @param key  秘钥
     */
    public static byte[] hMacSha384(byte[] data, byte[] key) {
        return encrypt(data, ALG_HMAC_SHA384, key);
    }

    /**
     * HmacSHA384 加密
     *
     * @param data 需加密的数据
     * @param key  秘钥
     */
    public static String hMacSha384(String data, byte[] key) {
        return encryptToHexString(data, ALG_HMAC_SHA384, key);
    }

    /**
     * HmacSHA512 加密
     *
     * @param data 需加密的数据
     * @param key  秘钥
     */
    public static byte[] hMacSha512(byte[] data, byte[] key) {
        return encrypt(data, ALG_HMAC_SHA512, key);
    }

    /**
     * HmacSHA512 加密
     *
     * @param data 需加密的数据
     * @param key  秘钥
     */
    public static String hMacSha512(String data, byte[] key) {
        return encryptToHexString(data, ALG_HMAC_SHA512, key);
    }

    /**
     * 加密
     *
     * @param data      需加密的数据
     * @param algorithm 算法
     * @param key       秘钥
     */
    public static String encryptToHexString(String data, String algorithm, byte[] key) {
        byte[] result = encrypt(data.getBytes(), algorithm, key);
        if (result != null) {
            return HexUtil.byteArrayToHexString(result);
        }
        return null;
    }

    /**
     * 加密
     *
     * @param data      需加密的数据
     * @param algorithm 算法
     * @param key       秘钥
     */
    public static byte[] encrypt(byte[] data, String algorithm, byte[] key) {
        return encrypt(data, algorithm, key, null);
    }

    /**
     * 加密
     *
     * @param data      需加密的数据
     * @param algorithm 算法
     * @param key       秘钥
     * @param params    算法参数
     */
    public static byte[] encrypt(byte[] data, String algorithm, byte[] key, AlgorithmParameterSpec params) {
        SecretKeySpec secretKey = new SecretKeySpec(key, algorithm);
        return encrypt(data, algorithm, secretKey, params);
    }

    /**
     * 加密
     *
     * @param data      需加密的数据
     * @param algorithm 算法
     * @param key       秘钥
     * @param params    算法参数
     */
    public static byte[] encrypt(byte[] data, String algorithm, Key key, AlgorithmParameterSpec params) {
        try {
            Mac mac = Mac.getInstance(algorithm);
            mac.init(key, params);
            return mac.doFinal(data);
        } catch (Exception e) {
            throw new BizEncryptException("encrypt error", e);
        }
    }
}
