package cn.easyjce.plugin.service.impl;

import cn.easyjce.plugin.ParamNullPointException;
import cn.easyjce.plugin.service.JceSpec;
import cn.easyjce.plugin.utils.LogUtil;
import cn.easyjce.plugin.utils.NotificationsUtil;
import com.intellij.notification.NotificationType;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

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

    public String execute(String type, String algorithm, Provider provider, String input, Map<String, String> paramsMap) {
        try {
            Map<String, byte[]> outputMap = JceSpec.valueOf(type).executeInternal(algorithm, provider, Hex.decodeHex(input), paramsMap);
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, byte[]> entry : outputMap.entrySet()) {
                sb.append(entry.getKey());
                sb.append(":");
                sb.append(Hex.encodeHexString(entry.getValue()));
                sb.append('\n');
            }
            return sb.toString();
        } catch (ParamNullPointException e) {
            NotificationsUtil.showNotice(NotificationType.ERROR, e.getMessage(), e.getName());
            LogUtil.LOG.warn(e.getMessage());
            return null;
        } catch (IllegalArgumentException e) {
            NotificationsUtil.showNotice(NotificationType.ERROR, "unsupported {0} type", type);
            LogUtil.LOG.warn(e.getMessage());
            return null;
        } catch (DecoderException e) {
            NotificationsUtil.showNotice(NotificationType.ERROR, "Illegal input data");
            LogUtil.LOG.warn(e.getMessage());
            return null;
        } catch (GeneralSecurityException e) {
            NotificationsUtil.showNotice(NotificationType.ERROR, "jce error");
            LogUtil.LOG.warn(e.getMessage());
            return null;
        }
    }
}
