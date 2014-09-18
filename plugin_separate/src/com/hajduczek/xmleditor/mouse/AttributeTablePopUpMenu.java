package com.hajduczek.xmleditor.mouse;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import com.hajduczek.xmleditor.Attribute;
import com.hajduczek.xmleditor.AttributesModel;
import com.hajduczek.xmleditor.utils.SchemaType;

public class AttributeTablePopUpMenu extends JPopupMenu {
	private static final long serialVersionUID = -6524284223481340575L;

	public AttributeTablePopUpMenu(boolean deleteOptionEnable, final JTable jTable, final int selectedRow,
			SchemaType schemaType, final Map<String, Boolean> availableAttributes) {
		this.addAddItem(jTable, schemaType, availableAttributes);
		this.addDeleteItem(deleteOptionEnable, jTable, selectedRow, availableAttributes);
	}

	/**
	 * Metoda buduje składnik listy menu kontekstowego dodający nowy wiersz do
	 * tabeli atrybutów.
	 * 
	 * @param jTable
	 *            Tabelka attrybutów do której mają być dodawane nowe wiersze.
	 */
	private void addAddItem(final JTable jTable, final SchemaType schemaType,
			final Map<String, Boolean> availableAttributes) {
		JMenu addItem = new JMenu("Add new attribute");
		this.add(addItem);

		if (schemaType == SchemaType.NO_SCHEMA) {
			JMenuItem emptyMenuItem = new JMenuItem("New empty attribute");

			emptyMenuItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (jTable.getModel() instanceof AttributesModel) {
						AttributesModel attributeModel = (AttributesModel) jTable.getModel();
						List<Attribute> attributes = attributeModel.getAttributes();
						attributes.add(new Attribute("enter_name", "enter_value", true));
						attributeModel.fireTableDataChanged();
					}
				}
			});

			addItem.add(emptyMenuItem);
		} else if (schemaType == SchemaType.XSD_SCHEMA || schemaType == SchemaType.DTD_SCHEMA) {
			boolean enable = false;
			if (availableAttributes != null) {
				for (String s : availableAttributes.keySet()) {
					if (!availableAttributes.get(s)) { // wybieram tylko te
														// atrybuty ktore nei sa
														// referencjami!
						JMenuItem menuItem = new JMenuItem(s);
						final String s2 = s; // zadziala??
						menuItem.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e) {
								if (jTable.getModel() instanceof AttributesModel) {
									AttributesModel attributeModel = (AttributesModel) jTable.getModel();
									List<Attribute> attributes = attributeModel.getAttributes();
									attributes.add(new Attribute(s2, "enter_value", true));
									attributeModel.fireTableDataChanged();
								}
							}
						});
						addItem.add(menuItem);
						enable = true;
					}
				}
			}
			addItem.setEnabled(enable);
		}
	}

	/**
	 * Metoda buduje składnik listy menu kontekstowego usuwający wybrany wiersz
	 * do tabeli atrybutów.
	 * 
	 * @param deleteOptionEnable
	 *            Wskaźnik czy kliknięto w tabelkę atrybutów czy poza nią.
	 * @param jTable
	 *            Tabelka z której mają być usuwane atrybuty.
	 * @param selectedRow
	 *            Wybrany wiersz tabeli, który ma zostać usunięty.
	 */
	private void addDeleteItem(boolean deleteOptionEnable, final JTable jTable, final int selectedRow,
			final Map<String, Boolean> availableAttributes) {

		JMenuItem deleteItem = new JMenuItem("Delete attribute");
		deleteItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("WYBRAŁEM USUŃ");
				if (jTable.getModel() instanceof AttributesModel) {
					AttributesModel attributeModel = (AttributesModel) jTable.getModel();
					List<Attribute> attributes = attributeModel.getAttributes();
					Object obj = attributeModel.getValueAt(selectedRow, 0);
					Attribute attrToDel = null;
					if (obj != null && attributes != null) {
						if (availableAttributes.get((String) obj)) { // attribute
																		// is
																		// reference
																		// and
																		// cannot
																		// be
																		// deleted
							JOptionPane.showMessageDialog(null,
									"The selected attribute is a reference and can not be removed.");
							return;
						}

						for (Attribute attr : attributes) {
							if (attr.getName().equals((String) obj)) {
								attrToDel = attr;
								break;
							}
						}
						if (attrToDel != null) {
							attributes.remove(attrToDel);
						}
						attributeModel.fireTableDataChanged();
					}
				}
			}
		});
		deleteItem.setEnabled(deleteOptionEnable);
		this.add(deleteItem);
	}
}