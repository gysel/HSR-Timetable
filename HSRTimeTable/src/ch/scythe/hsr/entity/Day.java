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
package ch.scythe.hsr.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.scythe.hsr.enumeration.TimeUnit;
import ch.scythe.hsr.enumeration.WeekDay;

@Deprecated
public class Day implements Serializable {

	private static final long serialVersionUID = 1L;
	// the LinkedHashMap has a defined ordering of the keys
	private final Map<TimeUnit, List<Lesson>> lessons = new LinkedHashMap<TimeUnit, List<Lesson>>();
	private final WeekDay weekDay;

	public Day(Collection<Lesson> lessons, WeekDay weekDay) {
		this.weekDay = weekDay;
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

	public Map<TimeUnit, List<Lesson>> getLessons() {
		return Collections.unmodifiableMap(lessons);
	}

	/**
	 * @return All {@link Lesson}s without empty {@link TimeUnit}s before the
	 *         the first lesson and after the last lesson.
	 */
	public Map<TimeUnit, List<Lesson>> getLessonsCompact() {
		Map<TimeUnit, List<Lesson>> result = new LinkedHashMap<TimeUnit, List<Lesson>>(lessons);
		List<TimeUnit> keys = TimeUnit.getAll();
		Set<TimeUnit> keysToRemove = new HashSet<TimeUnit>();
		for (int i = 0; i < keys.size(); i++) {
			TimeUnit key = keys.get(i);
			if (result.get(key) == null) {
				keysToRemove.add(key);
			} else {
				break;
			}
		}
		for (int i = keys.size() - 1; i >= 0; i--) {
			TimeUnit key = keys.get(i);
			if (result.get(key) == null) {
				keysToRemove.add(key);
			} else {
				break;
			}
		}
		for (TimeUnit timeUnit : keysToRemove) {
			result.remove(timeUnit);
		}
		return result;
	}

	public WeekDay getWeekDay() {
		return weekDay;
	}

}
