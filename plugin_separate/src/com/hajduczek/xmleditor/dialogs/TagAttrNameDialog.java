package com.hajduczek.xmleditor.dialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class TagAttrNameDialog extends Dialog {

	private String tagAttrName;
	private Combo combo;
	private List<String> availableRefs;
	
	private void saveInput() {
		this.tagAttrName = combo.getText();
	}

	@Override
	protected void okPressed() {
		this.saveInput();
		super.okPressed();
	}

	public String getTagAttrName() {
		return tagAttrName;
	}
	
	public TagAttrNameDialog(Shell parentShell, List<String> availableRefs) {
		super(parentShell);
		this.availableRefs = availableRefs;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
	    Composite container = new Composite(area, SWT.NONE);
	    container.setLayoutData(new GridData(GridData.FILL_BOTH));
	    GridLayout layout = new GridLayout(2, false);
	    container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    container.setLayout(layout);
	    Label lbttagAttrName = new Label(container, SWT.NONE);
	    lbttagAttrName.setText("Tag attribute name");

	    GridData dataTagAttrName = new GridData();
	    dataTagAttrName.grabExcessHorizontalSpace = true;
	    dataTagAttrName.horizontalAlignment = GridData.FILL;

	    combo = new Combo (container, SWT.READ_ONLY);
		combo.setItems(Arrays.copyOf(this.availableRefs.toArray(), this.availableRefs.size(), String[].class));
		Rectangle clientArea = container.getClientArea();
		combo.setBounds (clientArea.x, clientArea.y, 200, 200);
	    return area;
	}

	public static void main(String[] args) {
		//TEST CODE
		
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("Shell");
		shell.setSize(200, 200);
		shell.open();
		
		List<String> options = new ArrayList<String>();
		options.add("attribute1");
		options.add("attribute2");
		options.add("attribute3");
		
		TagAttrNameDialog dialog = new TagAttrNameDialog(shell, options);
		dialog.create();
		if (dialog.open() == Window.OK) {
			System.out.println(dialog.getTagAttrName());
		}
	}
}
