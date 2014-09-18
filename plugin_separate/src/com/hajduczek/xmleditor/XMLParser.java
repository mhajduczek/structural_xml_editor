package com.hajduczek.xmleditor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.hajduczek.xmleditor.utils.Schema;
import com.hajduczek.xmleditor.utils.SchemaType;

public class XMLParser {

	public static XmlDocument parse(String xml, String[] schemaDirs, Schema schema) throws Exception {
		// String xml =
		// "<xml><tag><inner attr=\"val\">tekst</inner><inner2 imie=\"Jan\" nazwisko=\"Kowalski\"/></tag><tag2 name=\"Lisa\">Maria</tag2></xml>";
		// generateDOTGraph(xml);
		
		long startTime = System.currentTimeMillis();
		Document doc = null;
		{
			InputStream is = null;
			try {
				DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = fact.newDocumentBuilder();
				// ustawienie zewnetrznego folderu z ktorego ma byc pobierane DTD
				// TODO: w preferencjach projektu ma byc istawiane
				// workingDirectoryURI!!!
	
				builder.setEntityResolver(new MyEntityResolver(schemaDirs));
	
				is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
	
				doc = builder.parse(is);
			} finally {
				is.close();
			}
			
		}
		Map<String, ArrayList<Integer>> references = new HashMap<String, ArrayList<Integer>>();

		XmlDocument xmlDocument = new XmlDocument();

		XmlTag tag = DFS(doc, doc.getDocumentElement(), xmlDocument, 0, schema, xmlDocument);
		xmlDocument.setChilds(tag.getChilds());
		xmlDocument.setRootElement(tag);
		xmlDocument.setEncoding(doc.getXmlEncoding());
		xmlDocument.setVersion(doc.getXmlVersion());
		xmlDocument.setSchema(schema);

		String svg = GraphVizCaller.callGraphViz(xmlDocument.toDOTString());

		SVGUtil.parseSVG(svg, xmlDocument);

		// find edges START

		if (schema == null || schema.getType() == null || schema.getType() == SchemaType.NO_SCHEMA) {
			for (XmlTag xmlTag : xmlDocument.getChildsMap().values()) {
				for (String s : xmlTag.getReferences()) {
					if (references.containsKey(s)) {
						references.get(s).add(xmlTag.getId());
					} else {
						references.put(s, new ArrayList<Integer>());
						references.get(s).add(xmlTag.getId());
					}
				}
			}
			Map<Integer, Integer> edges = new HashMap<Integer, Integer>();

			for (ArrayList<Integer> refs : references.values()) {
				edges.put(refs.get(0), refs.get(1));
			}
			
			xmlDocument.setEdges(edges);
		} else if (schema.getType() == SchemaType.XSD_SCHEMA || schema.getType() == SchemaType.DTD_SCHEMA) {

			Map<Integer, Integer> edges = new HashMap<Integer, Integer>();

			for (XmlTag xmlTag : xmlDocument.getChildsMap().values()) {
				for (String s : xmlTag.getRefs().values()) {
					for (XmlTag innerXmlTag : xmlDocument.getChildsMap().values()) {// WYSOCE
																					// NIE
																					// OPTYMALNE!
						if (innerXmlTag != null && innerXmlTag.getAttributes() != null) {
							for (Attribute attr : innerXmlTag.getAttributes()) {
								if ("ID".equalsIgnoreCase(attr.getName()) && s.equals(attr.getValue())) {
									edges.put(xmlTag.getId(), innerXmlTag.getId());
									break;
								}
							}
						}
					}
				}
			}
			xmlDocument.setEdges(edges);

			for (XmlTag xmlTag : xmlDocument.getChildsMap().values()) {
				Map<String, String> refs = xmlTag.getRefs();
				if (xmlTag.getAttributes() != null) {
					Iterator<Attribute> it = xmlTag.getAttributes().iterator();
					while (it.hasNext()) {
						Attribute attr = it.next();
						if (refs.containsKey(attr.getName())) {
							it.remove();
						}
					}
				}
			}
		}

		// find edges END

		System.out.println("XMLParser.parse duration: " + ((System.currentTimeMillis() - startTime)) + "ms");
		return xmlDocument;
	}

	public static XmlTag DFS(Document document, Node node, XmlDocument xmlDocument, int positionInParent,
			Schema schema, XmlElement parent) {
		Element element = (Element) node;
		XmlTag tag = new XmlTag(positionInParent, node.getNodeName());
		xmlDocument.getChildsMap().put(tag.getId(), tag);
		// workaround by svgparser się nie wysypał
		xmlDocument.getChildsMap().put(tag.getChilds().get(0).getId(), tag.getChilds().get(0));
		tag.setSchemaType(schema.getType());
		tag.setParent(parent);

		for (int i = 0; i < node.getChildNodes().getLength(); ++i) {
			int nodeType = node.getChildNodes().item(i).getNodeType();
			if (nodeType == Node.ELEMENT_NODE) {
				tag.getChilds().add(DFS(document, node.getChildNodes().item(i), xmlDocument, i, schema, tag));
			} else if (nodeType == Node.TEXT_NODE) {
				String textContent = node.getChildNodes().item(i).getTextContent();
				if (textContent == null) {
					continue;
				}
				String trimmedTextContent = textContent.trim();
				if (textContent.length() > 0 && trimmedTextContent.length() > 0) {
					String firstCharOrg = textContent.substring(0, 1);
					String firstCharTrim = trimmedTextContent.substring(0, 1);
					if (firstCharOrg.equals(firstCharTrim)) {
						TextNode textNode = new TextNode(i, trimmedTextContent);
						tag.addTextElement(textNode);
					}
				}
			}
		}

		buildComponent(tag, element, schema);
		return tag;
	}

	public static void buildComponent(XmlTag tag, Element element, Schema schema) {
		NamedNodeMap namedNodeMap = element.getAttributes();

		for (int i = 0; i < namedNodeMap.getLength(); ++i) {
			if ((schema == null || schema.getType() == SchemaType.NO_SCHEMA)
					&& "ref".equals(namedNodeMap.item(i).getNodeName())) {
				String refs = namedNodeMap.item(i).getNodeValue();
				String[] splitedRefs = refs.split(",");
				for (String s : splitedRefs) {
					tag.getReferences().add(s);
				}
			} else {
				String nodeName = namedNodeMap.item(i).getNodeName();
				String nodeValue = namedNodeMap.item(i).getNodeValue();
				if (schema != null
						&& (schema.getType() == SchemaType.XSD_SCHEMA || schema.getType() == SchemaType.DTD_SCHEMA)
						&& isRef(nodeName, schema)) {
					tag.getRefs().put(nodeName, nodeValue);
					tag.getAttributes().add(new Attribute(nodeName, nodeValue, false));
				} else {
					tag.getAttributes().add(new Attribute(nodeName, nodeValue, true));
				}
			}
		}
	}

	private static boolean isRef(String nodeName, Schema schema) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			builder = factory.newDocumentBuilder();
			InputStream is = new ByteArrayInputStream(schema.getContent().getBytes("UTF-8"));
			Document doc = builder.parse(is);

			NodeList nodeList = doc.getElementsByTagName("xs:attribute");

			boolean firstCondition = false;
			boolean secondCondition = false;

			for (int i = 0; i < nodeList.getLength(); ++i) {
				Node node = nodeList.item(i);
				NamedNodeMap namedNodeMap = node.getAttributes();
				for (int j = 0; j < namedNodeMap.getLength(); ++j) {
					if ("name".equals(namedNodeMap.item(j).getNodeName())
							&& nodeName.equals(namedNodeMap.item(j).getNodeValue())) {
						firstCondition = true;
					}
					if ("type".equals(namedNodeMap.item(j).getNodeName())
							&& ("xs:idref".equalsIgnoreCase(namedNodeMap.item(j).getNodeValue()) || "xs:idrefs"
									.equalsIgnoreCase(namedNodeMap.item(j).getNodeValue()))) {
						secondCondition = true;
					}
				}
				if (firstCondition && secondCondition) {
					return true;
				} else {
					firstCondition = false;
					secondCondition = false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}