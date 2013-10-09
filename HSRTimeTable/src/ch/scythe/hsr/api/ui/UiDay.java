/* 
 * Copyright (C) 2011 - 2013 Michi Gysel <michael.gysel@gmail.com>
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ch.scythe.hsr.enumeration.Weekday;

public class UiDay implements Serializable {

	private static final long serialVersionUID = 1L;
	private final Weekday weekday;
	private List<UiLesson> lessons = new ArrayList<UiLesson>();

	public UiDay(Weekday weekday) {
		this.weekday = weekday;
	}

	public List<UiLesson> getLessons() {
		return lessons;
	}

	public void setLessons(List<UiLesson> lessons) {
		this.lessons = lessons;
		Collections.sort(lessons, new Comparator<UiLesson>() {
			@Override
			public int compare(UiLesson l, UiLesson r) {
				int result = l.getTimeSlot().compareTo(r.getTimeSlot());
				if (result == 0) {
					result = l.getName().compareTo(r.getName());
				} else if ("spezial".equals(l.getTimeSlot())
						|| "spezial".equals(r.getTimeSlot())) {
					result = result * -1;
				}
				return result;
			}

		});
	}

	public Weekday getWeekday() {
		return weekday;
	}

}
