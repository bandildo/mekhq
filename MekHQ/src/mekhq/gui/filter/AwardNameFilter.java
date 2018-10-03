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

package mekhq.gui.filter;

import mekhq.campaign.personnel.Award;
import mekhq.gui.model.ScenarioAwardsAwardTableModel;

import javax.swing.*;

public class AwardNameFilter extends RowFilter<ScenarioAwardsAwardTableModel, Integer> {

    String text;

    public AwardNameFilter(String text) {
        this.text = text.toLowerCase();
    }

    @Override
    public boolean include(Entry<? extends ScenarioAwardsAwardTableModel, ? extends Integer> entry) {
        ScenarioAwardsAwardTableModel awardTableModel = entry.getModel();
        Award award = awardTableModel.getValueAt(entry.getIdentifier());

        if(award.getName().toLowerCase().contains(text) || award.getSet().toLowerCase().contains(text)){
            return true;
        }

        return false;
    }
}
