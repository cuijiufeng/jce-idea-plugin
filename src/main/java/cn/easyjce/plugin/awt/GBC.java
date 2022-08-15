package cn.easyjce.plugin.awt;

import com.intellij.util.ui.JBUI;

import java.awt.*;
import java.util.function.BooleanSupplier;

/**
 * @Class: GBC
 * @Date: 2022/7/22 10:19
 * @author: cuijiufeng
 */
public class GBC extends GridBagConstraints {
    //初始化左上角位置
    public GBC(int gridx, int gridy) {
        this.gridx = gridx;
        this.gridy = gridy;
        setWeight(1.0, 0);
        setFill(GridBagConstraints.HORIZONTAL, () -> true);
        setAnchor(GridBagConstraints.WEST, () -> true);
    }

    //初始化左上角位置和所占行数和列数
    public GBC(int gridx, int gridy, int gridwidth, int gridheight) {
        this.gridx = gridx;
        this.gridy = gridy;
        this.gridwidth = gridwidth;
        this.gridheight = gridheight;
        setWeight(1.0, 0);
        setFill(GridBagConstraints.HORIZONTAL, () -> true);
        setAnchor(GridBagConstraints.WEST, () -> true);
    }

    //对齐方式
    public GBC setAnchor(int anchor, BooleanSupplier supplier) {
        if (supplier.getAsBoolean()) {
            this.anchor = anchor;
        }
        return this;
    }

    //是否拉伸及拉伸方向
    public GBC setFill(int fill, BooleanSupplier supplier) {
        if (supplier.getAsBoolean()) {
            this.fill = fill;
        }
        return this;
    }

    //x和y方向上的增量
    public GBC setWeight(double weightx, double weighty) {
        this.weightx = weightx;
        this.weighty = weighty;
        return this;
    }

    public GBC setInsets(int distance) {
        this.insets = JBUI.insets(distance);
        return this;
    }

    public GBC setInsets(int top, int left, int bottom, int right) {
        this.insets = JBUI.insets(top, left, bottom, right);
        return this;
    }

    public GBC setIpad(int ipadx, int ipady) {
        this.ipadx = ipadx;
        this.ipady = ipady;
        return this;
    }
}