package com.hajduczek.xmleditor.utils;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.hajduczek.xmleditor.MyEntityResolver;

public class DtdParser {

	public static boolean parse(String xmlText, String schemaDir) throws Exception {//TODO: odpowiednie wyjątki
		try {
			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			domFactory.setValidating(true);
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			builder.setEntityResolver(new MyEntityResolver(new String[]{schemaDir}));
			builder.setErrorHandler(new ErrorHandler() {
			    @Override
			    public void error(SAXParseException exception) throws SAXException {
			        throw exception;
			    }
			    @Override
			    public void fatalError(SAXParseException exception) throws SAXException {
			    	throw exception;
			    }
	
			    @Override
			    public void warning(SAXParseException exception) throws SAXException {
			    	throw exception;
			    }
			});
			builder.parse(new InputSource(new ByteArrayInputStream(xmlText.getBytes())));
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
}
