package ch.scythe.hsr.api.ui;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class UiLessonTest {

	@Test
	public void testTESThasDescription() {
		// Set up fixture
		sut.setDescription("some description");
		// Exercise sut
		boolean actual = sut.hasDescription();
		// Verify outcome
		assertTrue(actual);

	}

	@Test
	public void testTESTdescriptionNull() {
		// Set up fixture
		sut.setDescription(null);
		// Exercise sut
		boolean actual = sut.hasDescription();
		// Verify outcome
		assertFalse(actual);

	}

	@Test
	public void testTESTdescriptionEmpty() {
		// Set up fixture
		sut.setDescription("");
		// Exercise sut
		boolean actual = sut.hasDescription();
		// Verify outcome
		assertFalse(actual);

	}

	private UiLesson sut;

	@Before
	public void setUp() throws Exception {
		sut = new UiLesson();
	}

}
