package cn.easyjce.plugin.actions.generate;

import cn.easyjce.plugin.intellij.AbstractGenerateCodeDialogWrapper;
import cn.easyjce.plugin.service.JceSpec;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import org.jetbrains.annotations.NotNull;

/**
 * @author cuijiufeng
 * @date 2022/8/21 22:01
 * @desc
 */
public class MessageDigestGenerateAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        // 过滤选择Java文件
        PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);
        if (psiFile == null || !(psiFile.getFileType() instanceof JavaFileType)) {
            return;
        }
        new AbstractGenerateCodeDialogWrapper(JceSpec.MessageDigest, event.getProject(), (PsiJavaFile) psiFile, event.getData(CommonDataKeys.EDITOR)){
        }.showAndGet();
    }
}
