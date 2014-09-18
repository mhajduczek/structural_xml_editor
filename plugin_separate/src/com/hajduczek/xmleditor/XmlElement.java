package com.hajduczek.xmleditor;

import java.io.Serializable;
import java.util.List;

import com.hajduczek.xmleditor.utils.Schema;

public abstract class XmlElement implements Serializable {
	
	private static final long serialVersionUID = 8880093091295947168L;

	protected XmlElement parent = null;
	protected String name = null;
	
	public String getPath() {
		if (parent == null) {
			return "";
		} else {
			return parent.getPath() + "/" + name;
		}
	}
	
	public abstract Schema getSchema();
	public abstract List<Attribute> getAttributes();
}
