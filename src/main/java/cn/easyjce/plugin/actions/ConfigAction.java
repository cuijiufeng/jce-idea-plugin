package cn.easyjce.plugin.actions;

import cn.easyjce.plugin.configurable.JcePluginConfigurable;
import cn.easyjce.plugin.utils.MessagesUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.ShowSettingsUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @Class: HelpAction
 * @Date: 2022/7/21 16:07
 * @author: cuijiufeng
 */
public class ConfigAction extends AnAction {
    public ConfigAction() {
        super(MessagesUtil.getI18nMessage("config"), null, AllIcons.General.Settings);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        //打开设置
        ShowSettingsUtil.getInstance().showSettingsDialog(e.getProject(), JcePluginConfigurable.class);
    }
}
