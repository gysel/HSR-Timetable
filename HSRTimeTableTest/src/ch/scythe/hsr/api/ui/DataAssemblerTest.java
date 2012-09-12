package ch.scythe.hsr.api.ui;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import ch.scythe.hsr.enumeration.Weekday;
import ch.scythe.hsr.helper.TextHelper;
import ch.scythe.hsr.json.JsonCourseAllocation;
import ch.scythe.hsr.json.JsonDay;
import ch.scythe.hsr.json.JsonLecturer;
import ch.scythe.hsr.json.JsonLesson;
import ch.scythe.hsr.json.JsonRoomAllocation;
import ch.scythe.hsr.json.JsonTimetableWeek;

public class DataAssemblerTest {

	@Test
	public void testConvertTESTweekdayMapping() {
		// Set up fixture
		JsonDay monday = createDay(1);
		JsonDay wednesday = createDay(3);
		JsonTimetableWeek jsonData = createWeek(monday, wednesday);
		// Exercise sut
		UiWeek uiData = DataAssembler.convert(jsonData);
		// Verify outcome
		assertNotNull(uiData.getDay(Weekday.MONDAY));
		assertNull(uiData.getDay(Weekday.TUESDAY));
		assertNotNull(uiData.getDay(Weekday.WEDNESDAY));
	}

	@Test
	public void testConvertTESTdayMapping() {
		String mathVname = "An2I-v2";
		String mathVtype = "Vorlesung";
		String mathUname = "An2I-u22";
		String mathUtype = "Uebung";
		// Set up fixture
		JsonDay wednesday = createDay(3, createLesson(mathVname, mathVtype, "", "10:10 - 10:55"),
				createLesson(mathUname, mathUtype, "", "11:05 - 11:50"));
		JsonTimetableWeek jsonData = createWeek(wednesday);
		// Exercise sut
		UiWeek uiData = DataAssembler.convert(jsonData);
		// Verify outcome
		UiDay day = uiData.getDay(Weekday.WEDNESDAY);
		assertNotNull(day);
		List<UiLesson> lessons = day.getLessons();
		//
		UiLesson mathV = lessons.get(0);
		assertEquals(mathVname, mathV.getName());
		//
		UiLesson mathU = lessons.get(1);
		assertEquals(mathUname, mathU.getName());
	}

	@Test
	public void testConvertTESTlessonMapping() {
		String name = "CN2Prak-p4";
		String type = "Praktikum";
		String description = "CN KW 9,11,14,16,18,20,22";
		String[] timeslots = new String[] { "13:10 - 13:55", "14:05 - 14:50", "15:10 - 15:55", "16:05 - 16:50" };
		// Set up fixture
		JsonDay wednesday = createDay(3, createLesson(name, type, description, timeslots));
		JsonTimetableWeek jsonData = createWeek(wednesday);
		// Exercise sut
		UiWeek uiData = DataAssembler.convert(jsonData);
		// Verify outcome
		UiDay day = uiData.getDay(Weekday.WEDNESDAY);
		assertNotNull(day);
		List<UiLesson> lessons = day.getLessons();
		//
		for (int i = 0; i < 4; i++) {
			assertEquals(timeslots[i], lessons.get(i).getTimeSlot());
			assertEquals(name, lessons.get(i).getName());
			assertEquals(type, lessons.get(i).getType());
			assertEquals(description, lessons.get(i).getDescription());
		}
	}

	@Test
	public void testConvertTESTroomsAndLecturersMapping() {
		String[] lecturers = new String[] { "LET", "JOL" };
		String[] rooms = new String[] { "2.103", "2.104" };
		// Set up fixture
		JsonLesson lesson = createLesson("", "", "", "");
		addLecturers(lesson, lecturers);
		addRooms(lesson, rooms);
		JsonDay wednesday = createDay(3, lesson);
		JsonTimetableWeek jsonData = createWeek(wednesday);
		// Exercise sut
		UiWeek uiData = DataAssembler.convert(jsonData);
		// Verify outcome
		UiDay day = uiData.getDay(Weekday.WEDNESDAY);
		assertNotNull(day);
		List<UiLesson> lessons = day.getLessons();
		//
		assertEquals(TextHelper.implodeArray(lecturers, DataAssembler.LIST_SEPARATOR), lessons.get(0).getLecturer());
		assertEquals(TextHelper.implodeArray(rooms, DataAssembler.LIST_SEPARATOR), lessons.get(0).getRoom());
	}

	private JsonTimetableWeek createWeek(JsonDay... days) {
		JsonTimetableWeek result = new JsonTimetableWeek();
		result.setDays(Arrays.asList(days));
		return result;
	}

	private JsonDay createDay(int id, JsonLesson... lessons) {
		JsonDay jsonDay = new JsonDay();
		jsonDay.setId(id);
		jsonDay.setLessons(Arrays.asList(lessons));
		return jsonDay;
	}

	private JsonLesson createLesson(String name, String type, String description, String... timeslots) {
		JsonLesson result = new JsonLesson();
		result.setName(name);
		result.setType(type);
		List<JsonCourseAllocation> courseAllocations = new ArrayList<JsonCourseAllocation>();
		for (String timeslot : timeslots) {
			JsonCourseAllocation jsonCourseAllocation = new JsonCourseAllocation();
			jsonCourseAllocation.setTimeslot(timeslot);
			jsonCourseAllocation.setDescription(description);
			courseAllocations.add(jsonCourseAllocation);
		}
		result.setCourseAllocations(courseAllocations);
		return result;
	}

	private void addLecturers(JsonLesson lesson, String[] lecturers) {
		List<JsonLecturer> jsonLecturers = new ArrayList<JsonLecturer>();
		for (String lecturer : lecturers) {
			JsonLecturer jsonLecturer = new JsonLecturer();
			jsonLecturer.setShortname(lecturer);
			jsonLecturers.add(jsonLecturer);
		}
		lesson.setLecturers(jsonLecturers);
	}

	private void addRooms(JsonLesson lesson, String[] rooms) {
		List<JsonRoomAllocation> jsonRooms = new ArrayList<JsonRoomAllocation>();
		for (String room : rooms) {
			JsonRoomAllocation jsonLecturer = new JsonRoomAllocation();
			jsonLecturer.setNumber(room);
			jsonRooms.add(jsonLecturer);
		}
		for (JsonCourseAllocation courseAllocations : lesson.getCourseAllocations()) {
			courseAllocations.setRoomAllocations(jsonRooms);
		}
	}

}
