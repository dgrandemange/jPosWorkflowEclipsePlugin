package org.jpos.jposext.jposworkflow.eclipse.model;

import org.jpos.jposext.jposworkflow.model.NodeNatureEnum;
import org.jpos.jposext.jposworkflow.model.ParticipantInfo;

/**
 * @author dgrandemange
 *
 */
public class NodeDataWrapper {
	
	private NodeNatureEnum nodeNature;
	
	private ParticipantInfo pInfo;

	public NodeNatureEnum getNodeNature() {
		return nodeNature;
	}

	public NodeDataWrapper(NodeNatureEnum nodeNature, ParticipantInfo pInfo) {
		super();
		this.nodeNature = nodeNature;
		this.pInfo = pInfo;
	}

	public void setNodeNature(NodeNatureEnum nodeNature) {
		this.nodeNature = nodeNature;
	}

	public ParticipantInfo getpInfo() {
		return pInfo;
	}

	public void setpInfo(ParticipantInfo pInfo) {
		this.pInfo = pInfo;
	}
	
}
