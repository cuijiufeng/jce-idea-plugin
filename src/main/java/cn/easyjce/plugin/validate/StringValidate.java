package cn.easyjce.plugin.validate;

import cn.easyjce.plugin.exception.ParameterIllegalException;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @Class: ParamValidateServiceImpl
 * @Date: 2022/7/29 17:26
 * @author: cuijiufeng
 */
public class StringValidate extends AbstractValidate<String>{

    public StringValidate(String name, String param) {
        super(name, param);
    }

    public StringValidate isNotBlank() {
        if (StringUtils.isBlank(param)) {
            throw new ParameterIllegalException("{0} parameter is empty", name);
        }
        return this;
    }

    public StringValidate in(List<String> strs) {
        if (strs.stream().noneMatch(str -> str.equals(param))) {
            throw new ParameterIllegalException("illegal {0} parameter", name);
        }
        return this;
    }

    public IntValidate parseInt() {
        try {
            return new IntValidate(name, Integer.parseInt(param));
        } catch (NumberFormatException e) {
            throw new ParameterIllegalException("{0} parameter type error", name);
        }
    }
}
