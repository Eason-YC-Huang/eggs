package com.hyc.plugin.action;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.swing.Icon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hyc.plugin.core.RunCodeHelper;
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

    private String codeTemplateId;

    /** you should never call this constructor on your code */
    public RunCodeAction() {
    }

    public static RunCodeAction of(String codeTemplateId) {
        CodeTemplateRepository codeTemplateRepository = ServiceManager.getService(CodeTemplateRepository.class);
        Map<String, CodeTemplate> codeTemplateMap = codeTemplateRepository.getCodeTemplateMap();
        CodeTemplate codeTemplate = codeTemplateMap.get(codeTemplateId);
        RunCodeAction runCodeAction = new RunCodeAction(codeTemplate.name, codeTemplate.desc, null);
        runCodeAction.codeTemplateId = codeTemplate.uuid;
        return runCodeAction;
    }


    private RunCodeAction(@Nullable @ActionText String text,
        @Nullable @ActionDescription String description,
        @Nullable Icon icon) {
        super(text, description, icon);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        // 为什么每次都调用ServiceManager获取CodeTemplateRepository呢?
        // 就是目前我不知道是否会有数据不同的问题
        CodeTemplateRepository codeTemplateRepository = ServiceManager.getService(CodeTemplateRepository.class);
        Map<String, CodeTemplate> codeTemplateMap = codeTemplateRepository.getCodeTemplateMap();
        CodeTemplate codeTemplate = codeTemplateMap.get(codeTemplateId);
        HashMap<String, Object> context = Maps.newHashMap();
        context.put("AnActionEvent", e);
        RunCodeHelper.compileAndRunCode(codeTemplate, context);
    }
}
