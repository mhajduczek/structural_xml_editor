package com.hajduczek.xmleditor.views;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.TableColumn;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.hajduczek.xmleditor.AttributesModel;
import com.hajduczek.xmleditor.ListenerStore;
import com.hajduczek.xmleditor.XmlDocument;
import com.hajduczek.xmleditor.XmlTag;
import com.hajduczek.xmleditor.mouse.AttributeTableClickListener;
import com.hajduczek.xmleditor.utils.AvailableAttributesFinder;
import com.hajduczek.xmleditor.utils.Schema;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;

public class ComponentPropertiesView extends ViewPart implements IPropertyChangeListener {

	private Composite plate = null; // moze nie bedzie potrzebne!
	private Frame frame = null;
	private JTabbedPane tabbedPane = null;

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		System.out.println("ktos mnie zawolal: " + event);
		tabbedPane.removeAll();

		if (event.getNewValue() instanceof XmlDocument) {
			final XmlDocument xmlDocument = (XmlDocument) event.getNewValue();

			JPanel generalPanel = new JPanel();

			GridBagLayout gridBagLayout = new GridBagLayout();
			generalPanel.setAlignmentY(Component.TOP_ALIGNMENT);
			generalPanel.setLayout(gridBagLayout);

			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.anchor = GridBagConstraints.LINE_START;
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			c.weighty = 0.3;
			generalPanel.add(new JLabel("Root element name: "), c);

			final JTextField rootElementName = new JTextField(xmlDocument.getRootElement() != null ? xmlDocument
					.getRootElement().getName() : "");
			rootElementName.addFocusListener(new FocusListener() {

				@Override
				public void focusGained(FocusEvent e) {
				}

				@Override
				public void focusLost(FocusEvent e) {
					xmlDocument.getRootElement().setName(rootElementName.getText());
				}
			});

			c.gridx = 1;
			c.gridy = 0;
			c.weightx = 1;
			c.weighty = 0.3;
			generalPanel.add(rootElementName, c);

			c.gridx = 0;
			c.gridy = 1;
			c.weightx = 1;
			c.weighty = 0.3;
			generalPanel.add(new JLabel("Encoding: "), c);

			final JTextField encoding = new JTextField(xmlDocument.getEncoding() != null ? xmlDocument.getEncoding()
					: "");
			encoding.addFocusListener(new FocusListener() {

				@Override
				public void focusGained(FocusEvent e) {
				}

				@Override
				public void focusLost(FocusEvent e) {
					xmlDocument.setEncoding(encoding.getText());
				}
			});

			c.gridx = 1;
			c.gridy = 1;
			c.weightx = 1;
			c.weighty = 0.3;
			generalPanel.add(encoding, c);

			c.gridx = 0;
			c.gridy = 2;
			c.weightx = 1;
			c.weighty = 0.3;
			generalPanel.add(new JLabel("Version: "), c);

			final JTextField version = new JTextField(xmlDocument.getVersion() != null ? xmlDocument.getVersion() : "");
			version.addFocusListener(new FocusListener() {

				@Override
				public void focusGained(FocusEvent e) {
				}

				@Override
				public void focusLost(FocusEvent e) {
					xmlDocument.setVersion(version.getText());
				}
			});

			c.gridx = 1;
			c.gridy = 2;
			c.weightx = 1;
			c.weighty = 0.3;
			generalPanel.add(version, c);

			c.gridx = 0;
			c.gridy = 3;
			c.gridwidth = 1;
			c.weighty = 1;
			generalPanel.add(new JPanel(), c);

			tabbedPane.addTab("General", null, generalPanel, "General options");

		} else if (event.getNewValue() instanceof XmlTag) {
			final XmlTag xmlTag = (XmlTag) event.getNewValue();
			JPanel generalPanel = new JPanel();

			GridBagLayout gridBagLayout = new GridBagLayout();
			generalPanel.setAlignmentY(Component.TOP_ALIGNMENT);
			generalPanel.setLayout(gridBagLayout);

			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.anchor = GridBagConstraints.LINE_START;
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			c.weighty = 0.3;
			generalPanel.add(new JLabel("Tag name: "), c);

			final JTextField tagName = new JTextField(xmlTag.getName());
			tagName.addFocusListener(new FocusListener() {

				@Override
				public void focusGained(FocusEvent e) {
				}

				@Override
				public void focusLost(FocusEvent e) {
					xmlTag.setName(tagName.getText());
				}
			});

			c.gridx = 1;
			c.gridy = 0;
			c.weightx = 1;
			c.weighty = 0.3;
			generalPanel.add(tagName, c);

			c.gridx = 0;
			c.gridy = 1;
			c.gridwidth = 2;
			c.weighty = 1;
			generalPanel.add(new JPanel(), c);

			tabbedPane.addTab("General", null, generalPanel, "General options");

			AttributesModel attributesModel = new AttributesModel(xmlTag);

			JTable attributesTable = new JTable(attributesModel);
			TableColumn columnName = attributesTable.getColumn("Name");
			columnName.setWidth(140);
			TableColumn columnValue = attributesTable.getColumn("Value");
			columnValue.setWidth(140);
			JPanel attrPanel = new JPanel(new BorderLayout());
			attrPanel.add(attributesTable.getTableHeader(), BorderLayout.PAGE_START);
			JScrollPane scrollPane = new JScrollPane(attributesTable);
			attrPanel.add(scrollPane, BorderLayout.CENTER);

			Schema schema = xmlTag.getSchema();
			String path = xmlTag.getPath();

			AvailableAttributesFinder aaf = new AvailableAttributesFinder(schema.getContent());
			Map<String, Boolean> availableAttributes = aaf.getAvailableAttributesForNode(path);

			attributesTable.addMouseListener(new AttributeTableClickListener(attributesTable, true, xmlTag
					.getSchemaType(), availableAttributes));
			scrollPane.addMouseListener(new AttributeTableClickListener(attributesTable, false, xmlTag.getSchemaType(),
					availableAttributes));

			tabbedPane.addTab("Attributes", null, attrPanel, "Attributes of this element");
			String text = ((XmlTag) event.getNewValue()).toString(0);

			JPanel textPanel = new JPanel();
			textPanel.setLayout(new GridLayout(0, 1));
			JTextArea textArea = new JTextArea(text);
			textArea.setEditable(false);
			textPanel.add(textArea);

			tabbedPane.addTab("Text", null, textPanel, "The text in the element");
		} else if (event.getNewValue() instanceof mxCell && ((mxCell) event.getNewValue()).isEdge()) {
			mxCell cell = (mxCell) event.getNewValue();
			mxICell source = cell.getSource();
			mxICell target = cell.getTarget();
			if (source != null && target != null && source.getParent() != null && target.getParent() != null) {
				XmlTag sourceTag = (XmlTag)source.getParent().getValue();
				XmlTag targetTag = (XmlTag)target.getParent().getValue();
				
				System.out.println(sourceTag + " -> " + targetTag);
				
				JPanel generalPanel = new JPanel();

				GridBagLayout gridBagLayout = new GridBagLayout();
				generalPanel.setAlignmentY(Component.TOP_ALIGNMENT);
				generalPanel.setLayout(gridBagLayout);

				GridBagConstraints c = new GridBagConstraints();
				c.fill = GridBagConstraints.HORIZONTAL;
				c.anchor = GridBagConstraints.LINE_START;
				c.gridx = 0;
				c.gridy = 0;
				c.weightx = 1;
				c.weighty = 0.3;
				generalPanel.add(new JLabel("Source tag name: "), c);

				final JTextField sourceTagName = new JTextField(sourceTag.getName() != null ? sourceTag.getName() : "");
				sourceTagName.setEditable(false);

				c.gridx = 1;
				c.gridy = 0;
				c.weightx = 1;
				c.weighty = 0.3;
				generalPanel.add(sourceTagName, c);

				c.gridx = 0;
				c.gridy = 1;
				c.weightx = 1;
				c.weighty = 0.3;
				generalPanel.add(new JLabel("Target tag name: "), c);

				final JTextField targetTagName = new JTextField(targetTag.getName() != null ? targetTag.getName() : "");
				targetTagName.setEditable(false);
				
				c.gridx = 1;
				c.gridy = 1;
				c.weightx = 1;
				c.weighty = 0.3;
				generalPanel.add(targetTagName, c);

				c.gridx = 0;
				c.gridy = 2;
				c.gridwidth = 1;
				c.weighty = 1;
				generalPanel.add(new JPanel(), c);

				tabbedPane.addTab("General", null, generalPanel, "General options");
			}
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		ListenerStore.addListener(this);
		plate = new Composite(parent, SWT.EMBEDDED | SWT.NO_BACKGROUND);
		frame = SWT_AWT.new_Frame(plate);
		frame.setLayout(new java.awt.GridLayout(1, 1));
		// panel = new JPanel();

		tabbedPane = new JTabbedPane();
		tabbedPane.setTabPlacement(JTabbedPane.LEFT);

		frame.add(tabbedPane);
	}

	@Override
	public void setFocus() {
		System.out.println("ktos zwrocil na mnie uwage!");
	}
}
