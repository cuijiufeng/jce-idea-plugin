package cn.easyjce.plugin.actions.generate;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiDeclarationStatement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.PsiType;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.testFramework.PsiTestUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author cuijiufeng
 * @date 2022/8/21 22:01
 * @desc
 */
public class MessageDigestGenerateAction extends AnAction {
    @SuppressWarnings("ConstantConditions")
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        // 过滤选择Java文件
        PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);
        if (psiFile == null || !(psiFile.getFileType() instanceof JavaFileType)) {
            return;
        }
        //代码生成工厂
        PsiElementFactory factory = JavaPsiFacade.getInstance(event.getProject()).getElementFactory();
        PsiJavaFile psiJavaFile = (PsiJavaFile) psiFile;
        Editor editor = event.getData(CommonDataKeys.EDITOR);
        PsiElement cursorElement = psiJavaFile.findElementAt(editor.getCaretModel().getOffset());

        //由于Intellij Platform不允许插件在主线程中进行实时的文件写入，只能通过异步任务来完成写入
        WriteCommandAction.runWriteCommandAction(event.getProject(), () -> {
            PsiElement parentElement = cursorElement.getParent();
            //类
            PsiType mdPsiType = factory.createTypeFromText("java.security.MessageDigest", null);

            //生成instance实例
            PsiExpression mdInstance = factory.createExpressionFromText("MessageDigest.getInstance(\"MD5\", \"SUN\")", null);
            PsiDeclarationStatement mdVariable = factory.createVariableDeclarationStatement("md", mdPsiType, mdInstance);
            PsiStatement mdInit = factory.createStatementFromText("md.update(new byte[]{1});", null);
            PsiExpression digestInit = factory.createExpressionFromText("md.digest(new byte[]{2})", null);
            PsiDeclarationStatement digestVariable = factory.createVariableDeclarationStatement("digest", PsiType.BYTE.createArrayType(), digestInit);
            //新增代码
            PsiElement tmp = parentElement.addAfter(mdVariable, cursorElement);
            tmp = parentElement.addAfter(mdInit, tmp);
            parentElement.addAfter(digestVariable, tmp);

            //自动import导入
            JavaCodeStyleManager.getInstance(event.getProject()).shortenClassReferences(parentElement);
            //格式化代码
            CodeStyleManager.getInstance(event.getProject()).reformat(parentElement);
            //检查psi操作代码的正确
            PsiTestUtil.checkFileStructure(psiFile);
        });
    }
}
