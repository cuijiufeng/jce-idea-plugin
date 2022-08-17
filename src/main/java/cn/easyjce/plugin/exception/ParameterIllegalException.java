package cn.easyjce.plugin.exception;

/**
 * @Class: ParamNullPointException
 * @Date: 2022/7/27 18:06
 * @author: cuijiufeng
 */
public class ParameterIllegalException extends JceRuntimeException {
    public ParameterIllegalException(String message, Object ... params) {
        super(message, params);
    }
}
