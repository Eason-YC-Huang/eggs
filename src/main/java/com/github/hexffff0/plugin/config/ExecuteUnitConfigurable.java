package com.github.hexffff0.plugin.config;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.swing.JComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.github.hexffff0.plugin.persistence.ExecuteUnit;
import com.github.hexffff0.plugin.persistence.ExecuteUnitRepository;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
/**
 * @author hyc
 * @since 2021/2/2
 */
public class ExecuteUnitConfigurable implements SearchableConfigurable {

    private final ExecuteUnitRepository executeUnitRepository;

    private ExecuteUnitConfigPanel executeUnitConfigPanel;

    public ExecuteUnitConfigurable() {
        this.executeUnitRepository = ServiceManager.getService(ExecuteUnitRepository.class);
    }

    @Override
    public @NotNull String getId() {
        return "0118-0125";
    }

    @Override
    public String getDisplayName() {
        return "EggsSettings";
    }

    @Override
    public @Nullable JComponent createComponent() {
        if (executeUnitConfigPanel == null) {
            executeUnitConfigPanel = new ExecuteUnitConfigPanel(executeUnitRepository);
        }
        return executeUnitConfigPanel.getRootPanel();
    }

    @Override
    public boolean isModified() {

        if (executeUnitConfigPanel == null) {
            return false;
        }

        List<ExecuteUnit> curExecuteUnitList = executeUnitConfigPanel.getExecuteUnitList();
        if (executeUnitRepository.size() != curExecuteUnitList.size()) {
            return true;
        }

        for (ExecuteUnit curExecuteUnit : curExecuteUnitList) {
            ExecuteUnit oldExecuteUnit = executeUnitRepository.getExecuteUnit(curExecuteUnit.uuid);
            if (oldExecuteUnit == null || !Objects.equals(curExecuteUnit, oldExecuteUnit)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void apply() throws ConfigurationException {
        List<ExecuteUnit> curExecuteUnitList = executeUnitConfigPanel.getExecuteUnitList();
        Map<String, ExecuteUnit> curExecuteUnitMap = curExecuteUnitList.stream().collect(Collectors.toConcurrentMap(x -> x.uuid, x -> x));
        executeUnitRepository.setExecuteUnitMap(curExecuteUnitMap);
        executeUnitConfigPanel.refresh(curExecuteUnitList);
    }
}
