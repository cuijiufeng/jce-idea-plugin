package cn.easyjce.plugin.utils;

import cn.easyjce.plugin.service.JceSpec;
import org.junit.Test;

import java.util.Arrays;

public class ReflectionUtilTest {

    @Test
    public void addEnum() throws Exception {
        ReflectionUtil.addEnum(JceSpec.class, "X509Store");
        System.out.println(Arrays.toString(JceSpec.values()));
    }
}