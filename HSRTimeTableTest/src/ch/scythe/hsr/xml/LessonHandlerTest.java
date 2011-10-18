package ch.scythe.hsr.xml;

import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import ch.scythe.hsr.entity.Day;
import ch.scythe.hsr.entity.Lesson;
import ch.scythe.hsr.entity.TimeUnit;

public class LessonHandlerTest {

	private static final String TYPE_PRACTICAL_COURSE = "Praktikum";
	private static final String ROOM_NETWORK_LAB = "2.103";

	@Test
	public void testScenarioSunnyPath() throws FileNotFoundException {
		// Set up fixture
		Date scenarioDate = new Date(2011, 10, 18);
		Day day = parse("ScenarioSunnyPath.xml", scenarioDate);
		// Exercise sut
		Map<TimeUnit, Lesson> lessons = day.getLessons();
		// Verify outcome
		assertLesson(lessons, TimeUnit.LESSON_2, ROOM_NETWORK_LAB, TYPE_PRACTICAL_COURSE);
		assertLesson(lessons, TimeUnit.LESSON_3, ROOM_NETWORK_LAB, TYPE_PRACTICAL_COURSE);
		assertLesson(lessons, TimeUnit.LESSON_4, ROOM_NETWORK_LAB, TYPE_PRACTICAL_COURSE);
		assertLesson(lessons, TimeUnit.LESSON_5, ROOM_NETWORK_LAB, TYPE_PRACTICAL_COURSE);
	}

	private SaxTimetableParser parser;

	@Before
	public void setUp() throws Exception {
		parser = new SaxTimetableParser();
	}

	private Day parse(String scenarioName, Date date) {
		List<Lesson> lessons = parser.parse(LessonHandlerTest.class.getResourceAsStream(scenarioName));
		return new Day(lessons, date);
	}

	private void assertLesson(Map<TimeUnit, Lesson> lessons, TimeUnit timeUnit, String roomName, String type) {
		Lesson lesson = lessons.get(timeUnit);
		Assert.assertEquals(roomName, lesson.getRoom());
		Assert.assertEquals(type, lesson.getType());
	}

}
