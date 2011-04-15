package org.jpos.jposext.jposworkflow.service;

import org.jpos.jposext.jposworkflow.model.Graph;

/**
 * Interface de r�duction du nombre de noeuds d'un graphe
 * 
 * @author dgrandemange
 *
 */
public interface IGraphReducer {
	
	/**
	 * M�thode de r�duction de graphe
	 * 
	 * @param graph Le graphe � r�duire
	 * @return Le graphe r�duit
	 */
	public Graph reduce(Graph graph);
	
}
