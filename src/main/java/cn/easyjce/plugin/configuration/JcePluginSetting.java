package cn.easyjce.plugin.configuration;

import cn.easyjce.plugin.global.PluginConstants;
import cn.easyjce.plugin.ui.MainPanel;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @Class: JcePluginConfiguration
 * @Date: 2022/7/22 17:55
 * @author: cuijiufeng
 */
@State(name = "JcePluginSetting", storages = @Storage(PluginConstants.SETTING_FILE))
public class JcePluginSetting implements PersistentStateComponent<JcePluginSetting> {
    private final Map<String, Object> settings;

    public JcePluginSetting() {
        settings = new HashMap<>(16);
    }

    public static Map<String, Object> getSetting() {
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
