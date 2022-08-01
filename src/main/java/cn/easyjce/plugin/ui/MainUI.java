package cn.easyjce.plugin.ui;

import cn.easyjce.plugin.beans.AlgorithmCombo;
import cn.easyjce.plugin.beans.Parameter;
import cn.easyjce.plugin.beans.ProviderCombo;
import cn.easyjce.plugin.beans.TypeCombo;
import cn.easyjce.plugin.event.EventPublisher;
import cn.easyjce.plugin.event.ParameterUIEvent;
import cn.easyjce.plugin.service.JceSpec;
import cn.easyjce.plugin.service.impl.JceServiceImpl;
import cn.easyjce.plugin.utils.NotificationsUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.util.ui.FormBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.security.Provider;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private JTextArea output;
    private JSplitPane splitter;
    private JPanel params;
    private List<Parameter> paramsList = Collections.emptyList();

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
        this.clear();
    }

    private void clear() {
        input.setText(null);
        output.setText(null);
        paramsList.forEach(Parameter::clear);
    }

    public void reloadProviderSelect(Provider[] providers) {
        providerSelect.removeAllItems();
        typeSelect.removeAllItems();
        algorithmSelect.removeAllItems();
        Arrays.stream(providers)
                .map(ProviderCombo::new)
                .sorted()
                .forEach(providerSelect::addItem);
    }
    public void reloadTypeSelect(Provider provider) {
        typeSelect.removeAllItems();
        algorithmSelect.removeAllItems();
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
                reloadTypeSelect(item.getProvider());
            }
        });
        typeSelect.addItemListener(event -> {
            if (ItemEvent.SELECTED == event.getStateChange()) {
                TypeCombo item = (TypeCombo) event.getItem();
                reloadAlgorithmSelect(item.getProvider(), item.getType());
                reviewParameterUI(this.paramsList = JceSpec.valueOf(item.getType()).params());
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
                clear();
            }
        });
        compute.addMouseListener(new MouseAdapter() {
            @SuppressWarnings("all")
            @Override
            public void mouseClicked(MouseEvent e) {
                ProviderCombo providerSelected = (ProviderCombo) providerSelect.getSelectedItem();
                TypeCombo typeSelected = (TypeCombo) typeSelect.getSelectedItem();
                AlgorithmCombo algoSelected = (AlgorithmCombo) algorithmSelect.getSelectedItem();
                //获取参数
                Map<String, String> paramsMap = paramsList.stream().collect(Collectors.toMap(Parameter::getKey, Parameter::getValue));
                JceServiceImpl service = ServiceManager.getService(JceServiceImpl.class);
                try {
                    output.setText(service.execute(typeSelected.getType(), algoSelected.getAlgorithm(), providerSelected.getProvider(), input.getText(), paramsMap));
                } catch (NullPointerException ex) {
                    NotificationsUtil.showNotice(NotificationType.ERROR, "please select the correct type and algorithm");
                }
            }
        });
        //当选择不同参数，整个参数UI重新绘制
        EventPublisher service = ServiceManager.getService(EventPublisher.class);
        service.addEventListener(ParameterUIEvent.class, event -> {
            paramsList.forEach(Parameter::toggleUI);
            reviewParameterUI(this.paramsList);
        });
    }

    private void reviewParameterUI(List<Parameter> paramsList) {
        this.clear();
        params.removeAll();
        FormBuilder formBuilder = FormBuilder.createFormBuilder();
        for (Parameter parameter : paramsList) {
            if (parameter.isHide()) {
                continue;
            }
            JPanel jPanel = new JPanel(new GridLayout());
            parameter.getComponent().forEach(jPanel::add);
            formBuilder.addLabeledComponent(parameter.getLabelComponent(), jPanel);
        }
        params.add(formBuilder.getPanel(), BorderLayout.CENTER);
        //刷新JPanel，否则显示非常缓慢
        params.updateUI();
    }
}
