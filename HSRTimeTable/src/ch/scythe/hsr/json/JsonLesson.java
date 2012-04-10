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
