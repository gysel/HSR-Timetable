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
package ch.scythe.hsr.json;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class JsonCourseAllocation {

	@SerializedName("Description")
	private String description;
	@SerializedName("ExamType")
	private String examType;
	@SerializedName("RoomAllocations")
	private List<JsonRoomAllocation> roomAllocations = new ArrayList<JsonRoomAllocation>();
	@SerializedName("Timeslot")
	private String timeslot;
	@SerializedName("Type")
	private String type;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getExamType() {
		return examType;
	}

	public void setExamType(String examType) {
		this.examType = examType;
	}

	public List<JsonRoomAllocation> getRoomAllocations() {
		return roomAllocations;
	}

	public void setRoomAllocations(List<JsonRoomAllocation> roomAllocations) {
		this.roomAllocations = roomAllocations;
	}

	public String getTimeslot() {
		return timeslot;
	}

	public void setTimeslot(String timeslot) {
		this.timeslot = timeslot;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
