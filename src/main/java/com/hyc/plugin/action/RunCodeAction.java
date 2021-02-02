package com.hyc.plugin.action;

import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.google.common.collect.Maps;
import com.hyc.plugin.core.RunCodeHelper;
import com.hyc.plugin.persistence.ExecuteUnit;
import com.hyc.plugin.persistence.ExecuteUnitRepository;
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
        ExecuteUnitRepository executeUnitRepository = ServiceManager.getService(ExecuteUnitRepository.class);
        Map<String, ExecuteUnit> codeTemplateMap = executeUnitRepository.getExecuteUnitMap();
        ExecuteUnit executeUnit = codeTemplateMap.get(codeTemplateId);
        RunCodeAction runCodeAction = new RunCodeAction(executeUnit.name, executeUnit.desc, null);
        runCodeAction.codeTemplateId = executeUnit.uuid;
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
        ExecuteUnitRepository executeUnitRepository = ServiceManager.getService(ExecuteUnitRepository.class);
        Map<String, ExecuteUnit> executeUnitMap = executeUnitRepository.getExecuteUnitMap();
        ExecuteUnit executeUnit = executeUnitMap.get(codeTemplateId);
        HashMap<String, Object> context = Maps.newHashMap();
        context.put("AnActionEvent", e);
        RunCodeHelper.compileAndRunCode(executeUnit, context);
    }
}
