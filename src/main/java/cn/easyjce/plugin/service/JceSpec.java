package cn.easyjce.plugin.service;

import cn.easyjce.plugin.ParamNullPointException;
import cn.easyjce.plugin.beans.Parameter;

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.util.Arrays;
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
        public List<Parameter> params() {
            return Collections.singletonList(new Parameter("keysize", Parameter.DisplayUI.SHOW));
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
        public List<Parameter> params() {
            return Collections.singletonList(new Parameter("length", Parameter.DisplayUI.SHOW));
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
    },
    Signature {
        @Override
        public List<Parameter> params() {
            return Arrays.asList(
                    new Parameter("type", Arrays.asList("sign", "verify"), Parameter.DisplayUI.NONE),
                    new Parameter("cert", Parameter.DisplayUI.HIDE),
                    new Parameter("private", Parameter.DisplayUI.SHOW),
                    new Parameter("public", Parameter.DisplayUI.HIDE));
        }
        @Override
        public Map<String, byte[]> executeInternal(String algorithm, Provider provider, byte[] inputBytes, Map<String, String> params) throws GeneralSecurityException {
            java.security.Signature instance = java.security.Signature.getInstance(algorithm, provider);
            Map<String, byte[]> rs = new HashMap<>(2);
            rs.put("output", instance.sign());
            return rs;
        }
    },
    CertStore,
    KeyStore,
    SaslClientFactory,
    SaslServerFactory,
    AlgorithmParameterGenerator,
    AlgorithmParameters,
    CertPathBuilder,
    CertPathValidator,
    CertificateFactory,
    Configuration,
    KeyFactory,
    Policy,
    KeyAgreement,
    Cipher,
    Mac,
    SecretKeyFactory,
    KeyManagerFactory,
    SSLContext,
    TrustManagerFactory,
    GssApiMechanism,
    TerminalFactory,
    KeyInfoFactory
    ;

    public Map<String, byte[]> executeInternal(String algorithm, Provider provider, byte[] inputBytes, Map<String, String> params) throws GeneralSecurityException {
        throw new IllegalArgumentException("unsupported");
    }

    public List<Parameter> params() {
        return Collections.emptyList();
    }
}
