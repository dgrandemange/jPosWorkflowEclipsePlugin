package org.jpos.jposext.jposworkflow.service;

import java.util.List;
import java.util.Map;

import org.jpos.jposext.jposworkflow.model.Graph;
import org.jpos.jposext.jposworkflow.model.ParticipantInfo;

/**
 * @author dgrandemange
 *
 */
public interface IContextMgmtInfoPopulator {

	/**
	 * @param jPosTxnMgrGroups
	 */
	public void processParticipantAnnotations(
			Map<String, List<ParticipantInfo>> jPosTxnMgrGroups);

	/**
	 * @param graphInter2
	 */
	public void updateReducedGraph(Graph graphInter2);
	
	
	
}
