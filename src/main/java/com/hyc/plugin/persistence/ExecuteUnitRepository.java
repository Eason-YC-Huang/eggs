package com.hyc.plugin.persistence;

import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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

    public Map<String, ExecuteUnit> getExecuteUnitMap() {
        return executeUnitMap;
    }

    public void setExecuteUnitMap(Map<String, ExecuteUnit> executeUnitMap) {
        this.executeUnitMap = executeUnitMap;
    }
}
