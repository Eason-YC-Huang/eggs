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
@State(name = "CodeTemplateRepository", storages = {@Storage("$APP_CONFIG$/CodeTemplateRepository.xml")})
public class CodeTemplateRepository implements PersistentStateComponent<CodeTemplateRepository> {

    private Map<String, CodeTemplate> codeTemplateMap = Maps.newConcurrentMap();

    @Override
    public @Nullable CodeTemplateRepository getState() {
        if (this.codeTemplateMap == null) {
            this.codeTemplateMap = Maps.newConcurrentMap();
        }
        return this;
    }

    @Override
    public void loadState(@NotNull CodeTemplateRepository state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public Map<String, CodeTemplate> getCodeTemplateMap() {
        return codeTemplateMap;
    }

    public void setCodeTemplateMap(Map<String, CodeTemplate> codeTemplateMap) {
        this.codeTemplateMap = codeTemplateMap;
    }
}
