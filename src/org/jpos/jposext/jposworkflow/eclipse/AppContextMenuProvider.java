package org.jpos.jposext.jposworkflow.eclipse;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;

public class AppContextMenuProvider extends ContextMenuProvider {

	private ActionRegistry actionRegistry;

	public AppContextMenuProvider(EditPartViewer viewer, ActionRegistry registry) {
		super(viewer);
		setActionRegistry(registry);
	}

	@Override
	public void buildContextMenu(IMenuManager menu) {
		IAction action;
		GEFActionConstants.addStandardActionGroups(menu);
		
		action = getActionRegistry().getAction("EXPORT_AS_IMAGE");
		menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
		
		action = getActionRegistry().getAction("EXPORT_AS_DOT");
		menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
				
	}

	private ActionRegistry getActionRegistry() {
		return actionRegistry;
	}

	private void setActionRegistry(ActionRegistry registry) {
		actionRegistry = registry;
	}

}
