package cn.easyjce.plugin.service;

import cn.easyjce.plugin.beans.Parameter;
import cn.easyjce.plugin.utils.LogUtil;
import cn.easyjce.plugin.validate.ByteArrValidate;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @Class: IJceSpec
 * @Date: 2022/8/2 14:21
 * @author: cuijiufeng
 */
public interface IJceSpec {

    default List<Parameter<?>> params(String algorithm) {
        return Collections.emptyList();
    }

    default Map<String, Object> executeInternal(String algorithm, Provider provider, byte[] inputBytes, Map<String, String> params)
            throws GeneralSecurityException, IOException {
        throw new UnsupportedOperationException("unsupported operation");
    }

    default SecretKey parseSecretKey(String algorithm, Provider provider, byte[] bytes) throws GeneralSecurityException {
        javax.crypto.SecretKeyFactory keyFactory = javax.crypto.SecretKeyFactory.getInstance(algorithm, provider);
        byte[] key = new ByteArrValidate("key", bytes).isNotEmpty().get();
        return keyFactory.generateSecret(new SecretKeySpec(key, algorithm));
    }

    default PrivateKey parsePrivateKey(String algorithm, Provider provider, byte[] bytes) throws GeneralSecurityException {
        java.security.KeyFactory instance;
        try {
            instance = java.security.KeyFactory.getInstance(algorithm, provider);
        } catch (NoSuchAlgorithmException e) {
            instance = java.security.KeyFactory.getInstance(algorithm);
            LogUtil.LOG.info(e.getMessage() + "\nfind it in" + instance.getProvider().getName());
        }
        byte[] priv = new ByteArrValidate("private key", bytes).isNotEmpty().get();
        return instance.generatePrivate(new PKCS8EncodedKeySpec(priv));
    }

    default PublicKey parsePublicKey(String algorithm, Provider provider, byte[] bytes) throws GeneralSecurityException {
        java.security.KeyFactory instance;
        try {
            instance = java.security.KeyFactory.getInstance(algorithm, provider);
        } catch (NoSuchAlgorithmException e) {
            instance = java.security.KeyFactory.getInstance(algorithm);
            LogUtil.LOG.info(e.getMessage() + "\nfind it in" + instance.getProvider().getName());
        }
        byte[] pub = new ByteArrValidate("public key", bytes).isNotEmpty().get();;
        return instance.generatePublic(new X509EncodedKeySpec(pub));
    }
}
