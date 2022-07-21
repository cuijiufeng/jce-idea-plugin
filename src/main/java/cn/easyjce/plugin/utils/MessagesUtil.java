package cn.easyjce.plugin.utils;

import com.intellij.AbstractBundle;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @Class: MessagesUtil
 * @Date: 2022/7/21 10:15
 * @author: cuijiufeng
 */
public class MessagesUtil extends AbstractBundle {
    public static final String BASE_NAME = "messages";
    private static final MessagesUtil INSTANCE = new MessagesUtil(BASE_NAME);

    protected MessagesUtil(@NotNull String pathToBundle) {
        super(pathToBundle);
    }

    @Override
    protected ResourceBundle findBundle(@NotNull String pathToBundle, @NotNull ClassLoader loader, ResourceBundle.@NotNull Control control) {
        return ResourceBundle.getBundle(pathToBundle, Locale.getDefault(), loader, control);
    }

    public static String getI18nMessage(String key, Object ... params) {
        try {
            return INSTANCE.messageOrDefault(key, key, params);
        } catch (Exception e) {
            return key;
        }
    }
}
