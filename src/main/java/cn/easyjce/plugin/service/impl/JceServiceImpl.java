package cn.easyjce.plugin.service.impl;

import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Class: JceServiceImpl
 * @Date: 2022/7/22 13:49
 * @author: cuijiufeng
 */
public final class JceServiceImpl {
    private Provider[] providers = Security.getProviders();

    public JceServiceImpl() {
        reloadProviders();
    }

    public void reloadProviders() {
        providers = Security.getProviders();
    }

    public Provider[] getProviders() {
        return providers;
    }
}
