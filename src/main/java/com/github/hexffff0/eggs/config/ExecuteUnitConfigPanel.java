package com.github.hexffff0.eggs.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import com.github.hexffff0.eggs.persistence.ExecuteUnit;
import com.github.hexffff0.eggs.persistence.ExecuteUnitRepository;
import com.github.hexffff0.eggs.persistence.ExecuteUnitList;
import com.google.common.collect.Lists;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.fileEditor.FileDocumentManager;
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

    private static final String DEFAULT_EXPORT_PATH = "ExecuteUnit.xml";

    public ExecuteUnitConfigPanel(ExecuteUnitRepository executeUnitRepository) {
        this.executeUnitListModel = new DefaultListModel<>();
        this.executeUnitList.setModel(executeUnitListModel);

        this.addSelectionListener(executeUnitList);
        this.addExecuteUnitListener(addExecuteUnitButton);
        this.removeExecuteUnitListener(removeExecuteUnitButton);

        exportButton.addActionListener(e -> {
            int index = executeUnitList.getSelectedIndex();
            ExecuteUnitEditPanel template = executeUnitListModel.get(index);

            String xml = ExecuteUnitList.toXml(template.getExecuteUnit());
            saveToFile(xml);
        });

        exportAllButton.addActionListener(e -> {
            List<ExecuteUnit> executeUnits = Lists.newArrayList();
            for (int i = 0; i < executeUnitListModel.getSize(); i++) {
                executeUnits.add(executeUnitListModel.get(i).getExecuteUnit());
            }
            String xml = ExecuteUnitList.toXml(executeUnits);
            saveToFile(xml);
        });

        importButton.addActionListener(e -> readFromFile().thenAccept(xml -> {
            try {
                List<ExecuteUnit> executeUnits = ExecuteUnitList.fromXml(xml);
                List<ExecuteUnit> currentExecuteUnits = getExecuteUnitList();
                currentExecuteUnits.addAll(executeUnits);
                refresh(currentExecuteUnits);
                Messages.showMessageDialog("Import finished!", "Import", null);
            } catch (Exception ex) {
                ex.printStackTrace();
                Messages.showMessageDialog("Fail to import\n" + ex.getMessage(), "Import Error", null);
            }
        }));

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
            int currentIdx = executeUnitList.getSelectedIndex();
            int size = executeUnitListModel.getSize();
            if (currentIdx >= 0 && currentIdx < size) {
                int result = Messages.showYesNoDialog("Delete this execute unit?", "Delete", null);
                if (result == Messages.OK) {
                    ExecuteUnitEditPanel executeUnitEditPanel = executeUnitListModel.get(currentIdx);
                    executeUnitListModel.remove(currentIdx);
                    executeUnitEditPanel.releaseEditor();
                    if (size - 1 != 0) {
                        executeUnitList.setSelectedIndex(0);
                    }
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

    private void resetTabPane(Collection<ExecuteUnit> executeUnitList) {
        this.resetTabPane(executeUnitList, 0);
    }

    private void resetTabPane(Collection<ExecuteUnit> executeUnitList, int executeUnitIdx) {
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

    private void saveToFile(String content) {
        final FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleLocalFileDescriptor();
        descriptor.setTitle("Choose Directory to Export");
        descriptor.setDescription("save to directory/" + DEFAULT_EXPORT_PATH + " or the file to overwrite");
        FileChooser.chooseFile(descriptor, null, rootPanel, null, virtualFile -> {
            String targetPath;
            if (virtualFile.isDirectory()) {
                targetPath = virtualFile.getPath() + '/' + DEFAULT_EXPORT_PATH;
            } else {
                targetPath = virtualFile.getPath();
            }

            Path path = Paths.get(targetPath);
            if (virtualFile.isDirectory() && Files.exists(path)) {
                int result = Messages.showYesNoDialog("Overwrite the file?\n" + path, "Overwrite", null);
                if (result != Messages.OK) {
                    return;
                }
            }

            try {
                Files.write(path, content.getBytes(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
                Messages.showMessageDialog("Exported to \n" + path, "Export Successful", null);
            } catch (IOException e) {
                e.printStackTrace();
                Messages.showMessageDialog("Error occurred\n" + e.getMessage(), "Export Error", null);
            }
        });
    }

    private CompletableFuture<String> readFromFile() {
        final FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFileDescriptor("xml");
        descriptor.setTitle("Choose File to Import");
        final CompletableFuture<String> result = new CompletableFuture<>();
        FileChooser.chooseFile(descriptor, null, rootPanel, null,
            virtualFile -> result.complete(FileDocumentManager.getInstance().getDocument(virtualFile).getText()));
        return result;
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
        int selectedIndex = this.executeUnitList.getSelectedIndex();
        if (selectedIndex < 0) {
            if (executeUnitList.size() > 0) {
                this.resetTabPane(executeUnitList, 0, 0);
            }
        } else {
            int tabIdx = this.executeUnitListModel.get(selectedIndex).curTabIdx();
            this.executeUnitListModel.removeAllElements();
            this.resetTabPane(executeUnitList, selectedIndex, tabIdx);
        }
    }

    public void releaseEditor() {
        for (int i = 0; i < executeUnitListModel.size(); i++) {
            ExecuteUnitEditPanel executeUnitEditPanel = executeUnitListModel.get(i);
            executeUnitEditPanel.releaseEditor();
        }
    }
}
