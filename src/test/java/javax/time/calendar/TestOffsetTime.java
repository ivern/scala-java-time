/*
 * Copyright (c) 2008-2009, Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package javax.time.calendar;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import javax.time.Instant;
import javax.time.calendar.field.AmPmOfDay;
import javax.time.calendar.field.HourOfDay;
import javax.time.calendar.field.MinuteOfHour;
import javax.time.calendar.field.NanoOfSecond;
import javax.time.calendar.field.SecondOfMinute;
import javax.time.calendar.format.CalendricalParseException;
import javax.time.period.MockPeriodProviderReturnsNull;
import javax.time.period.Period;
import javax.time.period.PeriodProvider;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test OffsetTime.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
@Test
public class TestOffsetTime {

    private static final ZoneOffset OFFSET_PONE = ZoneOffset.zoneOffset(1);
    private static final ZoneOffset OFFSET_PTWO = ZoneOffset.zoneOffset(2);
    private OffsetTime TEST_TIME;

    @BeforeMethod
    public void setUp() {
        TEST_TIME = OffsetTime.time(11, 30, 59, 500, OFFSET_PONE);
    }

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        Object obj = TEST_TIME;
        assertTrue(obj instanceof Calendrical);
        assertTrue(obj instanceof Serializable);
        assertTrue(obj instanceof Comparable<?>);
        assertTrue(obj instanceof TimeMatcher);
        assertTrue(obj instanceof TimeAdjuster);
        assertTrue(obj instanceof TimeProvider);
    }

    public void test_serialization() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(TEST_TIME);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertEquals(ois.readObject(), TEST_TIME);
    }

    public void test_immutable() {
        Class<OffsetTime> cls = OffsetTime.class;
        assertTrue(Modifier.isPublic(cls.getModifiers()));
        assertTrue(Modifier.isFinal(cls.getModifiers()));
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            assertTrue(Modifier.isPrivate(field.getModifiers()));
            assertTrue(Modifier.isFinal(field.getModifiers()));
        }
    }

    //-----------------------------------------------------------------------
    // factories
    //-----------------------------------------------------------------------
    void check(OffsetTime test, int h, int m, int s, int n, ZoneOffset offset) {
        assertEquals(test.getHourOfDay(), h);
        assertEquals(test.getMinuteOfHour(), m);
        assertEquals(test.getSecondOfMinute(), s);
        assertEquals(test.getNanoOfSecond(), n);
        assertEquals(test.getOffset(), offset);
    }

    //-----------------------------------------------------------------------
    public void factory_objectsHM() {
        OffsetTime test = OffsetTime.time(HourOfDay.hourOfDay(11), MinuteOfHour.minuteOfHour(30), OFFSET_PONE);
        check(test, 11, 30, 0, 0, OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_objectsHM_nullHour() {
        OffsetTime.time(null, MinuteOfHour.minuteOfHour(30), OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_objectsHM_nullMinute() {
        OffsetTime.time(HourOfDay.hourOfDay(11), null, OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_objectsHM_nullOffset() {
        OffsetTime.time(HourOfDay.hourOfDay(11), MinuteOfHour.minuteOfHour(30), null);
    }

    //-----------------------------------------------------------------------
    public void factory_objectsHMS() {
        OffsetTime test = OffsetTime.time(HourOfDay.hourOfDay(11), MinuteOfHour.minuteOfHour(30), SecondOfMinute.secondOfMinute(10), OFFSET_PONE);
        check(test, 11, 30, 10, 0, OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_objectsHMS_nullHour() {
        OffsetTime.time(null, MinuteOfHour.minuteOfHour(30), SecondOfMinute.secondOfMinute(10), OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_objectsHMS_nullMinute() {
        OffsetTime.time(HourOfDay.hourOfDay(11), null, SecondOfMinute.secondOfMinute(10), OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_objectsHMS_nullSecond() {
        OffsetTime.time(HourOfDay.hourOfDay(11), MinuteOfHour.minuteOfHour(30), null, OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_objectsHMS_nullOffset() {
        OffsetTime.time(HourOfDay.hourOfDay(11), MinuteOfHour.minuteOfHour(30), SecondOfMinute.secondOfMinute(10), null);
    }

    //-----------------------------------------------------------------------
    public void factory_objectsHMSN() {
        OffsetTime test = OffsetTime.time(HourOfDay.hourOfDay(11), MinuteOfHour.minuteOfHour(30),
                SecondOfMinute.secondOfMinute(10), NanoOfSecond.nanoOfSecond(500), OFFSET_PONE);
        check(test, 11, 30, 10, 500, OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_objectsHMSN_nullHour() {
        OffsetTime.time(null, MinuteOfHour.minuteOfHour(30), SecondOfMinute.secondOfMinute(10), NanoOfSecond.nanoOfSecond(500), 
                OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_objectsHMSN_nullMinute() {
        OffsetTime.time(HourOfDay.hourOfDay(11), null, SecondOfMinute.secondOfMinute(10), NanoOfSecond.nanoOfSecond(500), 
                OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_objectsHMSN_nullSecond() {
        OffsetTime.time(HourOfDay.hourOfDay(11), MinuteOfHour.minuteOfHour(30), null, NanoOfSecond.nanoOfSecond(500), OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_objectsHMSN_nullNano() {
        OffsetTime.time(HourOfDay.hourOfDay(11), MinuteOfHour.minuteOfHour(30), SecondOfMinute.secondOfMinute(10), null, 
                OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_objectsHMSN_nullOffset() {
        OffsetTime.time(HourOfDay.hourOfDay(11), MinuteOfHour.minuteOfHour(30), SecondOfMinute.secondOfMinute(10), 
                NanoOfSecond.nanoOfSecond(500), null);
    }

    //-----------------------------------------------------------------------
    public void factory_intsHM() {
        OffsetTime test = OffsetTime.time(11, 30, OFFSET_PONE);
        check(test, 11, 30, 0, 0, OFFSET_PONE);
    }

    //-----------------------------------------------------------------------
    public void factory_intsHMS() {
        OffsetTime test = OffsetTime.time(11, 30, 10, OFFSET_PONE);
        check(test, 11, 30, 10, 0, OFFSET_PONE);
    }

    //-----------------------------------------------------------------------
    public void factory_intsHMSN() {
        OffsetTime test = OffsetTime.time(11, 30, 10, 500, OFFSET_PONE);
        check(test, 11, 30, 10, 500, OFFSET_PONE);
    }

    //-----------------------------------------------------------------------
    public void factory_TimeProvider() {
        TimeProvider localTime = LocalTime.time(11, 30, 10, 500);
        OffsetTime test = OffsetTime.time(localTime, OFFSET_PONE);
        check(test, 11, 30, 10, 500, OFFSET_PONE);
    }

    //-----------------------------------------------------------------------
    public void factory_time_multiProvider_checkAmbiguous() {
        MockMultiProvider mmp = new MockMultiProvider(2008, 6, 30, 11, 30, 10, 500);
        OffsetTime test = OffsetTime.time(mmp, OFFSET_PTWO);
        check(test, 11, 30, 10, 500, OFFSET_PTWO);
    }

    //-----------------------------------------------------------------------
    // fromInstant()
    //-----------------------------------------------------------------------
    public void factory_fromInstant_multiProvider_checkAmbiguous() {
        MockMultiProvider mmp = new MockMultiProvider(2008, 6, 30, 11, 30, 10, 500);
        OffsetTime test = OffsetTime.fromInstant(mmp, ZoneOffset.UTC);
        check(test, 11, 30, 10, 500, ZoneOffset.UTC);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_InstantProvider_nullInstant() {
        OffsetTime.fromInstant((Instant) null, ZoneOffset.UTC);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_InstantProvider_nullOffset() {
        Instant instant = Instant.instant(0L);
        OffsetTime.fromInstant(instant, (ZoneOffset) null);
    }

    public void factory_fromInstant_InstantProvider_allSecsInDay() {
        for (int i = 0; i < (2 * 24 * 60 * 60); i++) {
            Instant instant = Instant.instant(i, 8);
            OffsetTime test = OffsetTime.fromInstant(instant, ZoneOffset.UTC);
            assertEquals(test.getHourOfDay(), (i / (60 * 60)) % 24);
            assertEquals(test.getMinuteOfHour(), (i / 60) % 60);
            assertEquals(test.getSecondOfMinute(), i % 60);
            assertEquals(test.getNanoOfSecond(), 8);
        }
    }

    public void factory_fromInstant_InstantProvider_beforeEpoch() {
        for (int i =-1; i >= -(24 * 60 * 60); i--) {
            Instant instant = Instant.instant(i, 8);
            OffsetTime test = OffsetTime.fromInstant(instant, ZoneOffset.UTC);
            assertEquals(test.getHourOfDay(), ((i + 24 * 60 * 60) / (60 * 60)) % 24);
            assertEquals(test.getMinuteOfHour(), ((i + 24 * 60 * 60) / 60) % 60);
            assertEquals(test.getSecondOfMinute(), (i + 24 * 60 * 60) % 60);
            assertEquals(test.getNanoOfSecond(), 8);
        }
    }

    //-----------------------------------------------------------------------
    public void factory_fromInstant_InstantProvider_maxYear() {
        OffsetTime test = OffsetTime.fromInstant(Instant.instant(Long.MAX_VALUE), ZoneOffset.UTC);
        int hour = (int) ((Long.MAX_VALUE / (60 * 60)) % 24);
        int min = (int) ((Long.MAX_VALUE / 60) % 60);
        int sec = (int) (Long.MAX_VALUE % 60);
        assertEquals(test.getHourOfDay(), hour);
        assertEquals(test.getMinuteOfHour(), min);
        assertEquals(test.getSecondOfMinute(), sec);
        assertEquals(test.getNanoOfSecond(), 0);
    }

    public void factory_fromInstant_InstantProvider_minYear() {
        long oneDay = 24 * 60 * 60;
        long addition = ((Long.MAX_VALUE / oneDay) + 2) * oneDay;
        
        OffsetTime test = OffsetTime.fromInstant(Instant.instant(Long.MIN_VALUE), ZoneOffset.UTC);
        long added = Long.MIN_VALUE + addition;
        int hour = (int) ((added / (60 * 60)) % 24);
        int min = (int) ((added / 60) % 60);
        int sec = (int) (added % 60);
        assertEquals(test.getHourOfDay(), hour);
        assertEquals(test.getMinuteOfHour(), min);
        assertEquals(test.getSecondOfMinute(), sec);
        assertEquals(test.getNanoOfSecond(), 0);
    }

    //-----------------------------------------------------------------------
    // parse()
    //-----------------------------------------------------------------------
    @Test(dataProvider = "sampleToString")
    public void factory_parse_validText(int h, int m, int s, int n, String offsetId, String parsable) {
        OffsetTime t = OffsetTime.parse(parsable);
        assertNotNull(t, parsable);
        assertEquals(t.getHourOfDay(), h);
        assertEquals(t.getMinuteOfHour(), m);
        assertEquals(t.getSecondOfMinute(), s);
        assertEquals(t.getNanoOfSecond(), n);
        assertEquals(t.getOffset(), ZoneOffset.zoneOffset(offsetId));
    }

    @DataProvider(name="sampleBadParse")
    Object[][] provider_sampleBadParse() {
        return new Object[][]{
                {"00;00"},
                {"12-00"},
                {"-01:00"},
                {"00:00:00-09"},
                {"00:00:00,09"},
                {"00:00:abs"},
                {"11"},
                {"11:30"},
                {"11:30+01:00[Europe/Paris]"},
        };
    }

    @Test(dataProvider = "sampleBadParse", expectedExceptions={CalendricalParseException.class})
    public void factory_parse_invalidText(String unparsable) {
        OffsetTime.parse(unparsable);
    }

    //-----------------------------------------------------------------------s
    @Test(expectedExceptions={IllegalCalendarFieldValueException.class})
    public void factory_parse_illegalHour() {
        OffsetTime.parse("25:00+01:00");
    }

    @Test(expectedExceptions={IllegalCalendarFieldValueException.class})
    public void factory_parse_illegalMinute() {
        OffsetTime.parse("12:60+01:00");
    }

    @Test(expectedExceptions={IllegalCalendarFieldValueException.class})
    public void factory_parse_illegalSecond() {
        OffsetTime.parse("12:12:60+01:00");
    }

    //-----------------------------------------------------------------------
    // constructor
    //-----------------------------------------------------------------------
    @Test(expectedExceptions=NullPointerException.class)
    public void constructor_nullTime() throws Throwable  {
        Constructor<OffsetTime> con = OffsetTime.class.getDeclaredConstructor(LocalTime.class, ZoneOffset.class);
        con.setAccessible(true);
        try {
            con.newInstance(null, OFFSET_PONE);
        } catch (InvocationTargetException ex) {
            throw ex.getCause();
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void constructor_nullOffset() throws Throwable  {
        Constructor<OffsetTime> con = OffsetTime.class.getDeclaredConstructor(LocalTime.class, ZoneOffset.class);
        con.setAccessible(true);
        try {
            con.newInstance(LocalTime.time(11, 30), null);
        } catch (InvocationTargetException ex) {
            throw ex.getCause();
        }
    }

    //-----------------------------------------------------------------------
    // basics
    //-----------------------------------------------------------------------
    @DataProvider(name="sampleTimes")
    Object[][] provider_sampleTimes() {
        return new Object[][] {
            {11, 30, 20, 500, OFFSET_PONE},
            {11, 0, 0, 0, OFFSET_PONE},
            {23, 59, 59, 999999999, OFFSET_PONE},
        };
    }

    @Test(dataProvider="sampleTimes")
    public void test_get(int h, int m, int s, int n, ZoneOffset offset) {
        LocalTime localTime = LocalTime.time(h, m, s, n);
        OffsetTime a = OffsetTime.time(localTime, offset);
        assertSame(a.getOffset(), offset);
        assertEquals(a.getChronology(), ISOChronology.INSTANCE);
        
        assertEquals(a.getHourOfDay(), localTime.getHourOfDay());
        assertEquals(a.getMinuteOfHour(), localTime.getMinuteOfHour());
        assertEquals(a.getSecondOfMinute(), localTime.getSecondOfMinute());
        assertEquals(a.getNanoOfSecond(), localTime.getNanoOfSecond());
        
        assertEquals(a.toHourOfDay(), localTime.toHourOfDay());
        assertEquals(a.toMinuteOfHour(), localTime.toMinuteOfHour());
        assertEquals(a.toSecondOfMinute(), localTime.toSecondOfMinute());
        assertEquals(a.toNanoOfSecond(), localTime.toNanoOfSecond());
        
        assertSame(a.toLocalTime(), localTime);
        assertEquals(a.toString(), localTime.toString() + offset.toString());
    }

    //-----------------------------------------------------------------------
    // get(CalendricalRule)
    //-----------------------------------------------------------------------
    public void test_get_CalendricalRule() {
        OffsetTime test = OffsetTime.time(12, 30, 40, 987654321, OFFSET_PONE);
        assertEquals(test.get(ISOChronology.yearRule()), null);
        assertEquals(test.get(ISOChronology.quarterOfYearRule()), null);
        assertEquals(test.get(ISOChronology.monthOfYearRule()), null);
        assertEquals(test.get(ISOChronology.monthOfQuarterRule()), null);
        assertEquals(test.get(ISOChronology.dayOfMonthRule()), null);
        assertEquals(test.get(ISOChronology.dayOfWeekRule()), null);
        assertEquals(test.get(ISOChronology.dayOfYearRule()), null);
        assertEquals(test.get(ISOChronology.weekOfWeekBasedYearRule()), null);
        assertEquals(test.get(ISOChronology.weekBasedYearRule()), null);
        
        assertEquals(test.get(ISOChronology.hourOfDayRule()), (Integer) 12);
        assertEquals(test.get(ISOChronology.minuteOfHourRule()), (Integer) 30);
        assertEquals(test.get(ISOChronology.secondOfMinuteRule()), (Integer) 40);
        assertEquals(test.get(ISOChronology.nanoOfSecondRule()), (Integer) 987654321);
        assertEquals(test.get(ISOChronology.hourOfAmPmRule()), (Integer) 0);
        assertEquals(test.get(ISOChronology.amPmOfDayRule()), AmPmOfDay.PM);
        
        assertEquals(test.get(LocalDate.rule()), null);
        assertEquals(test.get(LocalTime.rule()), test.toLocalTime());
        assertEquals(test.get(LocalDateTime.rule()), null);
        assertEquals(test.get(OffsetDate.rule()), null);
        assertEquals(test.get(OffsetTime.rule()), test);
        assertEquals(test.get(OffsetDateTime.rule()), null);
        assertEquals(test.get(ZonedDateTime.rule()), null);
        assertEquals(test.get(ZoneOffset.rule()), test.getOffset());
        assertEquals(test.get(TimeZone.rule()), null);
        assertEquals(test.get(YearMonth.rule()), null);
        assertEquals(test.get(MonthDay.rule()), null);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_get_CalendricalRule_null() {
        OffsetTime test = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        test.get((CalendricalRule<?>) null);
    }

    public void test_get_unsupported() {
        OffsetTime test = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        assertEquals(test.get(MockRuleNoValue.INSTANCE), null);
    }

    //-----------------------------------------------------------------------
    // withTime()
    //-----------------------------------------------------------------------
    public void test_withTime() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        LocalTime time = LocalTime.time(11, 31, 0);
        OffsetTime test = base.withTime(time);
        assertSame(test.toLocalTime(), time);
        assertSame(test.getOffset(), base.getOffset());
    }

    public void test_withTime_noChange() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        LocalTime time = LocalTime.time(11, 30, 59);
        OffsetTime test = base.withTime(time);
        assertSame(test, base);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_withTime_null() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        base.withTime(null);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_withTime_badProvider() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        base.withTime(new MockTimeProviderReturnsNull());
    }

    //-----------------------------------------------------------------------
    // withOffset()
    //-----------------------------------------------------------------------
    public void test_withOffset() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.withOffset(OFFSET_PTWO);
        assertSame(test.toLocalTime(), base.toLocalTime());
        assertSame(test.getOffset(), OFFSET_PTWO);
    }

    public void test_withOffset_noChange() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.withOffset(OFFSET_PONE);
        assertSame(test, base);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_withOffset_null() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        base.withOffset(null);
    }

    //-----------------------------------------------------------------------
    // adjustLocalTime()
    //-----------------------------------------------------------------------
    public void test_adjustLocalTime() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.adjustLocalTime(OFFSET_PTWO);
        OffsetTime expected = OffsetTime.time(12, 30, 59, OFFSET_PTWO);
        assertEquals(test, expected);
    }

    public void test_adjustLocalTime_noChange() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.adjustLocalTime(OFFSET_PONE);
        assertSame(test, base);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_adjustLocalTime_null() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        base.adjustLocalTime(null);
    }

    //-----------------------------------------------------------------------
    // with()
    //-----------------------------------------------------------------------
    public void test_with() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.with(HourOfDay.hourOfDay(1));
        assertEquals(test.toLocalTime(), LocalTime.time(1, 30, 59));
        assertSame(test.getOffset(), base.getOffset());
    }

    public void test_with_noChange() {
        LocalTime time = LocalTime.time(11, 30, 59);
        OffsetTime base = OffsetTime.time(time, OFFSET_PONE);
        OffsetTime test = base.with(time);
        assertSame(test, base);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_with_null() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        base.with(null);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_with_badAdjuster() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        base.with(new MockTimeAdjusterReturnsNull());
    }

    //-----------------------------------------------------------------------
    // withHourOfDay()
    //-----------------------------------------------------------------------
    public void test_withHourOfDay_normal() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.withHourOfDay(15);
        assertEquals(test, OffsetTime.time(15, 30, 59, OFFSET_PONE));
    }

    public void test_withHourOfDay_noChange() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.withHourOfDay(11);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withMinuteOfHour()
    //-----------------------------------------------------------------------
    public void test_withMinuteOfHour_normal() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.withMinuteOfHour(15);
        assertEquals(test, OffsetTime.time(11, 15, 59, OFFSET_PONE));
    }

    public void test_withMinuteOfHour_noChange() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.withMinuteOfHour(30);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withSecondOfMinute()
    //-----------------------------------------------------------------------
    public void test_withSecondOfMinute_normal() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.withSecondOfMinute(15);
        assertEquals(test, OffsetTime.time(11, 30, 15, OFFSET_PONE));
    }

    public void test_withSecondOfMinute_noChange() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.withSecondOfMinute(59);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withNanoOfSecond()
    //-----------------------------------------------------------------------
    public void test_withNanoOfSecond_normal() {
        OffsetTime base = OffsetTime.time(11, 30, 59, 1, OFFSET_PONE);
        OffsetTime test = base.withNanoOfSecond(15);
        assertEquals(test, OffsetTime.time(11, 30, 59, 15, OFFSET_PONE));
    }

    public void test_withNanoOfSecond_noChange() {
        OffsetTime base = OffsetTime.time(11, 30, 59, 1, OFFSET_PONE);
        OffsetTime test = base.withNanoOfSecond(1);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plus(PeriodProvider)
    //-----------------------------------------------------------------------
    public void test_plus_PeriodProvider() {
        PeriodProvider provider = Period.hoursMinutesSeconds(1, 2, 3);
        OffsetTime t = TEST_TIME.plus(provider);
        assertEquals(t, OffsetTime.time(12, 33, 2, 500, OFFSET_PONE));
    }

    public void test_plus_PeriodProvider_zero() {
        OffsetTime t = TEST_TIME.plus(Period.ZERO);
        assertSame(t, TEST_TIME);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_plus_PeriodProvider_null() {
        TEST_TIME.plus((PeriodProvider) null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_plus_PeriodProvider_badProvider() {
        TEST_TIME.plus(new MockPeriodProviderReturnsNull());
    }

    //-----------------------------------------------------------------------
    // plusHours()
    //-----------------------------------------------------------------------
    public void test_plusHours() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.plusHours(13);
        assertEquals(test, OffsetTime.time(0, 30, 59, OFFSET_PONE));
    }

    public void test_plusHours_zero() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.plusHours(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plusMinutes()
    //-----------------------------------------------------------------------
    public void test_plusMinutes() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.plusMinutes(30);
        assertEquals(test, OffsetTime.time(12, 0, 59, OFFSET_PONE));
    }

    public void test_plusMinutes_zero() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.plusMinutes(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plusSeconds()
    //-----------------------------------------------------------------------
    public void test_plusSeconds() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.plusSeconds(1);
        assertEquals(test, OffsetTime.time(11, 31, 0, OFFSET_PONE));
    }

    public void test_plusSeconds_zero() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.plusSeconds(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plusNanos()
    //-----------------------------------------------------------------------
    public void test_plusNanos() {
        OffsetTime base = OffsetTime.time(11, 30, 59, 0, OFFSET_PONE);
        OffsetTime test = base.plusNanos(1);
        assertEquals(test, OffsetTime.time(11, 30, 59, 1, OFFSET_PONE));
    }

    public void test_plusNanos_zero() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.plusNanos(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // minus(PeriodProvider)
    //-----------------------------------------------------------------------
    public void test_minus_PeriodProvider() {
        PeriodProvider provider = Period.hoursMinutesSeconds(1, 2, 3);
        OffsetTime t = TEST_TIME.minus(provider);
        assertEquals(t, OffsetTime.time(10, 28, 56, 500, OFFSET_PONE));
    }

    public void test_minus_PeriodProvider_zero() {
        OffsetTime t = TEST_TIME.minus(Period.ZERO);
        assertSame(t, TEST_TIME);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_minus_PeriodProvider_null() {
        TEST_TIME.minus((PeriodProvider) null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_minus_PeriodProvider_badProvider() {
        TEST_TIME.minus(new MockPeriodProviderReturnsNull());
    }

    //-----------------------------------------------------------------------
    // minusHours()
    //-----------------------------------------------------------------------
    public void test_minusHours() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.minusHours(-13);
        assertEquals(test, OffsetTime.time(0, 30, 59, OFFSET_PONE));
    }

    public void test_minusHours_zero() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.minusHours(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // minusMinutes()
    //-----------------------------------------------------------------------
    public void test_minusMinutes() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.minusMinutes(50);
        assertEquals(test, OffsetTime.time(10, 40, 59, OFFSET_PONE));
    }

    public void test_minusMinutes_zero() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.minusMinutes(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // minusSeconds()
    //-----------------------------------------------------------------------
    public void test_minusSeconds() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.minusSeconds(60);
        assertEquals(test, OffsetTime.time(11, 29, 59, OFFSET_PONE));
    }

    public void test_minusSeconds_zero() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.minusSeconds(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // minusNanos()
    //-----------------------------------------------------------------------
    public void test_minusNanos() {
        OffsetTime base = OffsetTime.time(11, 30, 59, 0, OFFSET_PONE);
        OffsetTime test = base.minusNanos(1);
        assertEquals(test, OffsetTime.time(11, 30, 58, 999999999, OFFSET_PONE));
    }

    public void test_minusNanos_zero() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        OffsetTime test = base.minusNanos(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // matches()
    //-----------------------------------------------------------------------
    public void test_matches() {
        OffsetTime test = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        assertTrue(test.matches(HourOfDay.hourOfDay(11)));
        assertFalse(test.matches(HourOfDay.hourOfDay(10)));
        assertTrue(test.matches(MinuteOfHour.minuteOfHour(30)));
        assertFalse(test.matches(MinuteOfHour.minuteOfHour(0)));
        assertTrue(test.matches(SecondOfMinute.secondOfMinute(59)));
        assertFalse(test.matches(SecondOfMinute.secondOfMinute(50)));
        assertTrue(test.matches(NanoOfSecond.nanoOfSecond(0)));
        assertFalse(test.matches(NanoOfSecond.nanoOfSecond(1)));
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_matches_null() {
        OffsetTime base = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        base.matches(null);
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    public void test_compareTo_time() {
        OffsetTime a = OffsetTime.time(11, 29, OFFSET_PONE);
        OffsetTime b = OffsetTime.time(11, 30, OFFSET_PONE);  // a is before b due to time
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
    }

    public void test_compareTo_offset() {
        OffsetTime a = OffsetTime.time(11, 30, OFFSET_PTWO);
        OffsetTime b = OffsetTime.time(11, 30, OFFSET_PONE);  // a is before b due to offset
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
    }

    public void test_compareTo_both() {
        OffsetTime a = OffsetTime.time(11, 50, OFFSET_PTWO);
        OffsetTime b = OffsetTime.time(11, 20, OFFSET_PONE);  // a is before b on instant scale
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
    }

    public void test_compareTo_hourDifference() {
        OffsetTime a = OffsetTime.time(10, 0, OFFSET_PONE);
        OffsetTime b = OffsetTime.time(11, 0, OFFSET_PTWO);  // a is before b despite being same time-line time
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_compareTo_null() {
        OffsetTime a = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        a.compareTo(null);
    }

    @Test(expectedExceptions=ClassCastException.class)
    @SuppressWarnings("unchecked")
    public void compareToNonOffsetTime() {
       Comparable c = TEST_TIME;
       c.compareTo(new Object());
    }

    //-----------------------------------------------------------------------
    // isAfter() / isBefore()
    //-----------------------------------------------------------------------
    public void test_isBeforeIsAfter() {
        OffsetTime a = OffsetTime.time(11, 30, 58, OFFSET_PONE);
        OffsetTime b = OffsetTime.time(11, 30, 59, OFFSET_PONE);  // a is before b due to time
        assertEquals(a.isBefore(b), true);
        assertEquals(a.isAfter(b), false);
        assertEquals(b.isBefore(a), false);
        assertEquals(b.isAfter(a), true);
        assertEquals(a.isBefore(a), false);
        assertEquals(b.isBefore(b), false);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isBefore_null() {
        OffsetTime a = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        a.isBefore(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isAfter_null() {
        OffsetTime a = OffsetTime.time(11, 30, 59, OFFSET_PONE);
        a.isAfter(null);
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleTimes")
    public void test_equals_true(int h, int m, int s, int n, ZoneOffset ignored) {
        OffsetTime a = OffsetTime.time(h, m, s, n, OFFSET_PONE);
        OffsetTime b = OffsetTime.time(h, m, s, n, OFFSET_PONE);
        assertEquals(a.equals(b), true);
        assertEquals(a.hashCode() == b.hashCode(), true);
    }
    @Test(dataProvider="sampleTimes")
    public void test_equals_false_hour_differs(int h, int m, int s, int n, ZoneOffset ignored) {
        h = (h == 23 ? 22 : h);
        OffsetTime a = OffsetTime.time(h, m, s, n, OFFSET_PONE);
        OffsetTime b = OffsetTime.time(h + 1, m, s, n, OFFSET_PONE);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_equals_false_minute_differs(int h, int m, int s, int n, ZoneOffset ignored) {
        m = (m == 59 ? 58 : m);
        OffsetTime a = OffsetTime.time(h, m, s, n, OFFSET_PONE);
        OffsetTime b = OffsetTime.time(h, m + 1, s, n, OFFSET_PONE);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_equals_false_second_differs(int h, int m, int s, int n, ZoneOffset ignored) {
        s = (s == 59 ? 58 : s);
        OffsetTime a = OffsetTime.time(h, m, s, n, OFFSET_PONE);
        OffsetTime b = OffsetTime.time(h, m, s + 1, n, OFFSET_PONE);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_equals_false_nano_differs(int h, int m, int s, int n, ZoneOffset ignored) {
        n = (n == 999999999 ? 999999998 : n);
        OffsetTime a = OffsetTime.time(h, m, s, n, OFFSET_PONE);
        OffsetTime b = OffsetTime.time(h, m, s, n + 1, OFFSET_PONE);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_equals_false_offset_differs(int h, int m, int s, int n, ZoneOffset ignored) {
        OffsetTime a = OffsetTime.time(h, m, s, n, OFFSET_PONE);
        OffsetTime b = OffsetTime.time(h, m, s, n, OFFSET_PTWO);
        assertEquals(a.equals(b), false);
    }

    public void test_equals_itself_true() {
        assertEquals(TEST_TIME.equals(TEST_TIME), true);
    }

    public void test_equals_string_false() {
        assertEquals(TEST_TIME.equals("2007-07-15"), false);
    }

    public void test_equals_null_false() {
        assertEquals(TEST_TIME.equals(null), false);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @DataProvider(name="sampleToString")
    Object[][] provider_sampleToString() {
        return new Object[][] {
            {11, 30, 59, 0, "Z", "11:30:59Z"},
            {11, 30, 59, 0, "+01:00", "11:30:59+01:00"},
            {11, 30, 59, 999000000, "Z", "11:30:59.999Z"},
            {11, 30, 59, 999000000, "+01:00", "11:30:59.999+01:00"},
            {11, 30, 59, 999000, "Z", "11:30:59.000999Z"},
            {11, 30, 59, 999000, "+01:00", "11:30:59.000999+01:00"},
            {11, 30, 59, 999, "Z", "11:30:59.000000999Z"},
            {11, 30, 59, 999, "+01:00", "11:30:59.000000999+01:00"},
        };
    }

    @Test(dataProvider="sampleToString")
    public void test_toString(int h, int m, int s, int n, String offsetId, String expected) {
        OffsetTime t = OffsetTime.time(h, m, s, n, ZoneOffset.zoneOffset(offsetId));
        String str = t.toString();
        assertEquals(str, expected);
    }

    //-----------------------------------------------------------------------
    // matchesTime()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleTimes")
    public void test_matchesTime_true(int h, int m, int s, int n, ZoneOffset offset) {
        OffsetTime a = OffsetTime.time(h, m, s, n, offset);
        LocalTime b = LocalTime.time(h, m, s, n);
        assertEquals(a.matchesTime(b), true);
    }
    @Test(dataProvider="sampleTimes")
    public void test_matchesTime_false_hour_differs(int h, int m, int s, int n, ZoneOffset offset) {
        OffsetTime a = OffsetTime.time(h, m, s, n, offset);
        LocalTime b = LocalTime.time(h, m, s, n).plusHours(1);
        assertEquals(a.matchesTime(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_matchesTime_false_minute_differs(int h, int m, int s, int n, ZoneOffset offset) {
        OffsetTime a = OffsetTime.time(h, m, s, n, offset);
        LocalTime b = LocalTime.time(h, m, s, n).plusMinutes(1);
        assertEquals(a.matchesTime(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_matchesTime_false_second_differs(int h, int m, int s, int n, ZoneOffset offset) {
        OffsetTime a = OffsetTime.time(h, m, s, n, offset);
        LocalTime b = LocalTime.time(h, m, s, n).plusSeconds(1);
        assertEquals(a.matchesTime(b), false);
    }
    @Test(dataProvider="sampleTimes")
    public void test_matchesTime_false_nano_differs(int h, int m, int s, int n, ZoneOffset offset) {
        OffsetTime a = OffsetTime.time(h, m, s, n, offset);
        LocalTime b = LocalTime.time(h, m, s, n).plusNanos(1);
        assertEquals(a.matchesTime(b), false);
    }

    public void test_matchesTime_itself_true() {
        assertEquals(TEST_TIME.matchesTime(TEST_TIME.toLocalTime()), true);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_matchesTime_null() {
        TEST_TIME.matchesTime(null);
    }
    
    //-----------------------------------------------------------------------
    // adjustTime()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleTimes")
    public void test_adjustTime(int h, int m, int s, int n, ZoneOffset ignored) {
        LocalTime a = LocalTime.time(h, m, s, n);
        assertSame(a.adjustTime(TEST_TIME.toLocalTime()), a);
        assertSame(TEST_TIME.adjustTime(a), TEST_TIME.toLocalTime());
    }

    public void test_adjustTime_same() {
        assertSame(OffsetTime.time(11, 30, 59, 500, OFFSET_PTWO).adjustTime(TEST_TIME.toLocalTime()), TEST_TIME.toLocalTime());
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_adjustTime_null() {
        TEST_TIME.adjustTime(null);
    }
}
