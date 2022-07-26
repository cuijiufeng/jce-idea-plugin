package cn.easyjce.plugin.global;

import cn.easyjce.plugin.utils.MessagesUtil;

/**
 * @Class: PluginConstants
 * @Date: 2022/7/20 17:54
 * @author: cuijiufeng
 */
public interface PluginConstants {
    /**
     *
     */
    String GITHUB_ADDR = "https://github.com/cuijiufeng";

    /**
     * 通知分组
     */
    String NOTIFICATION_GROUP = "jce.notify";

    /**
     * toolbar id
     */
    String TOOL_BAR_ID = "jce.toolbar";

    /**
     * setting file
     */
    String SETTING_FILE = "jce.persistent";

    /**
     * config name
     */
    String CONFIG_NAME = "JCE Plugin";

    interface CacheKey {
        String CONFIG_INPUT_RB_HEX = "config.input.radio.button.hex";
        String CONFIG_INPUT_RB_BASE64 = "config.input.radio.button.base64";
        String CONFIG_OUTPUT_RB_HEX = "config.output.radio.button.hex";
        String CONFIG_OUTPUT_RB_BASE64 = "config.output.radio.button.base64";
    }
}
