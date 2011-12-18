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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import ch.scythe.hsr.enumeration.TimeUnit;
import ch.scythe.hsr.enumeration.WeekDay;
import ch.scythe.hsr.error.EnumNotFoundException;

public class DayTest {

	@Test
	public void testDayCollectionOfLessonDate() throws EnumNotFoundException {
		// Set up fixture
		Collection<Lesson> lessons = new ArrayList<Lesson>();
		addLesson(lessons, TimeUnit.LESSON_2);
		addLesson(lessons, TimeUnit.LESSON_3);
		// Exercise sut
		sut = new Day(lessons, WeekDay.TUESDAY);
		Map<TimeUnit, List<Lesson>> actualLessons = sut.getLessons();
		// Verify outcome
		assertEquals(TimeUnit.getAll().size(), actualLessons.size());
		assertNull(actualLessons.get(TimeUnit.LESSON_1));
		assertNotNull(actualLessons.get(TimeUnit.LESSON_2));
		assertNotNull(actualLessons.get(TimeUnit.LESSON_3));
		assertNull(actualLessons.get(TimeUnit.LESSON_4));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetLessonsTESTmodification() throws EnumNotFoundException {
		// Set up fixture
		Collection<Lesson> lessons = new ArrayList<Lesson>();
		addLesson(lessons, TimeUnit.LESSON_2);
		// Exercise sut
		sut = new Day(lessons, WeekDay.TUESDAY);
		sut.getLessons().put(TimeUnit.LESSON_3, new ArrayList<Lesson>());
	}

	@Test
	public void testDayCollectionOfLessonDateTESTduplicateLesson() throws EnumNotFoundException {
		// Set up fixture
		Collection<Lesson> lessons = new ArrayList<Lesson>();
		addLesson(lessons, TimeUnit.LESSON_2);
		addLesson(lessons, TimeUnit.LESSON_2);
		// Exercise sut
		sut = new Day(lessons, WeekDay.TUESDAY);
		// Verify outcome
		assertEquals(2, sut.getLessons().get(TimeUnit.LESSON_2).size());
	}

	private Day sut;

	private void addLesson(Collection<Lesson> lessons, TimeUnit timeUnit) throws EnumNotFoundException {
		Lesson lesson = new Lesson();
		lesson.addTimeUnit(timeUnit.getId());
		lessons.add(lesson);
	}

}
