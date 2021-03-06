package com.hajduczek.xmleditor.views;

import java.awt.Frame;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.hajduczek.xmleditor.GraphData;



public class GraphOutlineView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "xx1.views.SampleView";

	public GraphOutlineView() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		Composite plate = new Composite(parent, SWT.EMBEDDED | SWT.NO_BACKGROUND);
		Frame frame = SWT_AWT.new_Frame(plate);
		frame.setLayout(new java.awt.GridLayout());
		frame.add(GraphData.getInstance().getGraphOutline());
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		System.out.println(new Date() + " setFocus");
	}
}