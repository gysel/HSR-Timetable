package ch.scythe.hsr.xml;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.AttributeList;
import org.xml.sax.HandlerBase;
import org.xml.sax.SAXException;

import ch.scythe.hsr.entity.Lesson;

public class LessonHandler extends HandlerBase {

	private static final String XML_ATTRIBUTE_ID = "id";
	private static final String XML_NODE_TIME_UNIT = "TimeUnit";
	private static final String XML_NODE_TYPE = "Type";
	private static final String XML_NODE_LESSON = "Lesson";
	private List<Lesson> lessons;
	private Lesson currentLesson;
	private StringBuilder builder;

	public List<Lesson> getLessons() {
		return lessons;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
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
	public void startElement(String name, AttributeList attributes)
			throws SAXException {

		super.startElement(name, attributes);
		if (name.equalsIgnoreCase(XML_NODE_LESSON)) {
			currentLesson = new Lesson();
			currentLesson.setIdentifier(attributes.getValue(XML_ATTRIBUTE_ID));
		} else if (currentLesson != null
				&& name.equalsIgnoreCase(XML_NODE_TIME_UNIT)) {
			currentLesson.addTimeUnit(Integer.parseInt(attributes
					.getValue(XML_ATTRIBUTE_ID)));

		}

	}

	@Override
	public void endElement(String name) throws SAXException {
		super.endElement(name);
		if (this.currentLesson != null) {
			if (name.equalsIgnoreCase(XML_NODE_TYPE)) {
				currentLesson.setType(builder.toString());
			} else if (name.equalsIgnoreCase(XML_NODE_LESSON)) {
				lessons.add(currentLesson);
			}
			builder.setLength(0);
		}
	}

}
