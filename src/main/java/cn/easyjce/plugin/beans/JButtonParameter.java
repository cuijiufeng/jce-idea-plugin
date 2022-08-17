package cn.easyjce.plugin.beans;

import cn.easyjce.plugin.utils.FileChooserUtil;
import cn.easyjce.plugin.utils.MessagesUtil;
import com.intellij.ui.components.JBTextField;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @Class: JButtonParameter
 * @Date: 2022/8/12 16:41
 * @author: cuijiufeng
 */
public class JButtonParameter extends Parameter<String> {
    private final JTextField textField;
    private final JButton button;
    private String filePath;

    public JButtonParameter(String label, String btnLabel) {
        super(label, 2, () -> true);
        this.textField = new JBTextField();
        this.textField.setEnabled(false);
        this.button = new JButton(MessagesUtil.getI18nMessage(btnLabel));
        this.button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) {
                    return;
                }
                filePath = FileChooserUtil.pathFilechooser(
                        new FileChooserUtil.ChooserDescBuilder().setChooseFiles(true).build(),
                        null,
                        null);
                textField.setText(filePath);
            }
        });
    }

    @Override
    public String getValue() {
        return Objects.nonNull(this.filePath) ? this.filePath : "";
    }

    @Override
    public List<? extends JComponent> getComponent() {
        return Arrays.asList(textField, button);
    }

    @Override
    public void clear() {
        this.filePath = null;
        this.textField.setText(null);
    }
}
