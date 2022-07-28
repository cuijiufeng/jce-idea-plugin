package cn.easyjce.plugin.beans;

import org.jetbrains.annotations.NotNull;

import java.security.Provider;
import java.util.Objects;

/**
 * @Class: AlgorithmCombo
 * @Date: 2022/7/28 14:44
 * @author: cuijiufeng
 */
public class AlgorithmCombo implements Comparable<AlgorithmCombo> {
    private final Provider provider;
    private final String algorithm;

    public AlgorithmCombo(Provider.Service service) {
        this.provider = service.getProvider();
        this.algorithm = service.getAlgorithm();
    }

    public Provider getProvider() {
        return provider;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    @Override
    public String toString() {
        return algorithm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(algorithm, ((AlgorithmCombo) o).algorithm);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(algorithm);
    }

    @Override
    public int compareTo(@NotNull AlgorithmCombo o) {
        return algorithm.compareTo(o.algorithm);
    }
}
