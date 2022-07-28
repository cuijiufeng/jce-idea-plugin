package cn.easyjce.plugin.beans;

import com.intellij.ui.components.JBTextField;
import org.apache.commons.collections.CollectionUtils;

import javax.swing.*;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Class: ServiceRequest
 * @Date: 2022/7/28 9:08
 * @author: cuijiufeng
 */
public class Parameter {
    private final ParameterEnum anEnum;
    private String label;
    private JTextField textField;
    private List<JRadioButton> radioButtons;

    public Parameter(ParameterEnum anEnum, String label) {
        this.anEnum = anEnum;
        this.label = label;
        this.textField = new JBTextField();
    }

    public Parameter(ParameterEnum anEnum, String label, List<String> rbText) {
        this.anEnum = anEnum;
        this.label = label;
        if (CollectionUtils.isEmpty(rbText)) {
            throw new NullPointerException("can't be null");
        }
        this.radioButtons = rbText.stream().map(JRadioButton::new).collect(Collectors.toList());
        if (!this.radioButtons.isEmpty() && this.radioButtons.stream().noneMatch(JRadioButton::isSelected)) {
            this.radioButtons.get(0).setSelected(true);
        }
        ButtonGroup bg = new ButtonGroup();
        for (JRadioButton radioButton : this.radioButtons) {
            bg.add(radioButton);
        }
    }

    public List<? extends JComponent> getComponent() {
        return anEnum.getComponent(this);
    }

    public String getKey() {
        return label;
    }

    public String getValue() {
        return anEnum.getValue(this);
    }

    public void clear() {
        anEnum.clear(this);
    }

    public enum ParameterEnum {
        TEXT_FIELD {
            @Override
            public String getValue(Parameter parameter) {
                return parameter.textField.getText();
            }
            @Override
            public List<? extends JComponent> getComponent(Parameter parameter) {
                return Collections.singletonList(parameter.textField);
            }
            @Override
            public void clear(Parameter parameter) {
                parameter.textField.setText(null);
            }
        },
        RADIO_BUTTON {
            @Override
            public String getValue(Parameter parameter) {
                for (JRadioButton radioButton : parameter.radioButtons) {
                    if (radioButton.isSelected()) {
                        return radioButton.getText();
                    }
                }
                return null;
            }
            @Override
            public List<? extends JComponent> getComponent(Parameter parameter) {
                return parameter.radioButtons;
            }
            @Override
            public void clear(Parameter parameter) {
                for (JRadioButton radioButton : parameter.radioButtons) {
                    radioButton.setSelected(false);
                }
                parameter.radioButtons.get(0).setSelected(true);
            }
        }
        ;
        public abstract String getValue(Parameter parameter);
        public abstract List<? extends JComponent> getComponent(Parameter parameter);
        public abstract void clear(Parameter parameter);
    }
}
