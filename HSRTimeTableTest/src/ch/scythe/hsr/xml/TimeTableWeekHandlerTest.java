package ch.scythe.hsr.xml;

import static junit.framework.Assert.*;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import ch.scythe.hsr.entity.Day;
import ch.scythe.hsr.entity.Lesson;
import ch.scythe.hsr.entity.TimetableWeek;
import ch.scythe.hsr.enumeration.TimeUnit;
import ch.scythe.hsr.enumeration.WeekDay;

public class TimeTableWeekHandlerTest {

	private static final String LECTURER_AN1 = "AUG";
	private static final String LECTURER_CN1_EXERCISE = "RIN, HEI";
	private static final String LECTURER_CN1_LAB = "RIN, HEI …";
	//
	private static final String IDENTIFIER_CN1_PRAK_P10 = "35272_CN1Prak-p10";
	private static final String IDENTIFIER_CN1_EXCERCISE = "35025_CN1-u15";
	private static final String IDENTIFIER_AN1I_V2 = "34713_An1I-v2";
	//
	private static final String TYPE_PRACTICAL_COURSE = "Praktikum";
	private static final String TYPE_EXERCISE = "Uebung";
	private static final String TYPE_LECTURE = "Vorlesung";
	//
	private static final String ROOM_NETWORK_LAB = "2.103";
	private static final String ROOM_MATH = "1.207";
	private static final String ROOM_NETWORK_EXERCISE = "1.212a";
	//
	private static final String DESCRIPTION_NETWORK_LAB = "gerade KW: CN1Prak-p10";
	private static final String DESCRIPTION_EMPTY = "";

	@Test
	public void testScenarioSunnyPathTESTparseTuesday() throws FileNotFoundException {
		// Set up fixture
		Day day = parse("ScenarioSunnyPath.xml", WeekDay.TUESDAY);
		// Exercise sut
		Map<TimeUnit, List<Lesson>> lessons = day.getLessons();
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
		assertLesson(lessons, TimeUnit.LESSON_7, ROOM_MATH, TYPE_LECTURE, IDENTIFIER_AN1I_V2, LECTURER_AN1,
				DESCRIPTION_EMPTY);
		//
		assertLesson(lessons, TimeUnit.LESSON_8, ROOM_NETWORK_EXERCISE, TYPE_EXERCISE, IDENTIFIER_CN1_EXCERCISE,
				LECTURER_CN1_EXERCISE, DESCRIPTION_EMPTY);
		assertLesson(lessons, TimeUnit.LESSON_9, ROOM_NETWORK_EXERCISE, TYPE_EXERCISE, IDENTIFIER_CN1_EXCERCISE,
				LECTURER_CN1_EXERCISE, DESCRIPTION_EMPTY);
	}

	@Test
	public void testScenarioTwoLessonsPerTimeUnit() throws Exception {
		// Set up fixture
		Day day = parse("ScenarioTwoLessonsPerTimeUnit.xml", WeekDay.TUESDAY);
		// Exercise sut
		Map<TimeUnit, List<Lesson>> lessons = day.getLessons();
		// Verify outcome
		assertNull(lessons.get(TimeUnit.LESSON_1));
		assertEquals(2, lessons.get(TimeUnit.LESSON_2).size());
		assertEquals(2, lessons.get(TimeUnit.LESSON_3).size());
		assertEquals(1, lessons.get(TimeUnit.LESSON_4).size());
	}

	private SaxTimetableParser parser;

	@Before
	public void setUp() throws Exception {
		parser = new SaxTimetableParser();
	}

	private Day parse(String scenarioName, WeekDay weekDay) {
		TimetableWeek timetableWeek = parser.parse(TimeTableWeekHandlerTest.class.getResourceAsStream(scenarioName));
		return timetableWeek.getDay(weekDay);
	}

	private void assertLesson(Map<TimeUnit, List<Lesson>> lessons, TimeUnit timeUnit, String roomName, String type,
			String identifier, String lecturer, String description) {
		List<Lesson> lessonsPerTimeUnit = lessons.get(timeUnit);
		assertEquals(1, lessonsPerTimeUnit.size());
		Lesson lesson = lessonsPerTimeUnit.get(0);
		assertEquals(roomName, lesson.getRoom());
		assertEquals(type, lesson.getType());
		assertEquals(identifier, lesson.getIdentifier());
		assertEquals(lecturer, lesson.getLecturersAsString(", "));
		assertEquals(description, lesson.getDescription());
	}

}
