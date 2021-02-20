package com.github.hexffff0.eggs.utils.ui;

import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.impl.EditorFactoryImpl;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.uiDesigner.core.GridConstraints;
/**
 * 通用的文本编辑器
 * 因为对IDEA的相关API不熟悉,目前需要手动调用release方法释放editor
 */
public class CommonTextEditor extends DialogWrapper {

    private JPanel rootPanel;

    private JPanel contentPanel;
    
    private Editor editor;

    private String content = "";


    public CommonTextEditor() {
        this(null, null, null);
    }

    public CommonTextEditor(@Nullable String fileType) {
        this(null, fileType, null);
    }

    public CommonTextEditor(@Nullable String title, @Nullable String fileType, @Nullable String defaultContent) {
        super(true); // use current window as parent
        String _title = StringUtils.isBlank(title) ? "CommonTextEditor" : title;
        String _content = defaultContent == null ? "" : defaultContent;
        String _fileType = fileType == null ? "" : fileType;

        init();
        setTitle(_title);

        EditorFactory factory = EditorFactory.getInstance();
        Document document = factory.createDocument(_content);
        this.editor = factory.createEditor(document, null, FileTypeManager.getInstance().getFileTypeByExtension(_fileType), false);
        GridConstraints constraints = new GridConstraints(0, 0, 1, 1,
            GridConstraints.ANCHOR_WEST, GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED,
            null, new Dimension(500, 300),
            null, 0, true);
        contentPanel.add(editor.getComponent(), constraints);
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return rootPanel;
    }

    @Override
    protected void doOKAction() {
        this.content = this.editor.getDocument().getText();
        super.doOKAction();
    }

    public String getText() {
        return this.content;
    }

    /** 释放editor */
    public void release() {
        EditorFactoryImpl.getInstance().releaseEditor(this.editor);
    }
    
}
