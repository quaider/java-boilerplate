package cn.kankancloud.jbp.core.util.encrypt;

import cn.kankancloud.jbp.core.exception.BizEncryptException;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.spec.AlgorithmParameterSpec;

/**
 * {@link Signature}
 *
 * @see <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#Signature">Standard Algorithm Name Documentation</a>
 * @see <a href="https://github.com/EncryptUtil/EncryptUtil/blob/master/encrypt-util/src/main/java/com/encrypt/util/SignatureUtils.java">SignatureUtils</a>
 */
public class SignatureUtil {

    //-------------------- algorithm --------------------//
    public static final String NONE_WITH_RSA = "NONEwithRSA";
    public static final String MD2_WITH_RSA = "MD2withRSA";
    public static final String MD5_WITH_RSA = "MD5withRSA";
    public static final String SHA1_WITH_RSA = "SHA1withRSA";
    public static final String SHA224_WITH_RSA = "SHA224withRSA";
    public static final String SHA256_WITH_RSA = "SHA256withRSA";
    public static final String SHA384_WITH_RSA = "SHA384withRSA";
    public static final String SHA512_WITH_RSA = "SHA512withRSA";
    public static final String SHA512_224_WITH_RSA = "SHA512/224withRSA";
    public static final String SHA512_256_WITH_RSA = "SHA512/256withRSA";
    public static final String RSASSA_PSS = "RSASSA-PSS";
    public static final String NONE_WITH_DSA = "NONEwithDSA";
    public static final String SHA1_WITH_DSA = "SHA1withDSA";
    public static final String SHA224_WITH_DSA = "SHA224withDSA";
    public static final String SHA256_WITH_DSA = "SHA256withDSA";
    public static final String SHA384_WITH_DSA = "SHA384withDSA";
    public static final String SHA512_WITH_DSA = "SHA512withDSA";
    public static final String NONE_WITH_ECDSA = "NONEwithECDSA";
    public static final String SHA1_WITH_ECDSA = "SHA1withECDSA";
    public static final String SHA224_WITH_ECDSA = "SHA224withECDSA";
    public static final String SHA256_WITH_ECDSA = "SHA256withECDSA";
    public static final String SHA384_WITH_ECDSA = "SHA384withECDSA";
    public static final String SHA512_WITH_ECDSA = "SHA512withECDSA";

    private SignatureUtil() {
    }

    /**
     * 校验签名
     *
     * @param data          未签名的数据
     * @param signatureData 需验证的签名数据
     * @param algorithm     算法
     * @param certificate
     */
    public static boolean verify(byte[] data, byte[] signatureData, String algorithm, Certificate certificate) {
        return verify(data, signatureData, algorithm, certificate, null);
    }

    /**
     * 校验签名
     *
     * @param data          未签名的数据
     * @param signatureData 需验证的签名数据
     * @param algorithm     算法
     * @param certificate
     * @param params        算法参数
     */
    public static boolean verify(byte[] data, byte[] signatureData, String algorithm, Certificate certificate, AlgorithmParameterSpec params) {
        try {
            Signature signature = Signature.getInstance(algorithm);
            signature.initVerify(certificate);
            if (params != null) {
                signature.setParameter(params);
            }
            signature.update(data);
            return signature.verify(signatureData);
        } catch (Exception e) {
            throw new BizEncryptException(e);
        }
    }

    /**
     * 校验签名
     *
     * @param data          未签名的数据
     * @param signatureData 需验证的签名数据
     * @param algorithm     算法
     * @param publicKey     公钥
     */
    public static boolean verify(byte[] data, byte[] signatureData, String algorithm, PublicKey publicKey) {
        return verify(data, signatureData, algorithm, publicKey, null);
    }

    /**
     * 校验签名
     *
     * @param data          未签名的数据
     * @param signatureData 需验证的签名数据
     * @param algorithm     算法
     * @param publicKey     公钥
     * @param params        算法参数
     */
    public static boolean verify(byte[] data, byte[] signatureData, String algorithm, PublicKey publicKey, AlgorithmParameterSpec params) {
        try {
            Signature signature = Signature.getInstance(algorithm);
            signature.initVerify(publicKey);
            if (params != null) {
                signature.setParameter(params);
            }
            signature.update(data);
            return signature.verify(signatureData);
        } catch (Exception e) {
            throw new BizEncryptException(e);
        }
    }

    /**
     * 签名
     *
     * @param data       未签名的数据
     * @param algorithm  算法
     * @param privateKey
     */
    public static byte[] sign(byte[] data, String algorithm, PrivateKey privateKey) {
        return sign(data, algorithm, privateKey, null, null);
    }

    /**
     * 签名
     *
     * @param data       未签名的数据
     * @param algorithm  算法
     * @param privateKey
     * @param params     算法参数
     * @param random
     */
    public static byte[] sign(byte[] data, String algorithm, PrivateKey privateKey, AlgorithmParameterSpec params, SecureRandom random) {
        try {
            Signature signature = Signature.getInstance(algorithm);
            signature.initSign(privateKey, random);
            if (params != null) {
                signature.setParameter(params);
            }
            signature.update(data);
            return signature.sign();
        } catch (Exception e) {
            throw new BizEncryptException(e);
        }
    }

}
