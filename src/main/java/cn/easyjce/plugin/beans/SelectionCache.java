package cn.easyjce.plugin.beans;

import javax.swing.*;
import java.util.List;

/**
 * @Class: RadioButtonCache
 * @Date: 2022/7/26 11:48
 * @author: cuijiufeng
 */
public class SelectionCache implements Cache {
    private List<JRadioButton> list;

    public void init(List<JRadioButton> datas) {
        ButtonGroup bg = new ButtonGroup();
        for (JRadioButton rb : datas) {
            list.add(rb);
            bg.add(rb);
        }
    }

    public JRadioButton[] getData() {
        return list.toArray(new JRadioButton[0]);
    }
}
