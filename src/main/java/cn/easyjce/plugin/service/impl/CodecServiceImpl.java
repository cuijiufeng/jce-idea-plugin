package cn.easyjce.plugin.service.impl;

import cn.easyjce.plugin.configuration.JcePluginState;
import cn.easyjce.plugin.exception.ParameterIllegalException;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

/**
 * @Class: CodecServiceImpl
 * @Date: 2022/7/29 16:14
 * @author: cuijiufeng
 */
public class CodecServiceImpl {

    public CodecServiceImpl() {
        //TODO 2022/7/29 16:20 从配置中获取输入输出使用的编码
        JcePluginState.getInstance();
    }

    public String encode(IO io, byte[] data) {
        return CodecEnum.HEX.encode(data);
    }

    public byte[] decode(IO io, String data) {
        try {
            return CodecEnum.HEX.decode(data);
        } catch (DecoderException e) {
            throw new ParameterIllegalException("Illegal input data");
        }
    }

    public enum IO {
        IN, OUT
    }

    public enum CodecEnum {
        HEX {
            @Override
            public String encode(byte[] data) {
                return Hex.encodeHexString(data);
            }
            @Override
            public byte[] decode(String data) throws DecoderException {
                return Hex.decodeHex(data);
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
        public abstract byte[] decode(String data) throws DecoderException;
    }
}
