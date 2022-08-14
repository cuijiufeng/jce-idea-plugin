package cn.easyjce.plugin.configurable;

import cn.easyjce.plugin.global.PluginConstants;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.OptionTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @Class: JcePluginConfiguration
 * @Date: 2022/7/22 17:55
 * @author: cuijiufeng
 */
@State(name = "JcePluginSetting", storages = @Storage(PluginConstants.SETTING_FILE))
public class JcePluginState implements PersistentStateComponent<JcePluginState> {
    @OptionTag(converter = RbValueConverter.class)
    private RbValueEnum inputRb = RbValueEnum.hex;
    @OptionTag(converter = RbValueConverter.class)
    private RbValueEnum outputRb = RbValueEnum.hex;

    public static JcePluginState getInstance() {
        return ApplicationManager.getApplication().getService(JcePluginState.class);
    }

    @Nullable
    @Override
    public JcePluginState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull JcePluginState state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public RbValueEnum getInputRb() {
        return inputRb;
    }

    public void setInputRb(RbValueEnum inputRb) {
        this.inputRb = inputRb;
    }

    public RbValueEnum getOutputRb() {
        return outputRb;
    }

    public void setOutputRb(RbValueEnum outputRb) {
        this.outputRb = outputRb;
    }

    public enum RbValueEnum {
        string, hex, base64;
    }
}
