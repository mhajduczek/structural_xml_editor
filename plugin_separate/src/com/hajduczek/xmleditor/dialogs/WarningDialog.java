package com.hajduczek.xmleditor.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class WarningDialog extends Dialog {

	private String text;
	
	public static void showWarningDialog(Shell parentShell, String text) {
		WarningDialog dialog = new WarningDialog(parentShell);
		dialog.setText(text);
		dialog.create();
		dialog.open();
	}
	
	private WarningDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected void okPressed() {
		super.okPressed();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
	    Composite container = new Composite(area, SWT.NONE);
	    container.setLayoutData(new GridData(GridData.FILL_BOTH));
	    GridLayout layout = new GridLayout(2, false);
	    container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    container.setLayout(layout);
	    Label text = new Label(container, SWT.NONE);
	    text.setText(this.text);

	    GridData dataTagAttrName = new GridData();
	    dataTagAttrName.grabExcessHorizontalSpace = true;
	    dataTagAttrName.horizontalAlignment = GridData.FILL;

	    return area;
	}

	@Override
	protected Point getInitialSize() {
		return new Point(450, 150);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public static void main(String[] args) {
		//TEST CODE
		
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("Shell");
		shell.setSize(200, 200);
		shell.open();
		
		WarningDialog.showWarningDialog(shell, "This is a test message.");
	}
}
