package com.hajduczek.xmleditor.editors;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;

import com.hajduczek.xmleditor.Attribute;
import com.hajduczek.xmleditor.GraphData;
import com.hajduczek.xmleditor.ListenerStore;
import com.hajduczek.xmleditor.PropertiesPanel;
import com.hajduczek.xmleditor.TagsAttributesSetter;
import com.hajduczek.xmleditor.XMLParser;
import com.hajduczek.xmleditor.XmlDocument;
import com.hajduczek.xmleditor.XmlTag;
import com.hajduczek.xmleditor.XmlTagName;
import com.hajduczek.xmleditor.properties.ProjectPropertyPage;
import com.hajduczek.xmleditor.utils.AvailableAttributesFinder;
import com.hajduczek.xmleditor.utils.DtdParser;
import com.hajduczek.xmleditor.utils.Schema;
import com.hajduczek.xmleditor.utils.SchemaFinder;
import com.hajduczek.xmleditor.utils.SchemaType;
import com.hajduczek.xmleditor.utils.XsdParser;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.mxGraphOutline;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxEdgeStyle;
import com.mxgraph.view.mxGraph;

public class MultiPageEditor extends MultiPageEditorPart implements IResourceChangeListener, IAdaptable {

	final int PORT_DIAMETER = 14;
	final int PORT_RADIUS = PORT_DIAMETER / 2;
	
	/** The text editor used in page 0. */
	private TextEditor editor;

	Composite page1composite;
	Composite page2composite;

	mxGraphComponent graphComponent;
	PropertiesPanel propertiesPanel;
	XmlDocument xmlDocument;

	Frame frame = null;

	boolean valid = false;

	mxGraphOutline graphOutline = GraphData.getInstance().getGraphOutline();

	Schema schema = null;
	
	boolean graphModified = false;
	
	boolean programmaticallyAddEdge = false;
	
	public Object getAdapter(Class required) {

		return super.getAdapter(required);
	}

	/**
	 * Creates a multi-page editor example.
	 */
	public MultiPageEditor() {
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	/**
	 * Creates page 0 of the multi-page editor, which contains a text editor.
	 */
	void createPage0() {
		try {
			editor = new TextEditor();
			int index = addPage(editor, getEditorInput());
			setPageText(index, "XML");
		} catch (PartInitException e) {
			ErrorDialog.openError(getSite().getShell(), "Error creating nested text editor", null, e.getStatus());
		}
	}

	/**
	 * Creates page 1 of the multi-page editor, which allows you to change the
	 * font used in page 2.
	 */
	void createPage1() {
		page1composite = new Composite(getContainer(), SWT.NONE);
		int index = addPage(page1composite);
		setPageText(index, "Schema");
	}

	private void reloadPage0() {
		if (xmlDocument != null) {
			editor.getDocumentProvider().getDocument(editor.getEditorInput()).set(xmlDocument.toString());
		}
	}

	private void reloadPage1() {
		Control[] childs = page1composite.getChildren();
		if (childs != null) {
			for (Control ctrl : childs) {
				ctrl.dispose();
			}
		}
		FillLayout layout = new FillLayout();
		page1composite.setLayout(layout);
		
		PlatformUI.getWorkbench().getPreferenceManager();

		Text text = new Text(page1composite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		text.setEditable(false);
		text.setSize(page1composite.getSize());
		String xml = editor.getDocumentProvider().getDocument(editor.getEditorInput()).get();
		String schemaDirectory = "";

		String activeProjectName = getActiveProjectName();
		if (activeProjectName != null) {
			IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
			for (IProject prj : projects) {
				try {
					if (prj.getName().equals(activeProjectName)) {
						schemaDirectory = prj.getPersistentProperty(new QualifiedName("",
								ProjectPropertyPage.SCHEMA_DIR_PROPERTY));
						break;
					}
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
	
			Schema schema = SchemaFinder.findSchema(xml, schemaDirectory);
			
			this.xmlDocument.setSchemaType(schema.getType());
			
			text.setText(schema.getContent());
			this.schema = schema;
		}
		
		page1composite.layout();
	}

	private String getActiveProjectName() {
		try {
			IEditorPart editorPart = getSite().getWorkbenchWindow().getActivePage().getActiveEditor();

			if (editorPart != null) {
				IFileEditorInput input = (IFileEditorInput) editorPart.getEditorInput();
				IFile file = input.getFile();
				IProject activeProject = file.getProject();
				String activeProjectName = activeProject.getName();

				return activeProjectName;
			}
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * Creates page 2 of the multi-page editor, which shows the sorted text.
	 */
	void createPage2() {
		page2composite = new Composite(getContainer(), SWT.EMBEDDED | SWT.NO_BACKGROUND);
		int index = addPage(page2composite);
		frame = SWT_AWT.new_Frame(page2composite);
		loadPage2();
		setPageText(index, "Graph");
	}

	void createXmlDocument() {
		String xml = editor.getDocumentProvider().getDocument(editor.getEditorInput()).get();
		// String xml =
		// "<xml><tag><inner attr=\"val\">tekst</inner><inner2 imie=\"Jan\" nazwisko=\"Kowalski\"/></tag><tag2 name=\"Lisa\">Maria</tag2></xml>";

		String[] schemaDirs = null;

		String activeProjectName = getActiveProjectName();

		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();

		if (projects != null) {
			if (activeProjectName != null) {
				for (IProject prj : projects) {
					try {
						if (prj.getName().equals(activeProjectName)) {
							schemaDirs = new String[1];
							schemaDirs[0] = prj.getPersistentProperty(new QualifiedName("",
									ProjectPropertyPage.SCHEMA_DIR_PROPERTY));
							break;
						}
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
			} else {
				schemaDirs = new String[projects.length];
				int i = 0;
				for (IProject prj : projects) {
					try {
						schemaDirs[i++] = prj.getPersistentProperty(new QualifiedName("",
								ProjectPropertyPage.SCHEMA_DIR_PROPERTY));
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
			}
		}

		if (projects != null && activeProjectName != null) { 
			this.validateDocument();
		}
		
		try {
			if (xml != null && this.schema != null) {
				xmlDocument = XMLParser.parse(xml, schemaDirs, this.schema);
				valid = true;
			}
		} catch (Exception e) {
			valid = false;
			e.printStackTrace();
		}
	}

	void loadPage2() {
		long startTime = System.currentTimeMillis();

		createXmlDocument();

		frame.setLayout(new java.awt.GridLayout());
		mxGraph graph = new mxGraph() { 
			public String getToolTipForCell(Object cell) {
				if (model.isVertex(cell)) {
					return ((XmlTag) model.getValue(cell)).getName();
				}
				return super.getToolTipForCell(cell);
			}
			
			public boolean isCellFoldable(Object cell, boolean collapse) {
				return false;
			}
		};

		graphComponent = new mxGraphComponent(graph);
		graphComponent.setDoubleBuffered(true);
		graphComponent.setTripleBuffered(true);

		graphComponent.setToolTips(true);
		
		drawNewGraph(graph);

		graph.addListener(mxEvent.CELL_CONNECTED, new mxIEventListener() {

			@Override
			public void invoke(Object sender, mxEventObject evt) {
				if (programmaticallyAddEdge) {
					return;
				}
				
				System.out.println("AKCJA!!! sender=" + sender + ", evt=" + evt.getProperties());
				if (evt != null) {
					Object terminal = evt.getProperty("terminal");
					System.out.println("terminal=" + terminal);

					com.mxgraph.model.mxICell edge = ((com.mxgraph.model.mxCell) evt.getProperty("edge"));
					com.mxgraph.model.mxICell source = ((com.mxgraph.model.mxCell) evt.getProperty("edge")).getSource();
					com.mxgraph.model.mxICell target = ((com.mxgraph.model.mxCell) evt.getProperty("edge")).getTarget();
					
					if (source != null && target != null) {
						XmlTag sourceXmlTag = (XmlTag) source.getParent().getValue();
						XmlTag targetXmlTag = (XmlTag) target.getParent().getValue();
						System.out.println("source: " + sourceXmlTag.getName());
						System.out.println("target: " + targetXmlTag.getName());
						
						//IF no schema
						if (schema == null || SchemaType.NO_SCHEMA == schema.getType()) {
							sourceXmlTag.getReferences().add(sourceXmlTag.getName() + targetXmlTag.getName());
							targetXmlTag.getReferences().add(sourceXmlTag.getName() + targetXmlTag.getName());
						//IF xml schema & dtd schema
						} else {
							System.out.println("SCHEMA: " + schema);
							Schema schema = sourceXmlTag.getSchema();
							String path = sourceXmlTag.getPath();
							AvailableAttributesFinder aaf = new AvailableAttributesFinder(schema.getContent());
							Map<String, Boolean> availableAttributes = aaf.getAvailableAttributesForNode(path);
							List<String> availableRefs = new ArrayList<String>();
							
							
							if (availableAttributes == null || availableAttributes.size() == 0) {
								edge.removeFromParent();
								return;
							}
							
							Map<String, String> existingRefs = sourceXmlTag.getRefs();
							
							for (String s : availableAttributes.keySet()) {
								if (availableAttributes.get(s)) {
									if (!existingRefs.containsKey(s)) {
										availableRefs.add(s);
									}
								}
							}
							
							if (availableRefs.size() == 0) {
								edge.removeFromParent();
								return;
							}
							
							TagsAttributesSetter tagsAttributesSetter = new TagsAttributesSetter(sourceXmlTag, targetXmlTag, schema.getType(), availableRefs);
							Display.getDefault().syncExec(tagsAttributesSetter);
							if (!tagsAttributesSetter.isValid()) {
								edge.removeFromParent();
							}
							//TODO: walidacja ze schema i jesli wszystko ok dodajemy referencje do tagow!!!
							
							if (tagsAttributesSetter.isValid()) {
								tagsAttributesSetter.addRefs();
							}
							sourceXmlTag.setSchemaType(schema.getType());
						}
					}
				}
			}
		});

		graphOutline.setGraphComponent(graphComponent);
		
		graphComponent.setAutoscrolls(true);
		graphComponent.setAutoScroll(true);

		/* ustawianie listenera dla podgladu komponentu */
		graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent e) {
				final mxCell cell = (mxCell) graphComponent.getCellAt(e.getX(), e.getY());
				if (cell == null) {
					System.out.println("teraz beda wyswietlane informacje o calym xmlu");
					ListenerStore.notify(new PropertyChangeEvent(this, "AllXmlKlik", "old", xmlDocument));
				} else if (cell.isEdge()) {
					System.out.println("Kliknalem w krawedz!");
					ListenerStore.notify(new PropertyChangeEvent(this, "KrawedzKlik", "old", cell));
					System.out.println("e.isPopupTrigger(): " + e.isPopupTrigger());
					
					if (e.getButton() == 3) {
						JPopupMenu jpm = new JPopupMenu();
						JMenuItem menuItem = new JMenuItem("Delete reference");
						menuItem.addActionListener(new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent e) {
								System.out.println("TU BEDE USUWAL REFERENCJE! " );
								mxICell source = cell.getSource();
								mxICell target = cell.getTarget();
								
								if (source != null && target != null && source.getParent() != null && target.getParent() != null) {
									XmlTag sourceTag = (XmlTag)source.getParent().getValue();
									XmlTag targetTag = (XmlTag)target.getParent().getValue();
									
									Object[] edges = graphComponent.getGraph().getEdgesBetween(source, target);
									
									if (edges != null) {
										for (Object o : edges) {
											graphComponent.getGraph().getModel().remove(o);
										}
									}
									
									if (sourceTag.getSchemaType() == SchemaType.NO_SCHEMA) { 
										List<String> references = sourceTag.getReferences();
										List<Attribute> attributes = targetTag.getAttributes();
										String id = null;
										if (attributes != null) {
											for (Attribute a : attributes) {
												if ("id".equals(a.getName())) {
													id = a.getValue();
													break;
												}
											}
										}
										if (id != null) {
											references.remove(id);
										}
									} else {
										Map<String, String> refs = sourceTag.getRefs();
										List<Attribute> attributes = targetTag.getAttributes();
										String id = null;
										if (attributes != null) {
											for (Attribute a : attributes) {
												if ("id".equals(a.getName())) {
													id = a.getValue();
													break;
												}
											}
										}
										if (id != null && refs != null && refs.containsValue(id)) {
											String key = null;
											for (Entry<String, String> entry : refs.entrySet()) {
												if (entry.getValue().equals(id)) {
													key = entry.getKey();
													break;
												}
											}
											if (key != null) {
												refs.remove(key);
											}
										}
									}
								}
							}
						});
					    jpm.add(menuItem);
						jpm.show(e.getComponent(), e.getX(), e.getY());
					}
				} else if ((cell.getValue() instanceof XmlTagName)) {
					System.out.println("Kliknalem w tagName!");
					ListenerStore.notify(new PropertyChangeEvent(this, "TagNameKrawedzKlik", "old", ((XmlTagName) cell
							.getValue()).getParent()));
				} else if ((cell.getValue() instanceof XmlTag)) {
					System.out.println("Kliknalem w tag!");
					ListenerStore.notify(new PropertyChangeEvent(this, "TagKlik", "old", (XmlTag) cell.getValue()));
				}
			}
		});
		
		frame.add(graphComponent);

		System.out.println("loadPage2 duration: " + ((System.currentTimeMillis() - startTime)) + "ms");
	}

	private void doSave() {
		long startTime = System.currentTimeMillis();
		clearGraph();
		drawNewGraph(this.graphComponent.getGraph());
		this.graphModified = false;
		System.out.println("doSave duration: " + ((System.currentTimeMillis() - startTime)) + "ms");
	}

	private void clearGraph() {
		if (this.graphComponent != null) {
			mxGraph graph = this.graphComponent.getGraph();
			graph.removeCells(graph.getChildVertices(graph.getDefaultParent()));
		}
	}

	private void drawNewGraph(mxGraph graph) {
		this.programmaticallyAddEdge = true;
		createXmlDocument();
		if (valid) {
			Object parent = graph.getDefaultParent();
			graph.getModel().beginUpdate();
			try {
				List<XmlTag> tags = new ArrayList<XmlTag>(xmlDocument.getChildsMap().values());
				Collections.sort(tags, new Comparator<XmlTag>() {
					@Override
					public int compare(XmlTag arg0, XmlTag arg1) {
						if (arg0.getArea() < arg1.getArea()) {
							return -1;
						} else if (arg0.getArea() > arg1.getArea()) {
							return 1;
						} else {
							return 0;
						}
					}
				});

				Map<Integer, Object> vertexMap = new HashMap<Integer, Object>();
				for (XmlTag tag : tags) {
					if (tag instanceof XmlTagName) {
						graph.insertVertex(parent, null, tag, tag.getX() + tag.getHeight() / 2,
								tag.getY() + tag.getWidth() / 2, 0, 0,
								"fontColor=#000000;fontStyle=1;rounded=true;line=none");
						continue;
					}
					Object vertex = graph.insertVertex(parent, null, tag, tag.getX(), tag.getY(), tag.getWidth(),
							tag.getHeight());
					
					((mxCell) vertex).setConnectable(false);
					
					mxGeometry geo1 = new mxGeometry(0, 0.5, PORT_DIAMETER, PORT_DIAMETER);
					geo1.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
					geo1.setRelative(true);

					mxCell port1 = new mxCell(null, geo1, "shape=ellipse;perimter=ellipsePerimeter");
					port1.setVertex(true);
					
					graph.addCell(port1, vertex);
					
					vertexMap.put(tag.getId(), port1);
				}
				for (Entry<Integer, Integer> entry : xmlDocument.getEdges().entrySet()) {
					graph.insertEdge(parent, null, null, vertexMap.get(entry.getKey()),
							vertexMap.get(entry.getValue()), "strokeColor=#000000");
				}
				Map<String, Object> style = graph.getStylesheet().getDefaultEdgeStyle();
				// z tym stylem krawedzie nie maja grotów
				if (schema == null || schema.getType() == null || schema.getType() == SchemaType.NO_SCHEMA) {
					style.put(mxConstants.STYLE_ENDARROW, mxEdgeStyle.SideToSide);
				}
			} finally {
				graph.getModel().endUpdate();
			}
		}
		graph.setCellsResizable(false);
		graph.setCellsEditable(false);
		graph.setCellsMovable(false);
		graph.setVertexLabelsMovable(false);
		graph.setAllowDanglingEdges(false);
		graph.setAutoOrigin(false);

		graph.setKeepEdgesInForeground(true);
		
		this.programmaticallyAddEdge = false;
	}

	/**
	 * Creates the pages of the multi-page editor.
	 */
	protected void createPages() {
		createPage0();
		createPage1();
		createPage2();
	}

	/**
	 * The <code>MultiPageEditorPart</code> implementation of this
	 * <code>IWorkbenchPart</code> method disposes all nested editors.
	 * Subclasses may extend.
	 */
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.dispose();
	}

	/**
	 * Saves the multi-page editor's document.
	 */
	public void doSave(IProgressMonitor monitor) {
		System.out.println("DO_SAVE");
		getEditor(0).doSave(monitor);
		doSave();
	}

	/**
	 * Saves the multi-page editor's document as another file. Also updates the
	 * text for page 0's tab, and updates this multi-page editor's input to
	 * correspond to the nested editor's.
	 */
	public void doSaveAs() {
		System.out.println("DO_SAVE_AS");
		IEditorPart editor = getEditor(0);
		editor.doSaveAs();
		setPageText(0, editor.getTitle());
		setInput(editor.getEditorInput());
	}

	/*
	 * (non-Javadoc) Method declared on IEditorPart
	 */
	public void gotoMarker(IMarker marker) {
		System.out.println("GO_TO_MARKER");
		setActivePage(0);
		IDE.gotoMarker(getEditor(0), marker);
	}

	/**
	 * The <code>MultiPageEditorExample</code> implementation of this method
	 * checks that the input is an instance of <code>IFileEditorInput</code>.
	 */
	public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException {
		if (!(editorInput instanceof IFileEditorInput))
			throw new PartInitException("Invalid Input: Must be IFileEditorInput");
		super.init(site, editorInput);
		setPartName(editorInput.getName());
	}

	/*
	 * (non-Javadoc) Method declared on IEditorPart.
	 */
	public boolean isSaveAsAllowed() {
		return true;
	}

	/**
	 * Calculates the contents of page 2 when the it is activated.
	 */
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);
		switch (newPageIndex) {
		case 0:
			reloadPage0();
			break;
		case 1:
			reloadPage1();
			break;
		case 2:
			this.graphModified = true;
			break;
		}
	}

	/**
	 * Closes all project files on project close.
	 */
	public void resourceChanged(final IResourceChangeEvent event) {
		if (event.getType() == IResourceChangeEvent.PRE_CLOSE) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					IWorkbenchPage[] pages = getSite().getWorkbenchWindow().getPages();
					for (int i = 0; i < pages.length; i++) {
						if (((FileEditorInput) editor.getEditorInput()).getFile().getProject()
								.equals(event.getResource())) {
							IEditorPart editorPart = pages[i].findEditor(editor.getEditorInput());
							pages[i].closeEditor(editorPart, true);
						}
					}
				}
			});
		}
	}

	public boolean validateDocument() {
		String xml = editor.getDocumentProvider().getDocument(editor.getEditorInput()).get();
		String schemaDirectory = "";

		String activeProjectName = getActiveProjectName();

		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (IProject prj : projects) {
			try {
				if (prj.getName().equals(activeProjectName)) {
					schemaDirectory = prj.getPersistentProperty(new QualifiedName("",
							ProjectPropertyPage.SCHEMA_DIR_PROPERTY));
					break;
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}

		this.schema = SchemaFinder.findSchema(xml, schemaDirectory);

		if (this.schema != null && this.schema.getType() == SchemaType.DTD_SCHEMA) {
			try {
				if (DtdParser.parse(xml, schemaDirectory)) {
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (this.schema != null && this.schema.getType() == SchemaType.XSD_SCHEMA) {
			try {
				if (XsdParser.parse(xml, schema.getPathToFile())) {
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
}
