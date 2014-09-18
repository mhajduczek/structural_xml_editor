package com.hajduczek.xmleditor.markers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class MarkersUtil {
	public static void addErrorMarker(String message, String location) {
		try {
			IResource resource = getFirstPackageExplorerProjectResource();
			IMarker marker = resource.createMarker(IMarker.PROBLEM);
			marker.setAttribute(IMarker.MESSAGE, "This a a task");
			marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
			marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
			marker.setAttribute(IMarker.LOCATION, "mh_loc");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void selectFirstProjectInPackageExplorer() {
		List<Object> openProjects = new ArrayList<Object>();

		for (IProject project : ResourcesPlugin.getWorkspace().getRoot()
				.getProjects()) {
			if (project.isOpen()) {
				final IJavaProject javaProject = JavaCore.create(project);

				if (javaProject != null) {
					openProjects.add(javaProject);
				}

				openProjects.add(project);
			}
		}

		Object[] projectsToSelect = openProjects.toArray();
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		IViewPart view;
		try {
			view = window.getActivePage().showView(
					"org.eclipse.jdt.ui.PackageExplorer");
			view.getSite().getSelectionProvider()
					.setSelection(new StructuredSelection(projectsToSelect));
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	private static IResource getFirstPackageExplorerProjectResource() {
		selectFirstProjectInPackageExplorer();

		ISelectionService ss = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getSelectionService();
		ISelection sel = ss.getSelection("org.eclipse.jdt.ui.PackageExplorer");
		Object selectedObject = sel;
		if (sel instanceof IStructuredSelection) {
			selectedObject = ((IStructuredSelection) sel).getFirstElement();
		}
		if (selectedObject instanceof IAdaptable) {
			IResource res = (IResource) ((IAdaptable) selectedObject)
					.getAdapter(IResource.class);
			return res;
		}
		return null;
	}
}
