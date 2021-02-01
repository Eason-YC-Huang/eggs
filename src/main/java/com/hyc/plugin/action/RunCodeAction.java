package com.hyc.plugin.action;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.swing.Icon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.google.common.collect.Lists;
import com.hyc.plugin.persistence.ClassBean;
import com.hyc.plugin.persistence.CodeTemplate;
import com.hyc.plugin.persistence.CodeTemplateRepository;
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

    public RunCodeAction(@Nullable @ActionText String text,
        @Nullable @ActionDescription String description,
        @Nullable Icon icon) {
        super(text, description, icon);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        CodeTemplateRepository codeTemplateRepository = ServiceManager.getService(CodeTemplateRepository.class);
        System.err.println("------ hello world ------");
        Map<String, CodeTemplate> codeTemplateMap = codeTemplateRepository.getCodeTemplateMap();

        CodeTemplate codeTemplate = new CodeTemplate();
        codeTemplate.name = UUID.randomUUID().toString();
        long currentTime = System.currentTimeMillis();
        codeTemplate.className = "ClassName:" + currentTime;
        codeTemplate.code = "Code" + currentTime;
        codeTemplate.classBeanList = Lists.newArrayList(new ClassBean("ClassName2" + currentTime, "Code2" + currentTime));
        codeTemplateMap.put(codeTemplate.uuid, codeTemplate);
    }
}
