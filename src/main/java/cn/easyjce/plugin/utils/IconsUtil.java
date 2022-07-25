package cn.easyjce.plugin.utils;

import com.intellij.ui.IconManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @Class: IconUtil
 * @Date: 2022/7/25 16:26
 * @author: cuijiufeng
 */
public class IconsUtil {
    public static Icon PLUGIN = load("/icons/logo.svg");

    @NotNull
    public static Icon load(@NotNull String path) {
        return IconManager.getInstance().getIcon(path, IconsUtil.class);
    }
}
