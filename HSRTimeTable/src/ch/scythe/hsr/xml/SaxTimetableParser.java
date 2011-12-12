package ch.scythe.hsr.xml;

import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import ch.scythe.hsr.entity.TimetableWeek;

public class SaxTimetableParser {

	public TimetableWeek parse(InputStream xml) {

		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser parser = factory.newSAXParser();
			TimeTableWeekHandler handler = new TimeTableWeekHandler();
			parser.parse(xml, handler);
			return handler.getWeek();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

}
