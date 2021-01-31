package com.hyc.plugin.core;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.mdkt.compiler.InMemoryJavaCompiler;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.expr.Name;
import com.hyc.plugin.utils.SystemInfo;
import com.intellij.util.PathUtil;
/**
 * @author hyc
 */
public class RunCodeHelper {

//    private static final Logger logger = Logger.getInstance(RunCodeHelper.class);

    private static final ClassLoader CLASS_LOADER = RunCodeHelper.class.getClassLoader();

    private static final String IMPORT_CLASS = RunCodeHelper.class.getName();

    public static void compileAndRunCode(@NotNull CodeTemplate codeTemplate, @NotNull Map<String, Object> context){
        try {
            InMemoryJavaCompiler jc = InMemoryJavaCompiler.newInstance();
            jc.ignoreWarnings();
            jc.useParentClassLoader(CLASS_LOADER);
            String classPath = parseDependenciesClassPath(codeTemplate.template, context);
            jc.useOptions("-classpath", classPath);
            Class<?> clazz = jc.compile(codeTemplate.className, codeTemplate.template);
            Object obj = clazz.newInstance();
            Method method = clazz.getDeclaredMethod("main", Map.class);
            method.invoke(obj, context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String parseDependenciesClassPath(String sourceCode, Map<String, Object> context) {
        CompilationUnit cu = StaticJavaParser.parse(sourceCode);
        cu.addImport(IMPORT_CLASS);
        Set<String> dependenciesPath = cu.getImports()
                                 .parallelStream()
                                 .map(ImportDeclaration::getName)
                                 .map(Name::asString)
                                 .filter(fullClassName->!fullClassName.startsWith("java"))
                                 .distinct()
                                 .map(fullClassName -> {
                                     try {
                                         return Class.forName(fullClassName);
                                     } catch (ClassNotFoundException e) {
                                         Class<?> clazz = tryInnerClass(fullClassName);
                                         if (clazz == null) {
//                                             logger.error("cannot find class " + fullClassName);
                                         }
                                         return clazz;
                                     }
                                 })
                                 .filter(Objects::nonNull)
                                 .map(PathUtil::getJarPathForClass)
                                 .collect(Collectors.toSet());

        context.values()
               .stream()
               .filter(Objects::nonNull)
               .forEach(obj -> dependenciesPath.add(obj.getClass().getName()));
        dependenciesPath.add(System.getProperty("java.class.path"));
        parseThirdPartLib(sourceCode.split(System.lineSeparator())[0], dependenciesPath);

        return String.join(SystemInfo.CLASS_PATH_DELIMITER, dependenciesPath);
    }

    private static Class<?> tryInnerClass(String fullClassName) {
        int idx = fullClassName.lastIndexOf(".");
        if (idx > 0) {
            char[] chars = fullClassName.toCharArray();
            chars[idx] = '$';
            String innerClassName = new String(chars);
            try {
                return Class.forName(innerClassName);
            } catch (ClassNotFoundException e) {
                return tryInnerClass(innerClassName);
            }
        }
        return null;
    }

    private static void parseThirdPartLib(String firstLine, Set<String> jarPaths) {
        if (firstLine.contains("libPath=")
            && firstLine.split("=").length == 2) {
            String libPaths = firstLine.split("=")[1];
            for (String libPath : libPaths.split(":")) {
                try {
                    List<String> lib = Files.list(Paths.get(libPath))
                                            .map(Path::getFileName)
                                            .map(Path::toString)
                                            .filter(fileName->fileName.endsWith(".jar"))
                                            .map(fileName -> libPath + fileName)
                                            .collect(Collectors.toList());
                    jarPaths.addAll(lib);
                } catch (IOException e) {
//                    logger.error(e);
                }
            }
        }
    }

    public static void main(String[] args) {

    }

}
