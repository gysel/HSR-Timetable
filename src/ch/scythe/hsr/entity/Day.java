package ch.scythe.hsr.entity;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Day {

	private final Map<TimeUnit, Lesson> lessons;

	public Day(Collection<Lesson> lessons) {
		this.lessons = new HashMap<TimeUnit, Lesson>();
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

	public Map<TimeUnit, Lesson> getLessons() {
		return Collections.unmodifiableMap(lessons);
	}

}
