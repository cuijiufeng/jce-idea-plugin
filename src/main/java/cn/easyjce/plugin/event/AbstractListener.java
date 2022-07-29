package cn.easyjce.plugin.event;

import java.util.EventListener;

/**
 * @Class: AbstractEventListener
 * @Date: 2022/7/28 16:01
 * @author: cuijiufeng
 */
@FunctionalInterface
public interface AbstractListener<E extends AbstractEvent> extends EventListener {

    void onEvent(E event);
}

