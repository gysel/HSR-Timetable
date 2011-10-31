package ch.scythe.hsr.xml;

import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import ch.scythe.hsr.entity.Lesson;
import ch.scythe.hsr.enumeration.WeekDay;

public class SaxTimetableParser {

	public List<Lesson> parse(InputStream xml, WeekDay dayOfWeek) {

		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser parser = factory.newSAXParser();
			LessonHandler handler = new LessonHandler(dayOfWeek);
			parser.parse(xml, handler);
			return handler.getLessons();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

}
