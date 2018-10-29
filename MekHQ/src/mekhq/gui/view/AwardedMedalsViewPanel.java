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

package mekhq.gui.view;

import megamek.common.util.DirectoryItems;
import mekhq.campaign.personnel.Award;
import mekhq.gui.utilities.ImageHelpers;
import mekhq.gui.utilities.WrapLayout;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This panel presents a list of medals
 */
public class AwardedMedalsViewPanel extends JPanel{

    private Dimension medalDimensions;
    private DirectoryItems awardIcons;

    public AwardedMedalsViewPanel(DirectoryItems awardIcons) {
        this(awardIcons, new Dimension(30,60));
    }

    public AwardedMedalsViewPanel(DirectoryItems awardIcons, Dimension medalDimensions) {
        this.awardIcons = awardIcons;
        this.medalDimensions = medalDimensions;
        this.setName("pnlMedals");
        this.setLayout(new WrapLayout(FlowLayout.LEFT));
    }

    /**
     * Refreshes the view to present the medals of a given list of awards
     * @param awards awards whose medals should be presented
     */
    public void refresh(Collection<Award> awards){

        removeAll();
        List<Award> filteredAwards = awards.stream().filter(a -> a.getNumberOfMedalFiles() > 0).sorted().collect(Collectors.toList());

        for(Award award : filteredAwards){
            JLabel medalLabel = new JLabel();

            Image medal = null;
            try{
                medal = (Image) awardIcons.getItem( award.getSet() + "/medals/", award.getMedalFileName(award.getQuantity()));
                if(medal == null) continue;
                medal = ImageHelpers.getScaledForBoundaries(medal, medalDimensions, Image.SCALE_DEFAULT);
                medalLabel.setIcon(new ImageIcon(medal));
                medalLabel.setToolTipText(award.getTooltipText());
                add(medalLabel);
            }
            catch (Exception err) {
                err.printStackTrace();
            }
        }
    }
}
