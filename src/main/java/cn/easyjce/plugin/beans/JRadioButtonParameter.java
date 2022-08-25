package cn.easyjce.plugin.beans;

import cn.easyjce.plugin.event.EventPublisher;
import cn.easyjce.plugin.event.ParameterUIEvent;
import com.intellij.openapi.components.ServiceManager;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Class: JRadioButtonParameter
 * @Date: 2022/8/12 16:10
 * @author: cuijiufeng
 */
public class JRadioButtonParameter extends Parameter {
    private final List<JRadioButton> radioButtons;

    public JRadioButtonParameter(String label, List<String> rbText, int maxCol) {
        super(label, maxCol, () -> true);
        this.radioButtons = rbText.stream().map(JRadioButton::new).collect(Collectors.toList());
        this.radioButtons.get(0).setSelected(true);
        ButtonGroup bg = new ButtonGroup();
        EventPublisher service = ServiceManager.getService(EventPublisher.class);
        for (JRadioButton radioButton : this.radioButtons) {
            bg.add(radioButton);
            radioButton.addItemListener(e -> {
                if (ItemEvent.SELECTED == e.getStateChange()) {
                    //当选择不同参数，整个参数UI重新绘制，并交换show与hide
                    service.publishEvent(new ParameterUIEvent(this));
                }
            });
        }
    }

    @Override
    public String getValue() {
        for (JRadioButton radioButton : this.radioButtons) {
            if (radioButton.isSelected()) {
                return radioButton.getText();
            }
        }
        return null;
    }

    @Override
    public List<? extends JComponent> getComponent() {
        return this.radioButtons;
    }
}
