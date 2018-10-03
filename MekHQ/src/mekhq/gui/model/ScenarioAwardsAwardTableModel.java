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

import mekhq.campaign.personnel.Award;
import mekhq.campaign.personnel.AwardsFactory;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class ScenarioAwardsAwardTableModel extends AbstractTableModel {

    private java.util.List<Award> awards;

    public ScenarioAwardsAwardTableModel() {
        super();
        awards = new ArrayList<>();
        for (String setName : AwardsFactory.getInstance().getAllSetNames()) {
            for (Award award : AwardsFactory.getInstance().getAllAwardsForSet(setName)) {
                awards.add(award);
            }
        }
    }

    private String[] columnNames = {"Set", "Name", "XP", "Edge", "Desc."};

    @Override
    public int getRowCount() {
        return awards.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int row, int col) {
        if (col == 0) {
            return awards.get(row).getSet();
        }
        if (col == 1){
            return awards.get(row).getName();
        }
        if (col == 2){
            return awards.get(row).getXPReward();
        }
        if (col == 3){
            return awards.get(row).getEdgeReward();
        }
        if (col == 4){
            return awards.get(row).getDescription();
        }
        return "[MISSING DATA]";
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Award getValueAt(Integer index) {
        return awards.get(index);
    }
}
