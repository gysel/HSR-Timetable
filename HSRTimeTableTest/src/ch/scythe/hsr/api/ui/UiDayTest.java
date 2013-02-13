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
package ch.scythe.hsr.api.ui;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ch.scythe.hsr.enumeration.Weekday;

public class UiDayTest {

	@Test
	public void getLessinsIsSorted() {
		UiDay day = new UiDay(Weekday.MONDAY);
		List<UiLesson> lessons = new ArrayList<UiLesson>();
		lessons.add(createLesson("15:10 - :55", "SE2"));
		lessons.add(createLesson("16:05 - :50", "SE2"));
		lessons.add(createLesson("15:10 - :55", "Dbs2"));
		lessons.add(createLesson("16:05 - :50", "Dbs2"));
		day.setLessons(lessons);
		// now the actual test
		List<UiLesson> sortedLessons = day.getLessons();
		assertEquals("SE2", sortedLessons.get(0).getName());
		assertEquals("Dbs2", sortedLessons.get(1).getName());
		assertEquals("SE2", sortedLessons.get(2).getName());
		assertEquals("Dbs2", sortedLessons.get(3).getName());
	}

	private UiLesson createLesson(String slot, String name) {
		UiLesson result = new UiLesson();
		result.setTimeSlot(slot);
		result.setName(name);
		return result;
	}

}
