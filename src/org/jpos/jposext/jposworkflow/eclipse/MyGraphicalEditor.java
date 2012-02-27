/*
 * Created on 17 juil. 08 by dgrandemange
 *
 * Copyright (c) 2005 Setib
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Setib ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Setib.
 */
package org.jpos.jposext.jposworkflow.eclipse;

import java.util.HashMap;
import java.util.List;
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
import org.jpos.jposext.jposworkflow.model.ParticipantInfo;
import org.jpos.jposext.jposworkflow.model.Transition;
import org.jpos.jposext.jposworkflow.service.support.GraphReducerImpl;
import org.jpos.jposext.jposworkflow.service.support.TxnMgrGroupsConverterImpl;


public class MyGraphicalEditor extends GraphicalEditor {

	public static final String ID = "org.jpos.jposext.jposworkflow.eclipse.mygraphicaleditor";

	public MyGraphicalEditor() {
		setEditDomain(new DefaultEditDomain(this));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#initializeGraphicalViewer()
	 */
	@Override
	protected void initializeGraphicalViewer() {
		GraphicalViewer viewer = getGraphicalViewer();
		viewer.setContents(createDirectedGraph());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#configureGraphicalViewer()
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
	}

	@SuppressWarnings("unchecked")
	private DirectedGraph createDirectedGraph() {
		DirectedGraph graph = new DirectedGraph();
		graph.setDefaultPadding(new Insets(20, 150, 20, 150));

		Map<String, List<ParticipantInfo>> jPosTxnMgrGroups = ((MyEditorInput) getEditorInput())
				.getJPosTxnMgrGroups();

		TxnMgrGroupsConverterImpl converter = new TxnMgrGroupsConverterImpl();
		Graph graphInter1 = converter.toGraph(jPosTxnMgrGroups);
		
		GraphReducerImpl reducer = new GraphReducerImpl();
		Graph graphInter2 = reducer.reduce(graphInter1);
		
		Map<String, Node> mapNodes = new HashMap<String, Node>();
		for (Transition t : graphInter2.getLstTransitions()) {
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

		for (Transition t : graphInter2.getLstTransitions()) {
			Edge edge = new Edge(mapNodes.get(t.getSource().getId()), mapNodes
					.get(t.getTarget().getId()));
			edge.data = t.getDesc();
			graph.edges.add(edge);
		}

		DirectedGraphLayout layout = new DirectedGraphLayout();
		layout.visit(graph);

		return graph;
	}

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

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#setPartName(java.lang.String)
	 */
	@Override
	public void setPartName(String partName) {
		super.setPartName(partName);
	}
}
