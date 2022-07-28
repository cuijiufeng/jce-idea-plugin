package cn.easyjce.plugin.beans;

import org.jetbrains.annotations.NotNull;

import java.security.Provider;

/**
 * @Class: ProviderCombo
 * @Date: 2022/7/28 14:42
 * @author: cuijiufeng
 */
public class ProviderCombo implements Comparable<ProviderCombo> {
    private final String name;
    private final Provider provider;

    public ProviderCombo(Provider provider) {
        this.name = provider.getName();
        this.provider = provider;
    }

    public String getName() {
        return name;
    }

    public Provider getProvider() {
        return provider;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(@NotNull ProviderCombo o) {
        return name.compareTo(o.name);
    }
}
