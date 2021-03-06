package com.hajduczek.xmleditor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableColumn;

public class XmlTagProperties extends JPanel {

	private static final long serialVersionUID = 8447536940324807099L;
	
	private XmlTag tag;
	
	public XmlTagProperties(XmlTag tag) {
		this.tag = tag;
		
		setBorder(new EmptyBorder(0, 0, 0, 0));
		setPreferredSize(new Dimension(290, 200));
		setMaximumSize(new Dimension(290, Integer.MAX_VALUE));
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		setAlignmentY(Component.TOP_ALIGNMENT);
		setLayout(gridBagLayout);

		JLabel tagNameLabel = new JLabel("Tag name:");
		JLabel tagNameValueLabel = new JLabel(this.tag.getName());
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.LINE_START;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 0.3;
		add(tagNameLabel, c);

		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 0.3;
		add(tagNameValueLabel, c);

		JLabel textLabel = new JLabel("Text:");
		JTextArea textValueArea = new JTextArea();
		textValueArea.setRows(3);
		textValueArea.setEditable(false);
		textValueArea.setText("TEST"); //TODO: this.tag.getTextValue()); tutaj wypisywac toString wszystkich dzieci!!!
		textValueArea.setLineWrap(true);
		JScrollPane textAreaScrollPanel = new JScrollPane(textValueArea);
		Dimension scrollPaneSize = new Dimension(100,60);
		textAreaScrollPanel.setPreferredSize(scrollPaneSize);
		
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1;
		c.gridwidth = 1;
		add(textLabel, c);
	
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 1;
		c.gridwidth = 2;
		c.gridheight = 3;
		add(textAreaScrollPanel, c);
		
		AttributesModel attributesModel = new AttributesModel(this.tag);

		JTable attributesTable = new JTable(attributesModel);
		TableColumn columnName = attributesTable.getColumn("Name");
		columnName.setWidth(140);
		TableColumn columnValue = attributesTable.getColumn("Value");
		columnValue.setWidth(140);
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(attributesTable.getTableHeader(), BorderLayout.PAGE_START);
		panel.add(attributesTable, BorderLayout.CENTER);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 6;
		c.weightx = 1;
		c.gridwidth = 2;
		add(panel, c);
		
		c.gridx = 0;
		c.gridy = 7;
		c.gridwidth = 2;
		c.weighty = 1;
		add(new JPanel(), c);
	}
}
