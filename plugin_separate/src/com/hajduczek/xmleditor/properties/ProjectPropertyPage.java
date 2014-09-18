package com.hajduczek.xmleditor.properties;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PropertyPage;

import com.hajduczek.xmleditor.preferences.PreferenceConstants;

public class ProjectPropertyPage extends PropertyPage {

	public static final String SCHEMA_DIR_PROPERTY = "schema.directory";
	public static final String SCHEMA_DIR_LABEL = "Path to schema directory:";
	public static final String DEFAULT_SCHEMA_DIR = "";

	private DirectoryFieldEditor dfe;

	public ProjectPropertyPage() {
		super();
	}

	private void addSection(Composite parent) {
		Composite composite = createDefaultComposite(parent);

		dfe = new DirectoryFieldEditor(PreferenceConstants.P_PATH_TO_SCHEMA_DIR, 
				SCHEMA_DIR_LABEL , composite);
		
		try {
			String owner =
				((IResource) getElement()).getPersistentProperty(
					new QualifiedName("", SCHEMA_DIR_PROPERTY));
			dfe.setStringValue((owner != null) ? owner : DEFAULT_SCHEMA_DIR);
		} catch (CoreException e) {
			dfe.setStringValue(DEFAULT_SCHEMA_DIR);
		}
	}

	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL);
		data.grabExcessHorizontalSpace = true;
		composite.setLayoutData(data);

		addSection(composite);
		return composite;
	}

	private Composite createDefaultComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		GridData data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		composite.setLayoutData(data);

		return composite;
	}

	protected void performDefaults() {
		super.performDefaults();
		dfe.setStringValue(DEFAULT_SCHEMA_DIR);
	}
	
	public boolean performOk() {
		try {
			((IResource) getElement()).setPersistentProperty(
				new QualifiedName("", SCHEMA_DIR_PROPERTY),
				dfe.getStringValue());
		} catch (CoreException e) {
			return false;
		}
		return true;
	}

}