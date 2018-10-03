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

package mekhq.gui.model;

import mekhq.campaign.ResolveScenarioTracker;

import javax.swing.table.AbstractTableModel;

public class ScenarioAwardsPersonTableModel extends AbstractTableModel {

    private java.util.List<ResolveScenarioTracker.PersonStatus> personStatusList;

    public ScenarioAwardsPersonTableModel(ResolveScenarioTracker tracker) {
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

    public ResolveScenarioTracker.PersonStatus getPersonAt(int row){
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
