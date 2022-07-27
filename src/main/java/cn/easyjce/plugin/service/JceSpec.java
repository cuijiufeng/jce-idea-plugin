package cn.easyjce.plugin.service;

import cn.easyjce.plugin.ParamNullPointException;

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Class: JceSpec
 * @Date: 2022/7/27 9:22
 * @author: cuijiufeng
 */
public enum JceSpec {
    MessageDigest {
        @Override
        public Map<String, byte[]> executeInternal(String algorithm, Provider provider, byte[] input, Map<String, String> params) throws GeneralSecurityException {
            java.security.MessageDigest instance = java.security.MessageDigest.getInstance(algorithm, provider);
            Map<String, byte[]> rs = new HashMap<>(2);
            rs.put("output", instance.digest(input));
            return rs;
        }
    },
    KeyPairGenerator {
        @Override
        public List<String> params() {
            return Collections.singletonList("keysize");
        }

        @Override
        public Map<String, byte[]> executeInternal(String algorithm, Provider provider, byte[] inputBytes, Map<String, String> params) throws GeneralSecurityException {
            java.security.KeyPairGenerator instance = java.security.KeyPairGenerator.getInstance(algorithm, provider);
            try {
                instance.initialize(Integer.parseInt(params.get("keysize")));
            } catch (NumberFormatException e) {
                //ignore
            }
            KeyPair keyPair = instance.genKeyPair();
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();
            Map<String, byte[]> rs = new HashMap<>(2);
            rs.put("private", privateKey.getEncoded());
            rs.put("public", publicKey.getEncoded());
            return rs;
        }
    },
    SecureRandom {
        @Override
        public List<String> params() {
            return Collections.singletonList("length");
        }
        @Override
        public Map<String, byte[]> executeInternal(String algorithm, Provider provider, byte[] inputBytes, Map<String, String> params) throws GeneralSecurityException {
            java.security.SecureRandom instance = java.security.SecureRandom.getInstance(algorithm, provider);
            int length = 0;
            try {
                length = Integer.parseInt(params.get("length"));
            } catch (NumberFormatException e) {
                throw new ParamNullPointException("{0} parameter is empty", "length");
            }
            byte[] output = new byte[length];
            //生成随机数种子
            byte[] seed = instance.generateSeed(length);
            instance.setSeed(seed);
            instance.nextBytes(output);
            Map<String, byte[]> rs = new HashMap<>(2);
            rs.put("output", output);
            return rs;
        }
    }
    ;

    public abstract Map<String, byte[]> executeInternal(String algorithm, Provider provider, byte[] inputBytes, Map<String, String> params) throws GeneralSecurityException;

    public List<String> params() {
        return Collections.emptyList();
    }
}
