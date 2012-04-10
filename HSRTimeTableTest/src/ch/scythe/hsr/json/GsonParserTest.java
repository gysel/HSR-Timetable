package ch.scythe.hsr.json;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

import ch.scythe.hsr.error.ResponseParseException;

public class GsonParserTest {

	@Test
	public void testParseTESTweekFields() throws ResponseParseException {
		// Exercise sut
		JsonTimetableWeek actual = sut.parse(createScenario("Timetable.json"));
		// Verify outcome
		assertEquals(7 + 1, actual.getDays().size());
		assertEquals("Peter Muster", actual.getPerson());
		assertEquals("FS 2012", actual.getSemester());
	}

	@Test
	public void testParseTESTdayFields() throws ResponseParseException {
		// Exercise sut
		JsonTimetableWeek week = sut.parse(createScenario("Timetable.json"));
		JsonDay actualDay = week.getDays().get(1);
		// Verify outcome
		assertEquals((Integer) 1, actualDay.getId());
		assertEquals("Montag", actualDay.getDescription());
		assertEquals(4, actualDay.getLessons().size());
	}

	@Test
	public void testParseTESTlessonFields() throws ResponseParseException {
		// Exercise sut
		JsonTimetableWeek week = sut.parse(createScenario("Timetable.json"));
		JsonLesson actualLesson = week.getDays().get(1).getLessons().get(0);
		// Verify outcome
		assertEquals("Prog2-v1", actualLesson.getName());
		assertEquals("Vorlesung", actualLesson.getType());
		assertEquals(2, actualLesson.getLecturers().size());
		assertEquals(2, actualLesson.getCourseAllocations().size());
	}

	@Test
	public void testParseTESTlecturerFields() throws ResponseParseException {
		// Exercise sut
		JsonTimetableWeek week = sut.parse(createScenario("Timetable.json"));
		JsonLecturer actualLecturer = week.getDays().get(1).getLessons().get(0).getLecturers().get(0);
		// Verify outcome
		assertEquals("Thomas Letsch", actualLecturer.getFullname());
		assertEquals("LET", actualLecturer.getShortname());
	}

	@Test
	public void testParseTESTcourseAllocationFields() throws ResponseParseException {
		// Exercise sut
		JsonTimetableWeek week = sut.parse(createScenario("Timetable.json"));
		JsonCourseAllocation actualCourseAlloction = week.getDays().get(1).getLessons().get(0).getCourseAllocations()
				.get(0);
		// Verify outcome
		assertEquals(1, actualCourseAlloction.getRoomAllocations().size());
		assertEquals("3.008", actualCourseAlloction.getRoomAllocations().get(0).getNumber());
		assertEquals("8:10 - 8:55", actualCourseAlloction.getTimeslot());
		assertEquals("wöchentlich", actualCourseAlloction.getType());
	}

	private GsonParser sut;

	@Before
	public void setUp() throws Exception {

		sut = new GsonParser();

	}

	private InputStream createScenario(String name) {
		return GsonParserTest.class.getResourceAsStream(name);
	}

}
