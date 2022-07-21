package cn.easyjce.plugin.utils;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * @Class: MessagesUtil
 * @Date: 2022/7/21 10:15
 * @author: cuijiufeng
 */
public class MessagesUtil {
    public static final String BASE_NAME = "messages";
    private static final ResourceBundle rb = ResourceBundle.getBundle(BASE_NAME);

    public static String getMessage(String key, String... params) {
        try {
            return new MessageFormat(rb.getString(key)).format(params);
        } catch (Exception e) {
            return key;
        }
    }
}
