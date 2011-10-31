package ch.scythe.hsr.enumeration;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

public class WeekDayTest {

	@Test
	public void test() {
		// Set up fixture
		Date sunday = new Date(2011 - 1900, 10 - 1, 30);
		Date monday = new Date(2011 - 1900, 10 - 1, 31);
		Date tuesday = new Date(2011 - 1900, 11 - 1, 1);
		// Exercise sut & verify outcome
		assertEquals(WeekDay.SUNDAY, WeekDay.getByDate(sunday));
		assertEquals(WeekDay.MONDAY, WeekDay.getByDate(monday));
		assertEquals(WeekDay.TUESDAY, WeekDay.getByDate(tuesday));
	}
}
