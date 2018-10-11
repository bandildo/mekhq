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
import mekhq.gui.dialog.ScenarioAwardsPanel;
import mekhq.gui.utilities.ImageHelpers;
import mekhq.gui.utilities.WrapLayout;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class AwardedMiscViewPanel extends JPanel {

    private List<Award> awards;
    private DirectoryItems awardIcons;

    public AwardedMiscViewPanel(List<Award> awards, DirectoryItems awardIcons) {
        this(awards, awardIcons, new Dimension(100,100));
    }

    public AwardedMiscViewPanel(List<Award> awards, DirectoryItems awardIcons, Dimension meiscDimensions) {
        this.awards = awards.stream().filter(a -> a.getMiscFileName() != null).sorted().collect(Collectors.toList());
        this.awardIcons = awardIcons;
        drawMisc(meiscDimensions);
    }

    private void drawMisc(Dimension dimension) {

        this.setName("pnlMisc");
        this.setLayout(new WrapLayout(FlowLayout.LEFT));

        for (Award award : awards) {
            JLabel miscLabel = new JLabel();

            Image miscAward = null;
            try {
                Image miscAwardBufferedImage = (Image) awardIcons.getItem(award.getSet() + "/misc/", award.getMiscFileName());
                if (miscAwardBufferedImage == null) continue;
                miscAward = ImageHelpers.getScaledForBoundaries(miscAwardBufferedImage, dimension, Image.SCALE_DEFAULT);
                miscLabel.setIcon(new ImageIcon(miscAward));
                miscLabel.setToolTipText(award.getTooltipText());
                this.add(miscLabel);
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
    }
}
