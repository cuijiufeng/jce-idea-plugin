package cn.easyjce.plugin.ui;

import cn.easyjce.plugin.configuration.JcePluginState;
import cn.easyjce.plugin.global.PluginConstants;
import cn.easyjce.plugin.utils.MessagesUtil;
import com.intellij.util.ui.FormBuilder;

import javax.swing.*;

/**
 * @Class: ConfigurationPanel
 * @Date: 2022/7/26 10:56
 * @author: cuijiufeng
 */
public class ConfigPanel {
    private final JPanel jPanel;
    private final JRadioButton inputHex = new JRadioButton("Hex");
    private final JRadioButton inputBase64 = new JRadioButton("Base64");
    private final JRadioButton outputHex = new JRadioButton("Hex");
    private final JRadioButton outputBase64 = new JRadioButton("Base64");

    public ConfigPanel() {
        FormBuilder builder = FormBuilder.createFormBuilder();

        ButtonGroup inputBg = new ButtonGroup();
        inputBg.add(inputHex);
        inputBg.add(inputBase64);
        ButtonGroup outputBg = new ButtonGroup();
        outputBg.add(outputHex);
        outputBg.add(outputBase64);
        builder.addComponent(new ConfigUI(MessagesUtil.getI18nMessage("system"))
                .addLineComponent(new JLabel(MessagesUtil.getI18nMessage("input code") + ":"), inputHex, inputBase64)
                .addLineComponent(new JLabel(MessagesUtil.getI18nMessage("output code") + ":"), outputHex, outputBase64)
                .getConfigPanel());
        //填充剩余空间
        builder.addComponentFillVertically(new JPanel(), 0);
        jPanel = builder.getPanel();
    }

    public JPanel getPanel() {
        return jPanel;
    }

    public boolean isModified() {
        boolean intputRbEquals = getInputConfigValue().equals(JcePluginState.getInstance().getInputRb());
        boolean outtputRbEquals = getOutputConfigValue().equals(JcePluginState.getInstance().getOutputRb());
        return !(intputRbEquals && outtputRbEquals);
    }

    public String getInputConfigValue() {
        if (inputBase64.isSelected()) {
            return PluginConstants.CacheConstants.CONFIG_VALUE_RB_BASE64;
        }
        return PluginConstants.CacheConstants.CONFIG_VALUE_RB_HEX;
    }

    public String getOutputConfigValue() {
        if (outputBase64.isSelected()) {
            return PluginConstants.CacheConstants.CONFIG_VALUE_RB_BASE64;
        }
        return PluginConstants.CacheConstants.CONFIG_VALUE_RB_HEX;
    }

    public void setInputConfigValue(String value) {
        if (PluginConstants.CacheConstants.CONFIG_VALUE_RB_BASE64.equals(value)) {
            inputBase64.setSelected(true);
            return;
        }
        inputHex.setSelected(true);
    }

    public void setOutputConfigValue(String value) {
        if (PluginConstants.CacheConstants.CONFIG_VALUE_RB_BASE64.equals(value)) {
            outputBase64.setSelected(true);
            return;
        }
        outputHex.setSelected(true);
    }
}
