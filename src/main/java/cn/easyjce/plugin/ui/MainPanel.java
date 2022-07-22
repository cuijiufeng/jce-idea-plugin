package cn.easyjce.plugin.ui;

import cn.easyjce.plugin.service.impl.JceServiceImpl;
import cn.easyjce.plugin.swt.GBC;
import cn.easyjce.plugin.utils.MessagesUtil;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextArea;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.security.Provider;
import java.util.Arrays;
import java.util.Objects;

/**
 * @Class: MainPanel
 * @Date: 2022/7/21 16:58
 * @author: cuijiufeng
 */
public class MainPanel extends JPanel {
    private final ComboBox<ProviderCombo> providerSelect;
    private final ComboBox<TypeCombo> typeSelect;
    private final ComboBox<AlgorithmCombo> algorithmSelect;

    public MainPanel(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        super(new BorderLayout());

        JceServiceImpl jceService = ServiceManager.getService(JceServiceImpl.class);
        ProviderCombo[] providers = Arrays.stream(jceService.getProviders())
                .map(ProviderCombo::new)
                .sorted()
                .toArray(ProviderCombo[]::new);
        providerSelect = new ComboBox<>(providers);
        TypeCombo[] types = providers[0].provider.getServices().stream()
                .map(TypeCombo::new)
                .distinct()
                .sorted()
                .toArray(TypeCombo[]::new);
        typeSelect = new ComboBox<>(types);
        AlgorithmCombo[] algorithms = providers[0].provider.getServices().stream()
                .filter(s -> s.getType().equals(types[0].type))
                .map(AlgorithmCombo::new)
                .sorted()
                .toArray(AlgorithmCombo[]::new);
        algorithmSelect = new ComboBox<>(algorithms);

        initView();
        initEvent();
    }

    private void initView() {
        JPanel top = new JPanel(new GridBagLayout());

        JPanel provider = new JPanel(new BorderLayout());
        provider.add(new JBLabel(MessagesUtil.getI18nMessage("algorithm provider") + ":"), BorderLayout.WEST);
        provider.add(providerSelect, BorderLayout.CENTER);
        JButton compute = new JButton(MessagesUtil.getI18nMessage("compute"));
        provider.add(compute, BorderLayout.EAST);
        top.add(provider, new GBC(0, 0, 1, 2));

        //分割线
        top.add(new JSeparator(), new GBC(0, 2, 1, 1));

        JPanel type = new JPanel(new BorderLayout());
        type.add(new JBLabel(MessagesUtil.getI18nMessage("type") + ":"), BorderLayout.WEST);
        type.add(typeSelect, BorderLayout.CENTER);
        top.add(type, new GBC(0, 3, 1, 2));

        //分割线
        top.add(new JSeparator(), new GBC(0, 5, 1, 1));

        JPanel algorithm = new JPanel(new BorderLayout());
        algorithm.add(new JBLabel(MessagesUtil.getI18nMessage("algorithm") + ":"), BorderLayout.WEST);
        algorithm.add(algorithmSelect, BorderLayout.CENTER);
        top.add(algorithm, new GBC(0, 6, 1, 2));

        this.add(top, BorderLayout.NORTH);

        JBSplitter jbSplitter = new JBSplitter(true);
        //jbSplitter.setBorder(JBUI.Borders.empty(30));
        JBTextArea input = new JBTextArea("First");
        jbSplitter.setFirstComponent(input);
        JBLabel output = new JBLabel("Second");
        jbSplitter.setSecondComponent(output);
        this.add(jbSplitter, BorderLayout.CENTER);
    }

    private void initEvent() {
        providerSelect.addItemListener(event -> {
            if (ItemEvent.SELECTED == event.getStateChange()) {
                typeSelect.removeAllItems();
                ProviderCombo item = (ProviderCombo) event.getItem();
                item.provider.getServices().stream()
                        .map(TypeCombo::new)
                        .distinct()
                        .sorted()
                        .forEach(typeSelect::addItem);
            }
        });
        typeSelect.addItemListener(event -> {
            if (ItemEvent.SELECTED == event.getStateChange()) {
                algorithmSelect.removeAllItems();
                TypeCombo item = (TypeCombo) event.getItem();
                item.provider.getServices().stream()
                        .filter(s -> s.getType().equals(item.type))
                        .map(AlgorithmCombo::new)
                        .sorted()
                        .forEach(algorithmSelect::addItem);
            }
        });
        algorithmSelect.addItemListener(event -> {
            if (ItemEvent.SELECTED == event.getStateChange()) {
                //ignore
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
        public int compareTo(@NotNull MainPanel.ProviderCombo o) {
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
        public int compareTo(@NotNull MainPanel.TypeCombo o) {
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
        public int compareTo(@NotNull MainPanel.AlgorithmCombo o) {
            return algorithm.compareTo(o.algorithm);
        }
    }
}
