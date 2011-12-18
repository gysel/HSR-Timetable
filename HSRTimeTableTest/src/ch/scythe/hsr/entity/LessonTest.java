/* 
 * Copyright (C) 2011 Michi Gysel <michael.gysel@gmail.com>
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

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ch.scythe.hsr.enumeration.TimeUnit;
import ch.scythe.hsr.error.EnumNotFoundException;

public class LessonTest {

	@Test
	public void testAddTimeUnit() throws EnumNotFoundException {
		// Set up fixture
		TimeUnit expectedTimeUnit = TimeUnit.LESSON_1;
		// Exercise sut
		sut.addTimeUnit(expectedTimeUnit.getId());
		// Verify outcome
		List<TimeUnit> actualTimeUnits = sut.getTimeUnits();
		Assert.assertEquals(1, actualTimeUnits.size());
		actualTimeUnits.get(0).equals(expectedTimeUnit);
	}

	@Test(expected = EnumNotFoundException.class)
	public void testAddTimeUnitTESTvalueTooLow() throws EnumNotFoundException {
		// Set up fixture
		int timeUnitId = TimeUnit.LESSON_1.getId() - 1;
		// Exercise sut
		sut.addTimeUnit(timeUnitId);
	}

	@Test(expected = EnumNotFoundException.class)
	public void testAddTimeUnitTESTvalueTooHigh() throws EnumNotFoundException {
		// Set up fixture
		int timeUnitId = TimeUnit.LESSON_12.getId() + 1;
		// Exercise sut
		sut.addTimeUnit(timeUnitId);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetTimeUnitsTESTmodification() throws Exception {
		// Set up fixture
		TimeUnit expectedTimeUnit = TimeUnit.LESSON_1;
		sut.addTimeUnit(expectedTimeUnit.getId());
		// Exercise sut
		sut.getTimeUnits().add(TimeUnit.LESSON_2);
	}

	@Test
	public void testLecturers() {
		String delimiter = ", ";
		assertEquals("", sut.getLecturersAsString(delimiter));
		// Set up fixture
		String lecturer1 = "FOO";
		String lecturer2 = "BAR";
		// Exercise sut
		sut.addLecturer(lecturer1);
		sut.addLecturer(lecturer2);
		// Verify outcome
		assertEquals(lecturer1 + delimiter + lecturer2, sut.getLecturersAsString(delimiter));
	}

	@Test
	public void testLecturersTESTtooMany() {
		String delimiter = ", ";
		assertEquals("", sut.getLecturersAsString(delimiter));
		// Set up fixture
		String lecturer1 = "FOO";
		String lecturer2 = "BAR";
		// Exercise sut
		sut.addLecturer(lecturer1);
		sut.addLecturer(lecturer2);
		sut.addLecturer("some");
		sut.addLecturer("more");
		// Verify outcome
		assertEquals(lecturer1 + delimiter + lecturer2 + " …", sut.getLecturersAsString(delimiter));
	}

	@Test
	public void testGetIdentifierShort() throws Exception {
		String noise = "34713_";
		String interestingPart = "An1I-v2";
		// Set up fixture
		sut.setIdentifier(noise + interestingPart);
		// Exercise sut
		String actualIdentifierShort = sut.getIdentifierShort();
		// Verify outcome
		assertEquals(interestingPart, actualIdentifierShort);
	}

	@Test
	public void testGetIdentifierShortTESTparsableButDuplicateSplittingDelimiter() throws Exception {
		String noise = "34713_";
		String interestingPart = "An1I_v2";
		// Set up fixture
		sut.setIdentifier(noise + interestingPart);
		// Exercise sut
		String actualIdentifierShort = sut.getIdentifierShort();
		// Verify outcome
		assertEquals(interestingPart, actualIdentifierShort);
	}

	@Test
	public void testGetIdentifierShortTESTnotParseable() throws Exception {
		String identifier = "anUnknownFormat";
		// Set up fixture
		sut.setIdentifier(identifier);
		// Exercise sut
		String actualIdentifierShort = sut.getIdentifierShort();
		// Verify outcome
		assertEquals(identifier, actualIdentifierShort);
	}

	@Test
	public void testGetIdentifierShortTESTslightlyNotParsable() throws Exception {
		String identifier = "34713-An1I_v2";
		// Set up fixture
		sut.setIdentifier(identifier);
		// Exercise sut
		String actualIdentifierShort = sut.getIdentifierShort();
		// Verify outcome
		assertEquals(identifier, actualIdentifierShort);
	}

	@Test
	public void testHasDescription() throws Exception {
		sut.setDescription(null);
		assertFalse(sut.hasDescription());
		//
		sut.setDescription("");
		assertFalse(sut.hasDescription());
		//
		sut.setDescription("some text");
		assertTrue(sut.hasDescription());

	}

	private Lesson sut;

	@Before
	public void setUp() throws Exception {
		sut = new Lesson();
	}

}
