package com.hyc.plugin.action;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
/**
 * @author hyc
 */
public class RunCodeActionGroup extends ActionGroup {

    @Override
    public AnAction @NotNull [] getChildren(@Nullable AnActionEvent e) {
        ActionManager actionManager = ActionManager.getInstance();

        AnAction a1;
        if ((a1 = actionManager.getAction("RunCodeAction")) == null) {
            a1 = new RunCodeAction("RunCodeAction", "Desc",null);
            actionManager.registerAction("RunCodeAction", a1);
        }

        AnAction a2;
        if ((a2 = actionManager.getAction("RunCodeAction2")) == null) {
            a2 = new RunCodeAction("RunCodeAction2", "Desc2",null);
            actionManager.registerAction("RunCodeAction2", a2);
        }
        return new AnAction[] {a1, a2};
    }
}
