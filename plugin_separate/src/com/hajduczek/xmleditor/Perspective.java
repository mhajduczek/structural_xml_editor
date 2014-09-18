package com.hajduczek.xmleditor;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {
	
	private static String ID_PROJECT_EXPLORER = "org.eclipse.ui.navigator.ProjectExplorer";
    
    public Perspective() {
        super();
    }

    /**
     * Defines the initial layout for a perspective.  
     *
     * Implementors of this method may add additional views to a
     * perspective.  The perspective already contains an editor folder
     * with <code>ID = ILayoutFactory.ID_EDITORS</code>.  Add additional views
     * to the perspective in reference to the editor folder.
     *
     * This method is only called when a new perspective is created.  If
     * an old perspective is restored from a persistence file then
     * this method is not called.
     *
     * @param layout the factory used to add views to the perspective
     */
    public void createInitialLayout(IPageLayout layout) {
        defineActions(layout);
        defineLayout(layout);
    }

    /**
     * Defines the initial actions for a page.  
     * @param layout The layout we are filling
     */
    public void defineActions(IPageLayout layout) {
		layout.addActionSet(IPageLayout.ID_NAVIGATE_ACTION_SET);
    }

    /**
     * Defines the initial layout for a page.  
     * @param layout The layout we are filling
     */
    public void defineLayout(IPageLayout layout) {
        // Editors are placed for free.
        String editorArea = layout.getEditorArea();

        // Top left.
        IFolderLayout topLeft = layout.createFolder(
                "topLeft", IPageLayout.LEFT, (float) 0.26, editorArea);
        topLeft.addView(ID_PROJECT_EXPLORER);
        topLeft.addPlaceholder(IPageLayout.ID_BOOKMARKS);

        // Add a placeholder for the old navigator to maintain compatibility
        topLeft.addPlaceholder("org.eclipse.ui.views.ResourceNavigator");

        IFolderLayout bottomLeft = layout.createFolder(
                "bottomLeft", IPageLayout.BOTTOM, (float) 0.66, "topLeft");
        bottomLeft.addView("com.hajduczek.xmleditor.views.GraphOutlineView");

        // Bottom right.
		IFolderLayout bottomRight = layout.createFolder(
                "bottomRight", IPageLayout.BOTTOM, (float) 0.66, editorArea);
		
		bottomRight.addView("com.hajduczek.xmleditor.views.ComponentPropertiesView");
		
    }
}
