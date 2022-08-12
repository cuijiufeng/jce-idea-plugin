package cn.easyjce.plugin.beans;

import cn.easyjce.plugin.event.EventPublisher;
import cn.easyjce.plugin.event.ParameterUIEvent;
import cn.easyjce.plugin.utils.MessagesUtil;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.ui.components.JBTextField;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

/**
 * @Class: ServiceRequest
 * @Date: 2022/7/28 9:08
 * @author: cuijiufeng
 */
public class Parameter {
    private final ParameterEnum anEnum;
    private final String label;
    private JTextField textField;
    private List<JRadioButton> radioButtons;
    private int maxCol = 1;
    private final BooleanSupplier show;

    public Parameter(String label, String tooltip, BooleanSupplier show) {
        this.anEnum = ParameterEnum.TEXT_FIELD;
        this.label = label;
        this.show = show;
        this.anEnum.init(this, tooltip);
    }

    public Parameter(String label, List<String> rbText, int maxCol) {
        this.anEnum = ParameterEnum.RADIO_BUTTON;
        this.label = label;
        this.show = () -> true;
        this.anEnum.init(this, null, rbText.toArray(new String[0]));
        this.maxCol = maxCol;
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

    public int getMaxCol() {
        return maxCol;
    }

    public boolean isShow() {
        return show.getAsBoolean();
    }

    public enum ParameterEnum {
        TEXT_FIELD {
            @Override
            public String getValue(Parameter parameter) {
                return parameter.textField.getText().trim();
            }
            @Override
            public List<? extends JComponent> getComponent(Parameter parameter) {
                return Collections.singletonList(parameter.textField);
            }
            @Override
            public void init(Parameter parameter, String tooltip, String... param) {
                parameter.textField = new JBTextField();
                parameter.textField.setToolTipText(MessagesUtil.getI18nMessage(tooltip));
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
            public void init(Parameter parameter, String tooltip, String... param) {
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
        public abstract void init(Parameter parameter, String tooltip, String... param);
        public abstract void clear(Parameter parameter);
    }
}
