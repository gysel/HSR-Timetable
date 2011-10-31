package ch.scythe.hsr.enumeration;

import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;

public enum WeekDay {

	MONDAY(1, 2), TUESDAY(2, 3), WEDNESDAY(3, 4), THURSDAY(4, 5), FRIDAY(5, 6), SATURDAY(6, 7), SUNDAY(7, 1);

	private final Integer id;
	private final Integer javaId;

	/**
	 * @see Calendar#DAY_OF_WEEK
	 */
	private static final Map<Integer, WeekDay> lookupByJavaId = new LinkedHashMap<Integer, WeekDay>();

	private WeekDay(Integer id, Integer javaId) {
		this.id = id;
		this.javaId = javaId;
	}

	static {
		for (WeekDay day : EnumSet.allOf(WeekDay.class)) {
			lookupByJavaId.put(day.getJavaId(), day);
		}
	}

	public Integer getId() {
		return id;
	}

	public Integer getJavaId() {
		return javaId;
	}

	public static WeekDay getByDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return lookupByJavaId.get(calendar.get(Calendar.DAY_OF_WEEK));
	}

}
