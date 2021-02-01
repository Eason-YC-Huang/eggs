package com.hyc.plugin.persistence;

import java.util.List;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
/**
 * @author hdr
 */
@State(name = "CodeTemplateRepository", storages = {@Storage("$APP_CONFIG$/CodeTemplateRepository.xml")})
public class CodeTemplateRepository implements PersistentStateComponent<CodeTemplateRepository> {

    private List<CodeTemplate> codeTemplateList;

    @Override
    public @Nullable CodeTemplateRepository getState() {
        if (this.codeTemplateList == null) {
            this.codeTemplateList = Lists.newArrayList();
        }
        System.err.println("----------- getState -----------");
        return this;
    }

    @Override
    public void loadState(@NotNull CodeTemplateRepository state) {
        System.err.println("----------- loadState -----------");
        XmlSerializerUtil.copyBean(state, this);
    }

    public List<CodeTemplate> getCodeTemplateList() {
        return codeTemplateList;
    }

    public void setCodeTemplateList(List<CodeTemplate> codeTemplateList) {
        this.codeTemplateList = codeTemplateList;
    }
}
