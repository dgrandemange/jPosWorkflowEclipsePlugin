package org.jpos.jposext.jposworkflow.service;

import org.jpos.jposext.jposworkflow.model.Graph;

/**
 * Interface de réduction du nombre de noeuds d'un graphe
 * 
 * @author dgrandemange
 *
 */
public interface IGraphReducer {
	
	/**
	 * Méthode de réduction de graphe
	 * 
	 * @param graph Le graphe à réduire
	 * @return Le graphe réduit
	 */
	public Graph reduce(Graph graph);
	
}
