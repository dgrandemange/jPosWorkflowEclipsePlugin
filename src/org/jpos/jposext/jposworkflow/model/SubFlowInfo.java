package org.jpos.jposext.jposworkflow.model;

import java.util.Map;

/**
 * @author dgrandemange
 * 
 */
public class SubFlowInfo extends ParticipantInfo {

	private Graph subFlowGraph;

	public SubFlowInfo() {
		super();
	}

	public SubFlowInfo(String ERname, Graph subFlowGraph,
			Map<String, SelectCriterion> selectCriteria) {
		super("<subflow>", ERname, selectCriteria);
		this.subFlowGraph = subFlowGraph;
	}

	/**
	 * @return the subFlowGraph
	 */
	public Graph getSubFlowGraph() {
		return subFlowGraph;
	}

	/**
	 * @param subFlowGraph the subFlowGraph to set
	 */
	public void setSubFlowGraph(Graph subFlowGraph) {
		this.subFlowGraph = subFlowGraph;
	}

}
