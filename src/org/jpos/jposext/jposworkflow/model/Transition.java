package org.jpos.jposext.jposworkflow.model;

public class Transition {

	private Node source;

	private Node target;

	private String id;
	
	private String desc;
	
	public Transition() {
		super();
	}

	public Transition(String id, Node source, Node target) {
		super();
		this.id=id;
		this.source = source;
		this.target = target;
		source.addAsSourceInTransition(this);
		target.addAsDestInTransition(this);
	}

	public void remove() {		
		source.removeFromTransitionAsSource(this);
		target.removeFromTransitionAsDest(this);
		source=null;
		target=null;
	}
	
	public Node getSource() {
		return source;
	}

	public void setSource(Node source) {
		this.source = source;
	}

	public Node getTarget() {
		return target;
	}

	public void setTarget(Node target) {
		this.target = target;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	
}
