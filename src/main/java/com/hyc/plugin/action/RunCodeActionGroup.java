package com.hyc.plugin.action;

import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.google.common.collect.Lists;
import com.hyc.plugin.persistence.ClassBean;
import com.hyc.plugin.persistence.CodeTemplate;
import com.hyc.plugin.persistence.CodeTemplateRepository;
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
        CodeTemplateRepository codeTemplateRepository = ServiceManager.getService(CodeTemplateRepository.class);
        Map<String, CodeTemplate> codeTemplateMap = codeTemplateRepository.getCodeTemplateMap();

        if (codeTemplateMap.isEmpty()) {
            addDefaultCodeTemplate(codeTemplateMap);
        }

        return codeTemplateMap.values()
                              .stream()
                              .map(this::getOrCreateAction)
                              .toArray(AnAction[]::new);
    }

    private AnAction getOrCreateAction(CodeTemplate codeTemplate) {
        String actionId = codeTemplate.uuid;
        AnAction action = ActionManager.getInstance().getAction(actionId);
        if (action == null) {
            action = RunCodeAction.of(actionId);
            ActionManager.getInstance().registerAction(actionId, action);
        }
        return action;
    }

    private void addDefaultCodeTemplate(Map<String, CodeTemplate> codeTemplateMap) {
        CodeTemplate codeTemplate = new CodeTemplate();
        codeTemplateMap.put(codeTemplate.uuid, codeTemplate);
        codeTemplate.name = "SayHello";
        codeTemplate.desc = "print hello to console";
        codeTemplate.className = "HelloWorld";
        codeTemplate.code = "import java.util.Map;\n" +
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
        codeTemplate.classBeanList = classBeanList;
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
