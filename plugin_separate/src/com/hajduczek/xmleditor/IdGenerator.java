package com.hajduczek.xmleditor;

public class IdGenerator {
	private int id;
	
	private static IdGenerator INSTANCE;
	
	private IdGenerator() {}
	
	public static synchronized IdGenerator getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new IdGenerator();
		}
		return INSTANCE;
	}
	
	public int getNextId() {
		return ++this.id;
	}
	
	public void resetCounter() {
		this.id = 0;
	}
}
