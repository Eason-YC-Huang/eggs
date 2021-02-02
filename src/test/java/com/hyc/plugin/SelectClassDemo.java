package com.hyc.plugin;

import java.util.Map;
import com.hyc.plugin.utils.JavaUtils;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.psi.PsiClass;
/**
 * @author hyc
 * @since 2021/2/2
 */
public class SelectClassDemo {

    public void main(Map<String, Object> content) {
        AnActionEvent event = (AnActionEvent) content.get("AnActionEvent");
        PsiClass selectedClass = JavaUtils.selectClass("select class", event.getProject());
        System.err.println(selectedClass.getText());
    }

}
