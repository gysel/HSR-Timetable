package ch.scythe.hsr.helper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateHelper {

	private static DateFormat mediumDateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
	private static DateFormat technicalDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static DateFormat weekDayFormat = new SimpleDateFormat("E");
	private static DateFormat weekNumberFormat = new SimpleDateFormat("w");

	public synchronized static String formatToWeekNumber(Date date) {
		return weekNumberFormat.format(date);
	}

	public synchronized static String formatToUserFriendlyFormat(Date date) {
		return weekDayFormat.format(date) + " " + mediumDateFormat.format(date);
	}

	public static String formatToTechnicalFormat(Date date) {
		return technicalDateFormat.format(date);
	}

}
