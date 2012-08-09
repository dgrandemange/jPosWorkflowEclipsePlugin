package org.jpos.jposext.jposworkflow.eclipse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
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
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.IAction;
import org.jpos.jposext.ctxmgmt.annotation.UpdateContextRule;
import org.jpos.jposext.ctxmgmt.annotation.UpdateContextRules;
import org.jpos.jposext.jposworkflow.eclipse.action.ExportAsDOTAction;
import org.jpos.jposext.jposworkflow.eclipse.action.ExportAsImageAction;
import org.jpos.jposext.jposworkflow.eclipse.editpart.AppEditPartFactory;
import org.jpos.jposext.jposworkflow.eclipse.model.NodeDataWrapper;
import org.jpos.jposext.jposworkflow.model.Graph;
import org.jpos.jposext.jposworkflow.model.ParticipantInfo;
import org.jpos.jposext.jposworkflow.model.Transition;
import org.jpos.jposext.jposworkflow.service.support.GraphReducerImpl;
import org.jpos.jposext.jposworkflow.service.support.TxnMgrGroupsConverterImpl;

/**
 * @author dgrandemange
 *
 */
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

		populateContextMgmtInfo(jPosTxnMgrGroups);

		TxnMgrGroupsConverterImpl converter = new TxnMgrGroupsConverterImpl();
		Graph graphInter1 = converter.toGraph(jPosTxnMgrGroups);
		
		GraphReducerImpl reducer = new GraphReducerImpl();
		Graph graphInter2 = reducer.reduce(graphInter1);
		updateReducedGraphTransitionsWithContextMgmtInfo(graphInter2);		
		
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
			Edge edge = new Edge(mapNodes.get(t.getSource().getId()),
					mapNodes.get(t.getTarget().getId()));
			edge.data = t;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#setPartName(java.lang.String)
	 */
	@Override
	public void setPartName(String partName) {
		super.setPartName(partName);
	}

	protected void populateContextMgmtInfo(
			Map<String, List<ParticipantInfo>> jPosTxnMgrGroups) {
		try {
			IProject project = ((MyEditorInput) this.getEditorInput())
					.getProject();

			IJavaProject jProject = (IJavaProject) project
					.getNature(JavaCore.NATURE_ID);

			for (Entry<String, List<ParticipantInfo>> entry : jPosTxnMgrGroups
					.entrySet()) {

				for (ParticipantInfo participantInfo : entry.getValue()) {
					Map<String, String[]> updCtxAttrByTransId = new HashMap<String, String[]>();
					participantInfo.setUpdCtxAttrByTransId(updCtxAttrByTransId);

					String javaClassPathFromFullClassName = getJavaClassPathFromFullClassName(participantInfo
							.getClazz());
					IPath path = new Path(javaClassPathFromFullClassName);

					if (null == path) {
						return;
					}

					IJavaElement jElt = jProject.findElement(path);

					if (null == jElt) {
						continue;
					}
					
					int elementType = jElt.getElementType();

					if (elementType == IJavaElement.COMPILATION_UNIT) {

						IType[] types = ((ICompilationUnit) jElt).getTypes();
						IType type = types[0];
						IAnnotation[] annotations = type.getAnnotations();
						for (IAnnotation annotation : annotations) {
							if (UpdateContextRules.class.getSimpleName()
									.equals(annotation.getElementName())) {
								IMemberValuePair[] memberValuePairs = annotation
										.getMemberValuePairs();
								for (IMemberValuePair memberValuePair : memberValuePairs) {
									if ("value".equals(memberValuePair
											.getMemberName())) {
										try {
											Object[] memberValues = (Object[]) memberValuePair
													.getValue();
											for (Object memberValue : memberValues) {
												if (memberValue instanceof IAnnotation) {
													IAnnotation subAnnotation = (IAnnotation) memberValue;
													if (UpdateContextRule.class
															.getSimpleName()
															.equals(subAnnotation
																	.getElementName())) {
														String id = null;
														String[] attrNames = null;
														IMemberValuePair[] subMemberValuePairs = subAnnotation
																.getMemberValuePairs();
														for (IMemberValuePair subMemberValuePair : subMemberValuePairs) {
															if ("id".equals(subMemberValuePair
																	.getMemberName())) {
																id = (String) subMemberValuePair
																		.getValue();
															} else if ("attrNames"
																	.equals(subMemberValuePair
																			.getMemberName())) {
																Object subMemberValue = subMemberValuePair
																		.getValue();
																if (subMemberValue instanceof String) {
																	attrNames = new String[] { (String) subMemberValue };
																} else if (subMemberValue instanceof Object[]) {
																	attrNames = Arrays
																			.copyOf((Object[]) subMemberValue,
																					((Object[]) subMemberValue).length,
																					String[].class);
																}
															}
														}

														if (null != attrNames) {
															if (null == id) {
																updCtxAttrByTransId
																		.put(UpdateContextRule.DEFAULT_ID,
																				attrNames);
															} else {
																updCtxAttrByTransId
																		.put(id,
																				attrNames);
															}
														}
													}
												}
											}

										} catch (ClassCastException e) {
											e.printStackTrace();
										}
									}
								}
							}
						}
					}

				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void updateReducedGraphTransitionsWithContextMgmtInfo(Graph graph) {
		updateReducedGraphTransitionsWithContextMgmtInfo(graph, null);
	}	

	protected void updateReducedGraphTransitionsWithContextMgmtInfo(Graph graph, org.jpos.jposext.jposworkflow.model.Node currentNode) {
		if (null == graph) {
			return;
		}
		
		if (graph.getFinalNode().equals(currentNode)) {
			return;
		}
		
		if (null == currentNode) {
			currentNode = graph.getInitialNode();
		}
		
		boolean doReturn=false;
		
		HashSet<String> sharedGuaranteedAttrsBetweenParentTransitions;
		HashSet<String> sharedOptionalAttrsBetweenParentTransitions;
		
		List<Transition> lstTransitionsAsDest = currentNode.getLstTransitionsAsDest();
		// How many transitions are referencing the current node ?		
		if (lstTransitionsAsDest.size() > 1) {
			// Several transitions are referencing the current node
			
			// Check for all transitions with current node as destination have their context mgmt info populated. Otherwise, return		
			for (Transition transition : lstTransitionsAsDest) {
					if (null == transition.getGuaranteedCtxAttributes()) {
						doReturn =true;
						break;
					}
			}
			
			if (doReturn) {
				System.out.println(String.format("Stop on group=%s,class=%s,referenced by %d transitions", currentNode.getParticipant().getGroupName(), currentNode.getParticipant().getClazz(), lstTransitionsAsDest.size()));				
				return;
			}
			
			HashSet<String> sharedAllAttrsBetweenParentTransitions = new HashSet<String>();
			
			// Compute guaranteed attributes commonly shared between current node's all parent transitions
			sharedGuaranteedAttrsBetweenParentTransitions = new HashSet<String>();
			boolean firstPass = true;
			for (Transition transition : lstTransitionsAsDest) {
				if (null != transition.getOptionalCtxAttributes()) {
					sharedAllAttrsBetweenParentTransitions.addAll(transition.getOptionalCtxAttributes());
				}
				
				List<String> currentTransitionGuaranteedCtxAttributes = transition.getGuaranteedCtxAttributes();
				
				if (null == currentTransitionGuaranteedCtxAttributes) {
					currentTransitionGuaranteedCtxAttributes = new ArrayList<String>();
				}
				
				sharedAllAttrsBetweenParentTransitions.addAll(currentTransitionGuaranteedCtxAttributes);
				
				if (firstPass) {							
					sharedGuaranteedAttrsBetweenParentTransitions.addAll(currentTransitionGuaranteedCtxAttributes);							
				}
				else {
					sharedGuaranteedAttrsBetweenParentTransitions.retainAll(currentTransitionGuaranteedCtxAttributes);
				}
				
				firstPass = false;
			}

			// Compute attributes NOT shared between current node's all parent transitions
			sharedOptionalAttrsBetweenParentTransitions = new HashSet<String>();
			for (String attr : sharedAllAttrsBetweenParentTransitions) {
				if (!(sharedGuaranteedAttrsBetweenParentTransitions.contains(attr))) {
					sharedOptionalAttrsBetweenParentTransitions.add(attr);
				}
			}
			
		}
		else if (lstTransitionsAsDest.size() == 1) {
			// One transition only is referencing the current node
			Transition t = lstTransitionsAsDest.get(0);
			
			sharedGuaranteedAttrsBetweenParentTransitions = new HashSet<String>();			
			sharedGuaranteedAttrsBetweenParentTransitions.addAll(t.getGuaranteedCtxAttributes());

			sharedOptionalAttrsBetweenParentTransitions = new HashSet<String>();			
			sharedOptionalAttrsBetweenParentTransitions.addAll(t.getOptionalCtxAttributes());
		}
		else {
			// There isn't any transition referencing the current node (i.e. initial node of the graph)
			sharedGuaranteedAttrsBetweenParentTransitions = new HashSet<String>();
			sharedOptionalAttrsBetweenParentTransitions = new HashSet<String>();
		}

		String[] ctxAttrSetByDefault = null;
		ParticipantInfo currentNodeParticipant = currentNode.getParticipant();
		Map<String, String[]> currNodeUpdCtxAttrByTransNameMap = null;
		if (null != currentNodeParticipant) {
			currNodeUpdCtxAttrByTransNameMap = currentNodeParticipant.getUpdCtxAttrByTransId();
			if (null != currNodeUpdCtxAttrByTransNameMap) {
				ctxAttrSetByDefault = currNodeUpdCtxAttrByTransNameMap.get(UpdateContextRule.DEFAULT_ID);
			}
		}		
		
		for (Transition transition : currentNode.getLstTransitionsAsSource()) {
			String transitionName = transition.getName();
			String[] ctxAttrSetForTransName = null;
			if (null != currNodeUpdCtxAttrByTransNameMap) {
				ctxAttrSetForTransName = currNodeUpdCtxAttrByTransNameMap.get(transitionName);
			}
			
			HashSet<String> ctxAttributes = new HashSet<String>();
			
			if (null != ctxAttrSetByDefault) {
				Collections.addAll(ctxAttributes, ctxAttrSetByDefault);
			}
			
			if (null != ctxAttrSetForTransName) {
				Collections.addAll(ctxAttributes, ctxAttrSetForTransName);
			}
			
			transition.setGuaranteedCtxAttributes(new ArrayList<String>());
			transition.getGuaranteedCtxAttributes().addAll(sharedGuaranteedAttrsBetweenParentTransitions);
			transition.getGuaranteedCtxAttributes().addAll(ctxAttributes);
			Collections.sort(transition.getGuaranteedCtxAttributes());
			
			transition.setOptionalCtxAttributes(new ArrayList<String>());
			transition.getOptionalCtxAttributes().addAll(sharedOptionalAttrsBetweenParentTransitions);
			Collections.sort(transition.getOptionalCtxAttributes());
			
			updateReducedGraphTransitionsWithContextMgmtInfo(graph, transition.getTarget());
		}
		
	}		
	
	final protected String getJavaClassPathFromFullClassName(String className) {
		return className.replaceAll("\\.", "/") + ".java";
	}
	
}
