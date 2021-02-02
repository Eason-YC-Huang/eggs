package com.hyc.plugin.action;

import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.google.common.collect.Lists;
import com.hyc.plugin.persistence.ClassBean;
import com.hyc.plugin.persistence.ExecuteUnit;
import com.hyc.plugin.persistence.ExecuteUnitRepository;
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

        if (executeUnitMap.isEmpty()) {
            addDefaultCodeTemplate(executeUnitMap);
        }

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

    private void addDefaultCodeTemplate(Map<String, ExecuteUnit> executeUnitMap) {
        ExecuteUnit executeUnit = new ExecuteUnit();
        executeUnitMap.put(executeUnit.uuid, executeUnit);
        executeUnit.name = "SayHello";
        executeUnit.desc = "print hello to console";
        executeUnit.className = "HelloWorld";
        executeUnit.sourceCode = "import java.util.Map;\n" +
            "/**\n" +
            " * @author hyc\n" +
            " * @since 2021/2/2\n" +
            " */\n" +
            "public class HelloWorld {\n" +
            "\n" +
            "    public void main(Map<String, Object> context) {\n" +
            "        HelloPrinter.printHello();\n" +
            "    }\n" +
            "    \n" +
            "}";

        List<ClassBean> classBeanList = Lists.newArrayList();
        executeUnit.classBeanList = classBeanList;
        ClassBean classBean = new ClassBean("HelloPrinter", "/**\n" +
            " * @author hyc\n" +
            " * @since 2021/2/2\n" +
            " */\n" +
            "public class HelloPrinter {\n" +
            "\n" +
            "    public static void printHello() {\n" +
            "        System.err.println(\"---------- hello ----------\");\n" +
            "    } \n" +
            "\n" +
            "}");
        classBeanList.add(classBean);

    }
}
