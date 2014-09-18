package com.hajduczek.xmleditor.utils;

import java.io.Serializable;

public class Schema implements Serializable {

	private static final long serialVersionUID = 5546411770783429039L;

	private SchemaType type;
	private String content;
	private String pathToFile;
	
	public Schema(SchemaType type, String content, String pathToFile) {
		this.type = type;
		this.content = content;
		this.pathToFile = pathToFile;
	}
	
	public SchemaType getType() {
		return type;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public void setType(SchemaType type) {
		this.type = type;
	}

	public String getPathToFile() {
		return pathToFile;
	}

	public void setPathToFile(String pathToFile) {
		this.pathToFile = pathToFile;
	}
}
