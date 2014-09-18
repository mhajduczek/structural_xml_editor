package com.hajduczek.xmleditor.test;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class MyBrowser {

	public static void main(String[] args) {
		final Display display = Display.getDefault();

		Frame frm = new Frame("MyBrowser");
		Canvas embedded = new Canvas();
		frm.add(embedded, BorderLayout.CENTER);

		frm.pack();

		final Shell composite = SWT_AWT.new_Shell(display, embedded);
		composite.setLayout(new FillLayout(SWT.VERTICAL));
		final Browser browser = installBrowser(composite,
				"http://www.google.com");
		frm.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				e.getWindow().dispose();
				// composite.dispose();
				// display.dispose();
			}
		});

		JTextField addr = new JTextField(80);
		addr.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				display.syncExec(new Runnable() {
					public void run() {
						browser.setUrl(((JTextComponent) e.getSource())
								.getText());
					}
				});
			}
		});
		frm.add(addr, BorderLayout.NORTH);

		frm.setSize(800, 600);
		frm.setVisible(true);
		while (frm.isDisplayable())
			if (!display.readAndDispatch())
				display.sleep();
		// display.dispose();
	}

	public static Browser installBrowser(Composite parent, String homeURL) {
		Browser browser = new Browser(parent, SWT.EMBEDDED);
		browser.setUrl(homeURL);
		return browser;
	}
}
