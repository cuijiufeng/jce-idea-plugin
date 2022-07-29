package cn.easyjce.plugin.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @Class: EventMulticaster
 * @Date: 2022/7/28 15:58
 * @author: cuijiufeng
 */
public class EventPublisher {
    private final Map<Class<? extends AbstractEvent>, Collection<AbstractListener<?>>> listeners = new HashMap<>(16);

    public void addEventListener(Class<? extends AbstractEvent> clazz, AbstractListener<?> listener) {
        Collection<AbstractListener<?>> listenerList = listeners.getOrDefault(clazz, new ArrayList<>(2));
        if (listenerList.contains(listener)) {
            return;
        }
        listenerList.add(listener);
        listeners.put(clazz, listenerList);
    }

    public void removeEventListener(Class<? extends AbstractEvent> clazz, AbstractListener<?> listener) {
        Collection<AbstractListener<?>> listenerList = listeners.getOrDefault(clazz, new ArrayList<>(2));
        if (!listenerList.contains(listener)) {
            return;
        }
        listenerList.remove(listener);
        listeners.put(clazz, listenerList);
    }

    public void publishEvent(final AbstractEvent event) {
        for (AbstractListener<?> listener : listeners.getOrDefault(event.getClass(), new ArrayList<>(2))) {
            invokeListener(listener, event);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void invokeListener(AbstractListener listener, AbstractEvent event) {
        listener.onEvent(event);
    }
}
