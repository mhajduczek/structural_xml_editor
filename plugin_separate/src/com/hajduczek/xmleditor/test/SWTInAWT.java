package com.hajduczek.xmleditor.test;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class SWTInAWT extends JFrame {

	private static final long serialVersionUID = 7065154310152431233L;
	private final Canvas canvas;

	public SWTInAWT() {

		setTitle("Simple example");
		setSize(800, 600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.canvas = new Canvas();
		this.canvas.setPreferredSize(new Dimension(800, 600));
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(this.canvas, BorderLayout.CENTER);

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setContentPane(panel);
		pack();
	}

	public void addSWTComponent() {
		Thread swtThread = new Thread() {
			@Override
			public void run() {
				try {
					Display display = new Display();
					Shell shell = SWT_AWT.new_Shell(display, canvas);
					shell.setLayout(new FillLayout());

					Label label = new Label(shell, SWT.BORDER);
					label.setText("This is a label:");
					label.setToolTipText("This is the tooltip of this label");

					Text text = new Text(shell, SWT.NONE);
					text.setText("This is the text in the text widget");
					text.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
					text.setForeground(display.getSystemColor(SWT.COLOR_WHITE));

					text.pack();
					label.pack();

					shell.open();
					while (!isInterrupted() && !shell.isDisposed()) {
						if (!display.readAndDispatch()) {
							display.sleep();
						}
					}
					shell.dispose();
					display.dispose();
				} catch (Exception e) {
					e.printStackTrace();
					interrupt();
				}
			}
		};
		swtThread.start();
	}

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SWTInAWT ex = new SWTInAWT();
				ex.setVisible(true);
				ex.addSWTComponent();
			}
		});
	}
}