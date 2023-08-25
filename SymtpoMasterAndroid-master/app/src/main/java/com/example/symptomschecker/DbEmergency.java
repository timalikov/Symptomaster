package com.example.symptomschecker;

public class DbEmergency {

	private int id;
	private String name;
	private double latitude;
	private double longitute;
	private String phoneNumber;
	private double distanceTo;

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

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitute() {
		return longitute;
	}

	public void setLongitute(double longitute) {
		this.longitute = longitute;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public double getDistanceTo() {
		return distanceTo;
	}

	public void setDistanceTo(double distanceTo) {
		this.distanceTo = distanceTo;
	}

	public String toString() {
		String out = "";
		String delimiter = "\n";
		String endOfString = "\n\n";

		out += "Emergency ";
		out += "id: " + this.getId() + delimiter;
		out += "name: " + this.getName() + delimiter;
		out += "latitude: " + this.getLatitude() + delimiter;
		out += "longitude: " + this.getLongitute() + delimiter;
		out += "phone_number: " + this.getPhoneNumber() + delimiter;

		out += endOfString;

		return out;
	}
}
