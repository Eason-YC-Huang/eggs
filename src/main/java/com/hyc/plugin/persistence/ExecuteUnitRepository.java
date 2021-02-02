package com.hyc.plugin.persistence;

import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
/**
 * @author hdr
 */
@State(name = "ExecuteUnitRepository", storages = {@Storage("$APP_CONFIG$/ExecuteUnitRepository.xml")})
public class ExecuteUnitRepository implements PersistentStateComponent<ExecuteUnitRepository> {

    private Map<String, ExecuteUnit> executeUnitMap = Maps.newConcurrentMap();

    @Override
    public @Nullable ExecuteUnitRepository getState() {
        if (this.executeUnitMap == null) {
            this.executeUnitMap = Maps.newConcurrentMap();
        }
        return this;
    }

    @Override
    public void loadState(@NotNull ExecuteUnitRepository state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public int size() {
        return this.executeUnitMap.size();
    }

    public ExecuteUnit getExecuteUnit(String executeUnitId) {
        return executeUnitMap.get(executeUnitId);
    }

    public Map<String, ExecuteUnit> getExecuteUnitMap() {
        return executeUnitMap;
    }

    public void setExecuteUnitMap(Map<String, ExecuteUnit> executeUnitMap) {
        this.executeUnitMap = executeUnitMap;
    }

    private Map<String, ExecuteUnit> createDefaultExecuteUnitMap() {
        Map<String, ExecuteUnit> executeUnitMap = Maps.newConcurrentMap();
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

        return executeUnitMap;
    }
}
