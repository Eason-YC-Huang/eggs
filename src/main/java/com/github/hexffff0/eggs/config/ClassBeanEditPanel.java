package com.github.hexffff0.eggs.config;

import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import com.google.common.base.Objects;
import com.github.hexffff0.eggs.persistence.ClassBean;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.impl.EditorFactoryImpl;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.uiDesigner.core.GridConstraints;
/**
 * @author hyc
 * @since 2021/2/2
 */
public class ClassBeanEditPanel {

    private JPanel rootPanel;

    private JTextField classNameText;

    private JButton removeButton;

    private JPanel codePanel;

    private String classBeanId;

    private Editor editor;

    public ClassBeanEditPanel(ClassBean classBean, ExecuteUnitEditPanel parent) {
        this.classBeanId = classBean.getUuid();
        this.classNameText.setText(classBean.getClassName());
        this.addCodeEditor(classBean.getSourceCode());

        // handle remove button event
        this.removeButton.addActionListener(event->{
            int result = Messages.showYesNoDialog("Really remove this class?", "Delete", null);
            if (result == Messages.OK) {
                parent.removeClassBeanEditPanel(this);
            }
        });
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

    public ClassBean getClassBean() {
        ClassBean classBean = new ClassBean(this.classBeanId);
        classBean.setClassName(classNameText.getText());
        classBean.setSourceCode(editor.getDocument().getText());
        return classBean;
    }

    public void releaseEditor() {
        EditorFactoryImpl.getInstance().releaseEditor(editor);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClassBeanEditPanel)) {
            return false;
        }
        ClassBeanEditPanel panel = (ClassBeanEditPanel) o;
        return Objects.equal(classBeanId, panel.classBeanId)
                && Objects.equal(classNameText.getText(), panel.classNameText.getText())
                // todo codePanel运行时对象的equals是怎么实现的?
                && Objects.equal(codePanel, panel.codePanel);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(classBeanId);
    }
}
