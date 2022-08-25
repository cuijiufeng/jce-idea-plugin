package cn.easyjce.plugin.utils;

import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiMethodCallExpression;

import java.util.Objects;

/**
 * @Class: PsiUtil
 * @Date: 2022/8/24 18:02
 * @author: cuijiufeng
 */
public class PsiElementUtil {

    public static PsiMethodCallExpression genHexDecode(PsiElementFactory factory, String paramName) {
        //类型
        return (PsiMethodCallExpression) factory.createExpressionFromText("org.apache.commons.codec.binary.Hex.decodeHex(" + paramName + ")", null);
    }

    public static void replaceMethodCallArgumentList(PsiMethodCallExpression methodCallExpression, PsiExpression ... args) {
        PsiExpression[] expressions = methodCallExpression.getArgumentList().getExpressions();
        if (Objects.isNull(args) || expressions.length != args.length) {
            throw new IllegalArgumentException("wrong number of parameters");
        }
        for (int i = 0; i < expressions.length; i++) {
            expressions[i].replace(args[i]);
        }
    }
}
