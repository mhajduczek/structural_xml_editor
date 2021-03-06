package com.hajduczek.xmleditor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hajduczek.xmleditor.utils.Schema;
import com.hajduczek.xmleditor.utils.SchemaType;


public class XmlDocument extends XmlElement {

	private static final long serialVersionUID = -1715541008435953204L;
	private List<XmlTag> childs = new ArrayList<XmlTag>();
	private Map<Integer, XmlTag> childsMap = new HashMap<Integer, XmlTag>();
	private List<Attribute> attributes = new ArrayList<Attribute>();
	private Map<Integer, Integer> edges = new HashMap<Integer, Integer>();
	private XmlTag rootElement = null;
	private String encoding = null;
	private String version = null;
	private Schema schema = null;
	
	public void setSchemaType(SchemaType schemaType) {
		for(XmlTag tag : childs) {
			tag.setSchemaType(schemaType);
		}
	}
	
	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	public List<XmlTag> getChilds() {
		return childs;
	}

	public void setChilds(List<XmlTag> childs) {
		this.childs = childs;
	}
	
	public Map<Integer, XmlTag> getChildsMap() {
		return childsMap;
	}

	public void setChildsMap(Map<Integer, XmlTag> childsMap) {
		this.childsMap = childsMap;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("<?xml "); 
		if (this.version != null) {
			result.append("version=\"" + this.version + "\" ");
		}
		if (this.encoding != null) {
			result.append("encoding=\"" + this.encoding + "\" "); 
		}
		result.append("?>\n");
		if (this.rootElement != null && this.childs != null && this.childs.size() > 0) {
			result.append(this.rootElement.toString(0));
		}
		return result.toString();
	}
	
	public String toDOTString() {
		String dot = "digraph G { ";
		dot += rootElement.toDOTString();
		dot += "}";
		return dot;
	}

	public Map<Integer, Integer> getEdges() {
		return edges;
	}

	public void setEdges(Map<Integer, Integer> edges) {
		this.edges = edges;
	}

	public XmlTag getRootElement() {
		return rootElement;
	}

	public void setRootElement(XmlTag rootElement) {
		this.rootElement = rootElement;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public Schema getSchema() {
		return schema;
	}
	
	public void setSchema(Schema schema) {
		this.schema = schema;
	}
}
