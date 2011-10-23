package ch.scythe.hsr.xml;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import ch.scythe.hsr.entity.Lesson;
import ch.scythe.hsr.error.EnumNotFoundException;

public class LessonHandler extends DefaultHandler {

	private static final String XML_NODE_ROOM = "Room";
	private static final String XML_ATTRIBUTE_ID = "id";
	private static final String XML_NODE_TIME_UNIT = "TimeUnit";
	private static final String XML_NODE_TYPE = "Type";
	private static final String XML_NODE_DESCRIPTION = "Description";
	private static final String XML_NODE_LESSON = "Lesson";
	private static final String XML_NODE_NAME_SHORT = "NameShort";
	private List<Lesson> lessons;
	private Lesson currentLesson;
	private StringBuilder builder;

	public List<Lesson> getLessons() {
		return lessons;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);
		builder.append(ch, start, length);
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		lessons = new ArrayList<Lesson>();
		builder = new StringBuilder();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		if (qName.equalsIgnoreCase(XML_NODE_LESSON)) {
			currentLesson = new Lesson();
			currentLesson.setIdentifier(attributes.getValue(XML_ATTRIBUTE_ID));
		} else if (currentLesson != null && qName.equalsIgnoreCase(XML_NODE_TIME_UNIT)) {
			try {
				currentLesson.addTimeUnit(Integer.parseInt(attributes.getValue(XML_ATTRIBUTE_ID)));
			} catch (EnumNotFoundException e) {
				throw new SAXException(e);
			}

		} else if (currentLesson != null && qName.equalsIgnoreCase(XML_NODE_ROOM)) {
			// TODO one lesson can have several rooms.
			currentLesson.setRoom(attributes.getValue(XML_ATTRIBUTE_ID));
		}

	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);
		if (this.currentLesson != null) {
			if (qName.equalsIgnoreCase(XML_NODE_TYPE)) {
				currentLesson.setType(builder.toString().trim());
			} else if (qName.equalsIgnoreCase(XML_NODE_NAME_SHORT)) {
				currentLesson.addLecturer(builder.toString().trim());
			} else if (qName.equalsIgnoreCase(XML_NODE_LESSON)) {
				lessons.add(currentLesson);
			} else if (qName.equalsIgnoreCase(XML_NODE_DESCRIPTION)) {
				currentLesson.setDescription(builder.toString().trim());
			}
			builder.setLength(0);
		}
	}

}
