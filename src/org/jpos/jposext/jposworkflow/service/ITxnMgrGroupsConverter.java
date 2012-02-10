package org.jpos.jposext.jposworkflow.service;

import java.util.List;
import java.util.Map;

import org.jpos.jposext.jposworkflow.model.Graph;
import org.jpos.jposext.jposworkflow.model.ParticipantInfo;


/**
 * Interface de transformation de groupes de participants JPos
 * 
 * @author dgrandemange
 *
 */
public interface ITxnMgrGroupsConverter {
	
	/**
	 * @param groups
	 * @return Un graphe
	 */
	Graph toGraph(Map<String, List<ParticipantInfo>> groups);
	
}
