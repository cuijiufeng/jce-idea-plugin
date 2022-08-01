package cn.easyjce.plugin.validate;

/**
 * @Class: AbstractValidate
 * @Date: 2022/8/1 15:24
 * @author: cuijiufeng
 */
public abstract class AbstractValidate<T> {
    protected final String name;
    protected final T param;

    public AbstractValidate(String name, T param) {
        this.name = name;
        this.param = param;
    }

    public T get() {
        return param;
    }
}
