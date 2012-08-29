package org.jpos.jposext.jposworkflow.service.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.jpos.jposext.ctxmgmt.annotation.UpdateContextRule;
import org.jpos.jposext.jposworkflow.model.Graph;
import org.jpos.jposext.jposworkflow.model.ParticipantInfo;
import org.jpos.jposext.jposworkflow.model.SubFlowInfo;
import org.jpos.jposext.jposworkflow.model.Transition;
import org.jpos.jposext.jposworkflow.service.IContextMgmtInfoPopulator;

/**
 * @author dgrandemange
 * 
 */
public abstract class ContextMgmtInfoPopulatorAbstractImpl implements
		IContextMgmtInfoPopulator {

	/* (non-Javadoc)
	 * @see org.jpos.jposext.jposworkflow.service.IContextMgmtInfoPopulator#processParticipantAnnotations(java.util.Map)
	 */
	abstract public void processParticipantAnnotations(
			Map<String, List<ParticipantInfo>> jPosTxnMgrGroups);

	/* (non-Javadoc)
	 * @see org.jpos.jposext.jposworkflow.service.IContextMgmtInfoPopulator#updateReducedGraph(org.jpos.jposext.jposworkflow.model.Graph)
	 */
	public void updateReducedGraph(Graph graph) {
		updateReducedGraphTransitionsWithContextMgmtInfo(graph, null);
	}

	protected void updateReducedGraphTransitionsWithContextMgmtInfo(
			Graph graph, org.jpos.jposext.jposworkflow.model.Node currentNode) {
		if (null == graph) {
			return;
		}

		if (null == currentNode) {
			currentNode = graph.getInitialNode();
		}

		boolean forceReturn = false;

		HashSet<String> sharedGuaranteedAttrsBetweenParentTransitions;
		HashSet<String> sharedOptionalAttrsBetweenParentTransitions;

		List<Transition> lstTransitionsAsDest = currentNode
				.getLstTransitionsAsDest();
		// How many transitions are referencing the current node ?
		if (lstTransitionsAsDest.size() > 1) {
			// Several transitions are referencing the current node

			// Check for all transitions with current node as destination have
			// their context mgmt info populated. Otherwise, return
			for (Transition transition : lstTransitionsAsDest) {
				if (null == transition.getGuaranteedCtxAttributes()) {
					forceReturn = true;
					break;
				}
			}

			if (forceReturn) {
				return;
			}

			HashSet<String> sharedAllAttrsBetweenParentTransitions = new HashSet<String>();

			// Compute guaranteed attributes commonly shared between current
			// node's all parent transitions
			sharedGuaranteedAttrsBetweenParentTransitions = new HashSet<String>();
			boolean firstPass = true;
			for (Transition transition : lstTransitionsAsDest) {
				if (null != transition.getOptionalCtxAttributes()) {
					sharedAllAttrsBetweenParentTransitions.addAll(transition
							.getOptionalCtxAttributes());
				}

				List<String> currentTransitionGuaranteedCtxAttributes = transition
						.getGuaranteedCtxAttributes();

				if (null == currentTransitionGuaranteedCtxAttributes) {
					currentTransitionGuaranteedCtxAttributes = new ArrayList<String>();
				}

				sharedAllAttrsBetweenParentTransitions
						.addAll(currentTransitionGuaranteedCtxAttributes);

				if (firstPass) {
					sharedGuaranteedAttrsBetweenParentTransitions
							.addAll(currentTransitionGuaranteedCtxAttributes);
				} else {
					sharedGuaranteedAttrsBetweenParentTransitions
							.retainAll(currentTransitionGuaranteedCtxAttributes);
				}

				firstPass = false;
			}

			// Compute attributes NOT shared between current node's all parent
			// transitions
			sharedOptionalAttrsBetweenParentTransitions = new HashSet<String>();
			for (String attr : sharedAllAttrsBetweenParentTransitions) {
				if (!(sharedGuaranteedAttrsBetweenParentTransitions
						.contains(attr))) {
					sharedOptionalAttrsBetweenParentTransitions.add(attr);
				}
			}

		} else if (lstTransitionsAsDest.size() == 1) {
			// One transition only is referencing the current node
			Transition t = lstTransitionsAsDest.get(0);

			sharedGuaranteedAttrsBetweenParentTransitions = new HashSet<String>();
			sharedGuaranteedAttrsBetweenParentTransitions.addAll(t
					.getGuaranteedCtxAttributes());

			sharedOptionalAttrsBetweenParentTransitions = new HashSet<String>();
			sharedOptionalAttrsBetweenParentTransitions.addAll(t
					.getOptionalCtxAttributes());
		} else {
			// There isn't any transition referencing the current node (i.e.
			// initial node of the graph)
			sharedGuaranteedAttrsBetweenParentTransitions = new HashSet<String>();
			sharedOptionalAttrsBetweenParentTransitions = new HashSet<String>();
		}

		// Check if some optional attributes are present in the guaranteed
		// attributes hashset
		for (String attr : sharedGuaranteedAttrsBetweenParentTransitions) {
			sharedOptionalAttrsBetweenParentTransitions.remove(attr);
		}

		ParticipantInfo currentNodeParticipant = currentNode.getParticipant();

		if (null != currentNodeParticipant) {
			if (null == currentNodeParticipant.getGuaranteedCtxAttributes()) {
				currentNodeParticipant
						.setGuaranteedCtxAttributes(new ArrayList<String>());
			}

			if (null == currentNodeParticipant.getOptionalCtxAttributes()) {
				currentNodeParticipant
						.setOptionalCtxAttributes(new ArrayList<String>());
			}

			currentNodeParticipant.getGuaranteedCtxAttributes().addAll(
					sharedGuaranteedAttrsBetweenParentTransitions);

			currentNodeParticipant.getOptionalCtxAttributes().addAll(
					sharedOptionalAttrsBetweenParentTransitions);
		}

		String[] ctxAttrSetByDefault = null;
		Map<String, String[]> currNodeUpdCtxAttrByTransNameMap = null;
		if (null != currentNodeParticipant) {
			currNodeUpdCtxAttrByTransNameMap = currentNodeParticipant
					.getUpdCtxAttrByTransId();
			if (null != currNodeUpdCtxAttrByTransNameMap) {
				ctxAttrSetByDefault = currNodeUpdCtxAttrByTransNameMap
						.get(UpdateContextRule.DEFAULT_ID);
			}
		}

		for (Transition transition : currentNode.getLstTransitionsAsSource()) {
			String transitionName = transition.getName();
			String[] ctxAttrSetForTransName = null;
			if (null != currNodeUpdCtxAttrByTransNameMap) {
				ctxAttrSetForTransName = currNodeUpdCtxAttrByTransNameMap
						.get(transitionName);

				transition.setAttributesAdded(new ArrayList<String>());

				HashSet<String> addedAttributes = new HashSet<String>();

				if (null != ctxAttrSetByDefault) {
					Collections.addAll(addedAttributes, ctxAttrSetByDefault);
				}

				if (null != ctxAttrSetForTransName) {
					Collections.addAll(addedAttributes, ctxAttrSetForTransName);
				}

				transition.getAttributesAdded().addAll(addedAttributes);
			}

			HashSet<String> ctxAttributes = new HashSet<String>();

			if (null != ctxAttrSetByDefault) {
				Collections.addAll(ctxAttributes, ctxAttrSetByDefault);
			}

			if (null != ctxAttrSetForTransName) {
				Collections.addAll(ctxAttributes, ctxAttrSetForTransName);
			}

			ParticipantInfo subFlowFinalNodeParticipant = null;
			if ((null != currentNodeParticipant) && (currentNodeParticipant instanceof SubFlowInfo)) {
				SubFlowInfo subFlowParticipant = (SubFlowInfo) currentNodeParticipant;
				Graph subFlowGraph = subFlowParticipant.getSubFlowGraph();
				
				if (null != subFlowGraph) {
					subFlowFinalNodeParticipant = subFlowGraph.getFinalNode().getParticipant();
				}								
			}
			
			HashSet<String> finalGuaranteedCtxAttributes = new HashSet<String>();
			List<String> transitionGuaranteedCtxAttributes = transition.getGuaranteedCtxAttributes();
			if (null != transitionGuaranteedCtxAttributes) {
				finalGuaranteedCtxAttributes.addAll(transitionGuaranteedCtxAttributes);
			}			
			finalGuaranteedCtxAttributes.addAll(sharedGuaranteedAttrsBetweenParentTransitions);
			finalGuaranteedCtxAttributes.addAll(ctxAttributes);
			if (null != subFlowFinalNodeParticipant) {
				if (null != subFlowFinalNodeParticipant.getGuaranteedCtxAttributes()) {
					finalGuaranteedCtxAttributes.addAll(subFlowFinalNodeParticipant.getGuaranteedCtxAttributes());
				}
			}
			transitionGuaranteedCtxAttributes = new ArrayList<String>();
			transitionGuaranteedCtxAttributes.addAll(finalGuaranteedCtxAttributes);
			Collections.sort(transitionGuaranteedCtxAttributes);
			transition.setGuaranteedCtxAttributes(transitionGuaranteedCtxAttributes);
			
			HashSet<String> finalOptionalCtxAttributes = new HashSet<String>();
			List<String> transitionOptionalCtxAttributes = transition.getOptionalCtxAttributes();
			if (null != transitionOptionalCtxAttributes) {
				finalOptionalCtxAttributes.addAll(transitionOptionalCtxAttributes);
			}			
			finalOptionalCtxAttributes.addAll(sharedOptionalAttrsBetweenParentTransitions);
			if (null != subFlowFinalNodeParticipant) {				
				if (null != subFlowFinalNodeParticipant.getOptionalCtxAttributes()) {
					finalOptionalCtxAttributes.addAll(subFlowFinalNodeParticipant.getOptionalCtxAttributes());
				}
			}
			transitionOptionalCtxAttributes = new ArrayList<String>();
			transitionOptionalCtxAttributes.addAll(finalOptionalCtxAttributes);
			Collections.sort(transitionOptionalCtxAttributes);
			transition.setOptionalCtxAttributes(transitionOptionalCtxAttributes);

			updateReducedGraphTransitionsWithContextMgmtInfo(graph,
					transition.getTarget());
		}

	}

}
