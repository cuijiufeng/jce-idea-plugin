package cn.easyjce.plugin.beans;

import cn.easyjce.plugin.event.EventPublisher;
import cn.easyjce.plugin.event.ParameterUIEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import org.apache.commons.collections.CollectionUtils;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.util.Arrays;
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
    private final String label;
    private final JBLabel labelComponent;
    private JTextField textField;
    private List<JRadioButton> radioButtons;
    private DisplayUI isShow;

    public Parameter(String label, DisplayUI isShow) {
        this.anEnum = ParameterEnum.TEXT_FIELD;
        this.label = label;
        this.labelComponent = new JBLabel(label + ":");
        this.isShow = isShow;
        this.anEnum.init(this);
    }

    public Parameter(String label, List<String> rbText) {
        this.anEnum = ParameterEnum.RADIO_BUTTON;
        this.label = label;
        this.labelComponent = new JBLabel(label + ":");
        if (CollectionUtils.isEmpty(rbText)) {
            throw new NullPointerException("can't be null");
        }
        this.isShow = DisplayUI.NONE;
        this.anEnum.init(this, rbText.toArray(new String[0]));
    }

    public JBLabel getLabelComponent() {
        return labelComponent;
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

    public boolean isHide() {
        return DisplayUI.HIDE.equals(this.isShow);
    }

    public void toggleUI() {
        switch (this.isShow) {
            case SHOW: this.isShow = DisplayUI.HIDE; break;
            case HIDE: this.isShow = DisplayUI.SHOW; break;
            case NONE:break;
            default:break;
        }
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
            public void init(Parameter parameter, String... param) {
                parameter.textField = new JBTextField();
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
            public void init(Parameter parameter, String... param) {
                parameter.radioButtons = Arrays.stream(param).map(JRadioButton::new).collect(Collectors.toList());
                if (parameter.radioButtons.stream().noneMatch(JRadioButton::isSelected)) {
                    parameter.radioButtons.get(0).setSelected(true);
                }
                ButtonGroup bg = new ButtonGroup();
                EventPublisher service = ServiceManager.getService(EventPublisher.class);
                for (JRadioButton radioButton : parameter.radioButtons) {
                    bg.add(radioButton);
                    radioButton.addItemListener(e -> {
                        if (ItemEvent.SELECTED == e.getStateChange()) {
                            //当选择不同参数，整个参数UI重新绘制，并交换show与hide
                            service.publishEvent(new ParameterUIEvent(parameter));
                        }
                    });
                }
            }
            @Override
            public void clear(Parameter parameter) {}
        }
        ;
        public abstract String getValue(Parameter parameter);
        public abstract List<? extends JComponent> getComponent(Parameter parameter);
        public abstract void init(Parameter parameter, String ... param);
        public abstract void clear(Parameter parameter);
    }

    public enum DisplayUI {
        NONE,SHOW,HIDE
    }
}
