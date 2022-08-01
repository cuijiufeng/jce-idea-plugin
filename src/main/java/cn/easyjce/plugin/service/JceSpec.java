package cn.easyjce.plugin.service;

import cn.easyjce.plugin.beans.Parameter;
import cn.easyjce.plugin.exception.ParameterIllegalException;
import cn.easyjce.plugin.service.impl.CodecServiceImpl;
import cn.easyjce.plugin.validate.ByteArrValidate;
import cn.easyjce.plugin.validate.StringValidate;
import com.intellij.openapi.components.ServiceManager;
import org.apache.commons.lang3.StringUtils;

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
            return Collections.singletonList(new Parameter("keysize", Parameter.DisplayUI.SHOW));
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
            return Collections.singletonList(new Parameter("length", Parameter.DisplayUI.SHOW));
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
            return Arrays.asList(
                    new Parameter("type", Arrays.asList("sign", "verify")),
                    new Parameter("private", Parameter.DisplayUI.SHOW),
                    new Parameter("cert", Parameter.DisplayUI.HIDE),
                    new Parameter("public", Parameter.DisplayUI.HIDE),
                    new Parameter("plain", Parameter.DisplayUI.HIDE));
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
                    throw new UnsupportedOperationException("unsupported operation type");
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

    public Map<String, Object> executeInternal(String algorithm, Provider provider, byte[] inputBytes, Map<String, String> params)
            throws GeneralSecurityException {
        throw new UnsupportedOperationException("unsupported operation");
    }

    public List<Parameter> params() {
        return Collections.emptyList();
    }
}
