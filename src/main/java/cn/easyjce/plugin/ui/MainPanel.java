package cn.easyjce.plugin.ui;

import cn.easyjce.plugin.utils.MessagesUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * @Class: MainPanel
 * @Date: 2022/7/21 16:58
 * @author: cuijiufeng
 */
public class MainPanel extends JPanel {
    public MainPanel(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        super(new BorderLayout());

        JPanel algoProvider = new JPanel();
        algoProvider.setLayout(new BorderLayout());
        algoProvider.add(new JBLabel(MessagesUtil.getI18nMessage("algo provider") + ":"), BorderLayout.WEST);
        algoProvider.add(new ComboBox<>(new String[]{"BC", "ES", "SW"}), BorderLayout.CENTER);

        this.add(algoProvider, BorderLayout.NORTH);
    }
}
