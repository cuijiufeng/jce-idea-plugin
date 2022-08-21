package cn.easyjce.plugin.actions.generate;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import org.jetbrains.annotations.NotNull;

/**
 * @author cuijiufeng
 * @date 2022/8/21 22:01
 * @desc
 */
public class MessageDigestGenerateAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        //由于Intellij Platform不允许插件在主线程中进行实时的文件写入，只能通过异步任务来完成写入
        WriteCommandAction.runWriteCommandAction(e.getProject(), () -> {
        });
    }
}
