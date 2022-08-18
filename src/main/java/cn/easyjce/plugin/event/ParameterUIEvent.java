package cn.easyjce.plugin.event;

import cn.easyjce.plugin.beans.Parameter;

/**
 * @Class: RadioButtonEvent
 * @Date: 2022/7/28 15:58
 * @author: cuijiufeng
 */
public class ParameterUIEvent extends AbstractEvent {
    public ParameterUIEvent(Parameter source) {
        super(source);
    }

    @Override
    public Parameter<?> getSource() {
        return  (Parameter<?>) super.getSource();
    }
}
