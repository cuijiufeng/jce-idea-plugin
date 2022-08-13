package cn.easyjce.plugin.beans;

import javax.swing.*;
import java.util.List;
import java.util.function.BooleanSupplier;

/**
 * @Class: ServiceRequest
 * @Date: 2022/7/28 9:08
 * @author: cuijiufeng
 */
public abstract class Parameter<T> {
    private final String label;
    private final int maxCol;
    private final BooleanSupplier show;

    public Parameter(String label, int maxCol, BooleanSupplier show) {
        this.label = label;
        this.maxCol = maxCol;
        this.show = show;
    }

    public String getKey() {
        return this.label;
    }

    public abstract T getValue();

    public abstract List<? extends JComponent> getComponent();

    public void clear() {};

    public int getMaxCol() {
        return this.maxCol;
    }

    public boolean isShow() {
        return this.show.getAsBoolean();
    }
}
