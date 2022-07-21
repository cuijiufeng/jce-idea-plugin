package cn.easyjce.plugin.utils;

import cn.easyjce.plugin.global.PluginConstants;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;

/**
 * @Class: MessageUtil
 * @Date: 2022/7/20 17:57
 * @author: cuijiufeng
 */
public class NotificationsUtil {

    public static void showNotice(NotificationType level, String content, Object ... args) {
        Notifications.Bus.notify(new Notification(
                PluginConstants.NOTIFICATION_GROUP,
                "Tip",
                MessagesUtil.getI18nMessage(content, args),
                level));
    }
}
