package cn.easyjce.plugin.validate;

import cn.easyjce.plugin.exception.ParameterIllegalException;

/**
 * @Class: IntValidate
 * @Date: 2022/8/1 15:22
 * @author: cuijiufeng
 */
public class IntValidate extends AbstractValidate<Integer>{

    public IntValidate(String name, Object param) {
        super(name, (Integer) param);
    }

    public IntValidate gt(int max) {
        if (param <= max) {
            throw new ParameterIllegalException("illegal {0} parameter", name);
        }
        return this;
    }

    public IntValidate lt(int min) {
        if (param >= min) {
            throw new ParameterIllegalException("illegal {0} parameter", name);
        }
        return this;
    }

    public IntValidate between(int max, int min) {
        if (param <= max || param >= min) {
            throw new ParameterIllegalException("illegal {0} parameter", name);
        }
        return this;
    }
}
