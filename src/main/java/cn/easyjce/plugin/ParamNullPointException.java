package cn.easyjce.plugin;

/**
 * @Class: ParamNullPointException
 * @Date: 2022/7/27 18:06
 * @author: cuijiufeng
 */
public class ParamNullPointException extends NullPointerException {
    private final String name;

    public ParamNullPointException(String msg, String paramName) {
        super(msg);
        this.name = paramName;
    }

    public String getName() {
        return name;
    }
}
