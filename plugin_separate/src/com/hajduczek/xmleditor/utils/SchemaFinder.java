package com.hajduczek.xmleditor.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class SchemaFinder {
	public static Schema findSchema(String xml, String schemaDirectory) {
		Schema result = new Schema(SchemaType.NO_SCHEMA, "No schema find.", null);
		
		if (xml != null) {
			if (xml.contains("noNamespaceSchemaLocation=")) {
				String[] splitedDoc = xml.split("noNamespaceSchemaLocation=\"");
				splitedDoc = splitedDoc[1].split("\"");
				String schemaFileName = splitedDoc[0];
				result = new Schema(SchemaType.XSD_SCHEMA, getSchemaFileContent(schemaDirectory, schemaFileName), schemaDirectory + File.separator + schemaFileName);
			} else if (xml.contains("schemaLocation=")) {
				String[] splitedDoc = xml.split("schemaLocation=\"");
				String schemaFileName = splitedDoc[0];
				splitedDoc = splitedDoc[1].split("\"");
				result = new Schema(SchemaType.XSD_SCHEMA, getSchemaFileContent(schemaDirectory, schemaFileName), schemaDirectory + File.separator + schemaFileName);
			} else if ((xml.contains("<!DOCTYPE") || xml.contains("<! DOCTYPE"))
					&& xml.contains(".dtd")) {
				String[] splitedDoc = xml.split(" ");
				if (splitedDoc != null) {
					for (String s : splitedDoc) {
						if (s.contains(".dtd")) {
							String schemaFileName = s.replaceAll("\"", "");
							schemaFileName = schemaFileName.split(">")[0];
							result = new Schema(SchemaType.DTD_SCHEMA, getSchemaFileContent(schemaDirectory, schemaFileName), schemaDirectory + File.separator + schemaFileName);
							break;
						}
					}
				}
			}
		}

		return result;
	}
	
	public static boolean checkIfSchemaExists(String xml, String schemaDirectory) {
		if (xml != null) {
			if (xml.contains("noNamespaceSchemaLocation=")) {
				String[] splitedDoc = xml.split("noNamespaceSchemaLocation=\"");
				splitedDoc = splitedDoc[1].split("\"");
				String schemaFileName = splitedDoc[0];
				return (new File(schemaDirectory, schemaFileName).exists());
			} else if ((xml.contains("<!DOCTYPE") || xml.contains("<! DOCTYPE"))
					&& xml.contains(".dtd")) {
				String[] splitedDoc = xml.split(" ");
				if (splitedDoc != null) {
					for (String s : splitedDoc) {
						if (s.contains(".dtd")) {
							String schemaFileName = s.replaceAll("\"", "");
							schemaFileName = schemaFileName.split(">")[0];
							return (new File(schemaDirectory, schemaFileName).exists());
						}
					}
				}
			}
			
		}
		return false;
	}

	private static String getSchemaFileContent(String schemaDirectory, String fileName) {
		StringBuilder schema = new StringBuilder();
		BufferedReader in = null;
		try {
			//"C:\\Users\\hajdi\\workspace_czysty\\plugin_separate\\dtd\\"
			File schemaFile = new File(schemaDirectory, fileName);
			in = new BufferedReader(new FileReader(schemaFile));
			String strLine;
			while ((strLine = in.readLine()) != null) {
				schema.append(strLine+"\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
			schema = new StringBuilder("Specified location does not indicate the schema file.");
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return schema.toString();
	}

	public static void main(String[] args) {
		String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"
				+ "<shiporder orderid=\"889923\" "
				+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance "
				+ "xsi:noNamespaceSchemaLocation=\"shiporder.xsd\">"
				+ "<orderperson>John Smith</orderperson>" + "<shipto>"
				+ "<name>Ola Nordmann</name>" + "<address>Langgt 23</address>"
				+ "<city>4000 Stavanger</city>" + "<country>Norway</country>"
				+ "</shipto>" + "<item>" + "<title>Empire Burlesque</title>"
				+ "<note>Special Edition</note>" + "<quantity>1</quantity>"
				+ "<price>10.90</price>" + "</item>" + "<item>"
				+ "<title>Hide your heart</title>" + "<quantity>1</quantity>"
				+ "<price>9.90</price>" + "</item>" + "</shiporder> ";
		String schemaDirectory = "C:\\Users\\hajdi\\workspace_czysty\\plugin_separate\\dtd\\";
		findSchema(schemaDirectory, xml);

		String xml2 = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
				+ "<!DOCTYPE note SYSTEM \"Note.dtd\">"
				+ "<note><to>Tove</to><from>Jani</from><heading>Reminder</heading><body>Don't forget me this weekend!</body></note>";

		findSchema(schemaDirectory, xml2);
	}
}
