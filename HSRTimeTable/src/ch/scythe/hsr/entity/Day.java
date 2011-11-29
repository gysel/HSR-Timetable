package ch.scythe.hsr.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ch.scythe.hsr.enumeration.TimeUnit;

public class Day {

	private final Map<TimeUnit, List<Lesson>> lessons;
	private final Date date;

	public Day(Collection<Lesson> lessons, Date date) {
		this.date = date;
		// the LinkedHashMap has a defined ordering of the keys
		this.lessons = new LinkedHashMap<TimeUnit, List<Lesson>>();
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

	public Day(Date date) {
		this.date = date;
		lessons = new HashMap<TimeUnit, List<Lesson>>();
	}

	public Map<TimeUnit, List<Lesson>> getLessons() {
		return Collections.unmodifiableMap(lessons);
	}

	public Date getDate() {
		return date;
	}

}
