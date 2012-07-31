package ch.scythe.hsr.helper;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TextHelperTest {

	@Test
	public void sanitize() {
		// Set up fixture
		String string = "foobar";
		// Exercise sut
		String actual = TextHelper.sanitize(string);
		// Verify outcome
		assertEquals(string, actual);
	}

	@Test
	public void sanitizeTESTnewLine() {
		// Set up fixture
		String string = "foobar";
		// Exercise sut
		String actual = TextHelper.sanitize(string + "\n");
		// Verify outcome
		assertEquals(string, actual);
	}

}
