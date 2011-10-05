package ch.scythe.hsr.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Lesson {

	private String type;
	private List<TimeUnit> timeUnits = new ArrayList<TimeUnit>();
	private String identifier;
	private String room;

	public void setType(String type) {
		this.type = type;

	}

	public String getType() {
		return type;
	}

	public void addTimeUnit(Integer timeUnitId) {
		timeUnits.add(TimeUnit.findById(timeUnitId));
	}

	public List<TimeUnit> getTimeUnits() {
		return Collections.unmodifiableList(timeUnits);
	}

	@Override
	public String toString() {
		return "<Lesson identifier=" + identifier + " type=" + type
				+ ", timeUnits=" + timeUnits + " >";
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setRoom(String room) {
		this.room = room;

	}

	public String getRoom() {
		return room;
	}
}
