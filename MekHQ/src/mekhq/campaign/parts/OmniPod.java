/*
 * Copyright (C) 2017 MegaMek team
 * 
 * This file is part of MekHQ.
 * 
 * MekHQ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
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

package mekhq.campaign.parts;

import java.io.PrintWriter;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import megamek.common.EquipmentType;
import megamek.common.TechConstants;
import mekhq.MekHqXmlUtil;
import mekhq.Version;
import mekhq.campaign.Campaign;
import mekhq.campaign.personnel.SkillType;

/**
 * An empty omnipod, which can be purchased or created when equipment is removed from a pod.
 * When fixed, the omnipod is removed from the warehouse and one replacement part is podded.
 * 
 * @author Neoancient
 *
 */
public class OmniPod extends Part {

    private static final long serialVersionUID = -8236359530423260992L;
    
    // Pods are specific to the type of equipment they contain.
    private Part partType;

    public OmniPod(Part partType, Campaign c) {
        super(0, false, c);
        this.partType = partType;
        partType.setOmniPodded(false);
        name = "OmniPod";
    }

    @Override
    public String getDetails() {
        return partType.getName();
    }
    
    @Override
    public int getBaseTime() {
        return partType.getBaseTime();
    }

    @Override
    public void updateConditionFromPart() {
        // do nothing
    }

    //This can only be found in the warehouse
    @Override
    public int getLocation() {
        return -1;
    }

    @Override
    public String checkFixable() {
        if (partType.getMissingPart().isReplacementAvailable()) {
            return null;
        }
        return "No equipment available to install";
    }

    //Podding equipment is a Class D (Maintenance) refit, which carries a +2 modifier.
    @Override
    public int getDifficulty() {
        return partType.getDifficulty() + 2;
    }

    //Weight is negligible
    @Override
    public double getTonnage() {
        return 0;
    }

    //Using tech rating for Omni construction option from IOps.
    @Override
    public int getTechRating() {
        return EquipmentType.RATING_E;
    }

    @Override
    public int getAvailability(int era) {
        if (era == EquipmentType.ERA_SL
                || (era == EquipmentType.ERA_SW && partType.getTechBase() == T_IS)) {
            return EquipmentType.RATING_X;
        } else if (era == EquipmentType.ERA_DA) {
            return Math.max(partType.getAvailability(era), EquipmentType.RATING_D);
        } else {
            return Math.max(partType.getAvailability(era), EquipmentType.RATING_E);
        }
    }

    @Override
    public int getIntroDate() {
        if (partType.getTechBase() == T_IS) {
            return Math.max(3052, partType.getIntroDate());
        } else {
            return Math.max(2850, partType.getIntroDate());
        }
    }

    @Override
    public int getExtinctDate() {
        return partType.getExtinctDate();
    }

    @Override
    public int getReIntroDate() {
        return partType.getReIntroDate();
    }

    @Override
    protected void loadFieldsFromXmlNode(Node wn) {
        NodeList nl = wn.getChildNodes();

        for (int x=0; x<nl.getLength(); x++) {
            Node wn2 = nl.item(x);
            if (wn2.getNodeName().equalsIgnoreCase("partType")) {
                partType = (MissingPart)Part.generateInstanceFromXML(wn2, new Version(null));
            }
        }
    }

    @Override
    public String getLocationName() {
        return null;
    }

    @Override
    public void updateConditionFromEntity(boolean checkForDestruction) {
        //do nothing
    }

    @Override
    public void remove(boolean salvage) {
        //do nothing
    }

    @Override
    public MissingPart getMissingPart() {
        return null;
    }

    @Override
    public boolean needsFixing() {
        return true;
    }
    
    @Override
    public void fix() {
        Part newPart = partType.clone();
        Part oldPart = campaign.checkForExistingSparePart(newPart.clone());
        if(null != oldPart) {
            newPart.setOmniPodded(true);
            campaign.addPart(newPart, 0);
            oldPart.decrementQuantity();
        }
    }
    
    
    @Override
    public String fail(int rating) {
        skillMin = ++rating;
        timeSpent = 0;
        shorthandedMod = 0;
        if(skillMin > SkillType.EXP_ELITE) {
            return " <font color='red'><b> failed and part destroyed.</b></font>";
        } else {
            //OmniPod is only added back to warehouse if repair fails without destroying part. 
            campaign.addPart(this, 0);
            return " <font color='red'><b> failed.</b></font>";
        }
    }
    
    @Override
    public String getStatus() {
        String toReturn = "Empty";
        if(isReservedForRefit()) {
            toReturn = "Reserved for Refit";
        }
        if(isReservedForReplacement()) {
            toReturn = "Reserved for Repair";
        }
        if(isBeingWorkedOn()) {
            toReturn = "Being worked on";
        }
        if(!isPresent()) {
            //toReturn = "" + getDaysToArrival() + " days to arrival";
            String dayName = "day";
            if(getDaysToArrival() > 1) {
                dayName += "s";
            }
            toReturn = "In transit (" + getDaysToArrival() + " " + dayName + ")";
        }
        return toReturn;
    }

    @Override
    public long getStickerPrice() {
        return (long)Math.ceil(partType.getStickerPrice() / 5.0);
    }

    @Override
    public int getTechLevel() {
        if (partType.isClanTechBase()) {
            return TechConstants.T_CLAN_TW;
        }
        return TechConstants.T_IS_TW_ALL;
    }

    @Override
    public boolean isSamePartType(Part part) {
        return part instanceof OmniPod
                && (partType.isSamePartType(((OmniPod)part).partType));
    }

    @Override
    public void writeToXml(PrintWriter pw1, int indent) {
        pw1.println(MekHqXmlUtil.indentStr(indent + 1) + "<partType>");
        partType.writeToXml(pw1, indent + 1);
        pw1.println(MekHqXmlUtil.indentStr(indent + 1) + "</partType>");
    }

    @Override
    public Part clone() {
        Part p = new OmniPod(partType, campaign);
        p.copyBaseData(this);
        return p;
    }

}
