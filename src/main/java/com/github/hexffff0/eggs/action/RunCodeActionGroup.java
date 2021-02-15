package com.github.hexffff0.eggs.action;

import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.github.hexffff0.eggs.persistence.ExecuteUnit;
import com.github.hexffff0.eggs.persistence.ExecuteUnitRepository;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
/**
 * @author hyc
 */
public class RunCodeActionGroup extends ActionGroup {

    @Override
    public AnAction @NotNull [] getChildren(@Nullable AnActionEvent e) {
        ExecuteUnitRepository executeUnitRepository = ServiceManager.getService(ExecuteUnitRepository.class);
        Map<String, ExecuteUnit> executeUnitMap = executeUnitRepository.getExecuteUnitMap();

        return executeUnitMap.values()
                              .stream()
                              .map(this::getOrCreateAction)
                              .toArray(AnAction[]::new);
    }

    private AnAction getOrCreateAction(ExecuteUnit executeUnit) {
        String actionId = executeUnit.uuid;
        AnAction action = ActionManager.getInstance().getAction(actionId);
        if (action == null) {
            action = RunCodeAction.of(actionId);
            ActionManager.getInstance().registerAction(actionId, action);
        }
        return action;
    }

}
