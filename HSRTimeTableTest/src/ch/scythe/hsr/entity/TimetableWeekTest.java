/* 
 * Copyright (C) 2011 - 2012 Michi Gysel <michael.gysel@gmail.com>
 *
 * This file is part of the HSR Timetable.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ch.scythe.hsr.entity;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import ch.scythe.hsr.enumeration.WeekDay;

public class TimetableWeekTest extends TestCase {

	@Test
	public void testGetDay() {
		WeekDay day = WeekDay.MONDAY;
		// Set up fixture
		List<Day> days = new ArrayList<Day>();
		Day monday = new Day(new ArrayList<Lesson>(), day);
		days.add(monday);
		TimetableWeek sut = new TimetableWeek(days);
		// Exercise sut
		Day actual = sut.getDay(day);
		// Verify outcome
		assertEquals(monday, actual);
		assertNull(sut.getDay(WeekDay.TUESDAY));
	}

	@Test
	public void testGetDayTESTnull() {
		// Set up fixture
		TimetableWeek sut = new TimetableWeek();
		// Exercise sut
		Day actual = sut.getDay(WeekDay.MONDAY);
		// Verify outcome
		assertNull(actual);
	}

}
