package org.jpos.jposext.jposworkflow.eclipse.action;

import java.util.HashMap;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author dgrandemange
 *
 */
public class ExportAsDOTAction extends SelectionAction {

	public ExportAsDOTAction(IWorkbenchPart part) {
		super(part);
		setLazyEnablementCalculation(false);
	}

	@Override
	protected boolean calculateEnabled() {
		// On laisse les EditPolicy decider si la commande est disponible ou non
		Command cmd = createExportAsDOTCommand();
		if (cmd == null)
			return false;
		return true;
	}

	protected void init() {
		setText("Export as DOT...");
		setToolTipText("Export as DOT");
		// On spécifie l'identifiant utilise pour associer cette action a
		// l'action globale de intégrée a Eclipse
		setId("EXPORT_AS_DOT");
		// Ajout d'une icone pour l'action.
		/*
		 * ImageDescriptor icon = AbstractUIPlugin.imageDescriptorFromPlugin(
		 * "TutoGEF", "icons/rename-icon.png"); if (icon != null)
		 * setImageDescriptor(icon);
		 */
		setEnabled(false);
	}

	private Command createExportAsDOTCommand() {
		Request renameReq = new Request("exportAsDOT");
		HashMap<String, String> reqData = new HashMap<String, String>();		
		renameReq.setExtendedData(reqData);
		EditPart object = (EditPart) getSelectedObjects().get(0);
		Command cmd = object.getCommand(renameReq);
		return cmd;
	}

	public void run() {
		execute(createExportAsDOTCommand());
	}
}
