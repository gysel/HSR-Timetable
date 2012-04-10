package ch.scythe.hsr.json;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class JsonCourseAllocation {

	@SerializedName("Description")
	private String description;
	@SerializedName("ExamType")
	private String examType;
	@SerializedName("RoomAllocations")
	private List<JsonRoomAllocation> roomAllocations;
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
