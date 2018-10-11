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
import java.util.List;
import java.util.stream.Collectors;

public class AwardedMedalsViewPanel extends JPanel{

    private List<Award> awards;
    private DirectoryItems awardIcons;

    public AwardedMedalsViewPanel(List<Award> awards, DirectoryItems awardIcons) {
        this(awards, awardIcons, new Dimension(30,60));
    }

    public AwardedMedalsViewPanel(List<Award> awards, DirectoryItems awardIcons, Dimension medalDimensions) {
        this.awards = awards.stream().filter(a -> a.getMedalFileName() != null).sorted().collect(Collectors.toList());
        this.awardIcons = awardIcons;
        drawMedals(medalDimensions);
    }

    public void drawMedals(Dimension medalDimensions){

        this.setName("pnlMedals");
        this.setLayout(new WrapLayout(FlowLayout.LEFT));

        for(Award award : awards){
            JLabel medalLabel = new JLabel();

            Image medal = null;
            try{
                medal = (Image) awardIcons.getItem( award.getSet() + "/medals/", award.getMedalFileName());
                if(medal == null) continue;
                medal = ImageHelpers.getScaledForBoundaries(medal, medalDimensions, Image.SCALE_DEFAULT);
                medalLabel.setIcon(new ImageIcon(medal));
                medalLabel.setToolTipText(award.getTooltipText());
                this.add(medalLabel);
            }
            catch (Exception err) {
                err.printStackTrace();
            }
        }
    }
}
