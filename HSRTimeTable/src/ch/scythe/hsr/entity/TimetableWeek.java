package ch.scythe.hsr.entity;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ch.scythe.hsr.enumeration.WeekDay;

public class TimetableWeek {

	private final Map<WeekDay, Day> days = new LinkedHashMap<WeekDay, Day>();

	public TimetableWeek(List<Day> days) {
		for (Day day : days) {
			this.days.put(day.getWeekDay(), day);
		}
	}

	public Day getDay(WeekDay weekDay) {
		return days.get(weekDay);
	}

}
