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

package mekhq.campaign.personnel;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class AwardTest {

    private Award award1;
    private Award award2;

    @BeforeClass
    public static void setUpClass(){
        AwardsFactory.getInstance().loadAwardsFromStream(AwardTestUtilities.getTestAwardSet(),"TestSet");
    }

    @Before
    public void setUp(){
        award1 = AwardsFactory.getInstance().generateNew("TestSet", "Test Award 1");
        award2 = AwardsFactory.getInstance().generateNew("TestSet", "Test Award 2");
    }

    @Test
    public void testGetName(){
        assertEquals(AwardsFactory.getInstance().generateNew("TestSet", "Test Award 1").getName(), "Test Award 1");
    }

    @Test
    public void testGetSet(){
        assertEquals(AwardsFactory.getInstance().generateNew("TestSet", "Test Award 1").getSet(), "TestSet");
    }

    @Test
    public void testGetDescription(){
        assertEquals(AwardsFactory.getInstance().generateNew("TestSet", "Test Award 1").getDescription(), "Test Award 1 description.");
    }

    @Test
    public void testCompareTo(){
        assertEquals(award1.compareTo(award2) , -1);
        assertEquals(award2.compareTo(award1) , 1);
    }

    @Test
    public void testNumberMedalFiles(){
        assertEquals(award1.getNumberOfMedalFiles(), 2);
    }

    @Test
    public void testNumberRibbonFiles(){
        assertEquals(award1.getNumberOfRibbonFiles(), 2);
    }

    @Test
    public void testNumberMiscFiles(){
        assertEquals(award1.getNumberOfMiscFiles(), 2);
    }

    @Test
    public void testGetMedalFileName(){
        assertEquals(award1.getMedalFileName(1), "TestAward1_medal1.png");
        assertEquals(award1.getMedalFileName(2), "TestAward1_medal2.png");
    }

    @Test
    public void testGetRibbonFileName(){
        assertEquals(award1.getRibbonFileName(1), "TestAward1_ribbon1.png");
        assertEquals(award1.getRibbonFileName(2), "TestAward1_ribbon2.png");
    }

    @Test
    public void testGetMiscFileName(){
        assertEquals(award1.getMiscFileName(1), "TestAward1_misc1.png");
        assertEquals(award1.getMiscFileName(2), "TestAward1_misc2.png");
    }

    @Test
    public void testGetTooltipText(){
        assertEquals(award1.getTooltipText(), "Test Award 1: Test Award 1 description.");
    }

    @Test
    public void testGetXpReward(){
        assertEquals(award1.getXPReward(), 3);
    }

    @Test
    public void testGetEdgeReward(){
        assertEquals(award1.getEdgeReward(), 0);
    }

    @Test
    public void testDates() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        assertEquals(award1.getQuantity(), 0);

        award1.addDate(dateFormat.parse("2000-01-01 00:00:00"));
        assertEquals(award1.getQuantity(), 1);
        assertEquals(award1.hasDates(), true);

        award1.addDate(dateFormat.parse("2000-02-01 00:00:00"));
        assertEquals(award1.getQuantity(), 2);
        assertEquals(award1.hasDates(), true);

        award1.removeDate(dateFormat.parse("2000-01-01 00:00:00"));
        assertEquals(award1.getQuantity(), 1);
        assertEquals(award1.hasDates(), true);

        award1.removeDate(dateFormat.parse("2000-02-01 00:00:00"));
        assertEquals(award1.getQuantity(), 0);
        assertEquals(award1.hasDates(), false);

        List<Date> newDates = new ArrayList<>();
        newDates.add(dateFormat.parse("2000-01-01 00:00:00"));
        newDates.add(dateFormat.parse("2000-02-01 00:00:00"));
        newDates.add(dateFormat.parse("2000-03-01 00:00:00"));

        award1.setDates(newDates);
        assertEquals(award1.getQuantity(), 3);

        award1.removeDate(dateFormat.parse("2000-02-01 00:00:00"));

        assertEquals(award1.getQuantity(), 2);
        assertArrayEquals(award1.getFormatedDates().toArray(), new String[]{"2000-01-01", "2000-03-01"});
    }
}
