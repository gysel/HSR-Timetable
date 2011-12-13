package ch.scythe.hsr.helper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateHelper {

	private static DateFormat fullDateFormat = new SimpleDateFormat("EEEE, d. MMMM yyyy");
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
