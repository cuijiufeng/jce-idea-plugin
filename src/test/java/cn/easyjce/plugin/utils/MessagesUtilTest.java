package cn.easyjce.plugin.utils;

import org.junit.Test;

public class MessagesUtilTest {

    @Test
    public void getI18nMessage() {
        System.out.println(MessagesUtil.getI18nMessage("unsupported {0} type", "a"));
    }
}