package org.jpos.jposext.jposworkflow.eclipse.helper;

import org.jpos.jposext.jposworkflow.eclipse.model.NodeDataWrapper;
import org.jpos.jposext.jposworkflow.model.NodeNatureEnum;
import org.jpos.jposext.jposworkflow.model.ParticipantInfo;
import org.jpos.jposext.jposworkflow.model.SubFlowInfo;

/**
 * @author dgrandemange
 * 
 */
public class ModelDataHelper {
	public static String getLabelFromNodeData(Object data) {
		String label;
		
		NodeDataWrapper dataWrapper = (NodeDataWrapper) data;
		ParticipantInfo pInfo = dataWrapper.getpInfo();
		if (isSubFlow(data)) {
			label = pInfo.getGroupName();
		} else {
			String className = pInfo.getClazz();
			int idx = className.lastIndexOf(".");
			String simpleClassName;
			if ((idx > -1) && (idx + 1 < className.length())) {
				simpleClassName = className.substring(idx + 1);
			} else {
				simpleClassName = className;
			}
			if (isUndefined(data)) {
				label = String.format("%s:%s", getGroupName(data),
						simpleClassName);
			} else {
				label = simpleClassName;
			}
		}
		return label;
	}

	public static boolean isGroup(Object data) {
		boolean res = false;
		NodeDataWrapper dataWrapper = (NodeDataWrapper) data;
		ParticipantInfo pInfo = dataWrapper.getpInfo();

		if (null != pInfo) {
			String groupName = pInfo.getGroupName();
			if (null != groupName) {
				res = !("".equals(groupName.trim()));
			}
		}
		return res;
	}

	public static boolean isSubFlow(Object data) {
		boolean res = false;
		NodeDataWrapper dataWrapper = (NodeDataWrapper) data;
		ParticipantInfo pInfo = dataWrapper.getpInfo();

		if (null != pInfo) {
			return (pInfo instanceof SubFlowInfo);
		}
		return res;
	}

	public static boolean isUndefined(Object data) {
		boolean res = false;
		NodeDataWrapper dataWrapper = (NodeDataWrapper) data;
		ParticipantInfo pInfo = dataWrapper.getpInfo();

		if (null != pInfo) {
			res = ParticipantInfo.UNDEFINED_CLAZZ.equals(pInfo.getClazz());
		}
		return res;
	}

	public static String getGroupName(Object data) {
		String res = "";
		NodeDataWrapper dataWrapper = (NodeDataWrapper) data;
		ParticipantInfo pInfo = dataWrapper.getpInfo();
		if (null != pInfo) {
			String groupName = pInfo.getGroupName();
			if (null != groupName) {
				res = groupName.trim();
			}
		}
		return res;
	}

	public static String getClassName(Object data) {
		String res = "";
		NodeDataWrapper dataWrapper = (NodeDataWrapper) data;
		ParticipantInfo pInfo = dataWrapper.getpInfo();
		if (null != pInfo) {
			String className = pInfo.getClazz();
			if (null != className) {
				res = className.trim();
			}
		}
		return res;
	}

	public static NodeNatureEnum getNodeNature(Object data) {
		NodeDataWrapper dataWrapper = (NodeDataWrapper) data;
		return dataWrapper.getNodeNature();
	}

	public static boolean isDynaGroup(Object data) {
		boolean res = false;
		NodeDataWrapper dataWrapper = (NodeDataWrapper) data;
		ParticipantInfo pInfo = dataWrapper.getpInfo();

		if (null != pInfo) {
			String groupName = pInfo.getGroupName();
			if (null != groupName) {
				res = groupName.matches(String.format("^%s_[0-9]*$",
						ParticipantInfo.DYNAGROUP_PREFIXE));
			}
		}
		return res;
	}

	public static ParticipantInfo getWrappedParticipantInfo(Object data) {
		ParticipantInfo pInfo = null;
		if (null != data) {
			NodeDataWrapper dataWrapper = (NodeDataWrapper) data;
			pInfo = dataWrapper.getpInfo();
		}
		return pInfo;
	}
}
