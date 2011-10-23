package ch.scythe.hsr.xml;

import static junit.framework.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import ch.scythe.hsr.entity.Day;
import ch.scythe.hsr.entity.Lesson;
import ch.scythe.hsr.entity.TimeUnit;

public class LessonHandlerTest {

	private static final String LECTURER_CN1_EXERCISE = "RIN, HEI";
	private static final String LECTURER_CN1_LAB = "RIN, HEI, SFF, WIP";
	private static final String IDENTIFIER_CN1_PRAK_P10 = "35272_CN1Prak-p10";
	private static final String IDENTIFIER_CN1_EXCERCISE = "35025_CN1-u15";
	private static final String TYPE_PRACTICAL_COURSE = "Praktikum";
	private static final String TYPE_EXERCISE = "Uebung";
	private static final String ROOM_NETWORK_LAB = "2.103";
	private static final String ROOM_NETWORK_EXERCISE = "1.212a";
	private static final String DESCRIPTION_NETWORK_LAB = "gerade KW: CN1Prak-p10";

	@Test
	public void testScenarioSunnyPath() throws FileNotFoundException {
		// Set up fixture
		Date scenarioDate = new Date(2011, 10, 18);
		Day day = parse("ScenarioSunnyPath.xml", scenarioDate);
		// Exercise sut
		Map<TimeUnit, Lesson> lessons = day.getLessons();
		// Verify outcome
		assertLesson(lessons, TimeUnit.LESSON_2, ROOM_NETWORK_LAB, TYPE_PRACTICAL_COURSE, IDENTIFIER_CN1_PRAK_P10,
				LECTURER_CN1_LAB, DESCRIPTION_NETWORK_LAB);
		assertLesson(lessons, TimeUnit.LESSON_3, ROOM_NETWORK_LAB, TYPE_PRACTICAL_COURSE, IDENTIFIER_CN1_PRAK_P10,
				LECTURER_CN1_LAB, DESCRIPTION_NETWORK_LAB);
		assertLesson(lessons, TimeUnit.LESSON_4, ROOM_NETWORK_LAB, TYPE_PRACTICAL_COURSE, IDENTIFIER_CN1_PRAK_P10,
				LECTURER_CN1_LAB, DESCRIPTION_NETWORK_LAB);
		assertLesson(lessons, TimeUnit.LESSON_5, ROOM_NETWORK_LAB, TYPE_PRACTICAL_COURSE, IDENTIFIER_CN1_PRAK_P10,
				LECTURER_CN1_LAB, DESCRIPTION_NETWORK_LAB);
		//
		assertLesson(lessons, TimeUnit.LESSON_8, ROOM_NETWORK_EXERCISE, TYPE_EXERCISE, IDENTIFIER_CN1_EXCERCISE,
				LECTURER_CN1_EXERCISE, "");
		assertLesson(lessons, TimeUnit.LESSON_9, ROOM_NETWORK_EXERCISE, TYPE_EXERCISE, IDENTIFIER_CN1_EXCERCISE,
				LECTURER_CN1_EXERCISE, "");
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

	private void assertLesson(Map<TimeUnit, Lesson> lessons, TimeUnit timeUnit, String roomName, String type,
			String identifier, String lecturer, String description) {
		Lesson lesson = lessons.get(timeUnit);
		assertEquals(roomName, lesson.getRoom());
		assertEquals(type, lesson.getType());
		assertEquals(identifier, lesson.getIdentifier());
		assertEquals(lecturer, lesson.getLecturersAsString(", "));
		assertEquals(description, lesson.getDescription());
	}

}
