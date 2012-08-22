package org.jpos.jposext.jposworkflow.service.support;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.jpos.jposext.jposworkflow.helper.GraphHelper;
import org.jpos.jposext.jposworkflow.model.Graph;
import org.jpos.jposext.jposworkflow.model.Node;
import org.jpos.jposext.jposworkflow.model.Transition;
import org.jpos.jposext.jposworkflow.service.IGraphReducer;

/**
 * Implémentation de base d'un réducteur de graphe partant du noeud final pour
 * remonter les noeuds parents et mutualiser les noeuds traversés qui sont
 * communs et pour lesquels leur noeuds fils suivent le même chemin
 * 
 * @author dgrandemange
 * 
 */
public class GraphReducerImpl implements IGraphReducer {

	class Counter {
		private int counter;

		public int inc() {
			counter = counter + 1;
			return counter;
		}
	}

	class SpecialTransition extends Transition {
		public int hashCode() {
			// On choisit les deux nombres impairs
			int result = 7;
			final int multiplier = 17;

			// Pour chaque attribut, on calcule le hashcode
			// que l'on ajoute au résultat après l'avoir multiplié
			// par le nombre "multiplieur" :
			result = multiplier * result + getSource().hashCode();
			result = multiplier * result + getTarget().hashCode();

			// On retourne le résultat :
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final Transition other = (Transition) obj;

			if (getSource() == null) {
				if (other.getSource() != null) {
					return false;
				}
			} else if (!(getSource().equals(other.getSource()))) {
				return false;
			}

			if (getTarget() == null) {
				if (other.getTarget() != null) {
					return false;
				}
			} else if (!(getTarget().equals(other.getTarget()))) {
				return false;
			}

			return true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jpos.jposext.jposworkflow.service.IGraphReducer#reduce(org.jpos.jposext
	 * .jposworkflow.model.Graph)
	 */
	public Graph reduce(Graph graph) {
		Map<String, Integer> lstTypeIdByType = new HashMap<String, Integer>();
		Map<Integer, Map<List<Integer>, List<Node>>> lstNodesSharingSameCumulTypes = new TreeMap<Integer, Map<List<Integer>, List<Node>>>(
				new Comparator<Integer>() {

					/**
					 * @param arg0
					 * @param arg1
					 * @return
					 */
					public int compare(Integer arg0, Integer arg1) {
						return arg1 - arg0;
					}
				});

		classifyNodesByPathInversedTraversal(graph, graph.getFinalNode(),
				new ArrayList<Integer>(), lstNodesSharingSameCumulTypes,
				lstTypeIdByType, new Counter(), 0);

		for (Entry<Integer, Map<List<Integer>, List<Node>>> entry : lstNodesSharingSameCumulTypes
				.entrySet()) {
			for (Entry<List<Integer>, List<Node>> entry2 : (entry.getValue())
					.entrySet()) {
				List<Node> lstNodes = entry2.getValue();
				if (lstNodes.size() > 1) {
					Node referend = lstNodes.get(0);
					for (int nodeIdx = 1; nodeIdx < lstNodes.size(); nodeIdx++) {
						Node currentNode = lstNodes.get(nodeIdx);
						for (Transition t : currentNode
								.getLstTransitionsAsDest()) {
							t.setTarget(referend);
							referend.addAsDestInTransition(t);
						}
						for (Transition t : currentNode
								.getLstTransitionsAsSource()) {
							t.setSource(referend);
							referend.addAsSourceInTransition(t);
						}
					}
				}
			}
		}

		Map<SpecialTransition, SpecialTransition> uniqTransitionsMap = new HashMap<SpecialTransition, SpecialTransition>();
		for (Transition t : graph.getLstTransitions()) {
			SpecialTransition tInter = new SpecialTransition();

			tInter.setId(t.getId());
			tInter.setSource(t.getSource());
			tInter.setTarget(t.getTarget());
			tInter.setName(t.getName());
			tInter.setDesc(t.getDesc());

			uniqTransitionsMap.put(tInter, tInter);
		}

		List<Transition> uniqTransitionsList = new ArrayList<Transition>();
		for (Entry<SpecialTransition, SpecialTransition> entry : uniqTransitionsMap
				.entrySet()) {
			Transition t = entry.getKey();

			Transition tInter = new Transition();

			tInter.setId(t.getId());
			tInter.setSource(t.getSource());
			tInter.setTarget(t.getTarget());
			tInter.setName(t.getName());
			tInter.setDesc(t.getDesc());

			uniqTransitionsList.add(tInter);
		}

		graph.setLstTransitions(uniqTransitionsList);

		GraphHelper.recomputeNodesTransitions(graph);
		
		return graph;
	}

	protected void classifyNodesByPathInversedTraversal(
			Graph graph,
			Node lastNode,
			List<Integer> cumulativeTypes,
			Map<Integer, Map<List<Integer>, List<Node>>> lstNodesSharingSameCumulTypes,
			Map<String, Integer> lstTypeIdByType, Counter typeCounter, int level) {

		for (Transition t : lastNode.getLstTransitionsAsDest()) {
			Node source = t.getSource();

			String type = source.getType();
			int typeId;
			if (lstTypeIdByType.containsKey(type)) {
				typeId = lstTypeIdByType.get(type).intValue();
			} else {
				typeId = typeCounter.inc();
				lstTypeIdByType.put(type, new Integer(typeId));
			}

			List<Integer> cumulativeTypeInter = new ArrayList<Integer>(
					cumulativeTypes);
			cumulativeTypeInter.add(new Integer(typeId));

			List<Node> lstNodes;
			if (!lstNodesSharingSameCumulTypes.containsKey(level)) {
				lstNodesSharingSameCumulTypes.put(level,
						new HashMap<List<Integer>, List<Node>>());
			}

			if (lstNodesSharingSameCumulTypes.get(level).containsKey(
					cumulativeTypeInter)) {
				lstNodes = lstNodesSharingSameCumulTypes.get(level).get(
						cumulativeTypeInter);
				if (!(lstNodes.contains(source))) {
					lstNodes.add(source);
				}
			} else {
				lstNodes = new ArrayList<Node>();
				lstNodesSharingSameCumulTypes.get(level).put(
						cumulativeTypeInter, lstNodes);
				lstNodes.add(source);
			}

			classifyNodesByPathInversedTraversal(graph, source,
					cumulativeTypeInter, lstNodesSharingSameCumulTypes,
					lstTypeIdByType, typeCounter, level + 1);
		}
	}

}
