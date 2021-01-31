package com.github.huangdaren1997.demo;

import java.util.Arrays;
import java.util.Map;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnonymousClass;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiTypeParameter;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.util.IncorrectOperationException;
import com.siyeh.ig.fixes.SerialVersionUIDBuilder;
import com.siyeh.ig.psiutils.SerializationUtils;
public class GenerateSerialVersionUID {

    public void main(Map<String, Object> context) {
        AnActionEvent event = (AnActionEvent) context.get("AnActionEvent");
        PsiFile currentFile = event.getData(CommonDataKeys.PSI_FILE);
        if (!(currentFile instanceof PsiJavaFile)) {
            return;
        }

        PsiJavaFile javaFile = (PsiJavaFile) currentFile;
        PsiClass[] classes = javaFile.getClasses();
        Arrays.stream(classes)
              .filter(GenerateSerialVersionUID::needsUIDField)
              .forEach(psiClass -> {
                  final long serialVersionUIDValue = SerialVersionUIDBuilder.computeDefaultSUID(psiClass);
                  final PsiElementFactory psiElementFactory = JavaPsiFacade.getInstance(psiClass.getProject())
                                                                           .getElementFactory();
                  final CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(psiClass.getProject());

                  if (codeStyleManager != null) {
                      try {
                          String fullDeclaration = "private static final long serialVersionUID = $value$L;".replace("$value$", String.valueOf(serialVersionUIDValue));
                          final PsiField psiField = psiElementFactory.createFieldFromText(fullDeclaration, null);
                          WriteCommandAction.runWriteCommandAction(psiClass.getProject(), () -> {
                              codeStyleManager.reformat(psiField);
                              psiClass.add(psiField);
                          });
                      } catch (IncorrectOperationException e) {
                          e.printStackTrace();
                      }
                  }
              });
    }

    public static boolean needsUIDField(@Nullable PsiClass aClass) {
        if (aClass == null) {
            return false;
        }
        if (aClass.isInterface() || aClass.isAnnotationType() || aClass.isEnum()) {
            return false;
        }
        if (aClass instanceof PsiTypeParameter || aClass instanceof PsiAnonymousClass) {
            return false;
        }

        if (!SerializationUtils.isDirectlySerializable(aClass)) {
            return false;
        }

        if (hasUIDField(aClass)) {
            return false;
        }

        return true;
    }

    public static boolean hasUIDField(@Nullable PsiClass psiClass) {
        return getUIDField(psiClass) != null;
    }

    @Nullable
    public static PsiField getUIDField(@Nullable PsiClass psiClass) {
        if (psiClass != null) {
            for (final PsiField field : psiClass.getFields()) {
                if ("serialVersionUID".equals(field.getName())) {
                    return field;
                }
            }
        }
        return null;
    }
}
