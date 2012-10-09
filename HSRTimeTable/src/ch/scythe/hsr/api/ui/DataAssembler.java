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
package ch.scythe.hsr.api.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ch.scythe.hsr.enumeration.Weekday;
import ch.scythe.hsr.json.JsonCourseAllocation;
import ch.scythe.hsr.json.JsonDay;
import ch.scythe.hsr.json.JsonLecturer;
import ch.scythe.hsr.json.JsonLesson;
import ch.scythe.hsr.json.JsonRoomAllocation;
import ch.scythe.hsr.json.JsonTimetableWeek;

public class DataAssembler {

	public static final String LIST_SEPARATOR = ", ";

	public static UiWeek convert(JsonTimetableWeek jsonData) {
		UiWeek result = new UiWeek();
		mapDays(result, jsonData);
		return result;
	}

	private static void mapDays(UiWeek uiWeek, JsonTimetableWeek jsonData) {
		List<JsonDay> jsonDays = jsonData.getDays();
		for (JsonDay jsonDay : jsonDays) {
			uiWeek.putDay(mapDay(jsonDay));
		}
	}

	private static UiDay mapDay(JsonDay jsonDay) {
		Weekday weekday = Weekday.getById(jsonDay.getId());
		UiDay result = new UiDay(weekday);
		result.setLessons(mapLessons(jsonDay.getLessons()));
		return result;
	}

	private static List<UiLesson> mapLessons(List<JsonLesson> lessons) {
		List<UiLesson> result = new ArrayList<UiLesson>();
		for (JsonLesson lesson : lessons) {
			for (JsonCourseAllocation allocation : lesson.getCourseAllocations()) {
				UiLesson uiLesson = new UiLesson();
				uiLesson.setName(lesson.getName());
				uiLesson.setType(lesson.getType());
				uiLesson.setTimeSlot(shortenTimeSlot(allocation.getTimeslot()));
				uiLesson.setDescription(allocation.getDescription());
				uiLesson.setLecturerShort(implodeLecturers(lesson.getLecturers(), true));
				uiLesson.setLecturerLong(implodeLecturers(lesson.getLecturers(), false));
				uiLesson.setRoom(implodeRooms(allocation.getRoomAllocations()));
				result.add(uiLesson);
			}
		}
		return result;
	}

	static String shortenTimeSlot(String timeSlot) {
		String[] tokens = timeSlot.trim().split("-"); // split by "-" in "8:10 - 8:55"
		String result = timeSlot;
		if (tokens.length == 2) {
			String[] secondTokens = tokens[1].trim().split(":"); // split by ":" in "8:55"
			result = tokens[0].trim() + " - :" + secondTokens[1].trim();
		}
		return result;
	}

	static String implodeLecturers(List<JsonLecturer> lecturers, boolean shortVersion) {
		StringBuilder result = new StringBuilder();
		for (Iterator<JsonLecturer> iterator = lecturers.iterator(); iterator.hasNext();) {
			JsonLecturer lecturer = iterator.next();
			if (shortVersion) {
				if (maxSize(result)) {
					result.append("…");
				} else {

					result.append(lecturer.getShortname());
				}
			} else {
				result.append(lecturer.getFullname());
			}
			if (iterator.hasNext() && !(shortVersion && maxSize(result))) {
				result.append(LIST_SEPARATOR);
			}
		}
		return result.toString();
	}

	private static boolean maxSize(StringBuilder result) {
		return result.length() >= 8;
	}

	static String implodeRooms(List<JsonRoomAllocation> rooms) {
		StringBuilder result = new StringBuilder();
		for (Iterator<JsonRoomAllocation> iterator = rooms.iterator(); iterator.hasNext();) {
			JsonRoomAllocation room = iterator.next();
			result.append(room.getNumber());
			if (iterator.hasNext()) {
				result.append(LIST_SEPARATOR);
			}
		}
		return result.toString();
	}
}
