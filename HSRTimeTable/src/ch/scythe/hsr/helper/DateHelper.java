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
package ch.scythe.hsr.helper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateHelper {

	private static DateFormat fullDateFormat = new SimpleDateFormat("d.M.yy H:mm");
	private static DateFormat technicalDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static DateFormat weekNumberFormat = new SimpleDateFormat("w");

	public synchronized static String formatToWeekNumber(Date date) {
		return weekNumberFormat.format(date);
	}

	public synchronized static String formatToUserFriendlyFormat(Date date) {
		return fullDateFormat.format(date);
	}

	public static String formatToTechnicalFormat(Date date) {
		return technicalDateFormat.format(date);
	}

	public static Date addDays(Date date, int numberOfDays) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, numberOfDays);
		return c.getTime();
	}

}
