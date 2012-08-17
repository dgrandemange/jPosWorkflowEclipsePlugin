package org.jpos.jposext.jposworkflow.model;

import java.util.List;

/**
 * @author dgrandemange
 *
 */
public class Transition {

	private Node source;

	private Node target;

	private String id;

	private String name;

	private String desc;
	
	private List<String> guaranteedCtxAttributes;
	
	private List<String> optionalCtxAttributes;

	private List<String> attributesAdded;
	
	public Transition() {
		super();
	}

	public Transition(String id, String name, Node source, Node target) {
		super();
		this.id = id;
		this.name = name;
		this.source = source;
		this.target = target;
		source.addAsSourceInTransition(this);
		target.addAsDestInTransition(this);
	}

	public void remove() {
		source.removeFromTransitionAsSource(this);
		target.removeFromTransitionAsDest(this);
		source = null;
		target = null;
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

	/**
	 * @return the ctxAttributes
	 */
	public List<String> getGuaranteedCtxAttributes() {
		return guaranteedCtxAttributes;
	}

	/**
	 * @param ctxAttributes
	 *            the ctxAttributes to set
	 */
	public void setGuaranteedCtxAttributes(List<String> guaranteedCtxAttributes) {
		this.guaranteedCtxAttributes = guaranteedCtxAttributes;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the optionalCtxAttributes
	 */
	public List<String> getOptionalCtxAttributes() {
		return optionalCtxAttributes;
	}

	/**
	 * @param optionalCtxAttributes the optionalCtxAttributes to set
	 */
	public void setOptionalCtxAttributes(List<String> optionalCtxAttributes) {
		this.optionalCtxAttributes = optionalCtxAttributes;
	}

	/**
	 * @return the attributesAdded
	 */
	public List<String> getAttributesAdded() {
		return attributesAdded;
	}

	/**
	 * @param attributesAdded the attributesAdded to set
	 */
	public void setAttributesAdded(List<String> attributesAdded) {
		this.attributesAdded = attributesAdded;
	}

}
