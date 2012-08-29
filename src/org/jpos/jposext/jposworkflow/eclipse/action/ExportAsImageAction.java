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
public class ExportAsImageAction extends SelectionAction {

	public ExportAsImageAction(IWorkbenchPart part) {
		super(part);
		setLazyEnablementCalculation(false);
	}

	@Override
	protected boolean calculateEnabled() {
		// On laisse les EditPolicy decider si la commande est disponible ou non
		Command cmd = createSaveImageCommand();
		if (cmd == null)
			return false;
		return true;
	}

	protected void init() {
		setText("Export as image...");
		setToolTipText("Export as image");
		// On spécifie l'identifiant utilise pour associer cette action a
		// l'action globale de intégrée a Eclipse
		setId("EXPORT_AS_IMAGE");
		// Ajout d'une icone pour l'action.
		/*
		 * ImageDescriptor icon = AbstractUIPlugin.imageDescriptorFromPlugin(
		 * "TutoGEF", "icons/rename-icon.png"); if (icon != null)
		 * setImageDescriptor(icon);
		 */
		setEnabled(false);
	}

	private Command createSaveImageCommand() {
		Request renameReq = new Request("exportAsImage");
		HashMap<String, String> reqData = new HashMap<String, String>();
		// reqData.put("newName", name);
		renameReq.setExtendedData(reqData);
		Object selectedObject = getSelectedObjects().get(0);
		if (selectedObject instanceof EditPart) {		
			Command cmd = ((EditPart) selectedObject).getCommand(renameReq);
			return cmd;
		}
		else {
			return null;
		}
	}

	public void run() {
		Command createSaveImageCommand = createSaveImageCommand();
		if (null != createSaveImageCommand) {
			execute(createSaveImageCommand);
		}
	}
}
