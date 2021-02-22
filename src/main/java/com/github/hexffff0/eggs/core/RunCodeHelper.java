package com.github.hexffff0.eggs.core;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.mdkt.compiler.DynamicClassLoader;
import org.mdkt.compiler.InMemoryJavaCompiler;
import com.github.hexffff0.eggs.persistence.ClassBean;
import com.github.hexffff0.eggs.persistence.ExecuteUnit;
import com.github.hexffff0.eggs.utils.SystemInfo;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.expr.Name;
import com.google.common.collect.Sets;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiClass;
import com.intellij.util.PathUtil;
/**
 * @author hyc
 */
public class RunCodeHelper {

    private static final Logger logger = Logger.getInstance(RunCodeHelper.class);

    private static final ClassLoader CLASS_LOADER = RunCodeHelper.class.getClassLoader();

    private static final String IMPORT_CLASS = RunCodeHelper.class.getName();

    public static void compileAndRunCode(@NotNull ExecuteUnit executeUnit, @NotNull Map<String, Object> context) {
        try {
            InMemoryJavaCompiler jc = InMemoryJavaCompiler.newInstance();
            jc.ignoreWarnings();
            jc.useParentClassLoader(new DynamicClassLoader(CLASS_LOADER));
            String classPath = parseDependenciesClassPath(executeUnit, context);
            jc.useOptions("-classpath", classPath);

            for (ClassBean classBean : executeUnit.classBeanList) {
                jc.addSource(classBean.getClassName(), classBean.getSourceCode());
            }

            Class<?> clazz = jc.compile(executeUnit.className, executeUnit.sourceCode);
            Object obj = clazz.newInstance();
            Method method = clazz.getDeclaredMethod("main", Map.class);
            method.invoke(obj, context);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private static String parseDependenciesClassPath(ExecuteUnit executeUnit, Map<String, Object> context) {

        Set<String> dependenciesPath = Sets.newConcurrentHashSet();
        dependenciesPath.addAll(doParseDependenciesClassPath(executeUnit.sourceCode));
        executeUnit.classBeanList
            .parallelStream()
            .map(ClassBean::getSourceCode)
            .forEach(sourceCode -> dependenciesPath.addAll(doParseDependenciesClassPath(sourceCode)));

        context.values()
               .stream()
               .filter(Objects::nonNull)
               .forEach(obj -> dependenciesPath.add(obj.getClass().getName()));

        dependenciesPath.add(System.getProperty("java.class.path"));
        dependenciesPath.add(PathUtil.getJarPathForClass(ServiceManager.class));
        dependenciesPath.add(PathUtil.getJarPathForClass(PsiClass.class));
        parseThirdPartLib(executeUnit.libPath, dependenciesPath);
        return String.join(SystemInfo.CLASS_PATH_DELIMITER, dependenciesPath);
    }

    private static Set<String> doParseDependenciesClassPath(String sourceCode) {
        CompilationUnit cu = StaticJavaParser.parse(sourceCode);
        cu.addImport(IMPORT_CLASS);
        return cu.getImports()
                 .parallelStream()
                 .map(ImportDeclaration::getName)
                 .map(Name::asString)
                 .filter(fullClassName -> !fullClassName.startsWith("java"))
                 .distinct()
                 .map(fullClassName -> {
                     try {
                         return Class.forName(fullClassName);
                     } catch (ClassNotFoundException e) {
                         Class<?> clazz = tryInnerClass(fullClassName);
                         if (clazz == null) {
                             // logger.error("cannot find class " + fullClassName);
                         }
                         return clazz;
                     }
                 })
                 .filter(Objects::nonNull)
                 .map(PathUtil::getJarPathForClass)
                 .collect(Collectors.toSet());
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

    private static void parseThirdPartLib(String libPaths, Set<String> jarPaths) {
        if (StringUtils.isBlank(libPaths)) {
            return;
        }

        for (String libPath : libPaths.split(":")) {

            if (!Files.isDirectory(Paths.get(libPath))) {
                jarPaths.add(libPath);
                continue;
            }

            try {
                Files.list(Paths.get(libPath))
                     .map(Path::getFileName)
                     .map(Path::toString)
                     .filter(fileName -> fileName.endsWith(".jar"))
                     .map(fileName -> libPath + fileName)
                     .forEach(jarPaths::add);
            } catch (IOException e) {
                 logger.error(e);
            }
        }
    }
}
