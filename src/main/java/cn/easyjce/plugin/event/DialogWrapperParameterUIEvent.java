package cn.easyjce.plugin.event;

import cn.easyjce.plugin.beans.Parameter;

/**
 * @author cuijiufeng
 * @Class DialogWrapperParameterUIEvent
 * @Date 2022/8/26 10:53
 */
public class DialogWrapperParameterUIEvent extends AbstractEvent {
    public DialogWrapperParameterUIEvent(Parameter source) {
        super(source);
    }

    @Override
    public Parameter getSource() {
        return  (Parameter) super.getSource();
    }
}
