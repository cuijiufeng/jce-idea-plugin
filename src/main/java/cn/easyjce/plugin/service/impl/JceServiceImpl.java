package cn.easyjce.plugin.service.impl;

import cn.easyjce.plugin.exception.JceUnsupportedOperationException;
import cn.easyjce.plugin.exception.ParameterIllegalException;
import cn.easyjce.plugin.service.JceSpec;
import cn.easyjce.plugin.utils.LogUtil;
import cn.easyjce.plugin.utils.NotificationsUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.components.ServiceManager;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.GeneralSecurityException;
import java.security.Provider;
import java.security.Security;
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

    public String execute(String type, String algorithm, Provider provider, String input, Map<String, ?> paramsMap) {
        CodecServiceImpl service = ServiceManager.getService(CodecServiceImpl.class);
        JceSpec jceSpec = JceSpec.valueOf(type);
        try {
            Map<String, Object> outputMap = jceSpec.executeInternal(algorithm, provider, service.decode(CodecServiceImpl.IO.IN, input), paramsMap);
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
        } catch (JceUnsupportedOperationException e) {
            NotificationsUtil.showNotice(NotificationType.ERROR, e.getMessage());
        } catch (IllegalArgumentException | IOException | GeneralSecurityException e) {
            NotificationsUtil.showNotice(NotificationType.ERROR, e.getMessage());
            LogUtil.LOG.error(e);
        } catch (Throwable e) {
            NotificationsUtil.showNotice(NotificationType.ERROR, "unknown error");
            LogUtil.LOG.error(e);
        }
        return null;
    }

    public void loadProviderJar(String path, String provider) {
        if (StringUtils.isBlank(path)) {
            return;
        }
        try {
            URLClassLoader classLoader = new URLClassLoader(new URL[]{ new File(path).toURI().toURL() });
            classLoader.loadClass("");
        } catch (MalformedURLException | ClassNotFoundException e) {
            NotificationsUtil.showNotice(NotificationType.ERROR, "provider load failed");
            LogUtil.LOG.warn(e);
        }
    }
}
