package org.jpos.jposext.jposworkflow.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dgrandemange
 *
 */
public class Node {

	private NodeNatureEnum nodeNature = NodeNatureEnum.COMMON; 
	
	private String id;

	private List<Transition> lstTransitionsAsSource = new ArrayList<Transition>();

	private List<Transition> lstTransitionsAsDest = new ArrayList<Transition>();

	private String type;

	private ParticipantInfo participant;

	public Node(String id) {
		super();
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public List<Transition> getLstTransitionsAsSource() {
		return lstTransitionsAsSource;
	}

	public void setLstTransitionsAsSource(
			List<Transition> lstTransitionsAsSource) {
		this.lstTransitionsAsSource = lstTransitionsAsSource;
	}

	public List<Transition> getLstTransitionsAsDest() {
		return lstTransitionsAsDest;
	}

	public void setLstTransitionsAsDest(List<Transition> lstTransitionsAsDest) {
		this.lstTransitionsAsDest = lstTransitionsAsDest;
	}

	public void addAsSourceInTransition(Transition t) {
		if (!(lstTransitionsAsSource.contains(t))) {
			lstTransitionsAsSource.add(t);
		}
	}

	public void addAsDestInTransition(Transition t) {
		if (!(lstTransitionsAsDest.contains(t))) {
			lstTransitionsAsDest.add(t);
		}
	}

	public void removeFromTransitionAsSource(Transition transition) {
		lstTransitionsAsSource.remove(transition);
	}

	public void removeFromTransitionAsDest(Transition transition) {
		lstTransitionsAsDest.remove(transition);
	}

	public boolean hasTransitionAsDest(Transition transition) {
		boolean res = false;
		for (Transition t : lstTransitionsAsDest) {
			if ((t.getSource().equals(transition.getSource()))
					&& (t.getTarget().equals(transition.getTarget()))) {
				res=true;
				break;
			}
		}
		return res;
	}

	public boolean hasTransitionAsSource(Transition transition) {
		boolean res = false;
		for (Transition t : lstTransitionsAsSource) {
			if ((t.getSource().equals(transition.getSource()))
					&& (t.getTarget().equals(transition.getTarget()))) {
				res=true;
				break;
			}
		}
		return res;

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ParticipantInfo getParticipant() {
		return participant;
	}

	public void setParticipant(ParticipantInfo participant) {
		this.participant = participant;
	}

	public NodeNatureEnum getNodeNature() {
		return nodeNature;
	}

	public void setNodeNature(NodeNatureEnum nodeNature) {
		this.nodeNature = nodeNature;
	}

	public void setType(String type) {
		this.type = type;
	}

}
