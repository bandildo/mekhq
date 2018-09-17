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

package mekhq.campaign.log;

import megamek.common.util.EncodeControl;
import mekhq.campaign.personnel.Award;
import mekhq.campaign.personnel.Person;

import java.text.MessageFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is responsible to control the logging of Award Log Entries.
 * @author Miguel Azevedo
 */
public class AwardLogger {
    private ResourceBundle logEntriesResourceMap;
    private static AwardLogger awardLogger;

    public AwardLogger() {
        this.logEntriesResourceMap = ResourceBundle.getBundle("mekhq.resources.LogEntries", new EncodeControl());
    }
    public static AwardLogger getInstance(){
        if(null == awardLogger){
            awardLogger = new AwardLogger();
        }
        return awardLogger;
    }

    public void award(Person person, Date date, Award award){
        String message = logEntriesResourceMap.getString("awarded.text");
        person.addLogEntry(new AwardLogEntry(date, MessageFormat.format(message, award.getName(), award.getSet(), award.getDescription())));
    }

    public void removedAward(Person person, Date date, Award award){
        String message = logEntriesResourceMap.getString("removedAward.text");
        person.addLogEntry(new AwardLogEntry(date, MessageFormat.format(message, award.getName())));
    }

    /**
     * Finds the award corresponding to a log entry
     * @param person owner of the log entry
     * @param logEntryText text of the log entry
     * @return award of the owner corresponding to the log entry text
     */
    public Award getAwardFromLogEntry(Person person, String logEntryText){
        String message = logEntriesResourceMap.getString("awarded.text");
        Pattern pattern = Pattern.compile(MessageFormat.format(message, "(.*)", "(.*)", "(.*)"));
        Matcher matcher = pattern.matcher(logEntryText);

        if(matcher.matches())
            return person.awardController.getAward(matcher.group(2), matcher.group(1));
        else
            return null;
    }
}
