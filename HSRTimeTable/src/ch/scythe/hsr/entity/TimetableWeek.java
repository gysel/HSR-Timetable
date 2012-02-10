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
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ch.scythe.hsr.enumeration.WeekDay;

public class TimetableWeek implements Serializable {

	private static final long serialVersionUID = 1L;
	private Date lastUpdate;
	private final Map<WeekDay, Day> days = new LinkedHashMap<WeekDay, Day>();

	public TimetableWeek(List<Day> days) {
		for (Day day : days) {
			this.days.put(day.getWeekDay(), day);
		}
	}

	public TimetableWeek() {
		lastUpdate = null;
	}

	public Day getDay(WeekDay weekDay) {
		return days.get(weekDay);
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

}
