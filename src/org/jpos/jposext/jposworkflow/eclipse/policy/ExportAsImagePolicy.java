package org.jpos.jposext.jposworkflow.eclipse.policy;

import org.eclipse.gef.Request;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.AbstractEditPolicy;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.jpos.jposext.jposworkflow.eclipse.MyEditorInput;
import org.jpos.jposext.jposworkflow.eclipse.command.ExportAsImageCommand;

/**
 * @author dgrandemange
 *
 */
public class ExportAsImagePolicy extends AbstractEditPolicy {

	public Command getCommand(Request request) {
		if (request.getType().equals("exportAsImage"))
			return createSaveImageCommand(request);
		return null;
	}

	protected Command createSaveImageCommand(Request renameRequest) {
		ExportAsImageCommand command = new ExportAsImageCommand();
		
		RootEditPart rootEditPart = this.getHost().getRoot();
		
		command.setEditPart(rootEditPart);
		
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        MyEditorInput editorInput = (MyEditorInput) page.getActiveEditor().getEditorInput();
		
		command.setDefaultName(editorInput.getName());
		
		return command;
	}
}
