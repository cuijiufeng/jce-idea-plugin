package cn.easyjce.plugin.configuration;

import cn.easyjce.plugin.global.PluginConstants;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @Class: JcePluginConfiguration
 * @Date: 2022/7/25 15:46
 * @author: cuijiufeng
 */
public class JcePluginConfiguration implements Configurable {
    @Override
    public String getDisplayName() {
        return PluginConstants.CONFIG_NAME;
    }

    @Override
    public @Nullable JComponent createComponent() {
        return null;
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {

    }

    @Override
    public void reset() {
    }

    @Override
    public void disposeUIResources() {
    }
}
