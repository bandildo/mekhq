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

import mekhq.IconPackage;
import mekhq.campaign.ResolveScenarioTracker;
import mekhq.gui.filter.AwardedAwardsFilter;
import mekhq.gui.model.ScenarioAwardsAwardTableModel;
import mekhq.gui.model.ScenarioAwardsPersonTableModel;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.HashMap;
import java.util.UUID;

/**
 * This is the main panel which will contain all the other components relatively to awarding awards in the end of a scenario
 */
public class ScenarioAwardsPanel extends JPanel {

    private ResolveScenarioTracker tracker;

    private ScenarioAwardsAwardTableModel awardTableModel = new ScenarioAwardsAwardTableModel();
    private ScenarioAwardsPersonTableModel personTableModel;

    private JTable personnelTable = new JTable();
    private JTable unawardedAwardsTable = new JTable();
    private JTable awardedAwardsTable = new JTable();

    private AwardPreviewPanel awardPreviewPanel;

    private HashMap<UUID, ScenarioAwardsAwardTableModel> personAwardTableModelMap = new HashMap<>();

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
        JPanel awardedAwardsPanel = new AwardedAwardsPanel(awardPreviewPanel, awardedAwardsTable, unawardedAwardsTable, personnelTable, awardTableModel,
                personAwardTableModelMap, personTableModel, this);
        this.add(awardedAwardsPanel, gridBagConstraints);

        // Buttons Panel
        gridBagConstraints.gridx = 2;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        JPanel buttonsPanel = new ButtonsPanel(personnelTable, personAwardTableModelMap, unawardedAwardsTable, awardedAwardsTable, awardTableModel, this);
        this.add(buttonsPanel, gridBagConstraints);

        // Unawarded Awards
        gridBagConstraints.gridx = 3;
        JPanel unawardedAwardsPanel = new UnawardedAwardsPanel(unawardedAwardsTable, awardedAwardsTable, awardTableModel);
        this.add(unawardedAwardsPanel, gridBagConstraints);

    }

    public HashMap<UUID, ScenarioAwardsAwardTableModel> getPersonAwardTableModelMap(){
        return personAwardTableModelMap;
    }

    /**
     * Repopulates the awarded awards table based on the person selected
     * @param personId id of the selected person (currently not used)
     * @param tableModel award table model of the selected person
     */
    public void repopulatedAwardedAwardsTable(UUID personId, ScenarioAwardsAwardTableModel tableModel) {
        awardedAwardsTable.setModel(tableModel);
        TableRowSorter<ScenarioAwardsAwardTableModel> sorter = new TableRowSorter<>((ScenarioAwardsAwardTableModel) awardedAwardsTable.getModel());
        sorter.setRowFilter(new AwardedAwardsFilter());
        awardedAwardsTable.setRowSorter(sorter);
    }

    /**
     * Repopulates the preview of the awarded awards
     * @param personId id of the selected person (currently not used)
     * @param tableModel award table model of the selected person
     */
    public void repopulateAwardPreviewPanel(UUID personId, ScenarioAwardsAwardTableModel tableModel){
        awardPreviewPanel.updatePreviewForAwards(tableModel.getAwards());
    }
}
