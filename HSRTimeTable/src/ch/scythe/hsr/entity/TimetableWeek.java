package ch.scythe.hsr.entity;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ch.scythe.hsr.enumeration.WeekDay;

public class TimetableWeek implements Serializable {

	private static final long serialVersionUID = 1L;
	private final Map<WeekDay, Day> days = new LinkedHashMap<WeekDay, Day>();

	public TimetableWeek(List<Day> days) {
		updateData(days);
	}

	public TimetableWeek() {
	}

	private void updateData(List<Day> days) {
		this.days.clear();
		for (Day day : days) {
			this.days.put(day.getWeekDay(), day);
		}

	}

	public Day getDay(WeekDay weekDay) {
		return days.get(weekDay);
	}

}
