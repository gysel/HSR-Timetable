/* 
 * Copyright (C) 2011 Michi Gysel <michael.gysel@gmail.com>
 *
 * This file is part of the HSR Timetable.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ch.scythe.hsr.xml;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import ch.scythe.hsr.entity.Day;
import ch.scythe.hsr.entity.Lesson;
import ch.scythe.hsr.entity.TimetableWeek;
import ch.scythe.hsr.enumeration.WeekDay;
import ch.scythe.hsr.error.EnumNotFoundException;

/**
 * <p>
 * Parses XML files returned by the HSR timetable webservice.
 * </p>
 * <p>
 * Methods currently supported:
 * </p>
 * <ul>
 * <li><code>GetOtherCurrentTimeTableResponse</code></li>
 * </ul>
 */
public class TimeTableWeekHandler extends DefaultHandler {

	private static final String XML_NODE_DAY = "Day";
	private static final String XML_NODE_ROOM = "Room";
	private static final String XML_ATTRIBUTE_ID = "id";
	private static final String XML_NODE_TIME_UNIT = "TimeUnit";
	private static final String XML_NODE_TYPE = "Type";
	private static final String XML_NODE_DESCRIPTION = "Description";
	private static final String XML_NODE_LESSON = "Lesson";
	private static final String XML_NODE_NAME_SHORT = "NameShort";
	//
	private StringBuilder builder;
	//
	private List<Day> days;
	// temporary variables
	private WeekDay currentDay;
	private Lesson currentLesson;
	private List<Lesson> currentLessons;

	public TimeTableWeekHandler() {
	}

	public TimetableWeek getWeek() {
		return new TimetableWeek(days);
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);
		builder.append(ch, start, length);
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		currentLessons = new ArrayList<Lesson>();
		days = new ArrayList<Day>();
		builder = new StringBuilder();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);

		if (qName.equalsIgnoreCase(XML_NODE_DAY)) {

			currentLessons.clear();

			Integer id = Integer.parseInt(attributes.getValue(XML_ATTRIBUTE_ID));
			currentDay = WeekDay.getById(id);

		} else if (currentDay != null) {

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

	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);

		if (XML_NODE_DAY.equalsIgnoreCase(qName)) {

			days.add(new Day(currentLessons, currentDay));
			currentDay = null;
			currentLessons.clear();

		} else if (currentDay != null) {

			if (this.currentLesson != null) {
				if (qName.equalsIgnoreCase(XML_NODE_TYPE)) {
					currentLesson.setType(builder.toString().trim());
				} else if (qName.equalsIgnoreCase(XML_NODE_NAME_SHORT)) {
					currentLesson.addLecturer(builder.toString().trim());
				} else if (qName.equalsIgnoreCase(XML_NODE_LESSON)) {
					currentLessons.add(currentLesson);
				} else if (qName.equalsIgnoreCase(XML_NODE_DESCRIPTION)) {
					currentLesson.setDescription(builder.toString().trim());
				}
			}

		}
		builder.setLength(0);

	}
}
