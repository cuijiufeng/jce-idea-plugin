package cn.easyjce.plugin.service;

import cn.easyjce.plugin.beans.Parameter;
import cn.easyjce.plugin.exception.OperationIllegalException;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Provider;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @Class: IJceSpec
 * @Date: 2022/8/2 14:21
 * @author: cuijiufeng
 */
public interface IJceSpec {

    default List<Parameter> params(String algorithm) {
        return Collections.emptyList();
    }

    default void validateParams(String algorithm, String input, Map<String, String> params) {}

    default List<PsiElement> generateJavaCode(PsiElementFactory factory, String provider, String algorithm, String input, Map<String, String> params) {
        throw new OperationIllegalException("{0} is not supported", ((JceSpec) this).name());
    }

    default Map<String, Object> executeInternal(String algorithm, Provider provider, byte[] inputBytes, Map<String, String> params)
            throws GeneralSecurityException, IOException {
        throw new OperationIllegalException("{0} is not supported", ((JceSpec) this).name());
    }
}
