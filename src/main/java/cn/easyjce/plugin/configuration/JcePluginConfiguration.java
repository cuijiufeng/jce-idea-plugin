package cn.easyjce.plugin.configuration;

import cn.easyjce.plugin.global.PluginConstants;
import cn.easyjce.plugin.ui.ConfigUI;
import cn.easyjce.plugin.utils.MessagesUtil;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.util.ui.FormBuilder;
import javafx.scene.control.RadioButton;
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
        FormBuilder builder = FormBuilder.createFormBuilder();
        ConfigUI configUI = new ConfigUI(MessagesUtil.getI18nMessage("system"));
        builder.addComponent(configUI
                .addLineComponent(
                        new JLabel(MessagesUtil.getI18nMessage("input code") + ":"),
                        new JRadioButton("Hex"),
                        new JRadioButton("Base64"))
                .addLineComponent(new JLabel(MessagesUtil.getI18nMessage("output code") + ":"),
                        new JRadioButton("Hex"),
                        new JRadioButton("Base64"))
                .getConfigPanel());
        //填充剩余空间
        builder.addComponentFillVertically(new JPanel(), 30);
        return builder.getPanel();
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
