package cn.easyjce.plugin.ui;

import cn.easyjce.plugin.beans.Cache;
import cn.easyjce.plugin.beans.SelectionCache;
import cn.easyjce.plugin.configuration.JcePluginSetting;
import cn.easyjce.plugin.global.PluginConstants;
import cn.easyjce.plugin.utils.MessagesUtil;
import com.intellij.util.ui.FormBuilder;

import javax.swing.*;
import java.util.List;
import java.util.Map;

/**
 * @Class: ConfigurationPanel
 * @Date: 2022/7/26 10:56
 * @author: cuijiufeng
 */
public class ConfigPanelBuilder {
    private final JPanel jPanel;
    private final JRadioButton inputHex;
    private final JRadioButton intputBase64;
    private final JRadioButton outputHex;
    private final JRadioButton outputBase64;

    public ConfigPanelBuilder() {
        FormBuilder builder = FormBuilder.createFormBuilder();

        Map<String, String> settings = JcePluginSetting.getSetting();
        ButtonGroup inputBg = new ButtonGroup();
        inputHex = new JRadioButton("Hex", Boolean.parseBoolean(settings.get(PluginConstants.CacheKey.CONFIG_INPUT_RB_HEX)));
        intputBase64 = new JRadioButton("Base64", Boolean.parseBoolean(settings.get(PluginConstants.CacheKey.CONFIG_INPUT_RB_BASE64)));
        inputBg.add(inputHex);
        inputBg.add(intputBase64);
        ButtonGroup outputBg = new ButtonGroup();
        outputHex = new JRadioButton("Hex", Boolean.parseBoolean(settings.get(PluginConstants.CacheKey.CONFIG_OUTPUT_RB_HEX)));
        outputBase64 = new JRadioButton("Base64", Boolean.parseBoolean(settings.get(PluginConstants.CacheKey.CONFIG_OUTPUT_RB_BASE64)));
        outputBg.add(outputHex);
        outputBg.add(outputBase64);
        builder.addComponent(new ConfigUI(MessagesUtil.getI18nMessage("system"))
                .addLineComponent(new JLabel(MessagesUtil.getI18nMessage("input code") + ":"), inputHex, intputBase64)
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
        Map<String, String> settings = JcePluginSetting.getSetting();
        return inputHex.isSelected() != Boolean.parseBoolean(settings.get(PluginConstants.CacheKey.CONFIG_INPUT_RB_HEX))
                || intputBase64.isSelected() != Boolean.parseBoolean(settings.get(PluginConstants.CacheKey.CONFIG_INPUT_RB_BASE64))
                || outputHex.isSelected() != Boolean.parseBoolean(settings.get(PluginConstants.CacheKey.CONFIG_OUTPUT_RB_HEX))
                || outputBase64.isSelected() != Boolean.parseBoolean(settings.get(PluginConstants.CacheKey.CONFIG_OUTPUT_RB_BASE64));
    }

    public JRadioButton getInputHex() {
        return inputHex;
    }

    public JRadioButton getIntputBase64() {
        return intputBase64;
    }

    public JRadioButton getOutputHex() {
        return outputHex;
    }

    public JRadioButton getOutputBase64() {
        return outputBase64;
    }
}
