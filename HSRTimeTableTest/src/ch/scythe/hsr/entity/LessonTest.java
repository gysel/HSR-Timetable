package ch.scythe.hsr.entity;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ch.scythe.hsr.enumeration.TimeUnit;
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

	@Test
	public void testLecturersTESTtooMany() {
		String delimiter = ", ";
		assertEquals("", sut.getLecturersAsString(delimiter));
		// Set up fixture
		String lecturer1 = "FOO";
		String lecturer2 = "BAR";
		// Exercise sut
		sut.addLecturer(lecturer1);
		sut.addLecturer(lecturer2);
		sut.addLecturer("some");
		sut.addLecturer("more");
		// Verify outcome
		assertEquals(lecturer1 + delimiter + lecturer2 + " …", sut.getLecturersAsString(delimiter));
	}

	@Test
	public void testGetIdentifierShort() throws Exception {
		String noise = "34713_";
		String interestingPart = "An1I-v2";
		// Set up fixture
		sut.setIdentifier(noise + interestingPart);
		// Exercise sut
		String actualIdentifierShort = sut.getIdentifierShort();
		// Verify outcome
		assertEquals(interestingPart, actualIdentifierShort);
	}

	@Test
	public void testGetIdentifierShortTESTparsableButDuplicateSplittingDelimiter() throws Exception {
		String noise = "34713_";
		String interestingPart = "An1I_v2";
		// Set up fixture
		sut.setIdentifier(noise + interestingPart);
		// Exercise sut
		String actualIdentifierShort = sut.getIdentifierShort();
		// Verify outcome
		assertEquals(interestingPart, actualIdentifierShort);
	}

	@Test
	public void testGetIdentifierShortTESTnotParseable() throws Exception {
		String identifier = "anUnknownFormat";
		// Set up fixture
		sut.setIdentifier(identifier);
		// Exercise sut
		String actualIdentifierShort = sut.getIdentifierShort();
		// Verify outcome
		assertEquals(identifier, actualIdentifierShort);
	}

	@Test
	public void testGetIdentifierShortTESTslightlyNotParsable() throws Exception {
		String identifier = "34713-An1I_v2";
		// Set up fixture
		sut.setIdentifier(identifier);
		// Exercise sut
		String actualIdentifierShort = sut.getIdentifierShort();
		// Verify outcome
		assertEquals(identifier, actualIdentifierShort);
	}

	@Test
	public void testHasDescription() throws Exception {
		sut.setDescription(null);
		assertFalse(sut.hasDescription());
		//
		sut.setDescription("");
		assertFalse(sut.hasDescription());
		//
		sut.setDescription("some text");
		assertTrue(sut.hasDescription());

	}

	private Lesson sut;

	@Before
	public void setUp() throws Exception {
		sut = new Lesson();
	}

}
