package cn.easyjce.plugin.ui;

import com.intellij.util.ui.FormBuilder;

import javax.swing.*;
import java.awt.*;

/**
 * @Class: ConfigUi
 * @Date: 2022/7/25 17:17
 * @author: cuijiufeng
 */
public class ConfigUI {
    private final FormBuilder formBuilder;
    private JPanel configPanel;
    private JLabel title;
    private JPanel content;

    public ConfigUI(String title) {
        this.title.setText(title);
        this.formBuilder = FormBuilder.createFormBuilder();
    }

    public ConfigUI addLineComponent(JComponent ... component) {
        JPanel jPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (JComponent jComponent : component) {
            jPanel.add(jComponent);
        }
        formBuilder.addComponent(jPanel);
        return this;
    }

    public JPanel getConfigPanel() {
        content.add(formBuilder.getPanel(), BorderLayout.CENTER);
        return configPanel;
    }
}
