package com.hajduczek.xmleditor;

import java.io.Serializable;



public class TextNode implements Serializable {

	private static final long serialVersionUID = -6257809342629368972L;
	
	private int positionInParent;
	private String value;
	
	public TextNode(int positionInParent, String value) {
		this.positionInParent = positionInParent;
		this.value = value;
	}

	public int getPositionInParent() {
		return positionInParent;
	}

	public void setPositionInParent(int positionInParent) {
		this.positionInParent = positionInParent;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return value;
	}
}
