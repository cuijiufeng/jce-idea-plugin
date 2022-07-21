package cn.easyjce.plugin.intellij;

import cn.easyjce.plugin.ui.ToolWindowPanel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

/**
 * @Class: WindowFactory
 * @Date: 2022/7/20 14:45
 * @author: cuijiufeng
 */
public class WindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(new ToolWindowPanel(project, toolWindow), "", false);
        toolWindow.getContentManager().addContent(content);
    }
}
