package cn.easyjce.plugin.configuration;

import cn.easyjce.plugin.beans.Cache;
import cn.easyjce.plugin.beans.SelectionCache;
import cn.easyjce.plugin.global.PluginConstants;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Class: JcePluginConfiguration
 * @Date: 2022/7/22 17:55
 * @author: cuijiufeng
 */
@State(name = "JcePluginSetting", storages = @Storage(PluginConstants.SETTING_FILE))
public class JcePluginSetting implements PersistentStateComponent<JcePluginSetting> {
    private final Map<String, String> settings;

    public JcePluginSetting() {
        settings = new HashMap<>(16);
        initSettings();
    }

    private void initSettings() {
        settings.putIfAbsent(PluginConstants.CacheKey.CONFIG_INPUT_RB_HEX, "true");
        settings.putIfAbsent(PluginConstants.CacheKey.CONFIG_INPUT_RB_BASE64, "false");
        settings.putIfAbsent(PluginConstants.CacheKey.CONFIG_OUTPUT_RB_HEX, "true");
        settings.putIfAbsent(PluginConstants.CacheKey.CONFIG_OUTPUT_RB_BASE64, "false");
    }

    public static Map<String, String> getSetting() {
        return ServiceManager.getService(JcePluginSetting.class).settings;
    }

    @Nullable
    @Override
    public JcePluginSetting getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull JcePluginSetting state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
