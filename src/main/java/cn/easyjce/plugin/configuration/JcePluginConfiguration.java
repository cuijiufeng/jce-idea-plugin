package cn.easyjce.plugin.configuration;

import cn.easyjce.plugin.global.PluginConstants;
import cn.easyjce.plugin.ui.ConfigPanelBuilder;
import cn.easyjce.plugin.ui.ConfigUI;
import cn.easyjce.plugin.utils.MessagesUtil;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.util.ui.FormBuilder;
import javafx.scene.control.RadioButton;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

/**
 * @Class: JcePluginConfiguration
 * @Date: 2022/7/25 15:46
 * @author: cuijiufeng
 * //TODO 2022/7/26 17:21 存在bug，重启配置会丢失
 */
public class JcePluginConfiguration implements Configurable {
    private ConfigPanelBuilder panelBuilder;

    @Override
    public String getDisplayName() {
        return PluginConstants.CONFIG_NAME;
    }

    @Override
    public @Nullable JComponent createComponent() {
        panelBuilder = new ConfigPanelBuilder();
        return panelBuilder.getPanel();
    }

    @Override
    public boolean isModified() {
        return panelBuilder.isModified();
    }

    @Override
    public void apply() throws ConfigurationException {
        Map<String, String> settings = JcePluginSetting.getSetting();
        settings.put(PluginConstants.CacheKey.CONFIG_INPUT_RB_HEX, String.valueOf(panelBuilder.getInputHex().isSelected()));
        settings.put(PluginConstants.CacheKey.CONFIG_INPUT_RB_BASE64, String.valueOf(panelBuilder.getIntputBase64().isSelected()));
        settings.put(PluginConstants.CacheKey.CONFIG_OUTPUT_RB_HEX, String.valueOf(panelBuilder.getOutputHex().isSelected()));
        settings.put(PluginConstants.CacheKey.CONFIG_OUTPUT_RB_BASE64, String.valueOf(panelBuilder.getOutputBase64().isSelected()));
    }

    @Override
    public void reset() {
        Map<String, String> settings = JcePluginSetting.getSetting();
        panelBuilder.getInputHex().setSelected(Boolean.parseBoolean(settings.get(PluginConstants.CacheKey.CONFIG_INPUT_RB_HEX)));
        panelBuilder.getIntputBase64().setSelected(Boolean.parseBoolean(settings.get(PluginConstants.CacheKey.CONFIG_INPUT_RB_BASE64)));
        panelBuilder.getOutputHex().setSelected(Boolean.parseBoolean(settings.get(PluginConstants.CacheKey.CONFIG_OUTPUT_RB_HEX)));
        panelBuilder.getOutputBase64().setSelected(Boolean.parseBoolean(settings.get(PluginConstants.CacheKey.CONFIG_OUTPUT_RB_BASE64)));
    }

    @Override
    public void disposeUIResources() {
        panelBuilder = null;
    }
}
