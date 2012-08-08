package org.jpos.jposext.jposworkflow.model;

/**
 * @author dgrandemange
 *
 */
public class NodeWrapper {
	private Node wrapped;

	public NodeWrapper(Node wrapped) {
		super();
		this.wrapped = wrapped;
	}

	public Node getWrapped() {
		return wrapped;
	}

	public void setWrapped(Node wrapped) {
		this.wrapped = wrapped;
	}
	
}
