package ch.scythe.hsr.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public enum TimeUnit {

	LESSON_1(1, "07:05", "07:50"), LESSON_2(2, "08:10", "08:55"), LESSON_3(3,
			"09:05", "09:50"), LESSON_4(4, "10:10", "10:55"), LESSON_5(5,
			"11:05", "11:50"), LESSON_6(6, "12:10", "12:55"), LESSON_7(7,
			"13:10", "13:55"), LESSON_8(8, "14:05", "14:50"), LESSON_9(9,
			"15:10", "15:55"), LESSON_10(10, "16:06", "16:50"), LESSON_11(11,
			"17:00", "17:45"), LESSON_12(12, "17:55", "18:40");

	private final Integer id;
	private final String startTime;
	private final String endTime;
	private static final Map<Integer, TimeUnit> lookup = new LinkedHashMap<Integer, TimeUnit>();

	private TimeUnit(Integer id, String startTime, String endTime) {
		this.id = id;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	static {
		for (TimeUnit unit : EnumSet.allOf(TimeUnit.class)) {
			lookup.put(unit.getId(), unit);
		}
	}

	public int getId() {
		return id;
	}

	public String getStartTime() {
		return startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public static TimeUnit findById(Integer id) {
		return lookup.get(id);
	}

	public static List<TimeUnit> getAll() {
		return Collections.unmodifiableList(new ArrayList<TimeUnit>(lookup
				.values()));
	}

}
