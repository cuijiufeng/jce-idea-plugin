package cn.easyjce.plugin.configurable;

import com.intellij.util.xmlb.Converter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author cuijiufeng
 * @date 2022/8/14 12:45
 * @desc
 */
public class RbValueConverter extends Converter<JcePluginState.RbValueEnum> {
    @Nullable
    @Override
    public JcePluginState.RbValueEnum fromString(@NotNull String value) {
        return JcePluginState.RbValueEnum.valueOf(value);
    }

    @Override
    public @Nullable String toString(JcePluginState.@NotNull RbValueEnum value) {
        return value.name();
    }
}
