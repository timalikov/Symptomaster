package com.example.symptomschecker;

public class DbUser {

	private int id;
	private String email;
	private String password;
	private String salt;
	private String firstname;
	private String lastname;
	private int gender;
	private String dob;
	private int height;
	private int weight;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public void setPasswordHashed(String password) {
		// get the password hasher
		PasswordHasher ph = new PasswordHasher();

		// generate 4 bytes salt
		String salt = ph.generateSalt();
		// get SHA512 digest
		String passwordHashed = ph.sha512(password, salt);

		// set salt
		this.setSalt(salt);
		// set the password
		this.setPassword(passwordHashed);
	}

	public String toString() {
		String out = "";
		String delimiter = " ";
		String endOfString = "\n";

		out += "id: " + this.getId() + delimiter;
		out += "email: " + this.getEmail() + delimiter;
		out += "password: " + this.getPassword() + delimiter;
		out += "salt: " + this.getSalt() + delimiter;
		out += "firstname: " + this.getFirstname() + delimiter;
		out += "lastname: " + this.getLastname() + delimiter;
		out += "gender: " + this.getGender() + delimiter;
		out += "dob: " + this.getDob() + delimiter;
		out += "height: " + this.getHeight() + delimiter;
		out += "weight: " + this.getWeight() + delimiter;

		out += endOfString;

		return out;
	}

	public Validator validate() {

		InputValidation iv = new InputValidation();
		Validator vResult = new Validator();
		Validator vFirstname, vLastname, vEmail, vDob, vHeight, vWeight, vPassword;

		Boolean verified = true;
		String status = "";

		// 1. validate Firstname
		// vFirstname = iv.validateString(this.getFirstname(),
		// Resources.getSystem().getString(R.string.first_name));
		vFirstname = iv.validateString(this.getFirstname(), "Firstname");
		verified &= vFirstname.isVerified();
		status += vFirstname.getStatus();

		// 2. validate Lastname
		vLastname = iv.validateString(this.getLastname(), "Lastname");
		verified &= vLastname.isVerified();
		status += vLastname.getStatus();

		// 3. validate Email
		vEmail = iv.validateEmail(this.getEmail(), "Email");
		verified &= vEmail.isVerified();
		status += vEmail.getStatus();

		// 4. validate Dob
		vDob = iv.validateDate(this.getDob(), "Dob");
		verified &= vDob.isVerified();
		status += vDob.getStatus();

		// 5. validate Height
		vHeight = iv.validateIntGeneral(this.getHeight(), "Height");
		verified &= vHeight.isVerified();
		status += vHeight.getStatus();

		// 6. validate Weight
		vWeight = iv.validateIntGeneral(this.getWeight(), "Weight");
		verified &= vWeight.isVerified();
		status += vWeight.getStatus();

		// 7. validate Password
		vPassword = iv.validateString(this.getPassword(), "Password");
		verified &= vPassword.isVerified();
		status += vPassword.getStatus();

		vResult.setVerified(verified);
		vResult.setStatus(status);

		return vResult;

	}

}
