package ch.scythe.hsr.json;

import com.google.gson.annotations.SerializedName;

public class JsonRoomAllocation {

	@SerializedName("Number")
	private String number;

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

}
