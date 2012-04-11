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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ch.scythe.hsr.enumeration.WeekDay;

public class UiDay implements Serializable {

	private static final long serialVersionUID = 1L;
	private final WeekDay weekday;
	private List<UiLesson> lessons = new ArrayList<UiLesson>();

	public UiDay(WeekDay weekday) {
		this.weekday = weekday;
	}

	public List<UiLesson> getLessons() {
		return lessons;
	}

	public void setLessons(List<UiLesson> lessons) {
		this.lessons = lessons;
	}

	public WeekDay getWeekday() {
		return weekday;
	}

}
