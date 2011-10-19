package ch.scythe.hsr.entity;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ch.scythe.hsr.error.EnumNotFoundException;

public class LessonTest {

	@Test
	public void testAddTimeUnit() throws EnumNotFoundException {
		// Set up fixture
		TimeUnit expectedTimeUnit = TimeUnit.LESSON_1;
		// Exercise sut
		sut.addTimeUnit(expectedTimeUnit.getId());
		// Verify outcome
		List<TimeUnit> actualTimeUnits = sut.getTimeUnits();
		Assert.assertEquals(1, actualTimeUnits.size());
		actualTimeUnits.get(0).equals(expectedTimeUnit);
	}

	@Test(expected = EnumNotFoundException.class)
	public void testAddTimeUnitTESTvalueTooLow() throws EnumNotFoundException {
		// Set up fixture
		int timeUnitId = TimeUnit.LESSON_1.getId() - 1;
		// Exercise sut
		sut.addTimeUnit(timeUnitId);
	}

	@Test(expected = EnumNotFoundException.class)
	public void testAddTimeUnitTESTvalueTooHigh() throws EnumNotFoundException {
		// Set up fixture
		int timeUnitId = TimeUnit.LESSON_12.getId() + 1;
		// Exercise sut
		sut.addTimeUnit(timeUnitId);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetTimeUnitsTESTmodification() throws Exception {
		// Set up fixture
		TimeUnit expectedTimeUnit = TimeUnit.LESSON_1;
		sut.addTimeUnit(expectedTimeUnit.getId());
		// Exercise sut
		sut.getTimeUnits().add(TimeUnit.LESSON_2);
	}

	@Test
	public void testLecturers() {
		String delimiter = ", ";
		assertEquals("", sut.getLecturersAsString(delimiter));
		// Set up fixture
		String lecturer1 = "FOO";
		String lecturer2 = "BAR";
		// Exercise sut
		sut.addLecturer(lecturer1);
		sut.addLecturer(lecturer2);
		// Verify outcome
		assertEquals(lecturer1 + delimiter + lecturer2, sut.getLecturersAsString(delimiter));
	}

	private Lesson sut;

	@Before
	public void setUp() throws Exception {
		sut = new Lesson();
	}

}
