package cn.easyjce.plugin.beans;

import org.jetbrains.annotations.NotNull;

import java.security.Provider;
import java.util.Objects;

/**
 * @Class: TypeCombo
 * @Date: 2022/7/28 14:43
 * @author: cuijiufeng
 */
public class TypeCombo implements Comparable<TypeCombo> {
    private final Provider provider;
    private final String type;

    public TypeCombo(Provider.Service service) {
        this.provider = service.getProvider();
        this.type = service.getType();
    }

    public Provider getProvider() {
        return provider;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(type, ((TypeCombo) o).type);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(type);
    }

    @Override
    public int compareTo(@NotNull TypeCombo o) {
        return type.compareTo(o.type);
    }
}
