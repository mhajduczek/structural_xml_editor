package com.hajduczek.xmleditor;

import java.util.List;

import org.eclipse.jface.window.Window;

import com.hajduczek.xmleditor.dialogs.TagAttrNameDialog;
import com.hajduczek.xmleditor.utils.SchemaType;

public class TagsAttributesSetter implements Runnable {

	private XmlTag source;
	private XmlTag target;
	private SchemaType schemaType;
	
	private String sourceRefAttrName;
	private String targetId;
	private List<String> availableRefs;
	
	private boolean valid = false;
	
	public TagsAttributesSetter(XmlTag source, XmlTag target, SchemaType schemaType, List<String> availableRefs) {
		super();
		this.source = source;
		this.target = target;
		this.schemaType = schemaType;
		this.availableRefs = availableRefs;
	}
	
	@Override
	public void run() {
		TagAttrNameDialog dialog = new TagAttrNameDialog(null, this.availableRefs);
		dialog.create();
		if (dialog.open() == Window.OK) {
			
			this.targetId = "";
			this.sourceRefAttrName = dialog.getTagAttrName();
			
			for (Attribute attr : target.getAttributes()) {
				if ("id".equals(attr.getName())) {
					targetId = attr.getValue();
					break;
				}
			}
			
			if (!"".equals(targetId) && !"".equals(sourceRefAttrName)) {
				this.valid = true;
			}
		}
	}

	public XmlTag getSource() {
		return source;
	}

	public void setSource(XmlTag source) {
		this.source = source;
	}

	public XmlTag getTarget() {
		return target;
	}

	public void setTarget(XmlTag target) {
		this.target = target;
	}

	public SchemaType getSchemaType() {
		return schemaType;
	}

	public void setSchemaType(SchemaType schemaType) {
		this.schemaType = schemaType;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}
	
	public void addRefs() {
		if (this.valid) {
			source.getRefs().put(this.sourceRefAttrName, this.targetId);
		}
	}
}
