package cn.easyjce.plugin.intellij;

import cn.easyjce.plugin.awt.GBC;
import cn.easyjce.plugin.beans.AlgorithmCombo;
import cn.easyjce.plugin.beans.Parameter;
import cn.easyjce.plugin.beans.ProviderCombo;
import cn.easyjce.plugin.exception.ParameterIllegalException;
import cn.easyjce.plugin.service.JceSpec;
import cn.easyjce.plugin.service.impl.JceServiceImpl;
import cn.easyjce.plugin.utils.MessagesUtil;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.testFramework.PsiTestUtil;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.security.Provider;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Class: JcePluginAbstractDialogWrapper
 * @Date: 2022/8/24 10:45
 * @author: cuijiufeng
 */
public abstract class AbstractGenerateCodeDialogWrapper extends DialogWrapper {
    protected final JceSpec JCESPEC;
    protected final Project project;
    protected final PsiJavaFile psiJavaFile;
    protected final Editor editor;

    private List<Parameter> paramsList = Collections.emptyList();
    private final JPanel params = new JPanel(new BorderLayout());
    private final ComboBox<ProviderCombo> providerSelect = new ComboBox<>();
    private final ComboBox<AlgorithmCombo> algorithmSelect = new ComboBox<>();
    private final JBTextArea inputJText = new JBTextArea(5, 80);

    protected AbstractGenerateCodeDialogWrapper(JceSpec jceSpec, Project project, PsiJavaFile psiJavaFile, Editor editor) {
        super(project, true);
        super.setTitle(jceSpec.name());
        super.init();
        this.JCESPEC = jceSpec;
        this.project = project;
        this.psiJavaFile = psiJavaFile;
        this.editor = editor;

        this.inputJText.setLineWrap(true);
        this.inputJText.setWrapStyleWord(true);

        this.initEvent();
        this.reloadProviderSelect(ServiceManager.getService(JceServiceImpl.class).getProviders());
    }

    protected void initEvent() {
        providerSelect.addItemListener(event -> {
            if (ItemEvent.SELECTED == event.getStateChange()) {
                ProviderCombo item = (ProviderCombo) event.getItem();
                reloadAlgorithmSelect(item.getProvider(), JCESPEC.name());
            }
        });
        algorithmSelect.addItemListener(event -> {
            if (ItemEvent.SELECTED == event.getStateChange()) {
                AlgorithmCombo item = (AlgorithmCombo) event.getItem();
                reviewParameterUI(this.paramsList = JCESPEC.params(item.getAlgorithm()));
            }
        });
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel jPanel = new JPanel(new GridBagLayout());
        jPanel.add(new JBLabel("provider:"), new GBC(0, 0)
                        .setWeight(0, 0, () -> true)
                        .setFill(GridBagConstraints.NONE, () -> true));
        jPanel.add(providerSelect, new GBC(1, 0));
        jPanel.add(new JBLabel("algorithm:"), new GBC(0, 1)
                        .setWeight(0, 0, () -> true)
                        .setFill(GridBagConstraints.NONE, () -> true));
        jPanel.add(algorithmSelect, new GBC(1, 1));
        jPanel.add(this.params, new GBC(0, 2, 2, 1));
        jPanel.add(new JBLabel("input:"), new GBC(0, 3, 2, 1));
        jPanel.add(new JBScrollPane(this.inputJText), new GBC(0, 4, 2, 1));
        return jPanel;
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {
        Map<String, String> paramsMap = paramsList.stream().collect(Collectors.toMap(Parameter::getKey, Parameter::getValue));
        AlgorithmCombo algorithmCombo = (AlgorithmCombo) algorithmSelect.getSelectedItem();
        try {
            JCESPEC.validateParams(algorithmCombo.getAlgorithm(), inputJText.getText(), paramsMap);
        } catch (ParameterIllegalException e) {
            return new ValidationInfo(MessagesUtil.getI18nMessage(e.getMessage(), e.getMsgParams()), null);
        }
        return null;
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
        //由于Intellij Platform不允许插件在主线程中进行实时的文件写入，只能通过异步任务来完成写入
        WriteCommandAction.runWriteCommandAction(this.project, () -> {
            PsiElement cursorElement = this.psiJavaFile.findElementAt(this.editor.getCaretModel().getOffset());

            ProviderCombo providerSelected = (ProviderCombo) providerSelect.getSelectedItem();
            AlgorithmCombo algorithmCombo = (AlgorithmCombo) algorithmSelect.getSelectedItem();
            Map<String, String> paramsMap = paramsList.stream().collect(Collectors.toMap(Parameter::getKey, Parameter::getValue));
            //参数1.代码生成工厂
            PsiElementFactory factory = JavaPsiFacade.getInstance(this.project).getElementFactory();
            //noinspection ConstantConditions
            JCESPEC.generateJavaCode(factory, cursorElement, providerSelected.getName(), algorithmCombo.getAlgorithm(), inputJText.getText(), paramsMap);

            //自动import导入
            JavaCodeStyleManager.getInstance(this.project).optimizeImports(this.psiJavaFile);
            //缩短类引用
            JavaCodeStyleManager.getInstance(this.project).shortenClassReferences(this.psiJavaFile);
            //格式化代码
            CodeStyleManager.getInstance(this.project).reformat(this.psiJavaFile);
            //检查psi操作代码的正确
            PsiTestUtil.checkFileStructure(this.psiJavaFile);
        });
    }

    public void reloadProviderSelect(Provider[] providers) {
        providerSelect.removeAllItems();
        algorithmSelect.removeAllItems();
        Arrays.stream(providers)
                .filter(p -> p.getServices().stream().anyMatch(s -> s.getType().equals(JCESPEC.name())))
                .map(ProviderCombo::new)
                .sorted()
                .forEach(providerSelect::addItem);
    }
    public void reloadAlgorithmSelect(Provider provider, String type) {
        algorithmSelect.removeAllItems();
        provider.getServices().stream()
                .filter(s -> s.getType().equals(type))
                .map(AlgorithmCombo::new)
                .sorted()
                .forEach(algorithmSelect::addItem);
    }

    private void reviewParameterUI(List<Parameter> paramsList) {
        params.removeAll();
        FormBuilder formBuilder = FormBuilder.createFormBuilder();
        for (Parameter parameter : paramsList) {
            if (parameter.isShow()) {
                JPanel jPanel = new JPanel(new GridBagLayout());
                for (int j = 0; j < parameter.getComponent().size(); j++) {
                    JComponent component = parameter.getComponent().get(j);
                    GBC constraints = new GBC(j % parameter.getMaxRow(), j / parameter.getMaxRow())
                            .setWeight(0, 0, () -> parameter.getComponent().size() > 1 && component instanceof JButton)
                            .setFill(GridBagConstraints.NONE, () -> component instanceof JComboBox || component instanceof JButton)
                            .setAnchor(GridBagConstraints.EAST, () -> parameter.getComponent().size() > 1 && component instanceof JButton);
                    jPanel.add(component, constraints);
                }
                formBuilder.addLabeledComponent(new JBLabel(parameter.getKey() + ":"), jPanel);
            }
        }
        params.add(formBuilder.getPanel(), BorderLayout.CENTER);
        //刷新JPanel，否则显示非常缓慢
        params.updateUI();
    }
}
