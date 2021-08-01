package com.github.huangdaren1997.demo;

import java.util.Map;
import org.intellij.lang.annotations.Language;
import com.github.hexffff0.eggs.utils.JavaUtils;
import com.github.hexffff0.eggs.utils.ui.CommonTextEditor;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
public class CreateMapping {

    @Language("JAVA")
    private static final String codeTemplate = "import org.etnaframework.plugin.mapstruct.BaseMapping;\n" +
        "import org.mapstruct.Mapper;\n" +
        "import org.mapstruct.factory.Mappers;\n" +
        "\n" +
        "@Mapper(componentModel = \"spring\")\n" +
        "public interface $InterfaceName$ extends BaseMapping<$source$, $dest$> {\n" +
        "\n" +
        "    $InterfaceName$ INSTANT = Mappers.getMapper($InterfaceName$.class);\n" +
        "\n" +
        "}";

    public void main(Map<String, Object> content) {
        AnActionEvent event = (AnActionEvent) content.get("AnActionEvent");

        PsiElement curFile = event.getData(CommonDataKeys.PSI_ELEMENT);
        if (!(curFile instanceof PsiDirectory)) {
            return;
        }
        PsiDirectory directory = (PsiDirectory) curFile;
        Project project = event.getData(CommonDataKeys.PROJECT);
        CommonTextEditor textEditor = new CommonTextEditor("input mapping name", null, null);
        try {
            if (textEditor.showAndGet()) {
                String interfaceName = textEditor.getText();
                PsiClass source = JavaUtils.selectClass(null, project);
                PsiClass dest = JavaUtils.selectClass(null, project);
                String code = codeTemplate.replace("$InterfaceName$", interfaceName)
                                          .replace("$source$", source.getName())
                                          .replace("$dest$", dest.getName());

                JavaUtils.createNewClass(code, interfaceName, directory);
            }
        } finally {
            textEditor.release();
        }
    }
}
