package ch.scythe.hsr.entity;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import ch.scythe.hsr.enumeration.TimeUnit;

public class Day {

	private final Map<TimeUnit, Lesson> lessons;
	private final Date date;

	public Day(Collection<Lesson> lessons, Date date) {
		this.date = date;
		// the LinkedHashMap has a defined ordering of the keys
		this.lessons = new LinkedHashMap<TimeUnit, Lesson>();
		// initialize all time units with an empty value...
		for (TimeUnit units : TimeUnit.getAll()) {
			this.lessons.put(units, null);
		}
		// and add all lessons to the assigned time slots
		for (Lesson lesson : lessons) {
			for (TimeUnit lessonUnit : lesson.getTimeUnits()) {
				if (this.lessons.get(lessonUnit) != null) {
					// TODO add better exception handling
					throw new RuntimeException("two lessons in the same slot");
				}
				this.lessons.put(lessonUnit, lesson);
			}
		}
	}

	public Day(Date date) {
		this.date = date;
		lessons = new HashMap<TimeUnit, Lesson>();
	}

	public Map<TimeUnit, Lesson> getLessons() {
		return Collections.unmodifiableMap(lessons);
	}

	public Date getDate() {
		return date;
	}

}
