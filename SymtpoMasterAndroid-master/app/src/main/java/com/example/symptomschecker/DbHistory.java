package com.example.symptomschecker;

public class DbHistory {

	private int id;
	private int userId;
	private String sequence;
	private String result;
	private String date;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getSequence() {
		return sequence;
	}
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
	public String toString() {
		String out = "";
		String delimiter = "\n";
		String endOfString = "\n\n";
		
		out += "History ";
		out += "id: " + this.getId() + delimiter;
		out += "user_id: " + this.getUserId() + delimiter;
		out += "sequence: " + this.getSequence() + delimiter;
		out += "result: " + this.getResult() + delimiter;
		out += "date: " + this.getDate() + delimiter;

		out += endOfString;

		return out;
	}
}
