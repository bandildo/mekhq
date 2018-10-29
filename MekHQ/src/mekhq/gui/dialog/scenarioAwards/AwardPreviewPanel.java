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
import mekhq.campaign.personnel.Award;
import mekhq.gui.view.AwardedMedalsViewPanel;
import mekhq.gui.view.AwardedMiscViewPanel;
import mekhq.gui.view.CustomPersonPortraitViewPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

/**
 * This panel will serve as a preview to awarded awards.
 */
public class AwardPreviewPanel extends JPanel {

    private AwardedMedalsViewPanel medalsPanel;
    private AwardedMiscViewPanel miscsPanel;
    private CustomPersonPortraitViewPanel personPanel;

    public AwardPreviewPanel(IconPackage iconPackage) {

        super();

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridheight = 2;
        personPanel = new CustomPersonPortraitViewPanel(iconPackage);
        add(personPanel, gbc);

        gbc.weightx = 1.0;
        gbc.weighty = 0.4;
        gbc.gridheight = 1;
        gbc.gridx = 1;
        gbc.gridy = 0;
        medalsPanel = new AwardedMedalsViewPanel(iconPackage.getAwardIcons(), new Dimension(20,40));
        Dimension preferedSize = medalsPanel.getPreferredSize();
        preferedSize.width = 300;
        medalsPanel.setPreferredSize(preferedSize);
        add(medalsPanel, gbc);

        gbc.gridy = 1;
        gbc.weighty = 0.6;
        miscsPanel = new AwardedMiscViewPanel(iconPackage.getAwardIcons(), new Dimension(60,60));
        preferedSize = miscsPanel.getPreferredSize();
        preferedSize.width = 300;
        miscsPanel.setPreferredSize(preferedSize);
        add(miscsPanel, gbc);
    }

    /**
     * Updates the panel for a list of awards
     * @param awards list of awards to preview
     */
    public void updatePreviewForAwards(Collection<Award> awards)
    {
        personPanel.refresh(awards, "", "default.gif");
        medalsPanel.refresh(awards);
        miscsPanel.refresh(awards);

        this.revalidate();
        this.repaint();
    }
}