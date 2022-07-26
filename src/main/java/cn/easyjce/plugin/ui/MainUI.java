package cn.easyjce.plugin.ui;

import cn.easyjce.plugin.service.impl.JceServiceImpl;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.ui.JBSplitter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.security.Provider;
import java.util.Arrays;
import java.util.Objects;

/**
 * @Class: MainUI
 * @Date: 2022/7/26 9:39
 * @author: cuijiufeng
 */
public class MainUI {
    private static final MainUI SINGLETON = new MainUI();
    private JPanel mainPanel;
    private JComboBox<ProviderCombo> providerSelect;
    private JComboBox<TypeCombo> typeSelect;
    private JComboBox<AlgorithmCombo> algorithmSelect;
    private JButton compute;
    private JButton clear;
    private JTextArea input;
    private JLabel output;
    private JSplitPane splitter;

    private MainUI() {
        initView();
        initEvent();
        reloadProviderSelect(ServiceManager.getService(JceServiceImpl.class).getProviders());
    }

    public static MainUI getInstance() {
        return SINGLETON;
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void reset() {
        reloadProviderSelect(ServiceManager.getService(JceServiceImpl.class).getProviders());
        input.setText("");
        output.setText("");
    }

    public void reloadProviderSelect(Provider[] providers) {
        providerSelect.removeAllItems();
        Arrays.stream(providers)
                .map(ProviderCombo::new)
                .sorted()
                .forEach(providerSelect::addItem);
    }
    public void reloadTypeSelect(Provider provider) {
        typeSelect.removeAllItems();
        provider.getServices().stream()
                .map(TypeCombo::new)
                .distinct()
                .sorted()
                .forEach(typeSelect::addItem);
    }
    public void reloadAlgorithmSelect(Provider provider, String type) {
        algorithmSelect.removeAllItems();
        provider.getServices().stream()
                .filter(s -> s.getType().equals(type))
                .map(AlgorithmCombo::new)
                .sorted()
                .forEach(algorithmSelect::addItem);
    }

    private void initView() {
    }

    private void initEvent() {
        providerSelect.addItemListener(event -> {
            if (ItemEvent.SELECTED == event.getStateChange()) {
                ProviderCombo item = (ProviderCombo) event.getItem();
                reloadTypeSelect(item.provider);
            }
        });
        typeSelect.addItemListener(event -> {
            if (ItemEvent.SELECTED == event.getStateChange()) {
                TypeCombo item = (TypeCombo) event.getItem();
                reloadAlgorithmSelect(item.provider, item.type);
            }
        });
        algorithmSelect.addItemListener(event -> {
            if (ItemEvent.SELECTED == event.getStateChange()) {
                //ignore
            }
        });
        clear.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                input.setText("");
                output.setText("");
            }
        });
        compute.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                input.setText("input");
                output.setText("output");
            }
        });
    }

    static class ProviderCombo implements Comparable<ProviderCombo> {
        private final String name;
        private final Provider provider;
        public ProviderCombo(Provider provider) {
            this.name = provider.getName();
            this.provider = provider;
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

    static class TypeCombo implements Comparable<TypeCombo> {
        private final Provider provider;
        private final String type;
        public TypeCombo(Provider.Service service) {
            this.provider = service.getProvider();
            this.type = service.getType();
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

    static class AlgorithmCombo implements Comparable<AlgorithmCombo> {
        private final Provider provider;
        private final String algorithm;
        public AlgorithmCombo(Provider.Service service) {
            this.provider = service.getProvider();
            this.algorithm = service.getAlgorithm();
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
}
