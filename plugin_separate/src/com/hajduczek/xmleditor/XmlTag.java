package com.hajduczek.xmleditor;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hajduczek.xmleditor.utils.Schema;
import com.hajduczek.xmleditor.utils.SchemaType;

public class XmlTag extends XmlElement {

	private static final long serialVersionUID = -413894683409074365L;
	
	public static final int SPACE_SIZE = 1;
	
	private SchemaType schemaType = SchemaType.NO_SCHEMA;
	
	private int id;
	private int x;
	private int y;
	private int width;
	private int height;
	
	private List<Attribute> attributes = new ArrayList<Attribute>();
	private List<XmlTag> childs = new ArrayList<XmlTag>();
	private List<TextNode> textElements = new ArrayList<TextNode>();
	
	private List<String> references = new ArrayList<String>(); //dla NO_SCHEMA
	
	private Map<String, String> refs = new HashMap<String, String>(); //dla XSD i DTD
	
	private int positionInParent;
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public void vertexPosition(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4) {
		this.x = min(x1, x2, x3, x4);
		this.y = min(y1, y2, y3, y4);
		this.width = countRange(x1, x2, x3, x4);
		this.height = countRange(y1, y2, y3, y4);
	}
	
	private int min(int v1, int v2, int v3, int v4) {
		int min = v1;
		if (v2 < min) min = v2;
		if (v3 < min) min = v3;
		if (v4 < min) min = v4;
		return min;
	}
	
	public int countRange(int v1, int v2, int v3, int v4) {
		List<Integer> valuesList = Arrays.asList(v1, v2, v3, v4);
		Collections.sort(valuesList);
		
		int range = 0;
		int min = valuesList.get(0);
		int max = valuesList.get(3);
		if (min <= 0 && max <= 0) {
			range = Math.abs(min) - Math.abs(max);
		} else if (min >= 0 && max >= 0) {
			range = max - min;
		} else if (min <= 0 && max >= 0) {
			range = max + Math.abs(min);
		}
		return range;
	}
	
	public void addOffset(int x, int y) {
		this.x = this.x + x;
		this.y = this.y + y;
	}
	
	public int getArea() {
		return this.x * this.y;
	}
	
	protected XmlTag() {}
	
	public XmlTag(int positionInParent, String name) {
		this.id = IdGenerator.getInstance().getNextId();
		this.positionInParent = positionInParent;
		this.name = name;
		if (name != null) {
			this.childs.add(new XmlTagName(this));
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<TextNode> getTextElements() {
		return this.textElements;
	}

	public void addTextElement(TextNode textElement) {
		this.textElements.add(textElement);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	
	public String info() {
		return "Tag [id=" + id + ", x=" + x + ", y=" + y + ", width=" + width
				+ ", height=" + height + ", attributes=" + attributes
				+ ", childs=" + childs + ", name=" + name + ", textElements="
				+ textElements + "]";
	}

	public String toDOTString() {
		String dot = " subgraph cluster_" + id + " { ";
		for (XmlTag tag : childs) {
			dot += tag.toDOTString();
		}
		dot += " } ";
		return dot;
	}
	
	@Override
	public String toString() {
		return "";//this.name;
	}
	
	public String toString(int indentation) {
		StringBuilder result = new StringBuilder();
		
		String space = "";
		if (indentation > 0) {
			space = new String(new char[indentation]).replace('\0', ' ');
		}
		
		if ((this.childs == null || this.childs.size() == 0) && (this.textElements == null || this.textElements.size() == 0)) {
			result.append(space + "<"+this.name+" ");
			if (this.attributes != null && this.attributes.size() > 0) {
				for (Attribute attr : this.attributes) {
					result.append(attr.getName() + "=\"" + attr.getValue() + "\" ");
				}
			}
			if (this.schemaType == SchemaType.NO_SCHEMA && this.references != null && this.references.size() > 0) {
				String refs = "";
				for (String s : this.references) {
					refs += s + ",";
				}
				refs = refs.substring(0, refs.length()-1);
				result.append("ref=\"" + refs + "\" ");
			} else if ((this.schemaType == SchemaType.DTD_SCHEMA || this.schemaType == SchemaType.XSD_SCHEMA)
					&& this.refs != null && this.refs.size() > 0) {
				//TODO: wyswietlanie xml'a w przypadku gdy schema jest typu DTD lub XSD
				//String refs = "";
				if (this.refs.size() == 1) {
					String key = (String) this.refs.keySet().toArray()[0];
					String value = this.refs.get(key);
					result.append(key + "=\"" + value + "\" ");
				} else {
					String[] keys = (String[]) this.refs.keySet().toArray();
					for (String key : keys) {
						String value = this.refs.get(key);
						result.append(key + "=" + value + " ");
					}
				}
			} 
			result.append("/>\n");
		} else {
			result.append(space + "<"+this.name+" ");
			if (this.attributes != null && this.attributes.size() > 0) {
				for (Attribute attr : this.attributes) {
					result.append(attr.getName() + "=\"" + attr.getValue() + "\" ");
				}
			}
			if (this.schemaType == SchemaType.NO_SCHEMA && this.references != null && this.references.size() > 0) {
				String refs = "";
				for (String s : this.references) {
					refs += s + ",";
				}
				refs = refs.substring(0, refs.length()-1);
				result.append("ref=\"" + refs + "\" ");
			} else if ((this.schemaType == SchemaType.DTD_SCHEMA || this.schemaType == SchemaType.XSD_SCHEMA)
					&& this.refs != null && this.refs.size() > 0) {
				//TODO: wyswietlanie xml'a w przypadku gdy schema jest typu DTD lub XSD
				//String refs = "";
				if (this.refs.size() == 1) {
					String key = (String) this.refs.keySet().toArray()[0];
					String value = this.refs.get(key);
					result.append(key + "=\"" + value + "\" ");
				} else {
					String[] keys = (String[]) this.refs.keySet().toArray();
					for (String key : keys) {
						String value = this.refs.get(key);
						result.append(key + "=" + value + " ");
					}
				}
			} 
			if (result.charAt(result.length()-1) == ' ') {
				result.deleteCharAt(result.length()-1);
			}
			result.append(">\n");
			//START
			Map<Integer, Object> innerElements = getInnerElementsMap();
			
			
			Integer[] keys = innerElements.keySet().toArray(new Integer[innerElements.size()]);
			Arrays.sort(keys);
			
			boolean firstIterate = true;
			for (Integer key : keys) {
				Object innerElement = innerElements.get(key);
				if (innerElement instanceof String) {
					if (firstIterate) {
						result.deleteCharAt(result.length()-1);
					}
					result.append(innerElement+"\n");
				} else if (innerElement instanceof XmlTag) {
					result.append(((XmlTag)innerElement).toString(indentation + SPACE_SIZE));
				}
				firstIterate = false;
			}
			//END
			result.append(space + "</" + this.name + ">\n");
		}
		
		return result.toString();
	}
	
	private Map<Integer, Object> getInnerElementsMap() {
		Map<Integer, Object> resultMap = new HashMap<Integer, Object>();
		
		for (TextNode tn : this.textElements) {
			resultMap.put(tn.getPositionInParent(), tn.getValue());
		}
		int offset = resultMap.size();
		for (XmlTag xt : this.childs) {
			resultMap.put(xt.getPositionInParent() + offset, xt);
		}
		
		return resultMap;
	}

	public int getPositionInParent() {
		return positionInParent;
	}

	public void setPositionInParent(int positionInParent) {
		this.positionInParent = positionInParent;
	}

	public List<String> getReferences() {
		return references;
	}

	public void setReferences(List<String> references) {
		this.references = references;
	}

	public SchemaType getSchemaType() {
		return schemaType;
	}

	public void setSchemaType(SchemaType schemaType) {
		this.schemaType = schemaType;
	}

	public Map<String, String> getRefs() {
		return refs;
	}

	public void setRefs(Map<String, String> refs) {
		this.refs = refs;
	}

	public XmlElement getParent() {
		return parent;
	}

	public void setParent(XmlElement parent) {
		this.parent = parent;
	}

	@Override
	public Schema getSchema() {
		if (parent != null) {
			return parent.getSchema();
		} else {
			return null;
		}
	}
}
