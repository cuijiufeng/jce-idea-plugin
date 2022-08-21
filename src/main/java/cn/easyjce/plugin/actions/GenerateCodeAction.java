package cn.easyjce.plugin.actions;

import cn.easyjce.plugin.global.PluginConstants;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import org.jetbrains.annotations.NotNull;

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
}
