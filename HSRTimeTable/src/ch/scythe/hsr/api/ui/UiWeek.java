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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ch.scythe.hsr.enumeration.Weekday;

public class UiWeek implements Serializable {

	private static final long serialVersionUID = 1L;
	private final Map<Weekday, UiDay> days = new HashMap<Weekday, UiDay>();
	private Date lastUpdate;

	public Map<Weekday, UiDay> getDays() {
		return days;
	}

	public UiDay getDay(Weekday weekday) {
		return days.get(weekday);
	}

	public void putDay(UiDay uiDay) {
		days.put(uiDay.getWeekday(), uiDay);

	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;

	}

}
