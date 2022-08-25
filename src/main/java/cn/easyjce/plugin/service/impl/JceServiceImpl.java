package cn.easyjce.plugin.service.impl;

import cn.easyjce.plugin.exception.OperationIllegalException;
import cn.easyjce.plugin.exception.ParameterIllegalException;
import cn.easyjce.plugin.service.JceSpec;
import cn.easyjce.plugin.ui.MainUI;
import cn.easyjce.plugin.utils.LogUtil;
import cn.easyjce.plugin.utils.MessagesUtil;
import cn.easyjce.plugin.utils.NotificationsUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.ui.Messages;
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
import java.util.Objects;

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
            JceSpec jceSpec = JceSpec.specValueOf(type, new OperationIllegalException("{0} is not supported", type));
            jceSpec.validateParams(algorithm, input, paramsMap);
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
        } catch (ParameterIllegalException | OperationIllegalException e) {
            NotificationsUtil.showNotice(NotificationType.ERROR, e.getMessage(), e.getMsgParams());
        } catch (IllegalArgumentException | IOException | GeneralSecurityException e) {
            NotificationsUtil.showNotice(NotificationType.ERROR, e.getMessage());
            LogUtil.LOG.error(e);
        } catch (Throwable e) {
            NotificationsUtil.showNotice(NotificationType.ERROR, "unknown error");
            LogUtil.LOG.error(e);
        }
        return null;
    }

    public boolean loadProviderJar(String path, String provider) {
        if (StringUtils.isBlank(path) || StringUtils.isBlank(provider)) {
            Messages.showWarningDialog(MessagesUtil.getI18nMessage("select the JCE jar package and fill in the provider class name"), "Tip");
            return false;
        }
        try {
            URLClassLoader classLoader = new URLClassLoader(new URL[]{ new File(path).toURI().toURL() });
            @SuppressWarnings("unchecked")
            Class<Provider> providerClass = (Class<Provider>) classLoader.loadClass(provider);
            //动态加载provider
            Provider providerInstance = providerClass.newInstance();
            if (Objects.nonNull(Security.getProvider(providerInstance.getName()))) {
                Messages.showInfoMessage(MessagesUtil.getI18nMessage("load provider repeatedly"), "Tip");
                return false;
            }
            Security.addProvider(providerInstance);
            MainUI.getInstance().reloadProviderSelect(getProviders());
            Messages.showInfoMessage(MessagesUtil.getI18nMessage("success"), "Tip");
            return true;
        } catch (MalformedURLException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            LogUtil.LOG.warn(e.getMessage());
            //DialogWrapper使用。MessageDialog extends DialogWrapper
            Messages.showErrorDialog(MessagesUtil.getI18nMessage("provider load failed"), "Tip");
        }  catch (Throwable e) {
            LogUtil.LOG.error(e);
            Messages.showErrorDialog(MessagesUtil.getI18nMessage("unknown error"), "Tip");
        }
        return false;
    }
}
