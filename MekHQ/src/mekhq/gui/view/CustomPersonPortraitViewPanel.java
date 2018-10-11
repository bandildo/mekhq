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

import megamek.common.Crew;
import megamek.common.util.DirectoryItems;
import mekhq.IconPackage;
import mekhq.campaign.personnel.Award;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class extends a JPanel that will display a portrait icon and, underneath, the ribbons of a given award list
 */
public class CustomPersonPortraitViewPanel extends JPanel {

    private JLabel lblPortrait;

    private DirectoryItems portraits;
    private DirectoryItems awardIcons;

    private List<Award> awards;
    private String portraitIconCategory;
    private String portraitIconFilename;

    private static final int MAX_NUMBER_OF_RIBBON_AWARDS_PER_ROW = 4;

    public CustomPersonPortraitViewPanel(String portraitIconCategory, String portraitIconFilename, List<Award> awards, IconPackage ip) {

        super();
        this.portraits = ip.getPortraits();
        this.awardIcons = ip.getAwardIcons();
        this.portraitIconCategory = portraitIconCategory;
        this.portraitIconFilename = portraitIconFilename;
        this.awards = awards;

        lblPortrait = new JLabel();

        setName("pnlPortrait");
        setBackground(Color.WHITE);
        setLayout(new GridBagLayout());

        lblPortrait.setName("lblPortait"); // NOI18N
        lblPortrait.setBackground(Color.WHITE);
        setPortrait();

        drawRibbons();

        GridBagConstraints gbc_lblPortrait = new GridBagConstraints();
        gbc_lblPortrait.gridx = 0;
        gbc_lblPortrait.gridy = 0;
        gbc_lblPortrait.fill = GridBagConstraints.NONE;
        gbc_lblPortrait.anchor = GridBagConstraints.NORTHWEST;
        gbc_lblPortrait.insets = new Insets(0,0,0,0);
        add(lblPortrait, gbc_lblPortrait);
    }

    /**
     * set the portrait for the given person.
     *
     * @return The <code>Image</code> of the pilot's portrait. This value
     *         will be <code>null</code> if no portrait was selected
     *          or if there was an error loading it.
     */
    private void setPortrait() {

        if(Crew.ROOT_PORTRAIT.equals(portraitIconCategory)) {
            portraitIconCategory = ""; //$NON-NLS-1$
        }

        // Return a null if the player has selected no portrait file.
        if ((null == portraitIconCategory) || (null == portraitIconFilename) || Crew.PORTRAIT_NONE.equals(portraitIconFilename)) {
            portraitIconFilename = "default.gif"; //$NON-NLS-1$
        }

        // Try to get the player's portrait file.
        Image portrait = null;
        try {
            portrait = (Image) portraits.getItem(portraitIconCategory, portraitIconFilename);
            if(null != portrait) {
                portrait = portrait.getScaledInstance(100, -1, Image.SCALE_DEFAULT);
            } else {
                portrait = (Image) portraits.getItem("", "default.gif");  //$NON-NLS-1$ //$NON-NLS-2$
                if(null != portrait) {
                    portrait = portrait.getScaledInstance(100, -1, Image.SCALE_DEFAULT);
                }
            }
            lblPortrait.setIcon(new ImageIcon(portrait));
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    /**
     * Draws the ribbons below the person portrait.
     */
    private void drawRibbons() {

        Box boxRibbons;

        List<Award> ribbonAwards = awards.stream().filter(a -> a.getRibbonFileName() != null).collect(Collectors.toList());

        if(ribbonAwards.size() <= 0){
            return;
        }

        boxRibbons = Box.createVerticalBox();
        boxRibbons.add(Box.createRigidArea(new Dimension(100,0)));
        GridBagConstraints gbc_pnlAllRibbons = new GridBagConstraints();
        gbc_pnlAllRibbons.gridx = 0;
        gbc_pnlAllRibbons.gridy = 1;
        gbc_pnlAllRibbons.fill = GridBagConstraints.NONE;
        gbc_pnlAllRibbons.anchor = GridBagConstraints.NORTHWEST;
        gbc_pnlAllRibbons.insets = new Insets(0,0,0,0);
        Collections.reverse(awards);

        int i = 0;
        Box rowRibbonsBox = null;
        ArrayList<Box> rowRibbonsBoxes = new ArrayList<>();

        for(Award award : awards){
            JLabel ribbonLabel = new JLabel();
            Image ribbon;

            if(i%MAX_NUMBER_OF_RIBBON_AWARDS_PER_ROW == 0){
                rowRibbonsBox = Box.createHorizontalBox();
                rowRibbonsBox.setBackground(Color.RED);
            }
            try{
                ribbon = (Image) awardIcons.getItem(award.getSet() + "/ribbons/", award.getRibbonFileName());
                if(ribbon == null) continue;
                ribbon = ribbon.getScaledInstance(25,8, Image.SCALE_DEFAULT);
                ribbonLabel.setIcon(new ImageIcon(ribbon));
                ribbonLabel.setToolTipText(award.getTooltipText());
                rowRibbonsBox.add(ribbonLabel, 0);
            }
            catch (Exception err) {
                err.printStackTrace();
            }

            i++;
            if(i%MAX_NUMBER_OF_RIBBON_AWARDS_PER_ROW == 0){
                rowRibbonsBoxes.add(rowRibbonsBox);
            }
        }
        if(i%MAX_NUMBER_OF_RIBBON_AWARDS_PER_ROW!=0){
            rowRibbonsBoxes.add(rowRibbonsBox);
        }

        Collections.reverse(rowRibbonsBoxes);
        for(Box box : rowRibbonsBoxes){
            boxRibbons.add(box);
        }

        add(boxRibbons, gbc_pnlAllRibbons);
    }
}
