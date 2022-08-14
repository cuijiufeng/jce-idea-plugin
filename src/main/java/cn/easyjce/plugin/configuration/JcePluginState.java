package cn.easyjce.plugin.configuration;

import cn.easyjce.plugin.global.PluginConstants;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @Class: JcePluginConfiguration
 * @Date: 2022/7/22 17:55
 * @author: cuijiufeng
 */
@State(name = "JcePluginSetting", storages = @Storage(PluginConstants.SETTING_FILE))
public class JcePluginState implements PersistentStateComponent<JcePluginState> {
    private String inputRb = PluginConstants.CacheConstants.CONFIG_VALUE_RB_HEX;
    private String outputRb = PluginConstants.CacheConstants.CONFIG_VALUE_RB_HEX;

    public static JcePluginState getInstance() {
        return ApplicationManager.getApplication().getService(JcePluginState.class);
    }

    @Nullable
    @Override
    public JcePluginState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull JcePluginState state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public String getInputRb() {
        return inputRb;
    }

    public void setInputRb(String inputRb) {
        this.inputRb = inputRb;
    }

    public String getOutputRb() {
        return outputRb;
    }

    public void setOutputRb(String outputRb) {
        this.outputRb = outputRb;
    }
}
