package cn.easyjce.plugin.service.impl;

import cn.easyjce.plugin.exception.ParameterIllegalException;
import cn.easyjce.plugin.service.JceSpec;
import cn.easyjce.plugin.utils.LogUtil;
import cn.easyjce.plugin.utils.NotificationsUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.components.ServiceManager;

import java.security.GeneralSecurityException;
import java.security.Provider;
import java.security.Security;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

/**
 * @Class: JceServiceImpl
 * @Date: 2022/7/22 13:49
 * @author: cuijiufeng
 */
public final class JceServiceImpl {

    public Provider[] getProviders() {
        return Security.getProviders();
    }

    public String execute(String type, String algorithm, Provider provider, String input, Map<String, String> paramsMap) {
        CodecServiceImpl service = ServiceManager.getService(CodecServiceImpl.class);
        try {
            Map<String, Object> outputMap = JceSpec.valueOf(type)
                    .executeInternal(algorithm, provider, service.decode(CodecServiceImpl.IO.IN, input), paramsMap);
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, Object> entry : outputMap.entrySet()) {
                sb.append(entry.getKey());
                sb.append(':');
                String value = entry.getValue().toString();
                if (entry.getValue() instanceof byte[]) {
                    value = service.encode(CodecServiceImpl.IO.OUT, (byte[]) entry.getValue());
                }
                sb.append(value);
                sb.append('\n');
            }
            return sb.toString();
        } catch (ParameterIllegalException e) {
            NotificationsUtil.showNotice(NotificationType.ERROR, e.getMessage(), e.getParams());
            LogUtil.LOG.warn(e.getMessage());
        } catch (IllegalArgumentException | UnsupportedOperationException e) {
            NotificationsUtil.showNotice(NotificationType.ERROR, e.getMessage());
        } catch (InvalidKeySpecException e) {
            NotificationsUtil.showNotice(NotificationType.ERROR, "key parsing failed");
            LogUtil.LOG.warn(e.getMessage());
        } catch (SignatureException e) {
            NotificationsUtil.showNotice(NotificationType.ERROR, e.getMessage());
            LogUtil.LOG.warn(e.getMessage());
        } catch (GeneralSecurityException e) {
            NotificationsUtil.showNotice(NotificationType.ERROR, e.getMessage());
            LogUtil.LOG.error(e);
        } catch (Throwable e) {
            NotificationsUtil.showNotice(NotificationType.ERROR, "unknown error");
            LogUtil.LOG.error(e);
        }
        return null;
    }
}
