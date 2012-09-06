package org.jpos.jposext.jposworkflow.eclipse.popup.actions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.jpos.jposext.jposworkflow.eclipse.MyEditorInput;
import org.jpos.jposext.jposworkflow.eclipse.MyGraphicalEditor;
import org.jpos.jposext.jposworkflow.eclipse.service.support.ContextMgmtInfoPopulatorEclipsePluginImpl;
import org.jpos.jposext.jposworkflow.model.Graph;
import org.jpos.jposext.jposworkflow.service.support.FacadeImpl;

/**
 * @author dgrandemange
 * 
 */
public class ViewGraphNotExpanded implements IObjectActionDelegate {

	private ISelection selection;

	/**
	 * Constructor for Action1.
	 */
	public ViewGraphNotExpanded() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		IStructuredSelection structuredSelection = (IStructuredSelection) selection;

		IResource iresource = (IResource) structuredSelection.getFirstElement();

		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		IFile selectedFile = (IFile) iresource;
		IProject project = selectedFile.getProject();

		Map<String, Graph> graphByEntityRef = new HashMap<String, Graph>();
		ContextMgmtInfoPopulatorEclipsePluginImpl ctxMgmtInfoPopulator = new ContextMgmtInfoPopulatorEclipsePluginImpl(
				project);

		Graph rootGraph = null;
		
		try {
			URL selectedUrl = selectedFile.getLocationURI().toURL();
			FacadeImpl facadeImpl = new FacadeImpl();
			rootGraph = facadeImpl.getGraphSubFlowMode(selectedUrl, ctxMgmtInfoPopulator, graphByEntityRef);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}		

		if (null != rootGraph) {
			MyEditorInput editorInput = new MyEditorInput(
					selectedFile.getName(), rootGraph);
			editorInput.setProject(project);
			try {
				MyGraphicalEditor openEditor = (MyGraphicalEditor) page
						.openEditor(editorInput, MyGraphicalEditor.ID, false);
				openEditor.setPartName(selectedFile.getName());
			} catch (PartInitException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}

	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}
	
}
