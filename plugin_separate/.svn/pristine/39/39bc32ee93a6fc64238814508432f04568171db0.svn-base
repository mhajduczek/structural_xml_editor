package com.hajduczek.xmleditor;

import java.util.ArrayList;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

public class ListenerStore {
	private static ArrayList<IPropertyChangeListener> listeners = new ArrayList<IPropertyChangeListener>();
	
	public static void addListener(IPropertyChangeListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}
	
	public static void removeListener(IPropertyChangeListener listener) {
		listeners.remove(listener);
	}
	
	public static void notify(PropertyChangeEvent event) {
		for (IPropertyChangeListener listener : listeners) {
			listener.propertyChange(event);
		}
	}	
}
