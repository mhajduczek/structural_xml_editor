package com.hajduczek.xmleditor.utils;

import java.io.File;
import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

public class XsdParser {

	public static boolean parse(File xmlFile, String xsdFileName) throws Exception {
		return parse(new StreamSource(xmlFile), xsdFileName);
	}
	
	public static boolean parse(String xmlText, String xsdFileName) throws Exception {
		return parse(new StreamSource(new StringReader(xmlText)), xsdFileName);
	}
	
	private static boolean parse(Source xmlSource, String xsdFileName) throws Exception {//TODO: odpowiednie wyjątki
		try {
			File schemaFile = new File(xsdFileName);
			SchemaFactory schemaFactory = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
					
			Schema schema = schemaFactory.newSchema(schemaFile);
			Validator validator = schema.newValidator();
			
		  validator.validate(xmlSource);
		  
		  return true;
		} catch (Exception e) {
		  System.out.println("Reason: " + e);
		  throw e;
		}
	}

}
