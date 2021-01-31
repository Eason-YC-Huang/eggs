package com.hyc.plugin.action;

import javax.swing.Icon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.hyc.plugin.core.CodeTemplateRepository;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.util.NlsActions.ActionDescription;
import com.intellij.openapi.util.NlsActions.ActionText;
/**
 * @author hyc
 */
public class RunCodeAction extends AnAction {

    public RunCodeAction() {
    }

    public RunCodeAction(@Nullable @ActionText String text, @Nullable @ActionDescription String description, @Nullable Icon icon) {
        super(text, description, icon);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        CodeTemplateRepository codeTemplateRepository = ServiceManager.getService(CodeTemplateRepository.class);
        System.err.println("------ hello world ------");
    }
}
