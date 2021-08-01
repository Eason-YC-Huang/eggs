package com.github.huangdaren1997.demo;

import java.util.Map;
import com.github.hexffff0.eggs.utils.JavaUtils;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
public class AutowiredHelper {


    public void main(Map<String, Object> content){

        AnActionEvent event = (AnActionEvent) content.get("AnActionEvent");

        PsiFile curFile = event.getData(CommonDataKeys.PSI_FILE);
        Project project = event.getData(CommonDataKeys.PROJECT);
        Editor editor = event.getData(CommonDataKeys.EDITOR);
        if (project == null
            || !(curFile instanceof PsiJavaFile)
            || editor == null) {
            return;
        }

        PsiClass selectedClass = JavaUtils.selectClass("select class", project);
        if (selectedClass == null) {
            return;
        }

        String className = selectedClass.getName();
        String varName = StringUtil.wordsToBeginFromLowerCase(className);

        String template = "    private %s %s;\n" +
            "\n" +
            "    @Autowired\n" +
            "    public void set%s(%s %s) {\n" +
            "        this.%s = %s;\n" +
            "    }";

        String code = String.format(template, className, varName, className, className, varName, varName, varName);
        JavaUtils.writeToCaret(code, curFile, editor);
    }

}

