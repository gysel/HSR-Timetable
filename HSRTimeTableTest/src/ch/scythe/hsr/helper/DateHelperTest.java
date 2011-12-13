package ch.scythe.hsr.helper;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.Locale;

import org.junit.Test;

public class DateHelperTest {

	@Test
	public void testFormatToUserFriendlyFormat() {
		Locale valueBefore = Locale.getDefault();
		//
		Locale.setDefault(Locale.GERMAN);
		assertEquals("Montag, 31. Oktober 2011",
				DateHelper.formatToUserFriendlyFormat(new Date(2011 - 1900, 10 - 1, 31)));
		//
		Locale.setDefault(valueBefore);
	}

}
