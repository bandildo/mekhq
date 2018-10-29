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
import mekhq.gui.model.ScenarioAwardsAwardTableModel;
import mekhq.gui.model.ScenarioAwardsPersonTableModel;
import mekhq.gui.utilities.JTableUtilities;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.HashMap;
import java.util.UUID;

/**
 * This is the leftmost panel in the ScenarioAwardsPanel. It will serve as a container for the list of personnel (top),
 * to the list of awards desired to be awarded to that person (bottom), and a preview of those awards (center)
 */
public class AwardedAwardsPanel extends JPanel {

    private AwardPreviewPanel awardPreviewPanel;
    private JTable awardedAwardsTable;
    private JTable unawardedAwardsTable;
    private JTable personnelTable;
    private ScenarioAwardsAwardTableModel awardTableModel;
    private HashMap<UUID, ScenarioAwardsAwardTableModel> personAwardTableModelMap;
    private ScenarioAwardsPersonTableModel personTableModel;
    private ScenarioAwardsPanel parent;

    public AwardedAwardsPanel(AwardPreviewPanel awardPreviewPanel, JTable awardedAwardsTable, JTable unawardedAwardsTable,
                              JTable personnelTable, ScenarioAwardsAwardTableModel awardTableModel,
                              HashMap<UUID, ScenarioAwardsAwardTableModel> personAwardTableModelMap,
                              ScenarioAwardsPersonTableModel personTableModel, ScenarioAwardsPanel parent) {
        super();
        this.awardPreviewPanel = awardPreviewPanel;
        this.awardedAwardsTable = awardedAwardsTable;
        this.unawardedAwardsTable = unawardedAwardsTable;
        this.personnelTable = personnelTable;
        this.awardTableModel = awardTableModel;
        this.personAwardTableModelMap = personAwardTableModelMap;
        this.personTableModel = personTableModel;
        this.parent = parent;

        setLayout(new GridBagLayout());
        TitledBorder borderAwarded = new TitledBorder("Personnel");
        borderAwarded.setTitleJustification(TitledBorder.CENTER);
        borderAwarded.setTitlePosition(TitledBorder.TOP);
        setBorder(borderAwarded);

        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        JScrollPane personnelPanel = createPersonnelPanel();
        add(personnelPanel, gridBagConstraints);

        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.gridy = 1;
        add(this.awardPreviewPanel, gridBagConstraints);

        gridBagConstraints.gridy = 2;
        this.awardedAwardsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        this.awardedAwardsTable.setModel(this.awardTableModel);
        this.awardedAwardsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JTableUtilities.resizeColumnWidth(this.awardedAwardsTable);

        this.awardedAwardsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent event) {
                AwardedAwardsPanel.this.unawardedAwardsTable.clearSelection();
            }
        });

        JScrollPane awardedAwardsPane = new JScrollPane(this.awardedAwardsTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        Dimension preferedSize = awardedAwardsPane.getPreferredSize();
        preferedSize.width = 400;
        preferedSize.height = 225;
        awardedAwardsPane.setPreferredSize(preferedSize);
        add(awardedAwardsPane, gridBagConstraints);
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

                ScenarioAwardsAwardTableModel selectedPersonTableModel;

                if(personAwardTableModelMap.containsKey(personId)){
                    selectedPersonTableModel = personAwardTableModelMap.get(personId);
                }
                else{
                    selectedPersonTableModel = new ScenarioAwardsAwardTableModel();
                    personAwardTableModelMap.put(personId, selectedPersonTableModel);
                }

                parent.repopulatedAwardedAwardsTable(personId, selectedPersonTableModel);
                parent.repopulateAwardPreviewPanel(personId, selectedPersonTableModel);
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
}
