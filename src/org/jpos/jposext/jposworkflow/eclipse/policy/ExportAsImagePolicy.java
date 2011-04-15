package org.jpos.jposext.jposworkflow.eclipse.policy;


import org.eclipse.gef.Request;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.AbstractEditPolicy;
import org.jpos.jposext.jposworkflow.eclipse.command.ExportAsImageCommand;

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
		
		return command;
	}
}
