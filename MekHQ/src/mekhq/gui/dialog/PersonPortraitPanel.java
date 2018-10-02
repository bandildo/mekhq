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

package mekhq.gui.dialog;

import mekhq.IconPackage;
import mekhq.campaign.personnel.Award;
import mekhq.campaign.personnel.Person;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A specific implementation of the CustomPersonPortraitPanel where the award list and the portrait are from a specific Person
 */
public class PersonPortraitPanel extends CustomPersonPortraitPanel{

    public PersonPortraitPanel(Person person, IconPackage ip) {
        super(person.getPortraitCategory(),
                person.getPortraitFileName(),
                person.getAwards(),
                ip);
    }
}
