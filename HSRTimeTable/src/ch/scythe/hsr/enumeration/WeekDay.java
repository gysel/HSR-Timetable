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
package ch.scythe.hsr.enumeration;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;

import ch.scythe.hsr.R;

public enum WeekDay implements Serializable {

	MONDAY(1, 2, R.string.weekday_monday), TUESDAY(2, 3, R.string.weekday_tuesday), WEDNESDAY(3, 4,
			R.string.weekday_wednesday), THURSDAY(4, 5, R.string.weekday_thursday), FRIDAY(5, 6,
			R.string.weekday_friday), SATURDAY(6, 7, R.string.weekday_saturday), SUNDAY(7, 1, R.string.weekday_sunday);

	private final Integer id;
	private final Integer javaId;

	private static final Map<Integer, WeekDay> lookupByJavaId = new LinkedHashMap<Integer, WeekDay>();
	private static final Map<Integer, WeekDay> lookupById = new LinkedHashMap<Integer, WeekDay>();
	private final int resourceReference;

	static {
		for (WeekDay day : EnumSet.allOf(WeekDay.class)) {
			lookupByJavaId.put(day.getJavaId(), day);
			lookupById.put(day.getId(), day);
		}
	}

	private WeekDay(Integer id, Integer javaId, int resourceReference) {
		this.id = id;
		this.javaId = javaId;
		this.resourceReference = resourceReference;
	}

	public Integer getId() {
		return id;
	}

	/**
	 * @see Calendar#DAY_OF_WEEK
	 */
	public Integer getJavaId() {
		return javaId;
	}

	public static WeekDay getById(Integer id) {
		return lookupById.get(id);

	}

	public static WeekDay getByDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return lookupByJavaId.get(calendar.get(Calendar.DAY_OF_WEEK));
	}

	public int getResourceReference() {
		return resourceReference;
	}

}
