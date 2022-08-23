package cn.easyjce.plugin.actions;

import cn.easyjce.plugin.global.PluginConstants;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author cuijiufeng
 * @date 2022/8/21 21:42
 * @desc
 */
public class GenerateCodeAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        JBPopupFactory.getInstance().createActionGroupPopup(
                "Jce Types",
                ((ActionGroup) ActionManager.getInstance().getAction(PluginConstants.POPUP_GENERATE_ID)),
                e.getDataContext(),
                JBPopupFactory.ActionSelectionAid.NUMBERING,
                false).showInBestPositionFor(e.getDataContext());
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        // 不存在模块不展示：选择多个模块
        if (Objects.isNull(event.getData(CommonDataKeys.PROJECT)) || Objects.isNull(event.getData(LangDataKeys.MODULE))) {
            event.getPresentation().setVisible(false);
            return;
        }

        // 非java的文件不显示
        VirtualFile file = event.getDataContext().getData(CommonDataKeys.VIRTUAL_FILE);
        if (file == null || !(file.getFileType() instanceof JavaFileType)) {
            event.getPresentation().setVisible(false);
        }
    }
}
