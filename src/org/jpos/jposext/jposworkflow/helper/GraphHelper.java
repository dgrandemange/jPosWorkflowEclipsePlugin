package org.jpos.jposext.jposworkflow.helper;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.jpos.jposext.jposworkflow.model.Graph;
import org.jpos.jposext.jposworkflow.model.Node;
import org.jpos.jposext.jposworkflow.model.ParticipantInfo;
import org.jpos.jposext.jposworkflow.model.Transition;

/**
 * @author dgrandemange
 *
 */
public class GraphHelper {

	public static void recomputeNodesTransitions(Graph graph) {
		List<Transition> lstTransitions = graph.getLstTransitions();

		// First reset nodes source/dest transitions list
		for (Transition t : lstTransitions) {
			t.getSource().setLstTransitionsAsDest(new ArrayList<Transition>());
			t.getSource()
					.setLstTransitionsAsSource(new ArrayList<Transition>());
			t.getTarget().setLstTransitionsAsDest(new ArrayList<Transition>());
			t.getTarget()
					.setLstTransitionsAsSource(new ArrayList<Transition>());
		}

		// Then recompute them
		for (Transition t : lstTransitions) {
			List<Transition> lstTransitionsAsSource = t.getSource()
					.getLstTransitionsAsSource();
			if (!lstTransitionsAsSource.contains(t)) {
				lstTransitionsAsSource.add(t);
			}
			
			List<Transition> lstTransitionsAsDest = t.getTarget().getLstTransitionsAsDest();
			if (!lstTransitionsAsDest.contains(t)) {
				lstTransitionsAsDest.add(t);
			}
		}
	}

	public static void dumpGraph(Graph graph, PrintWriter pw) {
		pw.println("==========");
		for (Transition t : graph.getLstTransitions()) {
			Node source = t.getSource();
			ParticipantInfo srcParticipant = source.getParticipant();
			String srcGroupName = (null != srcParticipant) ? srcParticipant.getGroupName() : null;
			
			Node target = t.getTarget();
			ParticipantInfo tgtParticipant = target.getParticipant();
			String tgtGroupName = (null != tgtParticipant) ? tgtParticipant.getGroupName() : null;
			
			pw.println(String.format("Tx [%s] / Source [%s] (%s) -- %s --> Target [%s] (%s)", t.getId(), source.getId(), srcGroupName, t.getDesc(), target.getId(), tgtGroupName));
		}
		pw.println("==========");
		pw.flush();
	}
	
}
