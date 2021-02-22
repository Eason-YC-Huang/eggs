package com.github.hexffff0.eggs;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import com.github.hexffff0.eggs.utils.JavaUtils;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.printer.PrettyPrinterConfiguration;
import com.google.common.collect.Maps;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
public class SmartMethodCopier {

    private static final String serviceMark = "Service";

    private static final String clientMark = "Client";

    private static final String apiMark = "Api";

    private static final String apiImplMark = "ApiImpl";

    private static final String boMark = "Bo";

    private static final String daoMark = "Dao";

    private static final String mapperMark = "Mapper";

    private static final String flowMark = "->";

    private static List<String> markList;

    private static Map<String, Function<List<PsiMethod>, String>> copyMethodStrategy;

    static{
        initMarkList();
        initCopyMethodStrategy();
    }

    private static void initCopyMethodStrategy() {
        copyMethodStrategy = Maps.newHashMap();
        copyMethodStrategy.put(serviceMark + flowMark + clientMark, serviceToClient());
    }

    private static void initMarkList() {
        markList = Lists.newArrayList();
        markList.add(serviceMark);
        markList.add(clientMark);
        markList.add(apiMark);
        markList.add(apiImplMark);
        markList.add(boMark);
        markList.add(daoMark);
        markList.add(mapperMark);
    }

    public void main(Map<String, Object> content) {
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

        List<PsiMethod> selectedMethods = JavaUtils.selectMethods(selectedClass, "select methods", true, true);
        if (CollectionUtils.isEmpty(selectedMethods)) {
            return;
        }

        String fromFileName = selectedClass.getContainingFile().getName();
        String curFileName = curFile.getName();
        Function<List<PsiMethod>, String> strategy = findCopyMethodStrategy(fromFileName, curFileName);
        String code = strategy.apply(selectedMethods);

        JavaUtils.writeToCaret(code, curFile, editor);
        JavaUtils.reformatCode(curFile,false);
    }

    private static Function<List<PsiMethod>, String> findCopyMethodStrategy(String fromFile, String toFile) {
        String fromFileMark = findMark(fromFile);
        String toFileMark = findMark(toFile);
        if (fromFileMark == null || toFileMark == null) {
            throw new RuntimeException("unsupported file " + fromFile + " " + toFile);
        }
        String key = fromFileMark + flowMark + toFileMark;
        Function<List<PsiMethod>, String> func = copyMethodStrategy.get(key);
        if (func == null) {
            throw new RuntimeException("func == null " + key + " keys:" + copyMethodStrategy.keySet());
        }
        return func;
    }

    private static String findMark(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return null;
        }

        fileName = fileName.replace(".java", "");

        for (String mark : markList) {
            if (fileName.endsWith(mark)) {
                return mark;
            }
        }

        return null;
    }

    private static Function<List<PsiMethod>, String> serviceToClient(){
        return methods -> {

            StringBuilder sb = new StringBuilder();

            for (PsiMethod method : methods) {

                JavaParser javaParser = new JavaParser();
                ParseResult<MethodDeclaration> parseResult = javaParser.parseMethodDeclaration(method.getText());
                if (parseResult.isSuccessful()) {
                    MethodDeclaration md = parseResult.getResult().get();
                    BlockStmt body = new BlockStmt();
                    NodeList<Statement> statements = new NodeList<>();
                    body.setStatements(statements);

                    String stm = String.format("%s result = %s.%s(%s);", method.getReturnType().getPresentableText(),
                        StringUtil.wordsToBeginFromLowerCase(method.getContainingClass().getName()),
                        method.getName(),
                        md.getParameters().stream().map(Parameter::getName).map(SimpleName::asString).collect(Collectors.joining(","))
                    );

                    statements.add(javaParser.parseStatement(stm).getResult().get());
                    statements.add(javaParser.parseStatement("throwIfMatch(result.isFailure(), result.getMsg());").getResult().get());
                    statements.add(javaParser.parseStatement("return result.getData();").getResult().get());
                    md.setBody(body);

                    sb.append(md.toString(new PrettyPrinterConfiguration()));
                    sb.append(System.lineSeparator());
                }
            }
            return sb.toString();
        };
    }
}
