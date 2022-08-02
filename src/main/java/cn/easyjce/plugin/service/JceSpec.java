package cn.easyjce.plugin.service;

import cn.easyjce.plugin.beans.Parameter;
import cn.easyjce.plugin.exception.ParameterIllegalException;
import cn.easyjce.plugin.service.impl.CodecServiceImpl;
import cn.easyjce.plugin.validate.ByteArrValidate;
import cn.easyjce.plugin.validate.StringValidate;
import com.intellij.openapi.components.ServiceManager;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
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
        public Map<String, Object> executeInternal(String algorithm, Provider provider, byte[] input, Map<String, String> params)
                throws GeneralSecurityException {
            java.security.MessageDigest instance = java.security.MessageDigest.getInstance(algorithm, provider);
            Map<String, Object> rs = new HashMap<>(2);
            rs.put("output", instance.digest(input));
            return rs;
        }
    },
    KeyPairGenerator {
        @Override
        public List<Parameter> params() {
            return Collections.singletonList(new Parameter("keysize"));
        }
        @Override
        public Map<String, Object> executeInternal(String algorithm, Provider provider, byte[] inputBytes, Map<String, String> params)
                throws GeneralSecurityException {
            java.security.KeyPairGenerator instance = java.security.KeyPairGenerator.getInstance(algorithm, provider);
            if (StringUtils.isNotBlank(params.get("keysize"))) {
                Integer keysize = new StringValidate("keysize", params.get("keysize"))
                        .parseInt()
                        .get();
                instance.initialize(keysize);
            }
            KeyPair keyPair = instance.genKeyPair();
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();
            Map<String, Object> rs = new HashMap<>(2);
            rs.put("private", privateKey.getEncoded());
            rs.put("public", publicKey.getEncoded());
            return rs;
        }
    },
    SecureRandom {
        @Override
        public List<Parameter> params() {
            return Collections.singletonList(new Parameter("length"));
        }
        @Override
        public Map<String, Object> executeInternal(String algorithm, Provider provider, byte[] inputBytes, Map<String, String> params)
                throws GeneralSecurityException {
            java.security.SecureRandom instance = java.security.SecureRandom.getInstance(algorithm, provider);
            Integer length = new StringValidate("length", params.get("length"))
                    .isNotBlank()
                    .parseInt()
                    .get();
            byte[] output = new byte[length];
            //生成随机数种子
            byte[] seed = instance.generateSeed(length);
            instance.setSeed(seed);
            instance.nextBytes(output);
            Map<String, Object> rs = new HashMap<>(2);
            rs.put("output", output);
            return rs;
        }
    },
    Signature {
        @Override
        public List<Parameter> params() {
            Parameter parameter = new Parameter("type", Arrays.asList("sign", "verify"), 2);
            return Arrays.asList(parameter,
                    new Parameter("private", () -> parameter.getValue().equals("sign")),
                    new Parameter("cert", () -> parameter.getValue().equals("verify")),
                    new Parameter("public", () -> parameter.getValue().equals("verify")),
                    new Parameter("plain", () -> parameter.getValue().equals("verify")));
        }
        @Override
        public Map<String, Object> executeInternal(String algorithm, Provider provider, byte[] inputBytes, Map<String, String> params)
                throws GeneralSecurityException {
            CodecServiceImpl service = ServiceManager.getService(CodecServiceImpl.class);
            java.security.Signature instance = java.security.Signature.getInstance(algorithm, provider);
            java.security.KeyFactory keyFactory = java.security.KeyFactory.getInstance(algorithm.split("with")[1], provider);
            String type = new StringValidate("type", params.get("type"))
                    .isNotBlank()
                    .in(Arrays.asList("sign", "verify"))
                    .get();
            Map<String, Object> rs = new HashMap<>(2);
            if (type.equals("sign")) {
                String privParam = new StringValidate("private", params.get("private"))
                        .isNotBlank()
                        .get();
                byte[] priv = service.decode(CodecServiceImpl.IO.IN, privParam);
                instance.initSign(keyFactory.generatePrivate(new PKCS8EncodedKeySpec(priv)));
                instance.update(new ByteArrValidate("input", inputBytes).isNotEmpty().get());
                rs.put("output", instance.sign());
            } else if (type.equals("verify")) {
                if (StringUtils.isNotBlank(params.get("public"))) {
                    byte[] pub = service.decode(CodecServiceImpl.IO.IN, params.get("public"));
                    instance.initVerify(keyFactory.generatePublic(new X509EncodedKeySpec(pub)));
                } else if (StringUtils.isNotBlank(params.get("cert"))) {
                    throw new UnsupportedOperationException("unsupported cert");
                } else {
                    throw new ParameterIllegalException("{0} parameter is empty", "cert、public");
                }
                String plain = new StringValidate( "plain", params.get("plain"))
                        .isNotBlank()
                        .get();
                instance.update(service.decode(CodecServiceImpl.IO.IN, plain));
                rs.put("output", instance.verify(new ByteArrValidate("input", inputBytes).isNotEmpty().get()));
            }
            return rs;
        }
    },
    CertStore,
    KeyStore,
    KeyGenerator {
        @Override
        public List<Parameter> params() {
            return Collections.singletonList(new Parameter("keysize"));
        }
        @Override
        public Map<String, Object> executeInternal(String algorithm, Provider provider, byte[] inputBytes, Map<String, String> params) throws GeneralSecurityException {
            javax.crypto.KeyGenerator instance = javax.crypto.KeyGenerator.getInstance(algorithm, provider);
            SecretKey secretKey = instance.generateKey();
            if (StringUtils.isNotBlank(params.get("keysize"))) {
                Integer keysize = new StringValidate("keysize", params.get("keysize"))
                        .parseInt()
                        .get();
                instance.init(keysize);
            }
            Map<String, Object> rs = new HashMap<>(2);
            rs.put("key", secretKey.getEncoded());
            return rs;
        }
    },
    SaslClientFactory,
    SaslServerFactory,
    AlgorithmParameterGenerator,
    AlgorithmParameters,
    CertPathBuilder,
    CertPathValidator,
    CertificateFactory,
    Configuration,
    KeyFactory{
        @Override
        public List<Parameter> params() {
            return Arrays.asList(new Parameter("private"), new Parameter("public"));
        }
        @Override
        public Map<String, Object> executeInternal(String algorithm, Provider provider, byte[] inputBytes, Map<String, String> params)
                throws GeneralSecurityException {
            CodecServiceImpl service = ServiceManager.getService(CodecServiceImpl.class);
            java.security.KeyFactory instance = java.security.KeyFactory.getInstance(algorithm, provider);
            String privParam = new StringValidate("private", params.get("private"))
                    .isNotBlank()
                    .get();
            byte[] priv = service.decode(CodecServiceImpl.IO.IN, privParam);
            PrivateKey privateKey = instance.generatePrivate(new PKCS8EncodedKeySpec(priv));
            String pubParam = new StringValidate("public", params.get("public"))
                    .isNotBlank()
                    .get();
            byte[] pub = service.decode(CodecServiceImpl.IO.IN, pubParam);
            PublicKey publicKey = instance.generatePublic(new X509EncodedKeySpec(pub));
            Map<String, Object> rs = new HashMap<>(2);
            rs.put("private", privateKey);
            rs.put("public", publicKey);
            return rs;
        }
    },
    Policy,
    KeyAgreement,
    Cipher {
        @Override
        public List<Parameter> params() {
            Parameter parameter = new Parameter("type", Arrays.asList("symmetric encryption", "symmetric decryption", "asymmetric encryption", "asymmetric decryption"), 2);
            return Arrays.asList(parameter,
                    new Parameter("key", () -> parameter.getValue().equals("symmetric encryption") || parameter.getValue().equals("symmetric decryption")),
                    new Parameter("private", () -> parameter.getValue().equals("asymmetric encryption")),
                    new Parameter("public", () -> parameter.getValue().equals("asymmetric decryption")));
        }
        @Override
        public Map<String, Object> executeInternal(String algorithm, Provider provider, byte[] inputBytes, Map<String, String> params) throws GeneralSecurityException, IOException {
            javax.crypto.Cipher instance = javax.crypto.Cipher.getInstance(algorithm, provider);
            //instance.init();
            //instance.update();
            Map<String, Object> rs = new HashMap<>(2);
            rs.put("output", instance.doFinal());
            return rs;
        }
    },
    Mac {
        @Override
        public List<Parameter> params() {
            return Collections.singletonList(new Parameter("key"));
        }
        @Override
        public Map<String, Object> executeInternal(String algorithm, Provider provider, byte[] inputBytes, Map<String, String> params) throws GeneralSecurityException {
            CodecServiceImpl service = ServiceManager.getService(CodecServiceImpl.class);
            javax.crypto.Mac instance = javax.crypto.Mac.getInstance(algorithm, provider);
            String keyParam = new StringValidate("key", params.get("key"))
                    .isNotBlank()
                    .get();
            byte[] key = service.decode(CodecServiceImpl.IO.IN, keyParam);
            instance.init(new SecretKeySpec(key, algorithm));
            instance.update(new ByteArrValidate("input", inputBytes).isNotEmpty().get());
            Map<String, Object> rs = new HashMap<>(2);
            rs.put("output", instance.doFinal());
            return rs;
        }
    },
    SecretKeyFactory {
        @Override
        public List<Parameter> params() {
            return super.params();
        }
        @Override
        public Map<String, Object> executeInternal(String algorithm, Provider provider, byte[] inputBytes, Map<String, String> params) throws GeneralSecurityException, IOException {
            javax.crypto.SecretKeyFactory instance = javax.crypto.SecretKeyFactory.getInstance(algorithm, provider);
            SecretKeySpec keySpec = new SecretKeySpec(new ByteArrValidate("input", inputBytes).isNotEmpty().get(), algorithm);
            SecretKey secretKey = instance.generateSecret(keySpec);
            Map<String, Object> rs = new HashMap<>(2);
            rs.put("key", secretKey);
            return rs;
        }
    },
    KeyManagerFactory,
    SSLContext,
    TrustManagerFactory,
    GssApiMechanism,
    TerminalFactory,
    KeyInfoFactory
    ;

    public List<Parameter> params() {
        return Collections.emptyList();
    }

    public Map<String, Object> executeInternal(String algorithm, Provider provider, byte[] inputBytes, Map<String, String> params)
            throws GeneralSecurityException, IOException {
        throw new UnsupportedOperationException("unsupported operation");
    }
}
