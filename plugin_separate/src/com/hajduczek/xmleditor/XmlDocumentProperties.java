package com.hajduczek.xmleditor;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class XmlDocumentProperties extends JPanel {

	private static final long serialVersionUID = -9074981946939907175L;
	private XmlDocument xmlDocument;

	public XmlDocumentProperties(XmlDocument xmlDocument) {
		this.xmlDocument = xmlDocument;

		setBorder(new EmptyBorder(0, 0, 0, 0));
		setPreferredSize(new Dimension(290, 200));
		setMaximumSize(new Dimension(290, Integer.MAX_VALUE));
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		setAlignmentY(Component.TOP_ALIGNMENT);
		setLayout(gridBagLayout);

		JLabel rootElementLabel = new JLabel("Document root element:");
		JLabel rootElementValueLabel = new JLabel(this.xmlDocument.getRootElement() != null ? this.xmlDocument.getRootElement().getName() : "");

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.LINE_START;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.3;
		c.weighty = 0.3;
		add(rootElementLabel, c);

		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 0.7;
		c.weighty = 0.3;
		add(rootElementValueLabel, c);
		
		JLabel encodingLabel = new JLabel("Encoding:");
		JLabel encodingValueLabel = new JLabel(this.xmlDocument.getEncoding() != null ? this.xmlDocument.getEncoding() : "");

		c.fill = GridBagConstraints.HORIZONTAL;
		
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0.3;
		c.weighty = 0.3;
		add(encodingLabel, c);

		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 0.7;
		c.weighty = 0.3;
		add(encodingValueLabel, c);
		
		JLabel versionLabel = new JLabel("Version:");
		JLabel versionValueLabel = new JLabel(this.xmlDocument.getVersion() != null ? this.xmlDocument.getVersion() : "");

		c.fill = GridBagConstraints.HORIZONTAL;
		
		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 0.3;
		c.weighty = 0.3;
		add(versionLabel, c);

		c.gridx = 1;
		c.gridy = 2;
		c.weightx = 0.7;
		c.weighty = 0.3;
		add(versionValueLabel, c);

		add(new JPanel(), c);
	}
}
