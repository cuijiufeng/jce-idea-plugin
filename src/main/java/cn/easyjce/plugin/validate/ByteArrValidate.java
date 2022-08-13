package cn.easyjce.plugin.validate;

import cn.easyjce.plugin.exception.ParameterIllegalException;

/**
 * @Class: ByteArrValidate
 * @Date: 2022/8/1 15:23
 * @author: cuijiufeng
 */
public class ByteArrValidate extends AbstractValidate<byte[]> {

    public ByteArrValidate(String name, Object param) {
        super(name, (byte[]) param);
    }

    public ByteArrValidate isNotEmpty() {
        if (param == null || param.length == 0) {
            throw new ParameterIllegalException("{0} parameter is empty", name);
        }
        return this;
    }
}
