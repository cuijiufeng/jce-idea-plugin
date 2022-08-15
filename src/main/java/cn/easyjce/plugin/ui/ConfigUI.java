package cn.easyjce.plugin.ui;

import cn.easyjce.plugin.awt.GBC;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.FormBuilder;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;

/**
 * @Class: ConfigUi
 * @Date: 2022/7/25 17:17
 * @author: cuijiufeng
 */
public class ConfigUI {
    private JPanel configPanel;
    private JLabel title;
    private JPanel content;
    private final FormBuilder formBuilder = FormBuilder.createFormBuilder();

    public ConfigUI(String title) {
        this.title.setText(title);
        this.content.add(this.formBuilder.getPanel(), BorderLayout.CENTER);
    }

    public ConfigUI addLineComponent(String label, JComponent ... components) {
        boolean existLabel = StringUtils.isNotBlank(label);
        JPanel jPanel = new JPanel(new GridBagLayout());
        if (existLabel) {
            GBC constraints = new GBC(0, 0)
                    .setWeight(0, 0)
                    .setFill(GridBagConstraints.NONE, () -> true);
            jPanel.add(new JBLabel(label), constraints);
        }
        for (int i = 0; i < components.length; i++) {
            JComponent component = components[i];
            GBC constraints = new GBC(existLabel ? i + 1 : i, 0)
                    .setFill(GridBagConstraints.NONE, () -> component instanceof JComboBox || component instanceof JButton);
            jPanel.add(component, constraints);
        }
        this.formBuilder.addComponent(jPanel);
        return this;
    }

    public JPanel getConfigPanel() {
        return this.configPanel;
    }
}
