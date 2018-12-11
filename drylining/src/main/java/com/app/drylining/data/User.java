package com.app.drylining.data;

public class User {
	private int id;
	private String name;
	private String pan;
	private String gender;
	private String dob;
	private String organization;
	private String email;
	private String mobileNo;
	private String password;
	private String referralCode;
	private String referralBy;

	public User() {
		id = -1;
		name = AppConstant.NULL_STRING;
		pan = AppConstant.NULL_STRING;
		gender = AppConstant.NULL_STRING;
		dob = AppConstant.NULL_STRING;
		organization = AppConstant.NULL_STRING;
		email = AppConstant.NULL_STRING;
		mobileNo = AppConstant.NULL_STRING;
		password = AppConstant.NULL_STRING;
		referralBy = AppConstant.NULL_STRING;
	}

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

	public String getPan() {
		return pan;
	}

	public void setPan(String pan) {
		this.pan = pan;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getReferralCode() {
		return referralCode;
	}

	public void setReferralCode(String referralCode) {
		this.referralCode = referralCode;
	}

	public String getReferralBy() {
		return referralBy;
	}

	public void setReferralBy(String referralBy) {
		this.referralBy = referralBy;
	}

	public void setDefault() {
		id = -1;
		name = AppConstant.NULL_STRING;
		pan = AppConstant.NULL_STRING;
		gender = AppConstant.NULL_STRING;
		dob = AppConstant.NULL_STRING;
		organization = AppConstant.NULL_STRING;
		email = AppConstant.NULL_STRING;
		mobileNo = AppConstant.NULL_STRING;
		password = AppConstant.NULL_STRING;

	}

}
