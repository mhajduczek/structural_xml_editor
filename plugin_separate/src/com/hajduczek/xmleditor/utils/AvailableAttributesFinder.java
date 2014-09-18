package com.hajduczek.xmleditor.utils;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class AvailableAttributesFinder {

	private class MyElement {

		private String name = "";
		private List<MyElement> elements = new ArrayList<MyElement>();
		private Map<String, Boolean> attributes = new HashMap<String, Boolean>();

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public List<MyElement> getElements() {
			return elements;
		}

		/**
		 * 
		 * @return Map contains attribute name and a flag indicating whether the attribute is a reference.
		 */
		public Map<String, Boolean> getAttributes() {
			return attributes;
		}

		@Override
		public String toString() {
			return "MyElement {name=" + name + ", elements=" + elements + ", attributes=" + attributes + "}";
		}
	}

	private MyElement rootElement;

	public AvailableAttributesFinder(String schemaXml) {

		try {

			InputStream is = new ByteArrayInputStream(schemaXml.getBytes("UTF-8"));
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(is);

			doc.getDocumentElement().normalize();

			Element root = doc.getDocumentElement();

			rootElement = new MyElement();
			rootElement.setName(root.getNodeName());

			NodeList nList = root.getChildNodes();

			createElement(nList, rootElement);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Map<String, Boolean> getAvailableAttributesForNode(String path) {
		Map<String, Boolean> result = null;

		if (path != null && path.contains("/")) {
			if (path.startsWith("/")) {
				path = path.substring(1, path.length());
			}
			String[] parts = path.split("/");
			Queue<String> pathQueue = new LinkedList<String>();
			for (String s : parts) {
				pathQueue.add(s);
			}
			result = this.getAvailableAttributesForNode(pathQueue, this.rootElement);
		}

		return result;
	}

	private Map<String, Boolean> getAvailableAttributesForNode(Queue<String> path, MyElement rootElement) {
		String head = path.peek();
		if (path.size() > 0) {
			for (int i = 0; i < rootElement.getElements().size(); i++) {
				if (head.equals(rootElement.getElements().get(i).getName())) {
					path.poll();
					if (head.equals(rootElement.getElements().get(i).getName()) && path.size() == 0) {
						return rootElement.getElements().get(i).getAttributes();
					} else {
						Map<String, Boolean> result = getAvailableAttributesForNode(path, rootElement.getElements().get(i));
						if (result != null) {
							return result;
						}
					}
				}
			}
		} else {
			if (head.equals(rootElement.getName())) {
				return rootElement.getAttributes();
			}
		}
		return null;
	}

	private void createElement(NodeList nodeList, MyElement parent) {
		if (nodeList != null && nodeList.getLength() > 0) {
			for (int i = 0; i < nodeList.getLength(); ++i) {
				Node node = nodeList.item(i);
				// this list comes from reference
				if ((node.getNodeName().contains("all") || node.getNodeName().contains("annotation")
						|| node.getNodeName().contains("any") || node.getNodeName().contains("anyAttribute")
						|| node.getNodeName().contains("appinfo") || node.getNodeName().contains("attributeGroup")
						|| node.getNodeName().contains("choice") || node.getNodeName().contains("complexContent")
						|| node.getNodeName().contains("complexType") || node.getNodeName().contains("documentation")
						|| node.getNodeName().contains("element") || node.getNodeName().contains("extension")
						|| node.getNodeName().contains("field") || node.getNodeName().contains("group")
						|| node.getNodeName().contains("import") || node.getNodeName().contains("include")
						|| node.getNodeName().contains("key") || node.getNodeName().contains("keyref")
						|| node.getNodeName().contains("list") || node.getNodeName().contains("notation")
						|| node.getNodeName().contains("redefine") || node.getNodeName().contains("restriction")
						|| node.getNodeName().contains("schema") || node.getNodeName().contains("selector")
						|| node.getNodeName().contains("sequence") || node.getNodeName().contains("simpleContent")
						|| node.getNodeName().contains("simpleType") || node.getNodeName().contains("union") || node
						.getNodeName().contains("unique"))
						&& node.getAttributes().getNamedItem("name") != null
						&& node.getAttributes().getNamedItem("name").getNodeValue().length() > 0) {
					MyElement element = new MyElement();
					element.setName(node.getAttributes().getNamedItem("name").getNodeValue());
					parent.getElements().add(element);
					createElement(node.getChildNodes(), element);
				} else if (node.getNodeName().contains("attribute")) {
					String type = node.getAttributes().getNamedItem("type").getNodeValue();
					if (type != null && (type.contains("IDREF") || type.contains("idref"))) {
						parent.getAttributes().put(node.getAttributes().getNamedItem("name").getNodeValue(), true);
					} else {
						parent.getAttributes().put(node.getAttributes().getNamedItem("name").getNodeValue(), false);
					}
				} else {
					createElement(node.getChildNodes(), parent);
				}
			}
		}
	}

	/*
	 * Main test method.
	 */
	public static void main(String[] args) throws IOException {
		StringBuilder xsd = new StringBuilder();
		
		BufferedReader br = new BufferedReader(new FileReader("customer_orders.xsd"));
		String line = null;
		while ((line = br.readLine()) != null) {
			xsd.append(line);
		}
		br.close();
		AvailableAttributesFinder availableAttributesFinder = new AvailableAttributesFinder(xsd.toString());
	
		System.out.println("TEST: "
				+ availableAttributesFinder.getAvailableAttributesForNode("USAddress/street/street"));
	}
}
