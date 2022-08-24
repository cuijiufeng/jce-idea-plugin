package cn.easyjce.plugin.actions.generate;

import cn.easyjce.plugin.intellij.AbstractGenerateCodeDialogWrapper;
import cn.easyjce.plugin.service.JceSpec;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        new MessageDigestGenerateGenerateCodeDialogWrapper(event.getProject(), (PsiJavaFile) psiFile, event.getData(CommonDataKeys.EDITOR))
                .showAndGet();
    }

    static class MessageDigestGenerateGenerateCodeDialogWrapper extends AbstractGenerateCodeDialogWrapper {

        public MessageDigestGenerateGenerateCodeDialogWrapper(Project project, PsiJavaFile psiJavaFile, Editor editor) {
            super(JceSpec.MessageDigest, project, psiJavaFile, editor);
        }

        @Override
        protected @Nullable ValidationInfo doValidate() {
            return super.doValidate();
        }
    }
}
