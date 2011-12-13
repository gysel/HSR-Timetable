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
