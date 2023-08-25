package com.example.symptomschecker;

public class DbResult {
	private int id;
	private String cause;
	private String recommendation;
	private int enable;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCause() {
		return cause;
	}

	public void setCause(String cause) {
		this.cause = cause;
	}

	public String getRecommendation() {
		return recommendation;
	}

	public void setRecommendation(String recommendation) {
		this.recommendation = recommendation;
	}

	public int getEnable() {
		return enable;
	}

	public void setEnable(int enable) {
		this.enable = enable;
	}

	public String toString() {
		String out = "";
		String delimiter = " ";
		String endOfString = "\n";

		out += "id: " + this.getId() + delimiter;
		out += "cause: " + this.getCause() + delimiter;
		out += "recommendation: " + this.getRecommendation() + delimiter;
		out += "enable: " + this.getEnable() + delimiter;

		out += endOfString;

		return out;
	}
}
