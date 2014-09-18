package com.hajduczek.xmleditor;


import java.io.Serializable;

public class Attribute implements Serializable {

	private static final long serialVersionUID = 735157890600336091L;

	private String name;
	private String value;
	private boolean editable;

	public Attribute(String name, String value, boolean editable) {
		this.name = name;
		this.value = value;
		this.editable = editable;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (editable) {
			this.name = name;
		}
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		if (editable) {
			this.value = value;
		}
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	@Override
	public String toString() {
		return "Attribute [name=" + name + ", value=" + value + "]";
	}

}
