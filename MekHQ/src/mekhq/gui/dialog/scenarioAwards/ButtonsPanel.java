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

package mekhq.gui.dialog.scenarioAwards;

import mekhq.campaign.ResolveScenarioTracker;
import mekhq.campaign.personnel.Award;
import mekhq.gui.model.ScenarioAwardsAwardTableModel;
import mekhq.gui.model.ScenarioAwardsPersonTableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.UUID;

/**
 * This is the center component of the ScenarioAwardsPanel. It includes two buttons that allow the addition and removal
 * of awards to a selected person.
 */
public class ButtonsPanel extends JPanel {

    private JTable personnelTable;
    private HashMap<UUID, ScenarioAwardsAwardTableModel> personAwardTableModelMap;
    private JTable unawardedAwardsTable;
    private JTable awardedAwardsTable;
    private ScenarioAwardsAwardTableModel awardTableModel;
    private ScenarioAwardsPanel parent;

    public ButtonsPanel(JTable personnelTable, HashMap<UUID, ScenarioAwardsAwardTableModel> personAwardTableModelMap,
                        JTable unawardedAwardsTable, JTable awardedAwardsTable, ScenarioAwardsAwardTableModel awardTableModel,
                        ScenarioAwardsPanel parent) {
        super();

        this.personnelTable = personnelTable;
        this.personAwardTableModelMap = personAwardTableModelMap;
        this.unawardedAwardsTable = unawardedAwardsTable;
        this.awardedAwardsTable = awardedAwardsTable;
        this.awardTableModel = awardTableModel;
        this.parent = parent;


        setLayout(new GridBagLayout());

        JButton addButton = new JButton();
        addButton.setAction(new AddAction());
        addButton.setText("<<<");
        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        add(addButton, gridBagConstraints);

        JButton removeButton = new JButton();
        removeButton.setAction(new RemoveAction());
        removeButton.setText(">>>");
        gridBagConstraints.gridy = 1;
        add(removeButton, gridBagConstraints);
    }

    class AddAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {

            ScenarioAwardsPersonTableModel model = (ScenarioAwardsPersonTableModel) personnelTable.getModel();

            boolean firstPersonProcessed = false;

            for(int personIndex : personnelTable.getSelectedRows())
            {
                ResolveScenarioTracker.PersonStatus personStatus = model.getPersonAt(personIndex);
                UUID personId = personStatus.getId();
                ScenarioAwardsAwardTableModel selectedPersonTableModel = personAwardTableModelMap.get(personId);

                int[] selectedAwards = unawardedAwardsTable.getSelectedRows();

                for(int i : selectedAwards){
                    int modelIndex = unawardedAwardsTable.convertRowIndexToModel(i);
                    Award award = awardTableModel.getValueAt(modelIndex);
                    selectedPersonTableModel.addAward(award);
                }

                if(!firstPersonProcessed){
                    parent.repopulatedAwardedAwardsTable(personId, selectedPersonTableModel);
                    parent.repopulateAwardPreviewPanel(personId, selectedPersonTableModel);
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
            ScenarioAwardsAwardTableModel selectedPersonTableModel = personAwardTableModelMap.get(personId);

            int[] selectedAwards = awardedAwardsTable.getSelectedRows();

            for(int i = selectedAwards.length - 1; i >= 0; i--){
                int modelIndex = awardedAwardsTable.convertRowIndexToModel(selectedAwards[i]);
                Award award = selectedPersonTableModel.getValueAt(modelIndex);
                selectedPersonTableModel.removeAward(award);
            }

            parent.repopulatedAwardedAwardsTable(personId, selectedPersonTableModel);
            parent.repopulateAwardPreviewPanel(personId, selectedPersonTableModel);
        }
    }
}
