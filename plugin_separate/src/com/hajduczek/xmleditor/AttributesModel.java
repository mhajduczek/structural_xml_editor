package com.hajduczek.xmleditor;

import java.util.List;

import javax.swing.table.DefaultTableModel;

public class AttributesModel extends DefaultTableModel {

	private static final long serialVersionUID = 8869485356484025275L;

	private String[] columnNames = { "Name", "Value" };

	private List<Attribute> attributes;

	public AttributesModel(XmlElement element) {
		super();
		this.attributes = element.getAttributes();
	}

	public int getRowCount() {
		if (this.attributes == null) {
			return 0;
		}
		return this.attributes.size();
	}

	public int getColumnCount() {
		return this.columnNames.length;
	}

	public String getColumnName(int columnIndex) {
		return this.columnNames[columnIndex];
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		String retValue;
		Attribute attribute = this.attributes.get(rowIndex);

		if (columnIndex == 0) {
			String name = attribute.getName();
			if (name == null) {
				name = "";
			}
			retValue = name;
		} else if (columnIndex == 1) {
			String value = attribute.getValue();
			if (value == null) {
				value = "";
			}
			retValue = value;
		} else {
			retValue = "";
		}
		return retValue;
	}

	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		Attribute attribute = this.attributes.get(rowIndex);
		switch (columnIndex) {
		case 0:
			attribute.setName(("" + value).trim());
			break;
		case 1:
			attribute.setValue(("" + value).trim());
			break;
		}
	}
	
	public List<Attribute> getAttributes() {
		return this.attributes;
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		Attribute attribute = this.attributes.get(row);
		return attribute.isEditable();
	}
}
