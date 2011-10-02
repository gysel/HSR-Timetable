package ch.scythe.hsr.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Lesson {

	private String type;
	private List<Integer> timeUnits = new ArrayList<Integer>();
	private String identifier;

	public void setType(String type) {
		this.type = type;

	}

	public String getType() {
		return type;
	}

	public void addTimeUnit(Integer timeUnit) {
		timeUnits.add(timeUnit);
	}

	public List<Integer> getTimeUnits() {
		return Collections.unmodifiableList(timeUnits);
	}

	@Override
	public String toString() {
		return "<Lesson identifier=" + identifier + " type=" + type
				+ ", timeUnit=" + timeUnits + " >";
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getIdentifier() {
		return identifier;
	}

}
