package ch.scythe.hsr.json;

import com.google.gson.annotations.SerializedName;

public class JsonLecturer {
	@SerializedName("Fullname")
	private String fullname;
	@SerializedName("Shortname")
	private String shortname;

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getShortname() {
		return shortname;
	}

	public void setShortname(String shortname) {
		this.shortname = shortname;
	}

}
