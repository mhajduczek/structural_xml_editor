package com.hajduczek.xmleditor.test;

import com.hajduczek.xmleditor.XMLParser;

public class XMLParserTest {

	public static void main(String[] args) {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"+
				"<!DOCTYPE note SYSTEM \"Note.dtd\">"+
				"<note><to>Tove</to><from>Jani</from><heading>Reminder</heading><body>Don't forget me this weekend!</body></note>";
	//"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"+
				//"<outer><tag><inner attr=\"val\">tekst</inner><inner2 imie=\"Jan\" nazwisko=\"Kowalski\"/></tag><tag2 name=\"Lisa\">Maria</tag2></outer>";

		try {
			if (xml != null) {
				XMLParser.parse(xml, null, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
