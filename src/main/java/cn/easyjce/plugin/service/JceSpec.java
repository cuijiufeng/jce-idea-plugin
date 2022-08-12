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
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.spec.DSAParameterSpec;
import java.security.spec.InvalidKeySpecException;
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
public enum JceSpec implements IJceSpec {
    SecureRandom {
        @Override
        public List<Parameter> params(String algorithm) {
            return Collections.singletonList(new Parameter("length", null, () -> true));
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
    Mac {
        @Override
        public List<Parameter> params(String algorithm) {
            return Collections.singletonList(new Parameter("key", null, () -> true));
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
            instance.update(inputBytes);
            Map<String, Object> rs = new HashMap<>(2);
            rs.put("output", instance.doFinal());
            return rs;
        }
    },
    KeyGenerator {
        @Override
        public List<Parameter> params(String algorithm) {
            return Collections.singletonList(new Parameter("keysize", null, () -> true));
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
    SecretKeyFactory {
        @Override
        public List<Parameter> params(String algorithm) {
            return super.params(algorithm);
        }
        @Override
        public Map<String, Object> executeInternal(String algorithm, Provider provider, byte[] inputBytes, Map<String, String> params) throws GeneralSecurityException, IOException {
            SecretKey secretKey = parseSecretKey(algorithm, provider, inputBytes);
            Map<String, Object> rs = new HashMap<>(2);
            rs.put("key", secretKey);
            return rs;
        }
    },
    KeyPairGenerator {
        @Override
        public List<Parameter> params(String algorithm) {
            return Collections.singletonList(new Parameter("keysize", null, () -> true));
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
    KeyFactory{
        @Override
        public List<Parameter> params(String algorithm) {
            return Arrays.asList(new Parameter("private", null, () -> true), new Parameter("public", null, () -> true));
        }
        @Override
        public Map<String, Object> executeInternal(String algorithm, Provider provider, byte[] inputBytes, Map<String, String> params)
                throws GeneralSecurityException {
            CodecServiceImpl service = ServiceManager.getService(CodecServiceImpl.class);
            byte[] priv = service.decode(CodecServiceImpl.IO.IN, params.get("private"));
            PrivateKey privateKey = parsePrivateKey(algorithm, provider, priv);
            byte[] pub = service.decode(CodecServiceImpl.IO.IN, params.get("public"));
            PublicKey publicKey = parsePublicKey(algorithm, provider, pub);
            Map<String, Object> rs = new HashMap<>(2);
            rs.put("private", privateKey);
            rs.put("public", publicKey);
            return rs;
        }
    },
    Signature {
        @Override
        public List<Parameter> params(String algorithm) {
            Parameter parameter = new Parameter("type", Arrays.asList("sign", "verify"), 2);
            return Arrays.asList(parameter,
                    new Parameter("private", null, () -> parameter.getValue().equals("sign")),
                    new Parameter("cert", null, () -> parameter.getValue().equals("verify")),
                    new Parameter("public", null, () -> parameter.getValue().equals("verify")),
                    new Parameter("plain", null, () -> parameter.getValue().equals("verify")));
        }
        @Override
        public Map<String, Object> executeInternal(String algorithm, Provider provider, byte[] inputBytes, Map<String, String> params)
                throws GeneralSecurityException {
            CodecServiceImpl service = ServiceManager.getService(CodecServiceImpl.class);
            java.security.Signature instance = java.security.Signature.getInstance(algorithm, provider);
            String type = new StringValidate("type", params.get("type"))
                    .isNotBlank()
                    .in(Arrays.asList("sign", "verify"))
                    .get();
            Map<String, Object> rs = new HashMap<>(2);
            if (type.equals("sign")) {
                byte[] priv = service.decode(CodecServiceImpl.IO.IN, params.get("private"));
                instance.initSign(parsePrivateKey(algorithm.split("with")[1], provider, priv));
                instance.update(new ByteArrValidate("input", inputBytes).isNotEmpty().get());
                rs.put("output", instance.sign());
            } else if (type.equals("verify")) {
                if (StringUtils.isNotBlank(params.get("public"))) {
                    byte[] pub = service.decode(CodecServiceImpl.IO.IN, params.get("public"));
                    instance.initVerify(parsePublicKey(algorithm.split("with")[1], provider, pub));
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
    Cipher {
        @Override
        public List<Parameter> params(String algorithm) {
            Parameter parameter = new Parameter("type", Arrays.asList("symmetric encryption", "symmetric decryption", "asymmetric encryption", "asymmetric decryption"), 2);
            return Arrays.asList(parameter, new Parameter("key", null, () -> true));
        }
        @Override
        public Map<String, Object> executeInternal(String algorithm, Provider provider, byte[] inputBytes, Map<String, String> params) throws GeneralSecurityException {
            CodecServiceImpl service = ServiceManager.getService(CodecServiceImpl.class);
            javax.crypto.Cipher instance = javax.crypto.Cipher.getInstance(algorithm, provider);
            String type = new StringValidate("type", params.get("type"))
                    .isNotBlank()
                    .in(Arrays.asList("symmetric encryption", "symmetric decryption", "asymmetric encryption", "asymmetric decryption"))
                    .get();
            //TODO 2022/8/8 17:22 对称加密可以使用向量iv
            if ("symmetric encryption".equals(type)) {
                byte[] key = service.decode(CodecServiceImpl.IO.IN, params.get("key"));
                SecretKey secretKey = new SecretKeySpec(key, algorithm.split("/")[0].split("_")[0]);
                instance.init(javax.crypto.Cipher.ENCRYPT_MODE, secretKey);
            } else if ("symmetric decryption".equals(type)) {
                byte[] key = service.decode(CodecServiceImpl.IO.IN, params.get("key"));
                SecretKey secretKey = new SecretKeySpec(key, algorithm.split("/")[0].split("_")[0]);
                instance.init(javax.crypto.Cipher.DECRYPT_MODE, secretKey);
            } else if ("asymmetric encryption".equals(type)) {
                Key key;
                try {
                    byte[] pub = service.decode(CodecServiceImpl.IO.IN, params.get("key"));
                    key = parsePublicKey(algorithm, provider, pub);
                } catch (InvalidKeySpecException e) {
                    byte[] priv = service.decode(CodecServiceImpl.IO.IN, params.get("key"));
                    key = parsePrivateKey(algorithm, provider, priv);
                }
                instance.init(javax.crypto.Cipher.ENCRYPT_MODE, key);
            } else if ("asymmetric decryption".equals(type)) {
                Key key;
                try {
                    byte[] pub = service.decode(CodecServiceImpl.IO.IN, params.get("key"));
                    key = parsePublicKey(algorithm, provider, pub);
                } catch (InvalidKeySpecException e) {
                    byte[] priv = service.decode(CodecServiceImpl.IO.IN, params.get("key"));
                    key = parsePrivateKey(algorithm, provider, priv);
                }
                instance.init(javax.crypto.Cipher.DECRYPT_MODE, key);
            }
            instance.update(new ByteArrValidate("input", inputBytes).isNotEmpty().get());
            Map<String, Object> rs = new HashMap<>(2);
            rs.put("output", instance.doFinal());
            return rs;
        }
    },
    KeyStore,
    CertStore,
    SaslClientFactory,
    SaslServerFactory,
    AlgorithmParameterGenerator {
        @Override
        public Map<String, Object> executeInternal(String algorithm, Provider provider, byte[] inputBytes, Map<String, String> params) throws GeneralSecurityException, IOException {
            java.security.AlgorithmParameterGenerator instance = java.security.AlgorithmParameterGenerator.getInstance(algorithm, provider);
            Map<String, Object> rs = new HashMap<>(2);
            rs.put("output", instance.generateParameters());
            return rs;
        }
    },
    AlgorithmParameters {
        @Override
        public List<Parameter> params(String algorithm) {
            return Arrays.asList(
                    new Parameter("p", "please enter a hexadecimal number", () -> algorithm.equals("DSA")),
                    new Parameter("q", "please enter a hexadecimal number", () -> algorithm.equals("DSA")),
                    new Parameter("g", "please enter a hexadecimal number", () -> algorithm.equals("DSA")));
        }
        @Override
        public Map<String, Object> executeInternal(String algorithm, Provider provider, byte[] inputBytes, Map<String, String> params)
                throws GeneralSecurityException, IOException {
            java.security.AlgorithmParameters instance = java.security.AlgorithmParameters.getInstance(algorithm, provider);
            if (inputBytes.length != 0) {
                instance.init(inputBytes);
                Map<String, Object> rs = new HashMap<>(2);
                rs.put("output", instance);
                return rs;
            }
            if ("DSA".equals(algorithm)) {
                BigInteger p = new BigInteger(params.get("p"), 16);
                BigInteger q = new BigInteger(params.get("q"), 16);
                BigInteger r = new BigInteger(params.get("g"), 16);
                instance.init(new DSAParameterSpec(p, q, r));
            } else {
                throw new UnsupportedOperationException("unsupported algorithm");
            }
            Map<String, Object> rs = new HashMap<>(2);
            rs.put("output", instance.getEncoded());
            return rs;
        }
    },
    CertPathBuilder,
    CertPathValidator,
    CertificateFactory,
    Configuration,
    Policy,
    KeyAgreement,
    KeyManagerFactory,
    SSLContext,
    TrustManagerFactory,
    GssApiMechanism,
    TerminalFactory,
    KeyInfoFactory
    ;
}
