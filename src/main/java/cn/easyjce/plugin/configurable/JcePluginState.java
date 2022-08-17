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

import java.util.Collections;
import java.util.List;

/**
 * @Class: JcePluginConfiguration
 * @Date: 2022/7/22 17:55
 * @author: cuijiufeng
 */
@State(name = "JcePluginSetting", storages = @Storage(PluginConstants.SETTING_FILE))
public class JcePluginState implements PersistentStateComponent<JcePluginState> {
    //设置配置初始默认值
    private boolean welcomeNoti = true;
    @OptionTag(converter = RbValueConverter.class)
    private RbValueEnum inputRb = RbValueEnum.hex;
    @OptionTag(converter = RbValueConverter.class)
    private RbValueEnum outputRb = RbValueEnum.hex;
    private List<String> historys = Collections.emptyList();

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

    public List<String> getHistorys() {
        return historys;
    }

    public void setHistorys(List<String> historys) {
        this.historys = historys;
    }

    public Boolean isWelcomeNoti() {
        return welcomeNoti;
    }

    public void setWelcomeNoti(Boolean welcomeNoti) {
        this.welcomeNoti = welcomeNoti;
    }

    public enum RbValueEnum {
        string, hex, base64;
    }
}
