package cn.easyjce.plugin.utils;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;

/**
 * @Class: PsiUtil
 * @Date: 2022/8/24 18:02
 * @author: cuijiufeng
 */
public class PsiElementUtil {

    public static PsiElement genHexDecode(PsiElementFactory factory, String paramName) {
        //类型
        return factory.createExpressionFromText("Hex.decodeHex(" + paramName + ")", null);
    }
}
