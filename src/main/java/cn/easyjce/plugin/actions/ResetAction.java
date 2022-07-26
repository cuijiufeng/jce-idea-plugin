package cn.easyjce.plugin.actions;

import cn.easyjce.plugin.ui.MainUI;
import cn.easyjce.plugin.utils.MessagesUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @Class: ResetAction
 * @Date: 2022/7/21 16:37
 * @author: cuijiufeng
 */
public class ResetAction extends AnAction {
    public ResetAction() {
        super(MessagesUtil.getI18nMessage("reset"), null, AllIcons.General.Reset);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        MainUI.getInstance().reset();
    }
}
