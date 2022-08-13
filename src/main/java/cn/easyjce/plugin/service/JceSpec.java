package cn.easyjce.plugin.service;

import cn.easyjce.plugin.beans.JButtonParameter;
import cn.easyjce.plugin.beans.JRadioButtonParameter;
import cn.easyjce.plugin.beans.JTextFieldParameter;
import cn.easyjce.plugin.beans.Parameter;
import cn.easyjce.plugin.exception.JceUnsupportedOperationException;
import cn.easyjce.plugin.exception.ParameterIllegalException;
import cn.easyjce.plugin.service.impl.CodecServiceImpl;
import cn.easyjce.plugin.validate.ByteArrValidate;
import cn.easyjce.plugin.validate.StringValidate;
import com.intellij.openapi.components.ServiceManager;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.DSAParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

/**
 * @Class: JceSpec
 * @Date: 2022/7/27 9:22
 * @author: cuijiufeng
 */
public enum JceSpec implements IJceSpec {
    SecureRandom {
        @Override
        public List<Parameter<?>> params(String algorithm) {
            return Collections.singletonList(new JTextFieldParameter("length", null, () -> true));
        }
        @Override
        public Map<String, Object> executeInternal(String algorithm, Provider provider, byte[] inputBytes, Map<String, ?> params)
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
        public Map<String, Object> executeInternal(String algorithm, Provider provider, byte[] input, Map<String, ?> params)
                throws GeneralSecurityException {
            java.security.MessageDigest instance = java.security.MessageDigest.getInstance(algorithm, provider);
            Map<String, Object> rs = new HashMap<>(2);
            rs.put("output", instance.digest(input));
            return rs;
        }
    },
    Mac {
        @Override
        public List<Parameter<?>> params(String algorithm) {
            return Collections.singletonList(new JTextFieldParameter("key", null, () -> true));
        }
        @Override
        public Map<String, Object> executeInternal(String algorithm, Provider provider, byte[] inputBytes, Map<String, ?> params) throws GeneralSecurityException {
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
        public List<Parameter<?>> params(String algorithm) {
            return Collections.singletonList(new JTextFieldParameter("keysize", null, () -> true));
        }
        @Override
        public Map<String, Object> executeInternal(String algorithm, Provider provider, byte[] inputBytes, Map<String, ?> params) throws GeneralSecurityException {
            javax.crypto.KeyGenerator instance = javax.crypto.KeyGenerator.getInstance(algorithm, provider);
            SecretKey secretKey = instance.generateKey();
            StringValidate stringValidate = new StringValidate("keysize", params.get("keysize"));
            if (stringValidate.isNotBlankNoEx()) {
                Integer keysize = stringValidate.parseInt().get();
                instance.init(keysize);
            }
            Map<String, Object> rs = new HashMap<>(2);
            rs.put("key", secretKey.getEncoded());
            return rs;
        }
    },
    SecretKeyFactory {
        @Override
        public List<Parameter<?>> params(String algorithm) {
            return super.params(algorithm);
        }
        @Override
        public Map<String, Object> executeInternal(String algorithm, Provider provider, byte[] inputBytes, Map<String, ?> params) throws GeneralSecurityException, IOException {
            SecretKey secretKey = parseSecretKey(algorithm, provider, inputBytes);
            Map<String, Object> rs = new HashMap<>(2);
            rs.put("key", secretKey);
            return rs;
        }
    },
    KeyPairGenerator {
        @Override
        public List<Parameter<?>> params(String algorithm) {
            return Collections.singletonList(new JTextFieldParameter("keysize", null, () -> true));
        }
        @Override
        public Map<String, Object> executeInternal(String algorithm, Provider provider, byte[] inputBytes, Map<String, ?> params)
                throws GeneralSecurityException {
            java.security.KeyPairGenerator instance = java.security.KeyPairGenerator.getInstance(algorithm, provider);
            StringValidate stringValidate = new StringValidate("keysize", params.get("keysize"));
            if (stringValidate.isNotBlankNoEx()) {
                Integer keysize = stringValidate.parseInt().get();
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
        public List<Parameter<?>> params(String algorithm) {
            return Arrays.asList(new JTextFieldParameter("private", null, () -> true), new JTextFieldParameter("public", null, () -> true));
        }
        @Override
        public Map<String, Object> executeInternal(String algorithm, Provider provider, byte[] inputBytes, Map<String, ?> params)
                throws GeneralSecurityException {
            CodecServiceImpl service = ServiceManager.getService(CodecServiceImpl.class);
            String privateParam = new StringValidate("private", params.get("private")).isNotBlank().get();
            byte[] priv = service.decode(CodecServiceImpl.IO.IN, privateParam);
            PrivateKey privateKey = parsePrivateKey(algorithm, provider, priv);
            String publicParam = new StringValidate("public", params.get("public")).isNotBlank().get();
            byte[] pub = service.decode(CodecServiceImpl.IO.IN, publicParam);
            PublicKey publicKey = parsePublicKey(algorithm, provider, pub);
            Map<String, Object> rs = new HashMap<>(2);
            rs.put("private", privateKey);
            rs.put("public", publicKey);
            return rs;
        }
    },
    Signature {
        @Override
        public List<Parameter<?>> params(String algorithm) {
            Parameter<String> parameter = new JRadioButtonParameter("type", Arrays.asList("sign", "verify"), 2);
            return Arrays.asList(parameter,
                    new JTextFieldParameter("private", null, () -> parameter.getValue().equals("sign")),
                    new JTextFieldParameter("cert", null, () -> parameter.getValue().equals("verify")),
                    new JTextFieldParameter("public", null, () -> parameter.getValue().equals("verify")),
                    new JTextFieldParameter("plain", null, () -> parameter.getValue().equals("verify")));
        }
        @Override
        public Map<String, Object> executeInternal(String algorithm, Provider provider, byte[] inputBytes, Map<String, ?> params)
                throws GeneralSecurityException {
            CodecServiceImpl service = ServiceManager.getService(CodecServiceImpl.class);
            java.security.Signature instance = java.security.Signature.getInstance(algorithm, provider);
            String type = new StringValidate("type", params.get("type"))
                    .isNotBlank()
                    .in(Arrays.asList("sign", "verify"))
                    .get();
            Map<String, Object> rs = new HashMap<>(2);
            if (type.equals("sign")) {
                String privateParam = new StringValidate("private", params.get("private")).isNotBlank().get();
                byte[] priv = service.decode(CodecServiceImpl.IO.IN, privateParam);
                instance.initSign(parsePrivateKey(algorithm.split("with")[1], provider, priv));
                instance.update(new ByteArrValidate("input", inputBytes).isNotEmpty().get());
                rs.put("output", instance.sign());
            } else if (type.equals("verify")) {
                StringValidate stringValidate = new StringValidate("public", params.get("public"));
                if (stringValidate.isNotBlankNoEx()) {
                    byte[] pub = service.decode(CodecServiceImpl.IO.IN, stringValidate.get());
                    instance.initVerify(parsePublicKey(algorithm.split("with")[1], provider, pub));
                } else if (new StringValidate("cert", params.get("cert")).isNotBlankNoEx()) {
                    throw new JceUnsupportedOperationException("unsupported cert");
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
        public List<Parameter<?>> params(String algorithm) {
            Parameter<String> parameter = new JRadioButtonParameter("type", Arrays.asList("symmetric encryption", "symmetric decryption", "asymmetric encryption", "asymmetric decryption"), 2);
            return Arrays.asList(parameter, new JTextFieldParameter("key", null, () -> true));
        }
        @Override
        public Map<String, Object> executeInternal(String algorithm, Provider provider, byte[] inputBytes, Map<String, ?> params) throws GeneralSecurityException {
            CodecServiceImpl service = ServiceManager.getService(CodecServiceImpl.class);
            javax.crypto.Cipher instance = javax.crypto.Cipher.getInstance(algorithm, provider);
            String type = new StringValidate("type", params.get("type"))
                    .isNotBlank()
                    .in(Arrays.asList("symmetric encryption", "symmetric decryption", "asymmetric encryption", "asymmetric decryption"))
                    .get();
            //TODO 2022/8/8 17:22 对称加密可以使用向量iv
            String keyParam = new StringValidate("key", params.get("key")).isNotBlank().get();
            byte[] keyBytes = service.decode(CodecServiceImpl.IO.IN, keyParam);
            if ("symmetric encryption".equals(type)) {
                SecretKey secretKey = new SecretKeySpec(keyBytes, algorithm.split("/")[0].split("_")[0]);
                instance.init(javax.crypto.Cipher.ENCRYPT_MODE, secretKey);
            } else if ("symmetric decryption".equals(type)) {
                SecretKey secretKey = new SecretKeySpec(keyBytes, algorithm.split("/")[0].split("_")[0]);
                instance.init(javax.crypto.Cipher.DECRYPT_MODE, secretKey);
            } else if ("asymmetric encryption".equals(type)) {
                Key key;
                try {
                    key = parsePublicKey(algorithm, provider, keyBytes);
                } catch (InvalidKeySpecException e) {
                    key = parsePrivateKey(algorithm, provider, keyBytes);
                }
                instance.init(javax.crypto.Cipher.ENCRYPT_MODE, key);
            } else if ("asymmetric decryption".equals(type)) {
                Key key;
                try {
                    key = parsePublicKey(algorithm, provider, keyBytes);
                } catch (InvalidKeySpecException e) {
                    key = parsePrivateKey(algorithm, provider, keyBytes);
                }
                instance.init(javax.crypto.Cipher.DECRYPT_MODE, key);
            }
            instance.update(new ByteArrValidate("input", inputBytes).isNotEmpty().get());
            Map<String, Object> rs = new HashMap<>(2);
            rs.put("output", instance.doFinal());
            return rs;
        }
    },
    KeyStore {
        @Override
        public List<Parameter<?>> params(String algorithm) {
            return Arrays.asList(
                    new JTextFieldParameter("password", null, () -> true),
                    new JButtonParameter("jks", "choose file"));
        }
        @Override
        public Map<String, Object> executeInternal(String algorithm, Provider provider, byte[] inputBytes, Map<String, ?> params)
                throws GeneralSecurityException, IOException {
            java.security.KeyStore instance = java.security.KeyStore.getInstance(algorithm, provider);
            String jksParam = new StringValidate("jks", params.get("jks")).isNotBlank().get();
            String passParam = new StringValidate("password", params.get("password")).isNotBlank().get();
            instance.load(new FileInputStream(jksParam), passParam.toCharArray());
            Map<String, Object> rs = new HashMap<>(2);
            Enumeration<String> aliases = instance.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                try {
                    rs.put(alias, instance.getEntry(alias, null));
                } catch (UnrecoverableEntryException e) {
                    rs.put(alias, instance.getEntry(alias, new KeyStore.PasswordProtection(passParam.toCharArray())));
                }
            }
            return rs;
        }
    },
    CertStore,
    SaslClientFactory,
    SaslServerFactory,
    AlgorithmParameterGenerator {
        @Override
        public Map<String, Object> executeInternal(String algorithm, Provider provider, byte[] inputBytes, Map<String, ?> params) throws GeneralSecurityException, IOException {
            java.security.AlgorithmParameterGenerator instance = java.security.AlgorithmParameterGenerator.getInstance(algorithm, provider);
            Map<String, Object> rs = new HashMap<>(2);
            rs.put("output", instance.generateParameters());
            return rs;
        }
    },
    AlgorithmParameters {
        @Override
        public List<Parameter<?>> params(String algorithm) {
            return Arrays.asList(
                    new JTextFieldParameter("p", "please enter a hexadecimal number", () -> algorithm.equals("DSA")),
                    new JTextFieldParameter("q", "please enter a hexadecimal number", () -> algorithm.equals("DSA")),
                    new JTextFieldParameter("g", "please enter a hexadecimal number", () -> algorithm.equals("DSA")));
        }
        @Override
        public Map<String, Object> executeInternal(String algorithm, Provider provider, byte[] inputBytes, Map<String, ?> params)
                throws GeneralSecurityException, IOException {
            java.security.AlgorithmParameters instance = java.security.AlgorithmParameters.getInstance(algorithm, provider);
            if (inputBytes.length != 0) {
                instance.init(inputBytes);
                Map<String, Object> rs = new HashMap<>(2);
                rs.put("output", instance);
                return rs;
            }
            if ("DSA".equals(algorithm)) {
                BigInteger p = new BigInteger(new StringValidate("p", params.get("p")).isNotBlank().get(), 16);
                BigInteger q = new BigInteger(new StringValidate("q", params.get("q")).isNotBlank().get(), 16);
                BigInteger r = new BigInteger(new StringValidate("g", params.get("g")).isNotBlank().get(), 16);
                instance.init(new DSAParameterSpec(p, q, r));
            } else {
                throw new JceUnsupportedOperationException("unsupported algorithm");
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
