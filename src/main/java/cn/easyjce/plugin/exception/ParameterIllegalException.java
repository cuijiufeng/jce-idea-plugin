package cn.easyjce.plugin.exception;

/**
 * @Class: ParamNullPointException
 * @Date: 2022/7/27 18:06
 * @author: cuijiufeng
 */
public class ParameterIllegalException extends IllegalArgumentException {
    private final Object[] params;

    public ParameterIllegalException(String msg, String... params) {
        super(msg);
        this.params = params;
    }

    public Object[] getParams() {
        return params;
    }
}
