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

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class JsonLesson {

	@SerializedName("Name")
	private String name;
	@SerializedName("Type")
	private String type;
	@SerializedName("Lecturers")
	private List<JsonLecturer> lecturers;
	@SerializedName("CourseAllocations")
	private List<JsonCourseAllocation> courseAllocations;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<JsonLecturer> getLecturers() {
		return lecturers;
	}

	public void setLecturers(List<JsonLecturer> lecturers) {
		this.lecturers = lecturers;
	}

	public List<JsonCourseAllocation> getCourseAllocations() {
		return courseAllocations;
	}

	public void setCourseAllocations(List<JsonCourseAllocation> courseAllocations) {
		this.courseAllocations = courseAllocations;
	}

}
