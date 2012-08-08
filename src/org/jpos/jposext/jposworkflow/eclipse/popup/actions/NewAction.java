package org.jpos.jposext.jposworkflow.eclipse.popup.actions;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;


import org.eclipse.core.resources.IFile;
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
import org.jpos.jposext.jposworkflow.model.ParticipantInfo;
import org.jpos.jposext.jposworkflow.service.support.TxnMgrConfigParserImpl;

/**
 * @author dgrandemange
 *
 */
public class NewAction implements IObjectActionDelegate {

	private ISelection selection;

	/**
	 * Constructor for Action1.
	 */
	public NewAction() {
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
		/*
		 * for (Iterator it= structuredSelection.iterator();it.hasNext();) {
		 * Object iterated = it.next(); MessageBox msgBox = new MessageBox(new
		 * Shell(), SWT.OK | SWT.ICON_INFORMATION);
		 * msgBox.setMessage(iterated.getClass().getSimpleName());
		 * msgBox.open(); }
		 */
		IResource iresource = (IResource) structuredSelection.getFirstElement();

		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		IFile file = (IFile) iresource;		

		TxnMgrConfigParserImpl txnMgrConfigParserImpl = new TxnMgrConfigParserImpl();
		Map<String, List<ParticipantInfo>> parsed = null;
		try {
			//parsed = txnMgrConfigParserImpl.parse(file.getContents());
			parsed = txnMgrConfigParserImpl.parse(file.getLocationURI().toURL());			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		if (parsed != null) {
			MyEditorInput editorInput = new MyEditorInput(file.getName(),
					parsed);
			editorInput.setProject(file.getProject());
			try {
				MyGraphicalEditor openEditor = (MyGraphicalEditor) page.openEditor(editorInput, MyGraphicalEditor.ID, false);
				openEditor.setPartName(file.getName());
			} catch (PartInitException e) {
				e.printStackTrace();
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
