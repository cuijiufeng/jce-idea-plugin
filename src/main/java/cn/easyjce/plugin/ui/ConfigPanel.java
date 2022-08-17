package cn.easyjce.plugin.ui;

import cn.easyjce.plugin.configurable.JcePluginState;
import cn.easyjce.plugin.service.impl.JceServiceImpl;
import cn.easyjce.plugin.utils.FileChooserUtil;
import cn.easyjce.plugin.utils.MessagesUtil;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Class: ConfigurationPanel
 * @Date: 2022/7/26 10:56
 * @author: cuijiufeng
 */
public class ConfigPanel {
    public static final int HISTORY_LIST_MAX_COUNT = 16;
    private final JPanel jPanel;
    private final List<JRadioButton> inputComponents = Arrays.asList(
            new JRadioButton(JcePluginState.RbValueEnum.string.name()),
            new JRadioButton(JcePluginState.RbValueEnum.hex.name()),
            new JRadioButton(JcePluginState.RbValueEnum.base64.name()));
    private final List<JRadioButton> outputComponents = Arrays.asList(
            new JRadioButton(JcePluginState.RbValueEnum.string.name()),
            new JRadioButton(JcePluginState.RbValueEnum.hex.name()),
            new JRadioButton(JcePluginState.RbValueEnum.base64.name()));
    private final JBList<String> addHistoryComponents = new JBList<>();

    public ConfigPanel() {
        ButtonGroup inputBg = new ButtonGroup();
        this.inputComponents.forEach(inputBg::add);
        ButtonGroup outputBg = new ButtonGroup();
        this.outputComponents.forEach(outputBg::add);
        JPanel systemConfigPanel = new ConfigUI(MessagesUtil.getI18nMessage("system"))
                .addLineComponent(MessagesUtil.getI18nMessage("input code") + ":", inputComponents.toArray(new JComponent[0]))
                .addLineComponent(MessagesUtil.getI18nMessage("output code") + ":", outputComponents.toArray(new JComponent[0]))
                .getConfigPanel();
        this.addHistoryComponents.setToolTipText("double click to fill in");
        JBTextField providerTf = new JBTextField();
        providerTf.setToolTipText(MessagesUtil.getI18nMessage("fill in the fully qualified name of the provider"));
        JBTextField pathTf = new JBTextField();
        pathTf.setEnabled(false);
        JButton fileBtn = new JButton(MessagesUtil.getI18nMessage("choose file"));
        JButton addProvider = new JButton(MessagesUtil.getI18nMessage("add"));
        JPanel extendConfigPanel = new ConfigUI(MessagesUtil.getI18nMessage("extend"))
                .addLineComponent("provider:", providerTf)
                .addLineComponent(null, pathTf, fileBtn)
                .addLineComponent(null, addProvider)
                .addLineComponent(null, new JBLabel(MessagesUtil.getI18nMessage("add history") + ":"))
                .addLineComponent(null, new JBScrollPane(addHistoryComponents))
                .getConfigPanel();
        this.jPanel = FormBuilder.createFormBuilder()
                .addComponent(systemConfigPanel)
                .addComponent(extendConfigPanel)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
        initEvent(fileBtn, addProvider, providerTf, pathTf);
    }

    private void initEvent(JButton fileBtn, JButton addProvider, JBTextField providerTf, JBTextField pathTf) {
        fileBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String jarFilePath = FileChooserUtil.pathFilechooser(
                        new FileChooserUtil.ChooserDescBuilder().setChooseFiles(true).setChooseJars(true).build(),
                        null,
                        null);
                pathTf.setText(jarFilePath);
            }
        });
        addProvider.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                List<String> list = getAddHistoryConfigValue();
                String element = providerTf.getText() + "=" + pathTf.getText();
                if (!list.contains(element)) {
                    list.add(0, element);
                    setAddHistoryConfigValue(list);
                }
                ServiceManager.getService(JceServiceImpl.class).loadProviderJar(pathTf.getText(), providerTf.getText());
                providerTf.setText(null);
                pathTf.setText(null);
            }
        });
        this.addHistoryComponents.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    @SuppressWarnings("unchecked")
                    String selectedValue = ((JList<String>) e.getSource()).getSelectedValue();
                    providerTf.setText(selectedValue.split("=")[0]);
                    pathTf.setText(selectedValue.split("=")[1]);
                }
            }
        });
    }

    public JPanel getPanel() {
        return this.jPanel;
    }

    public boolean isModified() {
        boolean intputRbEquals = getInputConfigValue().equals(JcePluginState.getInstance().getInputRb());
        boolean outtputRbEquals = getOutputConfigValue().equals(JcePluginState.getInstance().getOutputRb());
        boolean historysEquals = getAddHistoryConfigValue().equals(JcePluginState.getInstance().getHistorys());
        return !(intputRbEquals && outtputRbEquals && historysEquals);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public JcePluginState.RbValueEnum getInputConfigValue() {
        return this.inputComponents.stream()
                .filter(JRadioButton::isSelected)
                .map(JRadioButton::getText)
                .map(JcePluginState.RbValueEnum::valueOf)
                .findFirst()
                .get();
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public JcePluginState.RbValueEnum getOutputConfigValue() {
        return this.outputComponents.stream()
                .filter(JRadioButton::isSelected)
                .map(JRadioButton::getText)
                .map(JcePluginState.RbValueEnum::valueOf)
                .findFirst()
                .get();
    }

    public List<String> getAddHistoryConfigValue() {
        List<String> result = new ArrayList<>(HISTORY_LIST_MAX_COUNT);
        for (int i = 0; i < this.addHistoryComponents.getModel().getSize(); i++) {
            result.add(this.addHistoryComponents.getModel().getElementAt(i));
        }
        return result;
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void setInputConfigValue(JcePluginState.RbValueEnum value) {
        this.inputComponents.stream()
                .filter(rb -> rb.getText().equals(value.name()))
                .findFirst()
                .get()
                .setSelected(true);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void setOutputConfigValue(JcePluginState.RbValueEnum value) {
        this.outputComponents.stream()
                .filter(rb -> rb.getText().equals(value.name()))
                .findFirst()
                .get()
                .setSelected(true);
    }

    public void setAddHistoryConfigValue(List<String> historys) {
        String[] listData = historys.toArray(new String[0]);
        if (historys.size() > HISTORY_LIST_MAX_COUNT) {
            listData = Arrays.copyOfRange(listData, 0, HISTORY_LIST_MAX_COUNT);
        }
        this.addHistoryComponents.setListData(listData);
    }
}
