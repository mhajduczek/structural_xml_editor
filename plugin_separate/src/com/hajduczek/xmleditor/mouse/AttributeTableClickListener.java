package com.hajduczek.xmleditor.mouse;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

import javax.swing.JTable;

import com.hajduczek.xmleditor.utils.SchemaType;

public class AttributeTableClickListener extends MouseAdapter {
    
	private JTable jTable;
    private boolean isInTable;
    private int selectedRow;
    private SchemaType schemaType;
    private Map<String, Boolean> availableAttributes;
    
    public AttributeTableClickListener(JTable jTable, boolean isInTable, SchemaType schemaType, 
    		final Map<String, Boolean> availableAttributes) {
    	this.jTable = jTable;
    	this.isInTable = isInTable;
    	this.schemaType = schemaType;
    	this.availableAttributes = availableAttributes;
    }
	
    /**
     * Metoda reaguje na zdarzenie wciśnięcia przycisku myszki.
     */
	public void mousePressed(MouseEvent e) { 
		if (isInTable) {
			this.selectedRow = this.jTable.rowAtPoint(e.getPoint());
		}
	}

	/**
	 * Metoda reaguje na zdarzenie zwolnienia przycisku myszki.
	 */
    public void mouseReleased(MouseEvent e){
        if (e.isPopupTrigger()) {
        	showPopupMenu(e);
        }
    }

    /**
     * Metoda wyświetla menu kontekstowe.
     * 
     * @param e
     */
    private void showPopupMenu(MouseEvent e){
    	AttributeTablePopUpMenu menu = new AttributeTablePopUpMenu(isInTable, jTable, selectedRow, schemaType, availableAttributes);
        
    	menu.show(e.getComponent(), e.getX(), e.getY());
    }
}