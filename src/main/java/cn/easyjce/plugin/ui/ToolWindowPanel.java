package cn.easyjce.plugin.ui;

import cn.easyjce.plugin.global.PluginConstants;
import cn.easyjce.plugin.utils.NotificationsUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * @Class: MainPanel
 * @Date: 2022/7/20 15:21
 * @author: cuijiufeng
 */
public class ToolWindowPanel extends JPanel {
    public ToolWindowPanel(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        super(new BorderLayout());

        //将toolbar添加到toolwindow面板上
        AnAction action = ActionManager.getInstance().getAction(PluginConstants.TOOL_BAR_ID);
        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, ((ActionGroup) action), true);
        this.add(actionToolbar.getComponent(), BorderLayout.NORTH);

        //添加主体ui
        this.add(MainUI.getInstance().getMainPanel(), BorderLayout.CENTER);

        NotificationsUtil.showNotice(NotificationType.INFORMATION, "welcome");
    }
}
