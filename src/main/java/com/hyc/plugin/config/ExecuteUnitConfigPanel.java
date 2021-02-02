package com.hyc.plugin.config;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import com.google.common.collect.Lists;
import com.hyc.plugin.persistence.ExecuteUnit;
import com.hyc.plugin.persistence.ExecuteUnitRepository;
import com.intellij.openapi.ui.Messages;
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

    private JButton addExecuteUnitButton;

    private JButton removeExecuteUnitButton;

    private JButton importButton;

    private JButton exportButton;

    private JButton exportAllButton;

    private JSplitPane splitPane;

    public ExecuteUnitConfigPanel(ExecuteUnitRepository executeUnitRepository) {
        this.executeUnitListModel = new DefaultListModel<>();
        this.executeUnitList.setModel(executeUnitListModel);

        this.addSelectionListener(executeUnitList);
        this.addExecuteUnitListener(addExecuteUnitButton);
        this.removeExecuteUnitListener(removeExecuteUnitButton);

        Map<String, ExecuteUnit> executeUnitMap = executeUnitRepository.getExecuteUnitMap();
        if (executeUnitMap.size() != 0) {
            this.resetTabPane(executeUnitMap.values());
        }
    }

    // ----------- private method -----------

    private void addExecuteUnitListener(JButton addExecuteUnitButton) {
        addExecuteUnitButton.addActionListener(event -> {
            ExecuteUnit executeUnit = new ExecuteUnit();
            executeUnit.name = "NewExecuteUnit";
            ExecuteUnitEditPanel editPanel = new ExecuteUnitEditPanel(executeUnit);
            DefaultListModel<ExecuteUnitEditPanel> model = (DefaultListModel<ExecuteUnitEditPanel>) executeUnitList.getModel();
            model.addElement(editPanel);
            executeUnitList.setSelectedIndex(model.getSize() - 1);
        });
    }

    private void removeExecuteUnitListener(JButton removeExecuteUnitButton) {
        removeExecuteUnitButton.addActionListener(event -> {
            int index = executeUnitList.getSelectedIndex();
            int size = executeUnitListModel.getSize();
            if (index >= 0 && index < size) {
                int result = Messages.showYesNoDialog("Delete this execute unit?", "Delete", null);
                if (result == Messages.OK) {
                    int lastIndex = executeUnitList.getAnchorSelectionIndex();
                    ExecuteUnitEditPanel executeUnitEditPanel = executeUnitListModel.get(index);
                    executeUnitListModel.remove(index);
                    executeUnitEditPanel.releaseEditor();
                    int nextIndex = -1;
                    if (lastIndex >= 0 && lastIndex < index || lastIndex == index && index < size-1) {
                        nextIndex = lastIndex;
                    } else if (lastIndex == index || lastIndex > index && lastIndex < size-1) {
                        nextIndex = lastIndex - 1;
                    } else if (lastIndex >= index){
                        nextIndex = size-2; // should not be here
                    }
                    executeUnitList.setSelectedIndex(nextIndex);
                }
            }
        });
    }

    private void addSelectionListener(JList<ExecuteUnitEditPanel> executeUnitList) {
        executeUnitList.addListSelectionListener(event -> {
            if (event.getValueIsAdjusting()) {
                return;
            }

            int length = executeUnitListModel.getSize();
            int idx = executeUnitList.getSelectedIndex();
            if (idx < 0 || idx >= length) {
                splitPane.setRightComponent(executeUnitEditPanel);
                removeExecuteUnitButton.setEnabled(false);
                return;
            }

            ExecuteUnitEditPanel editPanel = executeUnitListModel.get(executeUnitList.getSelectedIndex());
            removeExecuteUnitButton.setEnabled(true);
            splitPane.setRightComponent(editPanel.getRootPanel());
        });
    }

    private void resetTabPane(Collection<ExecuteUnit> executeUnitList){
        this.resetTabPane(executeUnitList, 0);
    }

    private void resetTabPane(Collection<ExecuteUnit> executeUnitList , int executeUnitIdx) {
        this.resetTabPane(executeUnitList, executeUnitIdx, 0);
    }

    private void resetTabPane(Collection<ExecuteUnit> executeUnitList, int executeUnitIdx, int tabIdx) {
        executeUnitList.stream()
                       .filter(Objects::nonNull)
                       .map(ExecuteUnitEditPanel::new)
                       .forEach(executeUnitListModel::addElement);

        this.executeUnitList.setSelectedIndex(executeUnitIdx);
        this.executeUnitListModel.get(executeUnitIdx).setTabIdx(tabIdx);
    }

    // ----------- public method -----------

    public JPanel getRootPanel() {
        return this.rootPanel;
    }

    public List<ExecuteUnit> getExecuteUnitList() {
        List<ExecuteUnit> result = Lists.newArrayList();
        for (int i = 0; i < executeUnitListModel.size(); i++) {
            ExecuteUnitEditPanel executeUnitEditPanel = executeUnitListModel.get(i);
            ExecuteUnit executeUnit = executeUnitEditPanel.getExecuteUnit();
            result.add(executeUnit);
        }
        return result;
    }

    public void refresh(List<ExecuteUnit> executeUnitList) {
        int executeUnitIdx = this.executeUnitList.getSelectedIndex();
        int tabIdx = this.executeUnitListModel.get(executeUnitIdx).curTabIdx();
        this.executeUnitListModel.removeAllElements();
        this.resetTabPane(executeUnitList, executeUnitIdx, tabIdx);
    }

    public void releaseEditor() {
        for (int i = 0; i < executeUnitListModel.size(); i++) {
            ExecuteUnitEditPanel executeUnitEditPanel = executeUnitListModel.get(i);
            executeUnitEditPanel.releaseEditor();
        }
    }


}
