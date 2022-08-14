package cn.easyjce.plugin.ui;

import cn.easyjce.plugin.configuration.JcePluginState;
import cn.easyjce.plugin.utils.MessagesUtil;
import com.intellij.util.ui.FormBuilder;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

/**
 * @Class: ConfigurationPanel
 * @Date: 2022/7/26 10:56
 * @author: cuijiufeng
 */
public class ConfigPanel {
    private final JPanel jPanel;
    private final List<JRadioButton> inputComponents = Arrays.asList(
            new JRadioButton(JcePluginState.RbValueEnum.string.name()),
            new JRadioButton(JcePluginState.RbValueEnum.hex.name()),
            new JRadioButton(JcePluginState.RbValueEnum.base64.name()));
    private final List<JRadioButton> outputComponents = Arrays.asList(
            new JRadioButton(JcePluginState.RbValueEnum.string.name()),
            new JRadioButton(JcePluginState.RbValueEnum.hex.name()),
            new JRadioButton(JcePluginState.RbValueEnum.base64.name()));

    public ConfigPanel() {
        ButtonGroup inputBg = new ButtonGroup();
        this.inputComponents.forEach(inputBg::add);
        ButtonGroup outputBg = new ButtonGroup();
        this.outputComponents.forEach(outputBg::add);
        JPanel configPanel = new ConfigUI(MessagesUtil.getI18nMessage("system"))
                .addLineComponent(MessagesUtil.getI18nMessage("input code"), inputComponents.toArray(new JComponent[0]))
                .addLineComponent(MessagesUtil.getI18nMessage("output code"), outputComponents.toArray(new JComponent[0]))
                .getConfigPanel();
        this.jPanel = FormBuilder.createFormBuilder()
                .addComponent(configPanel)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    public JPanel getPanel() {
        return this.jPanel;
    }

    public boolean isModified() {
        boolean intputRbEquals = getInputConfigValue().equals(JcePluginState.getInstance().getInputRb());
        boolean outtputRbEquals = getOutputConfigValue().equals(JcePluginState.getInstance().getOutputRb());
        return !(intputRbEquals && outtputRbEquals);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public JcePluginState.RbValueEnum getInputConfigValue() {
        return this.inputComponents.stream()
                .filter(JRadioButton::isSelected)
                .map(JRadioButton::getText)
                .map(JcePluginState.RbValueEnum::valueOf)
                .findFirst()
                .get();
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public JcePluginState.RbValueEnum getOutputConfigValue() {
        return this.outputComponents.stream()
                .filter(JRadioButton::isSelected)
                .map(JRadioButton::getText)
                .map(JcePluginState.RbValueEnum::valueOf)
                .findFirst()
                .get();
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void setInputConfigValue(JcePluginState.RbValueEnum value) {
        this.inputComponents.stream()
                .filter(rb -> rb.getText().equals(value.name()))
                .findFirst()
                .get()
                .setSelected(true);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void setOutputConfigValue(JcePluginState.RbValueEnum value) {
        this.outputComponents.stream()
                .filter(rb -> rb.getText().equals(value.name()))
                .findFirst()
                .get()
                .setSelected(true);
    }
}
