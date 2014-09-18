package com.hajduczek.xmleditor.test;

import java.awt.Frame;
import java.awt.GridLayout;
import java.io.IOException;

import javax.swing.JComboBox;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class AWTInSWT {
	public static void main(String[] args) throws IOException {
		Display display = Display.getDefault();
	    final Shell shell = new Shell(display);
	    shell.setLayout(new FillLayout());
	    shell.setText("Test window");
	    
	    Composite composite = new Composite(shell, SWT.EMBEDDED | SWT.NO_BACKGROUND);
	    Frame frame = SWT_AWT.new_Frame(composite);
	    frame.setLayout(new GridLayout());
	    JComboBox<String> jComboBox = new JComboBox<String>(new String[]{"Position1","Position2"});
	    frame.add(jComboBox);
	    shell.pack();
	    shell.open();

	    while (!shell.isDisposed()) {
	      if (!display.readAndDispatch())
	        display.sleep();
	    }
	    display.dispose();
	}
}