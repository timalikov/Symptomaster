package com.example.symptomschecker;

import java.util.ArrayList;
import java.util.Iterator;

public class DbSymptom {
	private int id;
	private String name;
	private String description;
	private String question;
	private int enabled;
	private ArrayList<DbSymptom> childs;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public int getEnabled() {
		return enabled;
	}

	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}

	public ArrayList<DbSymptom> getChilds() {
		return childs;
	}

	public void setChilds(ArrayList<DbSymptom> childs) {
		this.childs = childs;
	}

		
	public String toString() {
		String out = "";
		String delimiter = "\n";
		String endOfString = "\n\n";

		out += "Symptom ";
		out += "id: " + this.getId() + delimiter;
		out += "name: " + this.getName() + delimiter;
		out += "description: " + this.getDescription() + delimiter;
		out += "question: " + this.getQuestion() + delimiter;
		out += "enabled: " + this.getEnabled() + delimiter;
		
		out += endOfString;

		return out;
	}

}
