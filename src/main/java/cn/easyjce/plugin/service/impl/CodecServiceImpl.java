package cn.easyjce.plugin.service.impl;

import cn.easyjce.plugin.configuration.JcePluginState;
import cn.easyjce.plugin.exception.ParameterIllegalException;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import java.nio.charset.StandardCharsets;

/**
 * @Class: CodecServiceImpl
 * @Date: 2022/7/29 16:14
 * @author: cuijiufeng
 */
public class CodecServiceImpl {

    public String encode(IO io, byte[] data) {
        switch (io) {
            case IN:
                switch (JcePluginState.getInstance().getInputRb()) {
                    case string:
                        return CodecEnum.STRING.encode(data);
                    case base64:
                        return CodecEnum.BASE64.encode(data);
                    case hex:
                    default:
                        return CodecEnum.HEX.encode(data);
                }
            case OUT:
            default:
                switch (JcePluginState.getInstance().getOutputRb()) {
                    case string:
                        return CodecEnum.STRING.encode(data);
                    case base64:
                        return CodecEnum.BASE64.encode(data);
                    case hex:
                    default:
                        return CodecEnum.HEX.encode(data);
                }
        }
    }

    public byte[] decode(IO io, String data) {
        switch (io) {
            case IN:
                switch (JcePluginState.getInstance().getInputRb()) {
                    case string:
                        return CodecEnum.STRING.decode(data);
                    case base64:
                        return CodecEnum.BASE64.decode(data);
                    case hex:
                    default:
                        return CodecEnum.HEX.decode(data);
                }
            case OUT:
            default:
                switch (JcePluginState.getInstance().getOutputRb()) {
                    case string:
                        return CodecEnum.STRING.decode(data);
                    case base64:
                        return CodecEnum.BASE64.decode(data);
                    case hex:
                    default:
                        return CodecEnum.HEX.decode(data);
                }
        }
    }

    public enum IO {
        IN, OUT
    }

    public enum CodecEnum {
        STRING {
            @Override
            public String encode(byte[] data) {
                return new String(data, StandardCharsets.UTF_8);
            }
            @Override
            public byte[] decode(String data){
                return data.getBytes(StandardCharsets.UTF_8);
            }
        },
        HEX {
            @Override
            public String encode(byte[] data) {
                return Hex.encodeHexString(data);
            }
            @Override
            public byte[] decode(String data){
                try {
                    return Hex.decodeHex(data);
                } catch (DecoderException e) {
                    throw new ParameterIllegalException("Illegal input data");
                }
            }
        },
        BASE64 {
            @Override
            public String encode(byte[] data) {
                return Base64.encodeBase64String(data);
            }
            @Override
            public byte[] decode(String data) {
                return Base64.decodeBase64(data);
            }
        }
        ;
        public abstract String encode(byte[] data);
        public abstract byte[] decode(String data);
    }
}
