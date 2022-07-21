package cn.easyjce.plugin.actions;

import cn.easyjce.plugin.global.PluginConstants;
import cn.easyjce.plugin.utils.MessagesUtil;
import cn.easyjce.plugin.utils.NotificationsUtil;
import com.intellij.icons.AllIcons;
import com.intellij.ide.BrowserUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @Class: HelpAction
 * @Date: 2022/7/21 16:07
 * @author: cuijiufeng
 */
public class HelpAction extends AnAction {
    public HelpAction() {
        super(MessagesUtil.getI18nMessage("help"), null, AllIcons.Actions.Help);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        //打开浏览器请求
        BrowserUtil.browse(PluginConstants.GITHUB_ADDR);
    }
}
