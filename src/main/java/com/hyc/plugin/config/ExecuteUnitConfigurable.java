package com.hyc.plugin.config;

import javax.swing.JComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.hyc.plugin.persistence.ExecuteUnitRepository;
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
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {

    }
}
