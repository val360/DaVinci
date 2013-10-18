package com.prosc.msi.davinci;

/**
 * Created by IntelliJ IDEA. User: val Date: 10/11/12 Time: 5:04 PM
 */
public class User {
	private String username;
	private String password;
	private UserRole role;
	private String department;
	private String email;
	private String region;

	public User() {
	}

	public User(String username, String password, UserRole role) {
		this.username = username;
		this.password = password;
		this.role = role;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getDepartment() {

		return department;
	}

	public String getEmail() {
		return email;
	}

	public String getRegion() {
		return region;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public UserRole getRole() {
		return role;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	public void setDepartment(String department) {
		this.department = department;
	}
}
