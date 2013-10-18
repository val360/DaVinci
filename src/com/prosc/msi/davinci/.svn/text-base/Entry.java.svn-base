package com.prosc.msi.davinci;

import java.io.Serializable;
import java.text.NumberFormat;

/**
 * Created by IntelliJ IDEA. User: val Date: 10/23/12 Time: 4:47 PM
 */
public class Entry implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private Short week;
	private Double value;
	private String department;
	private String region;

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {

		return type;
	}

	private String type;

	public void setRegion(String region) {
		this.region = region;
	}

	public String getRegion() {

		return region;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getDepartment() {

		return department;
	}

	public int getFieldId() {
		return fieldId;
	}

	private int fieldId;

	public void setName(String name) {
		this.name = name;
	}

	public void setWeek(Short week) {
		this.week = week;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public String getName() {

		return name;
	}

	public Short getWeek() {
		return week;
	}

	public Double getValue() {
		return value;
	}

	public String getValueCurrencyFormatted() {
		return NumberFormat.getCurrencyInstance().format(value);
	}

	public void setFieldId(int fieldId) {
		this.fieldId = fieldId;
	}
}
