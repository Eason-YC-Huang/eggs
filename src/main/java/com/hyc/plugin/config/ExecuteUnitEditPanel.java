package com.hyc.plugin.config;

import java.awt.Dimension;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import org.jetbrains.annotations.NotNull;
import com.google.common.collect.Sets;
import com.hyc.plugin.persistence.ClassBean;
import com.hyc.plugin.persistence.ExecuteUnit;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.impl.EditorFactoryImpl;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.uiDesigner.core.GridConstraints;
/**
 * @author hyc
 * @since 2021/2/2
 */
public class ExecuteUnitEditPanel{

    private JPanel rootPanel;

    private JTabbedPane executeUnitPanel;

    private String executeUnitId;

    private JTextField executeUnitNameText;

    private JTextField descriptionText;

    private JTextField libPathText;

    private JTextField classNameText;

    private JButton addClassButton;

    private JPanel codePanel;

    private Editor editor;

    private final Set<ClassBeanEditPanel> classBeanEditPanelSet = Sets.newHashSet();

    public ExecuteUnitEditPanel(ExecuteUnit executeUnit) {
        executeUnitId = executeUnit.uuid;
        executeUnitNameText.setText(executeUnit.name);
        descriptionText.setText(executeUnit.desc);
        libPathText.setText(executeUnit.libPath);
        classNameText.setText(executeUnit.className);
        addCodeEditor(executeUnit.sourceCode);

        // add ClassTab
        executeUnit.classBeanList.forEach(this::addClassBeanEditPanel);

        // handle Add Class button event
        addClassButton.addActionListener(event -> {
            ClassBean classBean = new ClassBean("NewClass", "");
            this.addClassBeanEditPanel(classBean);
        });
    }

    private void addClassBeanEditPanel(ClassBean classBean) {
        ClassBeanEditPanel classBeanEditPanel = new ClassBeanEditPanel(classBean, this);
        classBeanEditPanelSet.add(classBeanEditPanel);
        executeUnitPanel.addTab(classBean.getClassName(), classBeanEditPanel.getRootPanel());
    }

    public void removeClassBeanEditPanel(ClassBeanEditPanel classBeanEditPanel) {
        this.classBeanEditPanelSet.remove(classBeanEditPanel);
        this.executeUnitPanel.remove(classBeanEditPanel.getRootPanel());
        classBeanEditPanel.releaseEditor();
    }

    private void addCodeEditor(String code) {
        EditorFactory factory = EditorFactory.getInstance();
        Document javaTemplate = factory.createDocument(code);
        this.editor = factory.createEditor(javaTemplate, null, FileTypeManager.getInstance().getFileTypeByExtension("java"), false);
        GridConstraints constraints = new GridConstraints(0, 0, 1, 1,
            GridConstraints.ANCHOR_WEST, GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED,
            null, new Dimension(0, 0),
            null, 0, true);
        codePanel.add(editor.getComponent(), constraints);
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public String id() {
        return this.executeUnitId;
    }

    public String name() {
        return this.executeUnitNameText.getText();
    }

    public String desc() {
        return this.descriptionText.getText();
    }

    public String libPath() {
        return this.libPathText.getText();
    }

    public String className() {
        return this.classNameText.getText();
    }

    public String sourceCode() {
        return this.editor.getDocument().getText();
    }

    public int curTabIdx() {
        return this.executeUnitPanel.getSelectedIndex();
    }

    public void setTabIdx(int tabIdx) {
        this.executeUnitPanel.setSelectedIndex(tabIdx);
    }

    public ExecuteUnit getExecuteUnit() {
        ExecuteUnit executeUnit = new ExecuteUnit(this.id());
        executeUnit.name = this.name();
        executeUnit.desc = this.desc();
        executeUnit.libPath = this.libPath();
        executeUnit.className = this.className();
        executeUnit.sourceCode = this.sourceCode();
        executeUnit.classBeanList = this.classBeanEditPanelSet.stream().map(ClassBeanEditPanel::getClassBean).collect(Collectors.toList());
        return executeUnit;
    }

    public void releaseEditor() {
        EditorFactoryImpl.getInstance().releaseEditor(this.editor);
        classBeanEditPanelSet.forEach(ClassBeanEditPanel::releaseEditor);
    }

    @Override
    public String toString() {
        return name();
    }
}
