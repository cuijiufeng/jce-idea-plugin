package cn.easyjce.plugin.configurable;

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
public class JcePluginConfigurable implements Configurable {
    private ConfigPanel configPanel;

    @Override
    public String getDisplayName() {
        return PluginConstants.CONFIG_NAME;
    }

    @Override
    public @Nullable JComponent createComponent() {
        return (this.configPanel = new ConfigPanel()).getPanel();
    }

    @Override
    public boolean isModified() {
        return this.configPanel.isModified();
    }

    @Override
    public void apply() throws ConfigurationException {
        JcePluginState.getInstance().setWelcomeNoti(this.configPanel.getWelcomeNotiConfigValue());
        JcePluginState.getInstance().setInputRb(this.configPanel.getInputConfigValue());
        JcePluginState.getInstance().setOutputRb(this.configPanel.getOutputConfigValue());
        JcePluginState.getInstance().setHistorys(this.configPanel.getAddHistoryConfigValue());
    }

    @Override
    public void reset() {
        this.configPanel.setWelcomeNotiConfigValue(JcePluginState.getInstance().isWelcomeNoti());
        this.configPanel.setInputConfigValue(JcePluginState.getInstance().getInputRb());
        this.configPanel.setOutputConfigValue(JcePluginState.getInstance().getOutputRb());
        this.configPanel.setAddHistoryConfigValue(JcePluginState.getInstance().getHistorys());
    }

    @Override
    public void disposeUIResources() {
        this.configPanel = null;
    }
}
