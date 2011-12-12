package ch.scythe.hsr.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ch.scythe.hsr.enumeration.TimeUnit;
import ch.scythe.hsr.enumeration.WeekDay;

public class Day {

	// the LinkedHashMap has a defined ordering of the keys
	private final Map<TimeUnit, List<Lesson>> lessons = new LinkedHashMap<TimeUnit, List<Lesson>>();
	private final Date date;
	private final WeekDay weekDay;

	@Deprecated
	public Day(Collection<Lesson> lessons, Date date) {
		this.date = date;
		this.weekDay = null;
		putLessonsIntoTimeslots(lessons);
	}

	public Day(Collection<Lesson> lessons, WeekDay weekDay) {
		this.weekDay = weekDay;
		this.date = null;
		putLessonsIntoTimeslots(lessons);
	}

	private void putLessonsIntoTimeslots(Collection<Lesson> lessons) {
		// initialize all time units with an empty value...
		for (TimeUnit units : TimeUnit.getAll()) {
			this.lessons.put(units, null);
		}
		// and add all lessons to the assigned time slots
		for (Lesson lesson : lessons) {
			for (TimeUnit lessonUnit : lesson.getTimeUnits()) {
				if (this.lessons.get(lessonUnit) == null) {
					this.lessons.put(lessonUnit, new ArrayList<Lesson>());
				}
				this.lessons.get(lessonUnit).add(lesson);
			}
		}
	}

	@Deprecated
	public Day(Date date) {
		this.date = date;
		this.weekDay = null;
	}

	public Map<TimeUnit, List<Lesson>> getLessons() {
		return Collections.unmodifiableMap(lessons);
	}

	public Date getDate() {
		return date;
	}

	public WeekDay getWeekDay() {
		return weekDay;
	}

}
