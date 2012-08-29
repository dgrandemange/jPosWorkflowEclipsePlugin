package org.jpos.jposext.jposworkflow.service.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.StringTokenizer;

import org.jpos.jposext.jposworkflow.helper.GraphHelper;
import org.jpos.jposext.jposworkflow.model.Graph;
import org.jpos.jposext.jposworkflow.model.Node;
import org.jpos.jposext.jposworkflow.model.NodeNatureEnum;
import org.jpos.jposext.jposworkflow.model.NodeWrapper;
import org.jpos.jposext.jposworkflow.model.ParticipantInfo;
import org.jpos.jposext.jposworkflow.model.SelectCriterion;
import org.jpos.jposext.jposworkflow.model.Transition;
import org.jpos.jposext.jposworkflow.service.ITxnMgrGroupsConverter;

/**
 * Class helper
 * 
 * @author dgrandemange
 * 
 */
public class TxnMgrGroupsConverterImpl implements ITxnMgrGroupsConverter {

	private static final String GROUP_ID__CONTROL_BACK_TO_PARENT_FLOW = "*$controlBackToParentFlow$*";

	public class Counter {
		private int count = 0;

		public int inc() {
			count += 1;
			return count;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jpos.jposext.jposworkflow.service.ITxnMgrGroupsConverter#toGraph(
	 * java.util.Map)
	 */
	public Graph toGraph(Map<String, List<ParticipantInfo>> groups) {

		Node initialNode = new Node(Graph.INITIAL_NODE_ID);
		initialNode.setType(Graph.INITIAL_NODE_TYPE);
		initialNode.setNodeNature(NodeNatureEnum.INITIAL);

		Node finalNode = new Node(Graph.FINAL_NODE_ID);
		finalNode.setType(Graph.FINAL_NODE_TYPE);
		finalNode.setNodeNature(NodeNatureEnum.FINAL);
		finalNode.setParticipant(new ParticipantInfo());

		List<Transition> lstTransitions = new ArrayList<Transition>();

		Graph graph = new Graph(initialNode, finalNode, lstTransitions);

		Map<String, List<ParticipantInfo>> reducedGroups = new HashMap<String, List<ParticipantInfo>>();
		toOneParticipantPerGroup(clone(groups), reducedGroups);

		List<ParticipantInfo> rootGroup = reducedGroups
				.get(TxnMgrConfigParserImpl.DEFAULT_GROUP);

		if (null == rootGroup) {
			return graph;
		}

		String transitionName = "";
		String transitionDesc = "";
		Counter transitionSequence = (new TxnMgrGroupsConverterImpl()).new Counter();
		Counter nodeSequence = (new TxnMgrGroupsConverterImpl()).new Counter();
		NodeWrapper pLastNodeWrapper = new NodeWrapper(initialNode);
		Stack<String> groupsStack = new Stack<String>();
		
		core(graph, reducedGroups, rootGroup, pLastNodeWrapper, transitionName,
				transitionDesc, transitionSequence, nodeSequence, groupsStack);
		
		doFinalTransitions(graph, transitionSequence);
		
		replaceControlBackToParentFlowGroup(graph);
		
		GraphHelper.recomputeNodesTransitions(graph);
		
		return graph;
	}

	protected void core(Graph graph,
			Map<String, List<ParticipantInfo>> pgroups,
			List<ParticipantInfo> pgroup, NodeWrapper pLastNodeWrapper,
			String pTransitionName, String pTransitionDesc,
			Counter transitionSequence, Counter nodeSequence,
			Stack<String> groupsStack) {

		for (ParticipantInfo participant : pgroup) {
			Node source = pLastNodeWrapper.getWrapped();
			Node target = new Node(String.format("n%d", nodeSequence.inc()));
			target.setType(String.format("group=%s, class=%s",
					participant.getGroupName(), participant.getClazz()));
			target.setParticipant(participant);

			createTransition(graph,
					String.format("t%d", transitionSequence.inc()), source,
					target, pTransitionName, pTransitionDesc);
			pTransitionDesc = "";
			if (!(participant.isGroup())) {
				pLastNodeWrapper.setWrapped(target);
				if (groupsStack.size() > 0) {
					String groupId = groupsStack.pop();
					List<ParticipantInfo> group = pgroups.get(groupId);

					if (null == group) {
						group = new ArrayList<ParticipantInfo>();
						group.add(createDummyParticipantForGroup(groupId));
						pgroups.put(groupId, group);
					}

					core(graph, pgroups, group, pLastNodeWrapper, "", "",
							transitionSequence, nodeSequence, groupsStack);
				}
			} else {
				for (Entry<String, SelectCriterion> entry : participant
						.getSelectCriteria().entrySet()) {

					Stack<String> groupsStackInter = (Stack<String>) groupsStack
							.clone();

					SelectCriterion criterion = entry.getValue();
					String transitionName = criterion.getName();
					String transitionDesc = criterion.getDesc();

					NodeWrapper nodeWrapper = new NodeWrapper(target);

					StringTokenizer tokenizer;

					tokenizer = new StringTokenizer(criterion.getValue(), " ");
					List<String> lstTokens = new ArrayList<String>();
					while (tokenizer.hasMoreTokens()) {
						String groupId = tokenizer.nextToken();
						lstTokens.add(groupId);
					}

					if (lstTokens.size() > 0) {
						for (int tokenId = lstTokens.size(); tokenId > 0; tokenId--) {
							groupsStackInter.push(lstTokens.get(tokenId - 1));
						}
					} else {
						// No groups selected for transition.
						// If stack is empty, we push a dummy 'control back to
						// parent flow' group on the stack.
						if (groupsStackInter.isEmpty()) {
							groupsStackInter
									.push(GROUP_ID__CONTROL_BACK_TO_PARENT_FLOW);
						}
					}

					String groupId = groupsStackInter.pop();
					List<ParticipantInfo> group = pgroups.get(groupId);

					if (null == group) {
						group = new ArrayList<ParticipantInfo>();
						group.add(createDummyParticipantForGroup(groupId));
						pgroups.put(groupId, group);
					}

					core(graph, pgroups, group, nodeWrapper, transitionName,
							transitionDesc, transitionSequence, nodeSequence,
							groupsStackInter);
				}
			}
		}
	}

	private ParticipantInfo createDummyParticipantForGroup(String groupId) {
		ParticipantInfo pInfo = new ParticipantInfo();
		pInfo.setGroupName(groupId);
		pInfo.setClazz(ParticipantInfo.UNDEFINED_CLAZZ);
		pInfo.setSelectCriteria(new HashMap<String, SelectCriterion>());
		return pInfo;
	}

	protected void doFinalTransitions(Graph graph, Counter transitionSequence) {
		Map<String, Node> notSourceNodes = new HashMap<String, Node>();
		List<Transition> lstTransitions = graph.getLstTransitions();
		for (Transition t : lstTransitions) {
			Node currentNode = t.getTarget();
			if (currentNode.getLstTransitionsAsSource().size() == 0) {
				notSourceNodes.put(currentNode.getId(), currentNode);
			}
		}

		for (Entry<String, Node> entry : notSourceNodes.entrySet()) {
			createTransition(graph,
					String.format("t%d", transitionSequence.inc()),
					entry.getValue(), graph.getFinalNode(), "", "");
		}
	}

	protected void replaceControlBackToParentFlowGroup(Graph graph) {
		
		Node finalNode = graph.getFinalNode();
		List<Transition> lstTransitions = graph.getLstTransitions();
		for (Transition t : lstTransitions) {
			Node currentNode = t.getTarget();
			ParticipantInfo participant = currentNode.getParticipant();
			if (null != participant) {
				if (GROUP_ID__CONTROL_BACK_TO_PARENT_FLOW.equals(participant
						.getGroupName())) {
					t.setTarget(finalNode);
				}
			}
		}

		for (Transition t : finalNode.getLstTransitionsAsDest()) {
			Node currentNode = t.getSource();
			ParticipantInfo participant = currentNode.getParticipant();
			if (null != participant) {
				if (GROUP_ID__CONTROL_BACK_TO_PARENT_FLOW.equals(participant
						.getGroupName())) {
					graph.getLstTransitions().remove(t);
				}
			}
		}
				
	}

	protected void toOneParticipantPerGroup(
			Map<String, List<ParticipantInfo>> pgroups,
			Map<String, List<ParticipantInfo>> pReducedGroups) {

		Counter dynaGroupCounter = new Counter();

		for (Entry<String, List<ParticipantInfo>> entry : pgroups.entrySet()) {
			String last_group = null;
			List<ParticipantInfo> currentGroupParticipants = entry.getValue();
			for (int idx = currentGroupParticipants.size() - 1; idx >= 0; idx--) {
				ParticipantInfo pInfo = currentGroupParticipants.get(idx);
				if (last_group != null) {
					if (pInfo.getSelectCriteria() == null) {
						HashMap<String, SelectCriterion> selectCriteria = new HashMap<String, SelectCriterion>();
						pInfo.setSelectCriteria(selectCriteria);
					}

					if (pInfo.getSelectCriteria().size() == 0) {
						SelectCriterion criterion = new SelectCriterion(
								"default", "", "");
						pInfo.getSelectCriteria().put(criterion.getName(),
								criterion);
					}

					for (Entry<String, SelectCriterion> criterionEntry : pInfo
							.getSelectCriteria().entrySet()) {
						SelectCriterion criterion = criterionEntry.getValue();
						String selGroups = criterion.getValue();
						selGroups += String.format(" %s", last_group);
						criterion.setValue(selGroups);
					}
				}

				if (idx > 0) {
					List<ParticipantInfo> lstParticipantsDynaGroup = new ArrayList<ParticipantInfo>();
					String dynaGroupId = String.format("%s_%d",
							ParticipantInfo.DYNAGROUP_PREFIXE,
							dynaGroupCounter.inc());
					pReducedGroups.put(dynaGroupId, lstParticipantsDynaGroup);

//					ParticipantInfo clonePInfo = (ParticipantInfo) pInfo.clone();
					ParticipantInfo clonePInfo = GraphHelper.getCopy(pInfo);
					clonePInfo.setGroupName(dynaGroupId);

					lstParticipantsDynaGroup.add(clonePInfo);
					last_group = dynaGroupId;
				} else {
					List<ParticipantInfo> lstParticipantsDynaGroup = new ArrayList<ParticipantInfo>();

//					ParticipantInfo clonePInfo = (ParticipantInfo) pInfo.clone();
					ParticipantInfo clonePInfo = GraphHelper.getCopy(pInfo);
					lstParticipantsDynaGroup.add(clonePInfo);

					pReducedGroups
							.put(entry.getKey(), lstParticipantsDynaGroup);
				}
			}
		}
	}

	protected Map<String, List<ParticipantInfo>> clone(
			Map<String, List<ParticipantInfo>> pgroups) {
		Map<String, List<ParticipantInfo>> resClone = new HashMap<String, List<ParticipantInfo>>();
		for (Entry<String, List<ParticipantInfo>> entry : pgroups.entrySet()) {
			List<ParticipantInfo> clonedList = new ArrayList<ParticipantInfo>();
			for (ParticipantInfo pInfo : entry.getValue()) {
//				clonedList.add((ParticipantInfo) pInfo.clone());
				clonedList.add(GraphHelper.getCopy(pInfo));
			}
			resClone.put(entry.getKey(), clonedList);
		}
		return resClone;
	}
	
	protected void createTransition(Graph graph, String tid, Node source,
			Node target, String tname, String tdesc) {
		Transition t = new Transition(tid, tname, source, target);
		t.setDesc(tdesc);
		graph.getLstTransitions().add(t);
	}

}
