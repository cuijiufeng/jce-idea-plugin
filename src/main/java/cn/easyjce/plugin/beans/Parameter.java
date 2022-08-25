package cn.easyjce.plugin.beans;

import javax.swing.*;
import java.util.List;
import java.util.function.BooleanSupplier;

/**
 * @Class: ServiceRequest
 * @Date: 2022/7/28 9:08
 * @author: cuijiufeng
 */
public abstract class Parameter {
    private final String label;
    private final int maxRow;
    private final BooleanSupplier show;

    public Parameter(String label, int maxRow, BooleanSupplier show) {
        this.label = label;
        this.maxRow = maxRow;
        this.show = show;
    }

    public String getKey() {
        return this.label;
    }

    public abstract String getValue();

    public abstract List<? extends JComponent> getComponent();

    public void clear() {};

    public int getMaxRow() {
        return this.maxRow;
    }

    public boolean isShow() {
        return this.show.getAsBoolean();
    }
}
