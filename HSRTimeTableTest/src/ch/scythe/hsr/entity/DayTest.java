package ch.scythe.hsr.entity;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import ch.scythe.hsr.error.EnumNotFoundException;

public class DayTest {

	@Test
	public void testDayDate() {
		// Exercise sut
		sut = new Day(new Date(2011, 10, 18));
		// Verify outcome
		Map<TimeUnit, Lesson> actualLessons = sut.getLessons();
		assertTrue(actualLessons.isEmpty());
	}

	@Test
	public void testDayCollectionOfLessonDate() throws EnumNotFoundException {
		// Set up fixture
		Collection<Lesson> lessons = new ArrayList<Lesson>();
		addLesson(lessons, TimeUnit.LESSON_2);
		addLesson(lessons, TimeUnit.LESSON_3);
		// Exercise sut
		sut = new Day(lessons, new Date(2011, 10, 18));
		Map<TimeUnit, Lesson> actualLessons = sut.getLessons();
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
		sut = new Day(lessons, new Date(2011, 10, 18));
		sut.getLessons().put(TimeUnit.LESSON_3, new Lesson());
	}

	@Test
	@Ignore
	public void testDayCollectionOfLessonDateTESTduplicateLesson() throws EnumNotFoundException {
		// Set up fixture
		Collection<Lesson> lessons = new ArrayList<Lesson>();
		addLesson(lessons, TimeUnit.LESSON_2);
		addLesson(lessons, TimeUnit.LESSON_2);
		// Exercise sut
		sut = new Day(lessons, new Date(2011, 10, 18));
	}

	private Day sut;

	private void addLesson(Collection<Lesson> lessons, TimeUnit timeUnit) throws EnumNotFoundException {
		Lesson lesson = new Lesson();
		lesson.addTimeUnit(timeUnit.getId());
		lessons.add(lesson);
	}

}
