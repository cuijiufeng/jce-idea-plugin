package cn.easyjce.plugin.intellij;

import cn.easyjce.plugin.utils.NotificationsUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.tabs.impl.JBTabsImpl;
import org.jetbrains.annotations.NotNull;

/**
 * @Class: MainPanel
 * @Date: 2022/7/20 15:21
 * @author: cuijiufeng
 */
public class MainPanel extends SimpleToolWindowPanel {
    public MainPanel(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        super(true, true);
        NotificationsUtil.showNotice(NotificationType.INFORMATION, "welcome");

        JBTabsImpl jbTabs = new JBTabsImpl(project);
        //content
        setContent(jbTabs);
    }
}
