package cn.easyjce.plugin.event;

import java.util.EventObject;

/**
 * @Class: AbstractEvent
 * @Date: 2022/7/28 16:03
 * @author: cuijiufeng
 */
public abstract class AbstractEvent extends EventObject {

    public AbstractEvent(Object source) {
        super(source);
    }
}

