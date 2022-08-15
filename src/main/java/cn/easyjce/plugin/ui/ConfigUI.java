package cn.easyjce.plugin.ui;

import cn.easyjce.plugin.awt.GBC;
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
        JPanel jPanel = new JPanel(new GridBagLayout());
        if (StringUtils.isNotBlank(label)) {
            JLabel component = new JLabel(label + ":");
            component.setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));
            jPanel.add(component, new GBC(0, 0, 1, 1).setFill(GridBagConstraints.NONE, () -> true));
        }
        for (int i = 0; i < components.length; i++) {
            JComponent component = components[i];
            int x = StringUtils.isNotBlank(label) ? 2 * i + 1 : 2 * i;
            GBC constraints = new GBC(x, 0, 2, 1).setFill(GridBagConstraints.NONE, () -> component instanceof JComboBox || component instanceof JButton);
            component.setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));
            jPanel.add(component, constraints);
        }
        this.formBuilder.addComponent(jPanel);
        return this;
    }

    public JPanel getConfigPanel() {
        return this.configPanel;
    }
}
