package com.github.huangdaren1997.demo;

import java.util.List;
import java.util.Map;
import com.github.hexffff0.eggs.utils.JavaUtils;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
/**
 * @author hyc
 * @since 2021/2/3
 */
public class CopyMethod {

    public void main(Map<String, Object> context) {
        AnActionEvent event = (AnActionEvent) context.get("AnActionEvent");
        PsiClass selectedClass = JavaUtils.selectClass("select class", event.getProject());
        List<PsiMethod> methods = JavaUtils.selectMethods(selectedClass, "select method", true, true);
        Editor editor = event.getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            return;
        }
        PsiFile file = event.getData(CommonDataKeys.PSI_FILE);
        methods.forEach(method-> JavaUtils.writeToCaret(method.getText(), file, editor));
    }



}
