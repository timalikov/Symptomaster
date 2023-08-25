package com.example.symptomschecker;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputValidation {

	public static Validator validateString(String str, String label) {
		int stdStrLen = 128;
		String delimiter = "\n";

		Validator v = new Validator();
		v.setVerified(true);
		v.setStatus("");

		if (str.isEmpty()) {
			v.setVerified(false);
			v.setStatus(label + " cannot be empty" + delimiter);
		} else if (str.length() > stdStrLen) {
			v.setVerified(false);
			v.setStatus(label + " length over " + stdStrLen
					+ " characters" + delimiter);
		}

		return v;
	}
	
	public static Validator validateDate(String str, String label) {
		String delimiter = "\n";

		Validator v = new Validator();
		v.setVerified(true);
		v.setStatus("");

		if (str.isEmpty()) {
			v.setVerified(false);
			v.setStatus(label + " cannot be empty" + delimiter);
		} else if (str.equals("Select Date")) {
			v.setVerified(false);
			v.setStatus(label + " not set" + delimiter);
		}

		return v;
	}


	public static Validator validateIntGeneral(int input, String label) {
		String delimiter = "\n";
		Validator v = new Validator();

		v.setVerified(true);
		v.setStatus("");

		if (input < 1) {
			v.setVerified(false);
			v.setStatus(label
					+ " cannot be empty" + delimiter);
		}

		return v;
	}

	public static Validator validateEmail(String str, String label) {
		String delimiter = "\n";
		Validator v = new Validator();

		Pattern pattern = Pattern
				.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
						+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
		Matcher matcher = pattern.matcher(str);

		v.setVerified(false);
		v.setStatus(label + " is invalid" + delimiter);

		if (matcher.matches()) {
			v.setVerified(true);
			v.setStatus("");
		}

		return v;
	}

}
