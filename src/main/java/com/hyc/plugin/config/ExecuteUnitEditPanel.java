package com.hyc.plugin.config;

import java.awt.Dimension;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import com.google.common.collect.Sets;
import com.hyc.plugin.persistence.ClassBean;
import com.hyc.plugin.persistence.ExecuteUnit;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.uiDesigner.core.GridConstraints;
/**
 * @author hyc
 * @since 2021/2/2
 */
public class ExecuteUnitEditPanel {

    private JPanel rootPanel;

    private JTabbedPane executeUnitPanel;

    private String executeUnitId;

    private JTextField executeUnitNameText;

    private JTextField descriptionText;

    private JTextField libPathText;

    private JTextField classNameText;

    private JButton addClassButton;

    private JPanel codePanel;

    private final Set<ClassBeanEditPanel> classBeanEditPanelSet = Sets.newHashSet();

    public ExecuteUnitEditPanel(ExecuteUnit executeUnit) {
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
    }

    private void addCodeEditor(String code) {
        EditorFactory factory = EditorFactory.getInstance();
        Document javaTemplate = factory.createDocument(code);
        Editor editor = factory.createEditor(javaTemplate, null, FileTypeManager.getInstance().getFileTypeByExtension("java"), false);
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

    public String name() {
        return executeUnitNameText.getText();
    }

    @Override
    public String toString() {
        return name();
    }
}
