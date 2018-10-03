/*
 * Copyright (C) 2018 MegaMek team
 *
 * This file is part of MekHQ.
 *
 * MekHQ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * MekHQ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MekHQ.  If not, see <http://www.gnu.org/licenses/>.
 */

package mekhq.gui.dialog;

import mekhq.IconPackage;
import mekhq.campaign.ResolveScenarioTracker;
import mekhq.campaign.personnel.Award;
import mekhq.gui.filter.AwardNameFilter;
import mekhq.gui.filter.AwardSuggestionFilter;
import mekhq.gui.filter.AwardedAwardsFilter;
import mekhq.gui.model.ScenarioAwardsAwardTableModel;
import mekhq.gui.model.ScenarioAwardsPersonTableModel;
import mekhq.gui.utilities.JTableUtilities;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ScenarioAwardsPanel extends JPanel {

    private ResolveScenarioTracker tracker;

    private ScenarioAwardsAwardTableModel awardTableModel = new ScenarioAwardsAwardTableModel();
    private ScenarioAwardsPersonTableModel personTableModel;

    private JTable personnelTable = new JTable();
    private JTable unawardedAwardsTable = new JTable();
    private JTable awardedAwardsTable = new JTable();

    private AwardPreviewPanel awardPreviewPanel;

    private HashMap<UUID, java.util.List<Award>> awardedAwardsMap = new HashMap<>();

    public ScenarioAwardsPanel(ResolveScenarioTracker scenarioTracker, IconPackage iconPackage) {
        super();
        this.setLayout(new GridBagLayout());
        this.tracker = scenarioTracker;
        personTableModel = new ScenarioAwardsPersonTableModel(scenarioTracker);

        // Initializing gbc
        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.insets = new Insets(5, 5, 0, 0);

        // Personnel Table
        gridBagConstraints.gridx = 0;
        JPanel personnelPanel = createPersonnelPanel();
        this.add(personnelPanel, gridBagConstraints);

        // Unawarded Awards Table
        gridBagConstraints.gridx = 1;
        JPanel unawardedAwardsPanel = createUnawardedAwardsPanel();
        this.add(unawardedAwardsPanel, gridBagConstraints);

        // Buttons Panel
        gridBagConstraints.gridx = 2;
        JPanel buttonsPanel = createButtonsPanel();
        this.add(buttonsPanel, gridBagConstraints);

        // Awarded Awards Panel
        gridBagConstraints.gridx = 3;
        JPanel awardedAwardsPanel = createAwardedAwardsPanel();
        this.add(awardedAwardsPanel, gridBagConstraints);

        // Award preview panel
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        awardPreviewPanel = createAwardPreviewPanel(iconPackage);
        this.add(awardPreviewPanel, gridBagConstraints);
    }

    private JPanel createPersonnelPanel() {

        JPanel personnelPanel = new JPanel();
        personnelTable.setModel(personTableModel);
        personnelTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        personnelTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                repopulatedAwardedAwardsTable();
            }
        });

        personnelTable.changeSelection(0,0,false, false);
        JTableUtilities.resizeColumnWidth(personnelTable);
        personnelPanel.add(new JScrollPane(personnelTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
        return personnelPanel;
    }

    private void repopulatedAwardedAwardsTable(){

        int personIndex = personnelTable.convertRowIndexToModel(personnelTable.getSelectedRow());
        ResolveScenarioTracker.PersonStatus personStatus = personTableModel.getPersonAt(personIndex);

        java.util.List<Award> awardedAwards;
        UUID personId = personStatus.getId();

        TableRowSorter<ScenarioAwardsAwardTableModel> sorter = new TableRowSorter<>(awardTableModel);

        if(awardedAwardsMap.containsKey(personId)){
            awardedAwards = awardedAwardsMap.get(personId);
        }
        else{
            awardedAwards = new ArrayList<>();
            awardedAwardsMap.put(personId, awardedAwards);
        }
        sorter.setRowFilter(new AwardedAwardsFilter(awardedAwards));
        awardedAwardsTable.setRowSorter(sorter);
        JTableUtilities.resizeColumnWidth(awardedAwardsTable);
    }

    private JPanel createUnawardedAwardsPanel() {

        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.insets = new Insets(5, 5, 0, 0);

        JPanel unawardedAwardsPanel = new JPanel();
        unawardedAwardsPanel.setLayout(new GridBagLayout());
        TitledBorder borderUnawardedAwards = new TitledBorder("Awards");
        borderUnawardedAwards.setTitleJustification(TitledBorder.CENTER);
        borderUnawardedAwards.setTitlePosition(TitledBorder.TOP);
        unawardedAwardsPanel.setBorder(borderUnawardedAwards);

        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new GridLayout(0,2));
        JRadioButton allButton = new JRadioButton("All");
        allButton.setSelected(true);
        allButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                TableRowSorter<ScenarioAwardsAwardTableModel> sorter = new TableRowSorter<>(awardTableModel);
                sorter.setRowFilter(null);
                unawardedAwardsTable.setRowSorter(sorter);
            }
        });

        JRadioButton suggestedButton = new JRadioButton("Suggested");
        suggestedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                TableRowSorter<ScenarioAwardsAwardTableModel> sorter = new TableRowSorter<>(awardTableModel);
                sorter.setRowFilter(new AwardSuggestionFilter());
                unawardedAwardsTable.setRowSorter(sorter);
            }
        });

        ButtonGroup group = new ButtonGroup();
        group.add(allButton);
        group.add(suggestedButton);
        radioPanel.add(allButton);
        radioPanel.add(suggestedButton);
        unawardedAwardsPanel.add(radioPanel, gridBagConstraints);

        JTextField searchTextField = new JTextField("search", 30);
        searchTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                TableRowSorter<ScenarioAwardsAwardTableModel> sorter = new TableRowSorter<>(awardTableModel);
                sorter.setRowFilter(new AwardNameFilter(searchTextField.getText()));
                unawardedAwardsTable.setRowSorter(sorter);
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                TableRowSorter<ScenarioAwardsAwardTableModel> sorter = new TableRowSorter<>(awardTableModel);
                sorter.setRowFilter(new AwardNameFilter(searchTextField.getText()));
                unawardedAwardsTable.setRowSorter(sorter);
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) { }
        });
        gridBagConstraints.gridy = 1;
        unawardedAwardsPanel.add(searchTextField, gridBagConstraints);

        unawardedAwardsTable.setModel(awardTableModel);
        unawardedAwardsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        unawardedAwardsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        unawardedAwardsTable.getSelectionModel().addListSelectionListener(new AwardListSelectionHandler(unawardedAwardsTable));

        JTableUtilities.resizeColumnWidth(unawardedAwardsTable);

        gridBagConstraints.gridy = 2;
        unawardedAwardsPanel.add(new JScrollPane(unawardedAwardsTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), gridBagConstraints);

        return unawardedAwardsPanel;
    }

    private JPanel createAwardedAwardsPanel() {
        JPanel awardedAwardsPanel = new JPanel();
        TitledBorder borderAwarded = new TitledBorder("Awarded");
        borderAwarded.setTitleJustification(TitledBorder.CENTER);
        borderAwarded.setTitlePosition(TitledBorder.TOP);
        awardedAwardsPanel.setBorder(borderAwarded);

        awardedAwardsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        awardedAwardsTable.setModel(awardTableModel);
        awardedAwardsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        awardedAwardsTable.getSelectionModel().addListSelectionListener(new AwardListSelectionHandler(awardedAwardsTable));
        JTableUtilities.resizeColumnWidth(awardedAwardsTable);
        awardedAwardsPanel.add(new JScrollPane(awardedAwardsTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));

        return awardedAwardsPanel;
    }

    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        JButton addButton = new JButton();
        addButton.setAction(new AddAction());
        addButton.setText(">>>");
        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.insets = new Insets(5, 5, 0, 0);
        panel.add(addButton, gridBagConstraints);

        JButton removeButton = new JButton();
        removeButton.setAction(new RemoveAction());
        removeButton.setText("<<<");
        gridBagConstraints.gridy = 1;
        panel.add(removeButton, gridBagConstraints);

        return panel;
    }

    private AwardPreviewPanel createAwardPreviewPanel(IconPackage iconPackage){
        AwardPreviewPanel awardPreviewPanel = new AwardPreviewPanel(iconPackage);
        TitledBorder borderAwarded = new TitledBorder("AWARD PREVIEW");
        borderAwarded.setTitleJustification(TitledBorder.CENTER);
        borderAwarded.setTitlePosition(TitledBorder.TOP);
        awardPreviewPanel.setBorder(borderAwarded);

        return awardPreviewPanel;
    }

    class AddAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {

            ScenarioAwardsPersonTableModel model = (ScenarioAwardsPersonTableModel) personnelTable.getModel();
            ResolveScenarioTracker.PersonStatus personStatus = model.getPersonAt(personnelTable.getSelectedRow());

            int[] selectedAwards = unawardedAwardsTable.getSelectedRows();

            for(int i : selectedAwards){
                int modelIndex = unawardedAwardsTable.convertRowIndexToModel(i);
                Award award = awardTableModel.getValueAt(modelIndex);
                awardedAwardsMap.get(personStatus.getId()).add(award);
                repopulatedAwardedAwardsTable();
            }
        }
    }

    class RemoveAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {

            ScenarioAwardsPersonTableModel model = (ScenarioAwardsPersonTableModel) personnelTable.getModel();
            ResolveScenarioTracker.PersonStatus personStatus = model.getPersonAt(personnelTable.getSelectedRow());

            int[] selectedAwards = awardedAwardsTable.getSelectedRows();

            for(int i : selectedAwards){
                int modelIndex = awardedAwardsTable.convertRowIndexToModel(i);
                Award award = awardTableModel.getValueAt(modelIndex);
                awardedAwardsMap.get(personStatus.getId()).remove(award);
                repopulatedAwardedAwardsTable();
            }
        }
    }

    class AwardPreviewPanel extends JPanel{

        private GridBagConstraints gridBagConstraints = new GridBagConstraints();
        private IconPackage iconPackage;

        public AwardPreviewPanel(IconPackage iconPackage) {

            super();

            this.setLayout(new GridBagLayout());
            this.iconPackage = iconPackage;
            gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
            gridBagConstraints.weightx = 0.0;
        }

        public void updatePreviewForAwards(java.util.List<Award> awards){

            int i = 1;

            this.removeAll();
            addHeaders();
            gridBagConstraints.insets = new Insets(2, 2, 2, 10);

            for(Award award : awards) {
                gridBagConstraints.gridy = i;

                JLabel set = new JLabel();
                set.setText(award.getSet());
                gridBagConstraints.gridx = 1;
                this.add(set, gridBagConstraints);

                JLabel name = new JLabel();
                name.setText(award.getName());
                gridBagConstraints.gridx = 2;
                this.add(name, gridBagConstraints);

                JLabel desc = new JLabel();
                desc.setText(award.getDescription());
                gridBagConstraints.gridx = 3;
                this.add(desc, gridBagConstraints);

                i++;
            }

            GridBagConstraints gbc_pnlPortrait = new GridBagConstraints();
            gbc_pnlPortrait = new GridBagConstraints();
            gbc_pnlPortrait.gridx = 0;
            gbc_pnlPortrait.gridy = 0;
            gbc_pnlPortrait.fill = GridBagConstraints.NONE;
            gbc_pnlPortrait.gridheight = i;
            gbc_pnlPortrait.gridwidth = 1;
            gbc_pnlPortrait.anchor = GridBagConstraints.NORTHWEST;
            gbc_pnlPortrait.insets = new Insets(10,10,0,0);
            JPanel pnlPortrait = new CustomPersonPortraitPanel("", "default.gif", awards, iconPackage);
            this.add(pnlPortrait, gbc_pnlPortrait);

            this.revalidate();
            this.repaint();
        }

        private void addHeaders(){

            gridBagConstraints.insets = new Insets(2, 2, 5, 10);

            gridBagConstraints.gridy = 0;
            gridBagConstraints.gridx = 1;
            this.add(new JLabel("<html><b>Set</b></html>"), gridBagConstraints);

            gridBagConstraints.gridx = 2;
            this.add(new JLabel("<html><b>Name</b></html>"), gridBagConstraints);

            gridBagConstraints.gridx = 3;
            this.add(new JLabel("<html><b>Description</b></html>"), gridBagConstraints);
        }
    }

    class AwardListSelectionHandler implements ListSelectionListener{

        JTable myTable;

        public AwardListSelectionHandler(JTable mytable) {
            this.myTable = mytable;
        }

        @Override
        public void valueChanged(ListSelectionEvent listSelectionEvent) {

            ListSelectionModel selectionModel = (ListSelectionModel)listSelectionEvent.getSource();

            if(selectionModel.isSelectionEmpty()) return;

            java.util.List selectedAwards = new ArrayList();

            int minIndex = selectionModel.getMinSelectionIndex();
            int maxIndex = selectionModel.getMaxSelectionIndex();

            for(int i = minIndex; i <= maxIndex; i++){
                if(selectionModel.isSelectedIndex(i)){
                    int modelIndex = myTable.convertRowIndexToModel(i);
                    Award award = awardTableModel.getValueAt(modelIndex);
                    selectedAwards.add(award);
                }
            }

            awardPreviewPanel.updatePreviewForAwards(selectedAwards);
         }
    }
}
