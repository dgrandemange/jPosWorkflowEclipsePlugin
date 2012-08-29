package org.jpos.jposext.jposworkflow.eclipse;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.graph.DirectedGraph;
import org.eclipse.draw2d.graph.DirectedGraphLayout;
import org.eclipse.draw2d.graph.Edge;
import org.eclipse.draw2d.graph.Node;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.jface.action.IAction;
import org.jpos.jposext.jposworkflow.eclipse.action.ExportAsDOTAction;
import org.jpos.jposext.jposworkflow.eclipse.action.ExportAsImageAction;
import org.jpos.jposext.jposworkflow.eclipse.editpart.AppEditPartFactory;
import org.jpos.jposext.jposworkflow.eclipse.model.NodeDataWrapper;
import org.jpos.jposext.jposworkflow.model.Graph;
import org.jpos.jposext.jposworkflow.model.Transition;

/**
 * @author dgrandemange
 * 
 */
public class MyGraphicalEditor extends GraphicalEditor {

	public static final String ID = "org.jpos.jposext.jposworkflow.eclipse.mygraphicaleditor";

	public MyGraphicalEditor() {
		setEditDomain(new DefaultEditDomain(this));
	}

	@Override
	protected void initializeGraphicalViewer() {
		GraphicalViewer viewer = getGraphicalViewer();
		viewer.setContents(createDirectedGraph());
	}

	@Override
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();

		GraphicalViewer viewer = getGraphicalViewer();

		AppEditPartFactory appEditPartFactory = new AppEditPartFactory();
		IProject project = ((MyEditorInput) this.getEditorInput()).getProject();
		appEditPartFactory.setProject(project);
		viewer.setEditPartFactory(appEditPartFactory);

		ContextMenuProvider provider = new AppContextMenuProvider(viewer,
				getActionRegistry());
		viewer.setContextMenu(provider);
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
	}

	@SuppressWarnings("unchecked")
	private DirectedGraph createDirectedGraph() {
		DirectedGraph graph = new DirectedGraph();
		graph.setDefaultPadding(new Insets(20, 150, 20, 150));

		Graph graphInter = ((MyEditorInput) getEditorInput()).getGraph();
		
		Map<String, Node> mapNodes = new HashMap<String, Node>();
		for (Transition t : graphInter.getLstTransitions()) {
			Node nodeSource = new Node();
			nodeSource.data = new NodeDataWrapper(
					t.getSource().getNodeNature(), t.getSource()
							.getParticipant());
			mapNodes.put(t.getSource().getId(), nodeSource);

			Node nodeTarget = new Node();
			nodeTarget.data = new NodeDataWrapper(
					t.getTarget().getNodeNature(), t.getTarget()
							.getParticipant());
			mapNodes.put(t.getTarget().getId(), nodeTarget);
		}

		for (Entry<String, Node> node : mapNodes.entrySet()) {
			graph.nodes.add(node.getValue());
		}

		for (Transition t : graphInter.getLstTransitions()) {
			Edge edge = new Edge(mapNodes.get(t.getSource().getId()),
					mapNodes.get(t.getTarget().getId()));
			edge.data = t;
			graph.edges.add(edge);
		}

		DirectedGraphLayout layout = new DirectedGraphLayout();
		layout.visit(graph);

		return graph;
	}

	@Override
	public void createActions() {
		super.createActions();

		ActionRegistry registry = getActionRegistry();

		IAction action;

		action = new ExportAsImageAction(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		action = new ExportAsDOTAction(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

	}

	@Override
	public boolean isSaveOnCloseNeeded() {
		return false;
	}

	@Override
	public void setPartName(String partName) {
		super.setPartName(partName);
	}

}
