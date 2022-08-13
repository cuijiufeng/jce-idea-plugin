package cn.easyjce.plugin.beans;

import cn.easyjce.plugin.utils.MessagesUtil;
import com.intellij.ui.components.JBTextField;

import javax.swing.*;
import java.util.Collections;
import java.util.List;
import java.util.function.BooleanSupplier;

/**
 * @Class: JTextFieldParameter
 * @Date: 2022/8/12 16:09
 * @author: cuijiufeng
 */
public class JTextFieldParameter extends Parameter<String> {
    private final JTextField textField;

    public JTextFieldParameter(String label, String tooltip, BooleanSupplier show) {
        super(label, 1, show);
        this.textField = new JBTextField();
        this.textField.setToolTipText(MessagesUtil.getI18nMessage(tooltip));
    }

    @Override
    public String getValue() {
        return this.textField.getText().trim();
    }

    @Override
    public List<? extends JComponent> getComponent() {
        return Collections.singletonList(this.textField);
    }

    @Override
    public void clear() {
        this.textField.setText(null);
    }
}
