/* 
 * Copyright (C) 2011 - 2012 Michi Gysel <michael.gysel@gmail.com>
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
package ch.scythe.hsr.api.ui;

import java.io.Serializable;

public class UiLesson implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private String timeSlot;
	private String lecturerShort;
	private String lecturerLong;
	private String type;
	private String room;
	private String description;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTimeSlot() {
		return timeSlot;
	}

	public void setTimeSlot(String timeSlot) {
		this.timeSlot = timeSlot;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean hasDescription() {
		return description != null && description.length() > 0;
	}

	public String getLecturerShort() {
		return lecturerShort;
	}

	public void setLecturerShort(String lecturerShort) {
		this.lecturerShort = lecturerShort;
	}

	public String getLecturerLong() {
		return lecturerLong;
	}

	public void setLecturerLong(String lecturerLong) {
		this.lecturerLong = lecturerLong;
	}

}
