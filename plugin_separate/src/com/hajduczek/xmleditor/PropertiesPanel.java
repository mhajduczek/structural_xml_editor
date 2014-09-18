package com.hajduczek.xmleditor;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

public class PropertiesPanel extends JPanel {

	private static final long serialVersionUID = -5560960829695420116L;
	
	private Object element;
	
	public PropertiesPanel(Object element) {
		this.element = element;
		setTag(this.element);
		setPreferredSize(new Dimension(300, 200));
		setMaximumSize(new Dimension(300, Integer.MAX_VALUE));
		setBorder(new EmptyBorder(0,0,0,0));
	}
	
	public void setTag(Object element) {
		setVisible(false);
		
		removeAll();
		JPanel panel = null;
		
		
		if (element instanceof XmlTag) {
			panel = new XmlTagProperties((XmlTag) element);
		} else if (element instanceof XmlDocument) {
			panel = new XmlDocumentProperties((XmlDocument) element);
		} else {
			panel = new JPanel();
		}
		
		if (panel != null) {
			JScrollPane propertiesScrollPanel = new JScrollPane(panel);
			propertiesScrollPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
			propertiesScrollPanel.setAlignmentY(TOP_ALIGNMENT);
			add(propertiesScrollPanel);
		}
		
		setVisible(true);
	}

}
