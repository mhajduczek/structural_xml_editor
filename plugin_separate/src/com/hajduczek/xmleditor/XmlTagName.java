package com.hajduczek.xmleditor;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class XmlTagName extends XmlTag {

	private static final long serialVersionUID = -6791498945832435634L;

	public static final int SPACE_SIZE = 1;
	
	private int id;
	private int x;
	private int y;
	private int width;
	private int height;
	
	private XmlTag parent;
	
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
		//TODO: albo tak: Math.max(v1,Math.max(v2, Math.max(v3,v4))); ??
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
	
	public XmlTagName(XmlTag parent) {
		this.id = IdGenerator.getInstance().getNextId();
		this.parent = parent;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	
	public String info() {
		return "Tag [id=" + id + ", x=" + x + ", y=" + y + ", width=" + width
				+ ", height=" + height + "]";
	}

	public String toDOTString() {
		return " subgraph cluster_" + id + " { }";
	}
	
	@Override
	public String toString() {
		if (this.parent != null && this.parent.getName() != null) {
			String name = this.parent.getName();
			if (name.length() > 10) {
				name = name.substring(0, name.length() < 10 ? name.length() : 10) + "...";
			} else {
				name = name.substring(0, name.length() < 10 ? name.length() : 10);
			}
			return name; 
		}
		return "";
	}
	
	public String toString(int indentation) {
		return "";
	}

	@Override
	public List<Attribute> getAttributes() {
		return null;
	}

	public XmlTag getParent() {
		return parent;
	}

	public void setParent(XmlTag parent) {
		this.parent = parent;
	}
}
