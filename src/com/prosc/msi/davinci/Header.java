package com.prosc.msi.davinci;

/**
 * Created by IntelliJ IDEA. User: val Date: 12/12/12 Time: 12:22 PM
 */
public class Header {
	private String name;
	private String type;
	private String department;
	private String region;

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getName() {
		return name;
	}

	public String getDepartment() {
		return department;
	}

	public String getRegion() {
		return region;
	}
}

