package org.jpos.jposext.jposworkflow.service.support;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jpos.jposext.jposworkflow.model.Graph;
import org.jpos.jposext.jposworkflow.model.Node;
import org.jpos.jposext.jposworkflow.model.NodeNatureEnum;
import org.jpos.jposext.jposworkflow.model.ParticipantInfo;
import org.jpos.jposext.jposworkflow.model.Transition;
import org.jpos.jposext.jposworkflow.service.IGraphConverterService;

/**
 * @author dgrandemange
 *
 */
public class GraphConverterServiceImpl implements IGraphConverterService {

	/* (non-Javadoc)
	 * @see org.jpos.jposext.jposworkflow.service.IGraphConverterService#convertGraphToDOT(java.lang.String, org.jpos.jposext.jposworkflow.model.Graph, java.io.PrintWriter)
	 */	
	public void convertGraphToDOT(String name, Graph graph, PrintWriter pw) {
		pw.println(String.format("digraph \"%s\" {", name));
		Map<String, Node> nodes = new HashMap<String, Node>();

		for (Transition t : graph.getLstTransitions()) {
			Node srcNode = t.getSource();
			Node destNode = t.getTarget();

			nodes.put(srcNode.getId(), srcNode);
			nodes.put(destNode.getId(), destNode);
		}

		pw.println("node [shape=\"box\" peripheries=\"1\" style=\"filled\" color=\"#000000\" fillcolor=\"#FFFFCE\" fontname=\"Arial\" fontsize=\"10\"]");
		for (Entry<String, Node> entry : nodes.entrySet()) {
			Node node = entry.getValue();
			if (NodeNatureEnum.INITIAL.equals(node.getNodeNature())) {
				pw
						.println(String
								.format(
										"%s [label=\"\" shape=\"circle\" peripheries=\"1\" style=\"filled\" color=\"#000000\" fillcolor=\"#000000\"]",
										node.getId()));
			} else if (NodeNatureEnum.FINAL.equals(node.getNodeNature())) {
				pw
						.println(String
								.format(
										"%s [label=\"\" shape=\"circle\" peripheries=\"2\" style=\"filled\" color=\"#000000\" fillcolor=\"#000000\"]",
										node.getId()));
			} else {
				pw.println(String.format("%s [label=\"%s\"]", node.getId(),
						getLabelFromNodeData(node)));
			}
		}

		pw.println("edge [fontname=\"Arial\" fontsize=\"8\" dir=\"forward\" arrowhead=\"normal\"]");
		for (Transition t : graph.getLstTransitions()) {
			Node srcNode = t.getSource();
			Node destNode = t.getTarget();

			pw
					.println(String
							.format(
									"%s -> %s [label=\"%s\"]",
									srcNode.getId(), destNode.getId(), t
											.getDesc()));
		}

		pw.println(String.format("}", name));
		pw.flush();
	}

	public static String getLabelFromNodeData(Node node) {
		if (NodeNatureEnum.COMMON.equals(node.getNodeNature())) {
			return getLabelFromParticipantInfo(node.getParticipant());
		} else if (NodeNatureEnum.INITIAL.equals(node.getNodeNature())) {
			return "Inital state";
		} else if (NodeNatureEnum.FINAL.equals(node.getNodeNature())) {
			return "Final state";
		} else {
			return "";
		}
	}

	public static String getLabelFromParticipantInfo(ParticipantInfo pInfo) {
		String label;
		if (null != pInfo) {
			String className = pInfo.getClazz();

			int idx = className.lastIndexOf(".");
			String simpleClassName;
			if ((idx > -1) && (idx + 1 < className.length())) {
				simpleClassName = className.substring(idx + 1);
			} else {
				simpleClassName = className;
			}

			if (isUndefined(pInfo)) {
				label = String.format("%s:%s", getGroupName(pInfo),
						simpleClassName);
			} else {
				label = simpleClassName;
			}
		} else {
			label = "";
		}

		return label;
	}

	public static String getGroupName(ParticipantInfo pInfo) {
		String res = "";

		if (null != pInfo) {
			String groupName = pInfo.getGroupName();
			if (null != groupName) {
				res = groupName.trim();
			}
		}
		return res;
	}

	public static boolean isUndefined(ParticipantInfo pInfo) {
		boolean res = false;

		if (null != pInfo) {
			res = ParticipantInfo.UNDEFINED_CLAZZ.equals(pInfo.getClazz());
		}
		return res;
	}

}
