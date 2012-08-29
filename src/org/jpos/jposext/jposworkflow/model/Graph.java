package org.jpos.jposext.jposworkflow.model;

import java.util.List;

/**
 * @author dgrandemange
 * 
 */
public class Graph {

	public static final String INITIAL_NODE_TYPE = "INITIAL_NODE";
	public static final String INITIAL_NODE_ID = "INITIAL";
	public static final String FINAL_NODE_TYPE = "FINAL_NODE";
	public static final String FINAL_NODE_ID = "FINAL";

	private Node initialNode;

	private Node finalNode;

	private List<Transition> lstTransitions;

	public Graph(Node debutNode, Node finalNode, List<Transition> lstTransitions) {
		super();
		this.initialNode = debutNode;
		this.finalNode = finalNode;
		this.lstTransitions = lstTransitions;
	}

	public Node getInitialNode() {
		return initialNode;
	}

	public void setInitialNode(Node debutNode) {
		this.initialNode = debutNode;
	}

	public Node getFinalNode() {
		return finalNode;
	}

	public void setFinalNode(Node finalNode) {
		this.finalNode = finalNode;
	}

	public List<Transition> getLstTransitions() {
		return lstTransitions;
	}

	public void setLstTransitions(List<Transition> lstTransitions) {
		this.lstTransitions = lstTransitions;
	}

}
