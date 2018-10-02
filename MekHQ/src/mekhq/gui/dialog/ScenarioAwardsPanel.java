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
import mekhq.campaign.personnel.AwardsFactory;
import mekhq.gui.utilities.JTableUtilities;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.UUID;

public class ScenarioAwardsPanel extends JPanel {

    private ResolveScenarioTracker tracker;

    private JTable personnelTable = new JTable();
    private JList unawardedAwardList = new JList();
    private JList awardedAwardsList = new JList();
    private AwardPreviewPanel awardPreviewPanel;

    private DefaultListModel<Award> allAwardsListModel = new DefaultListModel<>();
    private DefaultListModel<Award> suggestedAwardsListModel = new DefaultListModel<>();
    private DefaultListModel<Award> awardedAwardsListModel = new DefaultListModel<>();

    private HashMap<UUID, DefaultListModel<Award>> awardedAwardsHashMap = new HashMap<>();

    public ScenarioAwardsPanel(ResolveScenarioTracker scenarioTracker, IconPackage iconPackage) {
        super();
        this.setLayout(new GridBagLayout());

        this.tracker = scenarioTracker;

        awardPreviewPanel = new AwardPreviewPanel(iconPackage);

        createPersonnelTable();
        createAvailableAwardList();
        createSuggestedAwardList();
        JPanel unawardedAwardList = createUnawardedAwardList();
        JPanel buttonsPanel = createButtons();
        createAwardedAwardList();
        createAwardPreviewPanel();

        // Personnel Table
        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.insets = new Insets(5, 5, 0, 0);

        this.add(new JScrollPane(personnelTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), gridBagConstraints);

        gridBagConstraints.gridx = 1;
        this.add(unawardedAwardList, gridBagConstraints);

        // Buttons Panel
        gridBagConstraints.gridx = 2;
        this.add(buttonsPanel, gridBagConstraints);

        // Awarded Awards Panel
        JPanel awardedAwardsPanel = new JPanel();
        TitledBorder borderAwarded = new TitledBorder("Awarded");
        borderAwarded.setTitleJustification(TitledBorder.CENTER);
        borderAwarded.setTitlePosition(TitledBorder.TOP);
        awardedAwardsPanel.setBorder(borderAwarded);

        awardedAwardsPanel.add(awardedAwardsList);
        gridBagConstraints.gridx = 3;
        this.add(awardedAwardsPanel, gridBagConstraints);

        // Award preview
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        this.add(awardPreviewPanel, gridBagConstraints);
    }

    private void createPersonnelTable() {

        personnelTable.setModel(new PersonTableModel());

        personnelTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        personnelTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {

                // MAYBE THERE IS A SIMPLER WAY TO GET THE MODEL
                PersonTableModel personTableModel= (PersonTableModel) personnelTable.getModel();
                ResolveScenarioTracker.PersonStatus personStatus = personTableModel.getPersonSelected(personnelTable.getSelectedRow());

                DefaultListModel<Award> model;
                UUID personId = personStatus.getId();

                if(awardedAwardsHashMap.containsKey(personId))
                    model = awardedAwardsHashMap.get(personId);
                else{
                    model = new DefaultListModel<>();
                    awardedAwardsHashMap.put(personId, model);
                }

                awardedAwardsList.setModel(model);
            }
        });

        personnelTable.changeSelection(0,0,false, false);

        JTableUtilities.resizeColumnWidth(personnelTable);
    }

    private void createAvailableAwardList() {

        for (String setName : AwardsFactory.getInstance().getAllSetNames()) {
            for (Award award : AwardsFactory.getInstance().getAllAwardsForSet(setName)) {
                allAwardsListModel.addElement(award);
            }
        }
    }

    private void createSuggestedAwardList() {

        for (String setName : AwardsFactory.getInstance().getAllSetNames()) {
            for (Award award : AwardsFactory.getInstance().getAllAwardsForSet(setName)) {
                if(award.getXPReward() > 0)
                    suggestedAwardsListModel.addElement(award);
            }
        }
    }

    private JPanel createUnawardedAwardList(){

        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.insets = new Insets(5, 5, 0, 0);

        JPanel availableAwardsPanel = new JPanel();
        availableAwardsPanel.setLayout(new GridBagLayout());
        TitledBorder borderAvailableAwards = new TitledBorder("Awards");
        borderAvailableAwards.setTitleJustification(TitledBorder.CENTER);
        borderAvailableAwards.setTitlePosition(TitledBorder.TOP);
        availableAwardsPanel.setBorder(borderAvailableAwards);

        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new GridLayout(0,2));
        JRadioButton allButton = new JRadioButton("All");
        allButton.setSelected(true);
        allButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                unawardedAwardList.setModel(allAwardsListModel);
            }
        });

        JRadioButton suggestedButton = new JRadioButton("Suggested");
        suggestedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                unawardedAwardList.setModel(suggestedAwardsListModel);
            }
        });

        ButtonGroup group = new ButtonGroup();
        group.add(allButton);
        group.add(suggestedButton);
        radioPanel.add(allButton);
        radioPanel.add(suggestedButton);
        availableAwardsPanel.add(radioPanel, gridBagConstraints);

        unawardedAwardList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        unawardedAwardList.setCellRenderer(new AwardCellRenderer());
        unawardedAwardList.addListSelectionListener(new AwardListSelectionHandler());
        unawardedAwardList.setModel(allAwardsListModel);
        gridBagConstraints.gridy = 1;
        availableAwardsPanel.add(unawardedAwardList, gridBagConstraints);

        return availableAwardsPanel;
    }

    private JPanel createButtons() {
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

    private void createAwardedAwardList() {

        awardedAwardsList.setCellRenderer(new AwardCellRenderer());
        awardedAwardsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }

    private void createAwardPreviewPanel(){
        TitledBorder borderAwarded = new TitledBorder("AWARD PREVIEW");
        borderAwarded.setTitleJustification(TitledBorder.CENTER);
        borderAwarded.setTitlePosition(TitledBorder.TOP);
        awardPreviewPanel.setBorder(borderAwarded);
    }

    class AddAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {

            PersonTableModel model = (PersonTableModel) personnelTable.getModel();
            ResolveScenarioTracker.PersonStatus personStatus = model.getPersonSelected(personnelTable.getSelectedRow());

            DefaultListModel<Award> personAwardListModel = awardedAwardsHashMap.get(personStatus.getId());

            for (Object award : unawardedAwardList.getSelectedValuesList()) {
                awardedAwardsListModel.addElement((Award) award);
                //availableAwardsListModel.removeElement(award);

                personAwardListModel.addElement((Award) award);
            }
        }
    }

    class RemoveAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            for (Object award : awardedAwardsList.getSelectedValuesList()) {
                //availableAwardsListModel.addElement((Award) award);
                awardedAwardsListModel.removeElement(award);
            }
        }
    }

    public class AwardCellRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Award) {
                String text = String.format("(%s) %s", ((Award) value).getSet(), ((Award) value).getName());
                setText(text);
            }
            return this;
        }
    }

    class PersonTableModel extends AbstractTableModel {

        private java.util.List<ResolveScenarioTracker.PersonStatus> personStatusList;

        public PersonTableModel() {
            personStatusList = tracker.getSortedPeople();
        }

        private String[] columnNames = {"Name", "Unit"};

        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return personStatusList.size();
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public ResolveScenarioTracker.PersonStatus getPersonSelected(int row){
            return personStatusList.get(row);
        }

        public Object getValueAt(int row, int col) {

            if (col == 0) {
                return personStatusList.get(row).getName();
            }
            if (col == 1){
                return personStatusList.get(row).getUnitName();
            }
            return "[MISSING DATA]";
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

        @Override
        public void valueChanged(ListSelectionEvent listSelectionEvent) {

            JList list = (JList)listSelectionEvent.getSource();

            if(!listSelectionEvent.getValueIsAdjusting() && !list.isSelectionEmpty()){
                awardPreviewPanel.updatePreviewForAwards(list.getSelectedValuesList());
            }
        }
    }
}
