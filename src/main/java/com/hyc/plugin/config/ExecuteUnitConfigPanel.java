package com.hyc.plugin.config;

import java.util.Collection;
import java.util.Objects;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import com.hyc.plugin.persistence.ExecuteUnit;
import com.hyc.plugin.persistence.ExecuteUnitRepository;
/**
 * @author hyc
 * @since 2021/2/2
 */
public class ExecuteUnitConfigPanel {

    private JPanel rootPanel;

    private JPanel executeUnitEditPanel;

    private JPanel executeUnitListPanel;

    private JList<ExecuteUnitEditPanel> executeUnitList;

    private DefaultListModel<ExecuteUnitEditPanel> executeUnitListModel;

    private JButton addButton;

    private JButton removeButton;

    private JButton importButton;

    private JButton exportButton;

    private JButton exportAllButton;

    private JSplitPane splitPane;

    public ExecuteUnitConfigPanel(ExecuteUnitRepository executeUnitRepository) {
        this.executeUnitListModel = new DefaultListModel<>();
        this.executeUnitList.setModel(executeUnitListModel);

        this.addSelectionListener(executeUnitList);

        this.resetTabPane(executeUnitRepository.getExecuteUnitMap().values());
    }

    private void addSelectionListener(JList<ExecuteUnitEditPanel> executeUnitList) {
        executeUnitList.addListSelectionListener(event->{
            if (event.getValueIsAdjusting()) {
                return;
            }

            int length = executeUnitListModel.getSize();
            int idx = executeUnitList.getSelectedIndex();
            if (idx < 0 || idx >= length) {
                splitPane.setRightComponent(executeUnitEditPanel);
                removeButton.setEnabled(false);
                return;
            }

            ExecuteUnitEditPanel editPanel = executeUnitListModel.get(executeUnitList.getSelectedIndex());
            removeButton.setEnabled(true);
            splitPane.setRightComponent(editPanel.getRootPanel());

        });
    }

    private void resetTabPane(Collection<ExecuteUnit> executeUnitList){
        executeUnitList.stream()
                       .filter(Objects::nonNull)
                       .map(ExecuteUnitEditPanel::new)
                       .forEach(executeUnitListModel::addElement);

        this.executeUnitList.setSelectedIndex(0);
    }

    public JPanel getRootPanel() {
        return this.rootPanel;
    }
}
