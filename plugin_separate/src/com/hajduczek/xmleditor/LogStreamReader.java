package com.hajduczek.xmleditor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LogStreamReader implements Runnable {

	private BufferedReader reader;
	private String allText;
	
	public LogStreamReader(InputStream is) {
		this.reader = new BufferedReader(new InputStreamReader(is));
	}

	public void run() {
		try {
			String line = null;
			allText = "";
			while ((line = reader.readLine()) != null) {
				allText += line;
			}
			reader.close();

			//System.out.println(allText);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getAllText() {
		return allText;
	}
}