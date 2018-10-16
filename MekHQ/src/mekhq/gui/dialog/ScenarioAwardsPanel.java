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
import mekhq.gui.view.AwardedMedalsViewPanel;
import mekhq.gui.view.AwardedMiscViewPanel;
import mekhq.gui.view.CustomPersonPortraitViewPanel;

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
import java.util.*;

public class ScenarioAwardsPanel extends JPanel {

    private ResolveScenarioTracker tracker;

    private ScenarioAwardsAwardTableModel awardTableModel = new ScenarioAwardsAwardTableModel();
    private ScenarioAwardsPersonTableModel personTableModel;

    private JTable personnelTable = new JTable();
    private JTable unawardedAwardsTable = new JTable();
    private JTable awardedAwardsTable = new JTable();

    private AwardPreviewPanel awardPreviewPanel;

    private HashMap<UUID, Set<Award>> awardedAwardsMap = new HashMap<>();

    public ScenarioAwardsPanel(ResolveScenarioTracker scenarioTracker, IconPackage iconPackage) {
        super();
        this.tracker = scenarioTracker;
        personTableModel = new ScenarioAwardsPersonTableModel(scenarioTracker);

        this.setLayout(new GridBagLayout());

        // This needs to be first because some listeners depend on it :(
        awardPreviewPanel = new AwardPreviewPanel(iconPackage);

        // Initializing gbc
        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;

        // Awarded Awards Panel
        gridBagConstraints.gridx = 1;
        JPanel leftSidePanel = createLeftSidePanel();
        this.add(leftSidePanel, gridBagConstraints);

        // Buttons Panel
        gridBagConstraints.gridx = 2;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        JPanel buttonsPanel = createButtonsPanel();
        this.add(buttonsPanel, gridBagConstraints);

        // Unawarded Awards
        gridBagConstraints.gridx = 3;
        JPanel unawardedAwardsPanel = createUnawardedAwardsPanel();
        this.add(unawardedAwardsPanel, gridBagConstraints);
    }

    private JPanel createLeftSidePanel() {
        JPanel leftSidePanel = new JPanel();
        leftSidePanel.setLayout(new GridBagLayout());
        TitledBorder borderAwarded = new TitledBorder("Personnel");
        borderAwarded.setTitleJustification(TitledBorder.CENTER);
        borderAwarded.setTitlePosition(TitledBorder.TOP);
        leftSidePanel.setBorder(borderAwarded);

        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        JScrollPane personnelPanel = createPersonnelPanel();
        leftSidePanel.add(personnelPanel, gridBagConstraints);

        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.gridy = 1;
        leftSidePanel.add(awardPreviewPanel, gridBagConstraints);

        gridBagConstraints.gridy = 2;
        awardedAwardsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        awardedAwardsTable.setModel(awardTableModel);
        awardedAwardsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JTableUtilities.resizeColumnWidth(awardedAwardsTable);
        awardedAwardsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent event) {
                unawardedAwardsTable.clearSelection();
            }
        });
        JScrollPane awardedAwardsPane = new JScrollPane(awardedAwardsTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        Dimension preferedSize = awardedAwardsPane.getPreferredSize();
        preferedSize.width = 400;
        preferedSize.height = 225;
        awardedAwardsPane.setPreferredSize(preferedSize);
        leftSidePanel.add(awardedAwardsPane, gridBagConstraints);

        return leftSidePanel;
    }

    private JScrollPane createPersonnelPanel() {
        personnelTable.setModel(personTableModel);
        personnelTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        personnelTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        personnelTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                int personIndex = personnelTable.convertRowIndexToModel(personnelTable.getSelectedRow());
                ResolveScenarioTracker.PersonStatus personStatus = personTableModel.getPersonAt(personIndex);
                UUID personId = personStatus.getId();

                Set<Award> awardedAwards;

                if(awardedAwardsMap.containsKey(personId)){
                    awardedAwards = awardedAwardsMap.get(personId);
                }
                else{
                    awardedAwards = new HashSet<>();
                    awardedAwardsMap.put(personId, awardedAwards);
                }

                repopulatedAwardedAwardsTable(personId, awardedAwards);
                repopulateAwardPreviewPanel(personId, awardedAwards);
            }
        });

        personnelTable.changeSelection(0,0,false, false);

        JScrollPane pane = new JScrollPane(personnelTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        Dimension preferedSize = pane.getPreferredSize();
        preferedSize.width = 400;
        preferedSize.height = 125;
        pane.setPreferredSize(preferedSize);
        return pane;
    }

    private void repopulatedAwardedAwardsTable(UUID personId, Collection<Award> awardedAwards){

        TableRowSorter<ScenarioAwardsAwardTableModel> sorter = new TableRowSorter<>(awardTableModel);

        sorter.setRowFilter(new AwardedAwardsFilter(awardedAwards));
        awardedAwardsTable.setRowSorter(sorter);
        JTableUtilities.resizeColumnWidth(awardedAwardsTable);
    }

    private void repopulateAwardPreviewPanel(UUID personId, Collection<Award> awardedAwards){
        awardPreviewPanel.updatePreviewForAwards(awardedAwards);
    }

    private JPanel createUnawardedAwardsPanel() {

        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;

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
//        unawardedAwardsTable.removeColumn(unawardedAwardsTable.getColumnModel().getColumn(1));
        JTableUtilities.resizeColumnWidth(unawardedAwardsTable);
        unawardedAwardsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent event) {
                awardedAwardsTable.clearSelection();
            }
        });

        gridBagConstraints.gridy = 2;
        JScrollPane pane = new JScrollPane(unawardedAwardsTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        Dimension preferedSize = pane.getPreferredSize();
        preferedSize.width = 400;
        pane.setPreferredSize(preferedSize);
        unawardedAwardsPanel.add(pane, gridBagConstraints);

        return unawardedAwardsPanel;
    }

    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        JButton addButton = new JButton();
        addButton.setAction(new AddAction());
        addButton.setText("<<<");
        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        panel.add(addButton, gridBagConstraints);

        JButton removeButton = new JButton();
        removeButton.setAction(new RemoveAction());
        removeButton.setText(">>>");
        gridBagConstraints.gridy = 1;
        panel.add(removeButton, gridBagConstraints);

        return panel;
    }

    public HashMap<UUID, Set<Award>> getAwardedAwardsMap(){ return awardedAwardsMap; }

    class AddAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {

            ScenarioAwardsPersonTableModel model = (ScenarioAwardsPersonTableModel) personnelTable.getModel();

            boolean firstPersonProcessed = false;

            for(int personIndex : personnelTable.getSelectedRows())
            {
                ResolveScenarioTracker.PersonStatus personStatus = model.getPersonAt(personIndex);
                UUID personId = personStatus.getId();
                Set<Award> awardedAwards = awardedAwardsMap.get(personId);

                int[] selectedAwards = unawardedAwardsTable.getSelectedRows();

                for(int i : selectedAwards){
                    int modelIndex = unawardedAwardsTable.convertRowIndexToModel(i);
                    Award award = awardTableModel.getValueAt(modelIndex);

                    if(awardedAwards.contains(award)){
                        Award personAward = awardedAwards.stream().filter(award::equals).findAny().orElse(null);
                        personAward.incrementQuantity();
                    }
                    else{
                        award.incrementQuantity();
                        awardedAwards.add(award);
                    }
                }

                if(!firstPersonProcessed){
                    repopulatedAwardedAwardsTable(personId, awardedAwards);
                    repopulateAwardPreviewPanel(personId, awardedAwards);
                }
                firstPersonProcessed = true;
            }
        }
    }

    class RemoveAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {

            ScenarioAwardsPersonTableModel model = (ScenarioAwardsPersonTableModel) personnelTable.getModel();
            ResolveScenarioTracker.PersonStatus personStatus = model.getPersonAt(personnelTable.getSelectedRow());
            UUID personId = personStatus.getId();
            Set<Award> awardedAwards = awardedAwardsMap.get(personId);

            int[] selectedAwards = awardedAwardsTable.getSelectedRows();

            for(int i = selectedAwards.length - 1; i >= 0; i--){
                int modelIndex = awardedAwardsTable.convertRowIndexToModel(selectedAwards[i]);
                Award award = awardTableModel.getValueAt(modelIndex);

                if(awardedAwards.contains(award)){
                    Award personAward = awardedAwards.stream().filter(award::equals).findAny().orElse(null);
                    personAward.decrementQuantity();
                    if(personAward.getQuantity() == 0){
                        awardedAwards.remove(award);
                    }
                }

                repopulatedAwardedAwardsTable(personId, awardedAwards);
                repopulateAwardPreviewPanel(personId, awardedAwards);
            }

            repopulatedAwardedAwardsTable(personId, awardedAwards);
            repopulateAwardPreviewPanel(personId, awardedAwards);
        }
    }

    class AwardPreviewPanel extends JPanel{

        private AwardedMedalsViewPanel medalsPanel;
        private AwardedMiscViewPanel miscsPanel;
        private CustomPersonPortraitViewPanel personPanel;

        public AwardPreviewPanel(IconPackage iconPackage) {

            super();

            setLayout(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();

            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.gridheight = 2;
            personPanel = new CustomPersonPortraitViewPanel(iconPackage);
            add(personPanel, gbc);

            gbc.weightx = 1.0;
            gbc.weighty = 0.4;
            gbc.gridheight = 1;
            gbc.gridx = 1;
            gbc.gridy = 0;
            medalsPanel = new AwardedMedalsViewPanel(iconPackage.getAwardIcons(), new Dimension(20,40));
            Dimension preferedSize = medalsPanel.getPreferredSize();
            preferedSize.width = 300;
            medalsPanel.setPreferredSize(preferedSize);
            add(medalsPanel, gbc);

            gbc.gridy = 1;
            gbc.weighty = 0.6;
            miscsPanel = new AwardedMiscViewPanel(iconPackage.getAwardIcons(), new Dimension(60,60));
            preferedSize = miscsPanel.getPreferredSize();
            preferedSize.width = 300;
            miscsPanel.setPreferredSize(preferedSize);
            add(miscsPanel, gbc);
        }

        public void updatePreviewForAwards(Collection<Award> awards)
        {
            personPanel.refresh(awards, "", "default.gif");
            medalsPanel.refresh(awards);
            miscsPanel.refresh(awards);

            this.revalidate();
            this.repaint();
        }
    }
}
