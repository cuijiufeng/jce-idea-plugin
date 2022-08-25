package cn.easyjce.plugin.service;

import cn.easyjce.plugin.beans.JButtonParameter;
import cn.easyjce.plugin.beans.JRadioButtonParameter;
import cn.easyjce.plugin.beans.JTextFieldParameter;
import cn.easyjce.plugin.beans.Parameter;
import cn.easyjce.plugin.exception.OperationIllegalException;
import cn.easyjce.plugin.exception.ParameterIllegalException;
import cn.easyjce.plugin.service.impl.CodecServiceImpl;
import cn.easyjce.plugin.utils.LogUtil;
import cn.easyjce.plugin.validate.StringValidate;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiType;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.spec.DSAParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Class: JceSpec
 * @Date: 2022/7/27 9:22
 * @author: cuijiufeng
 */
@SuppressWarnings("unused")
public enum JceSpec implements IJceSpec {
    DEFAULT,
    SecureRandom {
        @Override
        public List<Parameter> params(String algorithm) {
            return Collections.singletonList(new JTextFieldParameter("length", null, () -> true));
        }
        @Override
        public void validateParams(String algorithm, String input, Map<String, String> params) {
            new StringValidate("length", params.get("length")).isNotBlank().parseInt();
        }
        @Override
        public void generateJavaCode(PsiElementFactory factory, PsiElement cursorElement, String provider, String algorithm, String input, Map<String, String> params) {
            PsiElement parentElement = cursorElement.getParent();

            PsiElement srVariable = factory.createVariableDeclarationStatement("sr",
                    factory.createTypeFromText("java.security.SecureRandom", null),
                    factory.createExpressionFromText("SecureRandom.getInstance(\"" + algorithm + "\", \"" + provider + "\")", null));
            PsiElement outputVariable = factory.createVariableDeclarationStatement("output",
                    PsiType.BYTE.createArrayType(),
                    factory.createExpressionFromText("new byte[" + params.get("length") + "]", null));
            PsiElement seedVariable = factory.createVariableDeclarationStatement("seed",
                    PsiType.BYTE.createArrayType(),
                    factory.createExpressionFromText("sr.generateSeed(" + params.get("length") + ")", null));

            PsiElement setSeedExp = factory.createStatementFromText("sr.setSeed(seed);", null);
            PsiElement nextBytesExp = factory.createStatementFromText("sr.nextBytes(output);", null);

            srVariable = parentElement.addAfter(srVariable, cursorElement);
            outputVariable = parentElement.addAfter(outputVariable, srVariable);
            seedVariable = parentElement.addAfter(seedVariable, outputVariable);
            setSeedExp = parentElement.addAfter(setSeedExp, seedVariable);
            parentElement.addAfter(nextBytesExp, setSeedExp);
        }
        @Override
        public Map<String, Object> executeInternal(String algorithm, Provider provider, byte[] inputBytes, Map<String, String> params)
                throws GeneralSecurityException {
            java.security.SecureRandom instance = java.security.SecureRandom.getInstance(algorithm, provider);
            int length = Integer.parseInt(params.get("length"));
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
        public List<Parameter> params(String algorithm) {
            return Collections.singletonList(new JTextFieldParameter("salt", null, () -> true));
        }
        @Override
        public void generateJavaCode(PsiElementFactory factory, PsiElement cursorElement, String provider, String algorithm, String input, Map<String, String> params) {
            PsiElement parentElement = cursorElement.getParent();
            boolean existSalt = StringUtils.isNotBlank(params.get("salt"));

            PsiElement mdVariable = factory.createVariableDeclarationStatement("md",
                    factory.createTypeFromText("java.security.MessageDigest", null),
                    factory.createExpressionFromText("MessageDigest.getInstance(\"" + algorithm + "\", \"" + provider + "\")", null));

            PsiElement mdinit = factory.createStatementFromText("md.update(org.apache.commons.codec.binary.Hex.decodeHex(\"" + params.get("salt") + "\"));", null);
            PsiElement digestVariable = factory.createVariableDeclarationStatement("digest",
                    PsiType.BYTE.createArrayType(),
                    factory.createExpressionFromText("md.digest(org.apache.commons.codec.binary.Hex.decodeHex(\"" + input + "\"))", null));

            if (existSalt) {
                //新增代码
                mdVariable = parentElement.addAfter(mdVariable, cursorElement);
                mdinit = parentElement.addAfter(mdinit, mdVariable);
                parentElement.addAfter(digestVariable, mdinit);
            } else {
                //新增代码
                mdVariable = parentElement.addAfter(mdVariable, cursorElement);
                parentElement.addAfter(digestVariable, mdVariable);
            }
        }
        @Override
        public Map<String, Object> executeInternal(String algorithm, Provider provider, byte[] input, Map<String, String> params)
                throws GeneralSecurityException {
            CodecServiceImpl service = ServiceManager.getService(CodecServiceImpl.class);
            java.security.MessageDigest instance = java.security.MessageDigest.getInstance(algorithm, provider);
            if (StringUtils.isNotBlank(params.get("salt"))) {
                instance.update(service.decode(CodecServiceImpl.IO.IN, params.get("salt")));
            }
            Map<String, Object> rs = new HashMap<>(2);
            rs.put("output", instance.digest(input));
            return rs;
        }
    },
    Mac {
        @Override
        public List<Parameter> params(String algorithm) {
            return Collections.singletonList(new JTextFieldParameter("key", null, () -> true));
        }
        @Override
        public void validateParams(String algorithm, String input, Map<String, String> params) {
            new StringValidate("key", params.get("key")).isNotBlank();
        }
        @Override
        public void generateJavaCode(PsiElementFactory factory, PsiElement cursorElement, String provider, String algorithm, String input, Map<String, String> params) {
            PsiElement parentElement = cursorElement.getParent();

            PsiElement macVariable = factory.createVariableDeclarationStatement("mac",
                    factory.createTypeFromText("javax.crypto.Mac", null),
                    factory.createExpressionFromText("Mac.getInstance(\"" + algorithm + "\", \"" + provider + "\")", null));

            PsiElement macInitExp = factory.createStatementFromText(
                    "mac.init(new javax.crypto.spec.SecretKeySpec(org.apache.commons.codec.binary.Hex.decodeHex(\"" + params.get("key") + "\"), \"" + algorithm + "\"));", null);
            PsiElement macUpdateExp = factory.createStatementFromText("mac.update(org.apache.commons.codec.binary.Hex.decodeHex(\"" + input + "\"));", null);
            PsiElement bytesVariable = factory.createVariableDeclarationStatement("bytes",
                    PsiType.BYTE.createArrayType(),
                    factory.createExpressionFromText("mac.doFinal()", null));

            macVariable = parentElement.addAfter(macVariable, cursorElement);
            macInitExp = parentElement.addAfter(macInitExp, macVariable);
            macUpdateExp = parentElement.addAfter(macUpdateExp, macInitExp);
            parentElement.addAfter(bytesVariable, macUpdateExp);
        }
        @Override
        public Map<String, Object> executeInternal(String algorithm, Provider provider, byte[] inputBytes, Map<String, String> params) throws GeneralSecurityException {
            CodecServiceImpl service = ServiceManager.getService(CodecServiceImpl.class);
            javax.crypto.Mac instance = javax.crypto.Mac.getInstance(algorithm, provider);
            byte[] key = service.decode(CodecServiceImpl.IO.IN, params.get("key"));
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
            return Collections.singletonList(new JTextFieldParameter("keysize", null, () -> true));
        }
        @Override
        public Map<String, Object> executeInternal(String algorithm, Provider provider, byte[] inputBytes, Map<String, String> params) throws GeneralSecurityException {
            javax.crypto.KeyGenerator instance = javax.crypto.KeyGenerator.getInstance(algorithm, provider);
            SecretKey secretKey = instance.generateKey();
            if (StringUtils.isNotBlank(params.get("keysize"))) {
                instance.init(Integer.parseInt(params.get("keysize")));
            }
            Map<String, Object> rs = new HashMap<>(2);
            rs.put("key", secretKey.getEncoded());
            return rs;
        }
    },
    SecretKeyFactory {
        @Override
        public void validateParams(String algorithm, String input, Map<String, String> params) {
            new StringValidate("key", input).isNotBlank();
        }
        @Override
        public Map<String, Object> executeInternal(String algorithm, Provider provider, byte[] inputBytes, Map<String, String> params) throws GeneralSecurityException {
            SecretKey secretKey = parseSecretKey(algorithm, provider, inputBytes);
            Map<String, Object> rs = new HashMap<>(2);
            rs.put("key", secretKey);
            return rs;
        }
    },
    KeyPairGenerator {
        @Override
        public List<Parameter> params(String algorithm) {
            return Collections.singletonList(new JTextFieldParameter("keysize", null, () -> true));
        }
        @Override
        public Map<String, Object> executeInternal(String algorithm, Provider provider, byte[] inputBytes, Map<String, String> params)
                throws GeneralSecurityException {
            java.security.KeyPairGenerator instance = java.security.KeyPairGenerator.getInstance(algorithm, provider);
            if (StringUtils.isNotBlank(params.get("keysize"))) {
                instance.initialize(Integer.parseInt(params.get("keysize")));
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
            return Arrays.asList(new JTextFieldParameter("private", null, () -> true), new JTextFieldParameter("public", null, () -> true));
        }
        @Override
        public void validateParams(String algorithm, String input, Map<String, String> params) {
            new StringValidate("private", params.get("private")).isNotBlank();
            new StringValidate("public", params.get("public")).isNotBlank();
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
            Parameter parameter = new JRadioButtonParameter("type", Arrays.asList("sign", "verify"), 2);
            return Arrays.asList(parameter,
                    new JTextFieldParameter("private", null, () -> parameter.getValue().equals("sign")),
                    new JTextFieldParameter("cert", null, () -> parameter.getValue().equals("verify")),
                    new JTextFieldParameter("public", null, () -> parameter.getValue().equals("verify")),
                    new JTextFieldParameter("plain", null, () -> parameter.getValue().equals("verify")));
        }
        @Override
        public void validateParams(String algorithm, String input, Map<String, String> params) {
            new StringValidate("input", input).isNotBlank();
            String type = new StringValidate("type", params.get("type")).isNotBlank().in(Arrays.asList("sign", "verify")).get();
            if (params.get("type").equals("sign")) {
                new StringValidate("private", params.get("private")).isNotBlank();
            }
            new StringValidate( "plain", params.get("plain")).isNotBlank();
        }
        @Override
        public Map<String, Object> executeInternal(String algorithm, Provider provider, byte[] inputBytes, Map<String, String> params)
                throws GeneralSecurityException {
            CodecServiceImpl service = ServiceManager.getService(CodecServiceImpl.class);
            java.security.Signature instance = java.security.Signature.getInstance(algorithm, provider);
            Map<String, Object> rs = new HashMap<>(2);
            if (params.get("type").equals("sign")) {
                byte[] priv = service.decode(CodecServiceImpl.IO.IN, params.get("private"));
                instance.initSign(parsePrivateKey(algorithm.split("with")[1], provider, priv));
                instance.update(inputBytes);
                rs.put("output", instance.sign());
            } else if (params.get("type").equals("verify")) {
                if (StringUtils.isNotBlank(params.get("public"))) {
                    byte[] pub = service.decode(CodecServiceImpl.IO.IN, params.get("public"));
                    instance.initVerify(parsePublicKey(algorithm.split("with")[1], provider, pub));
                } else if (StringUtils.isNotBlank(params.get("cert"))) {
                    throw new OperationIllegalException("unsupported cert");
                } else {
                    throw new ParameterIllegalException("{0} parameter is empty", "cert、public");
                }
                instance.update(service.decode(CodecServiceImpl.IO.IN, params.get("plain")));
                rs.put("output", instance.verify(inputBytes));
            }
            return rs;
        }
    },
    Cipher {
        @Override
        public List<Parameter> params(String algorithm) {
            Parameter parameter = new JRadioButtonParameter("type", Arrays.asList("symmetric encryption", "symmetric decryption", "asymmetric encryption", "asymmetric decryption"), 2);
            return Arrays.asList(parameter,
                    new JTextFieldParameter("key", null, () -> true),
                    new JTextFieldParameter("nounce", null, () -> parameter.getValue().equals("symmetric encryption") || parameter.getValue().equals("symmetric decryption")));
        }
        @Override
        public void validateParams(String algorithm, String input, Map<String, String> params) {
            new StringValidate("type", params.get("type")).isNotBlank()
                    .in(Arrays.asList("symmetric encryption", "symmetric decryption", "asymmetric encryption", "asymmetric decryption"));
            new StringValidate("key", params.get("key")).isNotBlank();
            new StringValidate("input", input).isNotBlank();
        }
        @Override
        public Map<String, Object> executeInternal(String algorithm, Provider provider, byte[] inputBytes, Map<String, String> params) throws GeneralSecurityException {
            CodecServiceImpl service = ServiceManager.getService(CodecServiceImpl.class);
            javax.crypto.Cipher instance = javax.crypto.Cipher.getInstance(algorithm, provider);
            byte[] keyBytes = service.decode(CodecServiceImpl.IO.IN, params.get("key"));
            if ("symmetric encryption".equals(params.get("type"))) {
                SecretKey secretKey = new SecretKeySpec(keyBytes, algorithm.split("/")[0].split("_")[0]);
                if (StringUtils.isNotBlank(params.get("nounce"))) {
                    instance.init(javax.crypto.Cipher.ENCRYPT_MODE, secretKey,
                            new IvParameterSpec(service.decode(CodecServiceImpl.IO.IN, params.get("nounce"))));
                } else {
                    instance.init(javax.crypto.Cipher.ENCRYPT_MODE, secretKey);
                }
            } else if ("symmetric decryption".equals(params.get("type"))) {
                SecretKey secretKey = new SecretKeySpec(keyBytes, algorithm.split("/")[0].split("_")[0]);
                if (StringUtils.isNotBlank(params.get("nounce"))) {
                    instance.init(javax.crypto.Cipher.DECRYPT_MODE, secretKey,
                            new IvParameterSpec(service.decode(CodecServiceImpl.IO.IN, params.get("nounce"))));
                } else {
                    instance.init(javax.crypto.Cipher.DECRYPT_MODE, secretKey);
                }
            } else if ("asymmetric encryption".equals(params.get("type"))) {
                Key key;
                try {
                    key = parsePublicKey(algorithm, provider, keyBytes);
                } catch (InvalidKeySpecException e) {
                    key = parsePrivateKey(algorithm, provider, keyBytes);
                }
                instance.init(javax.crypto.Cipher.ENCRYPT_MODE, key);
            } else if ("asymmetric decryption".equals(params.get("type"))) {
                Key key;
                try {
                    key = parsePublicKey(algorithm, provider, keyBytes);
                } catch (InvalidKeySpecException e) {
                    key = parsePrivateKey(algorithm, provider, keyBytes);
                }
                instance.init(javax.crypto.Cipher.DECRYPT_MODE, key);
            }
            instance.update(inputBytes);
            Map<String, Object> rs = new HashMap<>(2);
            rs.put("output", instance.doFinal());
            return rs;
        }
    },
    KeyStore {
        @Override
        public List<Parameter> params(String algorithm) {
            return Arrays.asList(
                    new JTextFieldParameter("password", null, () -> true),
                    new JButtonParameter("jks", "choose file"));
        }
        @Override
        public void validateParams(String algorithm, String input, Map<String, String> params) {
            new StringValidate("jks", params.get("jks")).isNotBlank();
            new StringValidate("password", params.get("password")).isNotBlank();
        }
        @Override
        public Map<String, Object> executeInternal(String algorithm, Provider provider, byte[] inputBytes, Map<String, String> params)
                throws GeneralSecurityException, IOException {
            java.security.KeyStore instance = loadKeyStore(algorithm, provider, new FileInputStream(params.get("jks")), params.get("password"));
            Map<String, Object> rs = new HashMap<>(2);
            Enumeration<String> aliases = instance.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                try {
                    rs.put(alias, instance.getEntry(alias, null));
                } catch (UnrecoverableEntryException e) {
                    rs.put(alias, instance.getEntry(alias, new KeyStore.PasswordProtection(params.get("password").toCharArray())));
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
        public Map<String, Object> executeInternal(String algorithm, Provider provider, byte[] inputBytes, Map<String, String> params) throws GeneralSecurityException {
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
                    new JTextFieldParameter("p", "please enter a hexadecimal number", () -> algorithm.equals("DSA")),
                    new JTextFieldParameter("q", "please enter a hexadecimal number", () -> algorithm.equals("DSA")),
                    new JTextFieldParameter("g", "please enter a hexadecimal number", () -> algorithm.equals("DSA")));
        }
        @Override
        public void validateParams(String algorithm, String input, Map<String, String> params) {
            if ("DSA".equals(algorithm)) {
                new StringValidate("p", params.get("p")).isNotBlank();
                new StringValidate("q", params.get("q")).isNotBlank();
                new StringValidate("g", params.get("g")).isNotBlank();
            }
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
                throw new OperationIllegalException("unsupported algorithm");
            }
            Map<String, Object> rs = new HashMap<>(2);
            rs.put("output", instance.getEncoded());
            return rs;
        }
    },
    CertPathBuilder,
    CertPathValidator,
    CertificateFactory {
        @Override
        public List<Parameter> params(String algorithm) {
            return Collections.singletonList(new JButtonParameter("cert", "choose file"));
        }
        @Override
        public void validateParams(String algorithm, String input, Map<String, String> params) {
            new StringValidate("cert", params.get("cert")).isNotBlank();
        }
        @Override
        public Map<String, Object> executeInternal(String algorithm, Provider provider, byte[] inputBytes, Map<String, String> params)
                throws GeneralSecurityException, IOException {
            java.security.cert.CertificateFactory instance = java.security.cert.CertificateFactory.getInstance(algorithm, provider);
            Map<String, Object> rs = new HashMap<>(2);
            rs.put("output", instance.generateCertificate(new FileInputStream(params.get("cert"))));
            return rs;
        }
    },
    Configuration,
    Policy,
    KeyAgreement,
    KeyManagerFactory,
    SSLContext,
    TrustManagerFactory,
    GssApiMechanism,
    TerminalFactory,
    KeyInfoFactory,
    TransformService,
    XMLSignatureFactory,
    ;

    protected SecretKey parseSecretKey(String algorithm, Provider provider, byte[] bytes) throws GeneralSecurityException {
        javax.crypto.SecretKeyFactory keyFactory = javax.crypto.SecretKeyFactory.getInstance(algorithm, provider);
        return keyFactory.generateSecret(new SecretKeySpec(bytes, algorithm));
    }

    protected PrivateKey parsePrivateKey(String algorithm, Provider provider, byte[] bytes) throws GeneralSecurityException {
        java.security.KeyFactory instance;
        try {
            instance = java.security.KeyFactory.getInstance(algorithm, provider);
        } catch (NoSuchAlgorithmException e) {
            instance = java.security.KeyFactory.getInstance(algorithm);
            LogUtil.LOG.info(e.getMessage() + "\nfind it in" + instance.getProvider().getName());
        }
        return instance.generatePrivate(new PKCS8EncodedKeySpec(bytes));
    }

    protected PublicKey parsePublicKey(String algorithm, Provider provider, byte[] bytes) throws GeneralSecurityException {
        java.security.KeyFactory instance;
        try {
            instance = java.security.KeyFactory.getInstance(algorithm, provider);
        } catch (NoSuchAlgorithmException e) {
            instance = java.security.KeyFactory.getInstance(algorithm);
            LogUtil.LOG.info(e.getMessage() + "\nfind it in" + instance.getProvider().getName());
        }
        return instance.generatePublic(new X509EncodedKeySpec(bytes));
    }

    protected KeyStore loadKeyStore(String algorithm, Provider provider, InputStream is, String password) throws GeneralSecurityException, IOException {
        KeyStore instance;
        try {
            instance = java.security.KeyStore.getInstance(algorithm, provider);
        } catch (KeyStoreException e) {
            instance = java.security.KeyStore.getInstance(algorithm);
            LogUtil.LOG.info(e.getMessage() + "\nfind it in" + instance.getProvider().getName());
        }
        instance.load(is, password.toCharArray());
        return instance;
    }

    public static JceSpec specValueOf(String name, RuntimeException valueOfEx) {
        try {
            return valueOf(name);
        } catch (IllegalArgumentException e) {
            if (Objects.nonNull(valueOfEx)) {
                throw valueOfEx;
            }
            return DEFAULT;
        }
    }
}
