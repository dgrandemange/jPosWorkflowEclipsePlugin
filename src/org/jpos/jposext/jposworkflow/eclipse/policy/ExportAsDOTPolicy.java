package org.jpos.jposext.jposworkflow.eclipse.policy;


import org.eclipse.gef.Request;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.AbstractEditPolicy;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.jpos.jposext.jposworkflow.eclipse.MyEditorInput;
import org.jpos.jposext.jposworkflow.eclipse.command.ExportAsDOTCommand;

public class ExportAsDOTPolicy extends AbstractEditPolicy {

	public Command getCommand(Request request) {
		if (request.getType().equals("exportAsDOT"))
			return createExportAsDOTCommand(request);
		return null;
	}

	protected Command createExportAsDOTCommand(Request renameRequest) {
		ExportAsDOTCommand command = new ExportAsDOTCommand();
		
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        MyEditorInput editorInput = (MyEditorInput) page.getActiveEditor().getEditorInput();
		command.setJPosTxnMgrGroups(editorInput.getJPosTxnMgrGroups());
		
		RootEditPart rootEditPart = this.getHost().getRoot();		
		command.setEditPart(rootEditPart);
		
		return command;
	}
}
