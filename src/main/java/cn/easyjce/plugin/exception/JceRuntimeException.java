package cn.easyjce.plugin.exception;

/**
 * @Class: MessagesParamsRuntimeException
 * @Date: 2022/8/17 10:52
 * @author: cuijiufeng
 */
public class JceRuntimeException extends RuntimeException {
    private final Object[] msgParams;

    public JceRuntimeException(String message, Object ... msgParams) {
        super(message);
        this.msgParams = msgParams;
    }

    public Object[] getMsgParams() {
        return msgParams;
    }
}
