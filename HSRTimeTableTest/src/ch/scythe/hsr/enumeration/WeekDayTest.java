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
package ch.scythe.hsr.enumeration;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

public class WeekDayTest {

	@Test
	public void test() {
		// Set up fixture
		Date sunday = new Date(2011 - 1900, 10 - 1, 30);
		Date monday = new Date(2011 - 1900, 10 - 1, 31);
		Date tuesday = new Date(2011 - 1900, 11 - 1, 1);
		// Exercise sut & verify outcome
		assertEquals(Weekday.SUNDAY, Weekday.getByDate(sunday));
		assertEquals(Weekday.MONDAY, Weekday.getByDate(monday));
		assertEquals(Weekday.TUESDAY, Weekday.getByDate(tuesday));
	}
}
