package com.hajduczek.xmleditor.test;

//import com.sun.org.apache.xml.internal.serialize.OutputFormat;
//im/port com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class PrettyPrintXML {
	public static void main(String[] args) {
		// String unformattedXml =
		// "<?xml version=\"1.0\" encoding=\"utf-8\"?><note><to>Tove</to><from>Jani</from><heading>Reminder</heading><body>Don't forget me this weekend!</body></note>";

		String unformattedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><QueryMessage\n"
				+ "        xmlns=\"http://www.SDMX.org/resources/SDMXML/schemas/v2_0/message\"\n"
				+ "        xmlns:query=\"http://www.SDMX.org/resources/SDMXML/schemas/v2_0/query\">\n"
				+ "    <Query>\n"
				+ "        <query:CategorySchemeWhere>\n"
				+ "   \t\t\t\t\t         <query:AgencyID>\n\n\n\nECB\n\n\n\n</query:AgencyID>\n"
				+ "        </query:CategorySchemeWhere>\n"
				+ "    </Query>\n\n\n\n\n" + "</QueryMessage>";
		int lineWidth = 60;
		int indent = 2;
		System.out.println("\n### Before Format ###\n");
		System.out.println(unformattedXml);
		System.out.println("\n### After Format ###\n");
		System.out.println(formatXML(unformattedXml, lineWidth, indent));
	}

	public static String formatXML(String unformattedXml, int lineWidth,
			int indent) {
		try {
	/*		final org.w3c.dom.Document document = parseXmlString(unformattedXml);
			OutputFormat format = new OutputFormat(document);
			format.setLineWidth(lineWidth);
			format.setIndenting(true);
			format.setIndent(indent);
			Writer out = new StringWriter();
			XMLSerializer serializer = new XMLSerializer(out, format);
			serializer.serialize(document);
			return out.toString();*/ return null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Document parseXmlString(String in) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			db.setErrorHandler(null);
			InputSource is = new InputSource(new StringReader(in));
			return db.parse(is);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
