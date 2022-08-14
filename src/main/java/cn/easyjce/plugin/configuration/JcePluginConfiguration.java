package cn.easyjce.plugin.configuration;

import cn.easyjce.plugin.global.PluginConstants;
import cn.easyjce.plugin.ui.ConfigPanel;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @Class: JcePluginConfiguration
 * @Date: 2022/7/25 15:46
 * @author: cuijiufeng
 */
public class JcePluginConfiguration implements Configurable {
    private ConfigPanel configPanel;

    @Override
    public String getDisplayName() {
        return PluginConstants.CONFIG_NAME;
    }

    @Override
    public @Nullable JComponent createComponent() {
        configPanel = new ConfigPanel();
        return configPanel.getPanel();
    }

    @Override
    public boolean isModified() {
        return configPanel.isModified();
    }

    @Override
    public void apply() throws ConfigurationException {
        JcePluginState.getInstance().setInputRb(configPanel.getInputConfigValue());
        JcePluginState.getInstance().setOutputRb(configPanel.getOutputConfigValue());
    }

    @Override
    public void reset() {
        configPanel.setInputConfigValue(JcePluginState.getInstance().getInputRb());
        configPanel.setOutputConfigValue(JcePluginState.getInstance().getOutputRb());
    }

    @Override
    public void disposeUIResources() {
        configPanel = null;
    }
}
