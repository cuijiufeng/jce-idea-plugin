package cn.easyjce.plugin.exception;

/**
 * @author cuijiufeng
 * @date 2022/8/13 13:23
 * @desc
 */
public class OperationIllegalException extends JceRuntimeException {
    public OperationIllegalException(String message, Object ... params) {
        super(message, params);
    }
}
