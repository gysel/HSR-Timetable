package ch.scythe.hsr.json;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class JsonDay {

	@SerializedName("Id")
	private Integer id;
	@SerializedName("Description")
	private String description;
	@SerializedName("Lessons")
	private List<JsonLesson> lessons;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<JsonLesson> getLessons() {
		return lessons;
	}

	public void setLessons(List<JsonLesson> lessons) {
		this.lessons = lessons;
	}

}
