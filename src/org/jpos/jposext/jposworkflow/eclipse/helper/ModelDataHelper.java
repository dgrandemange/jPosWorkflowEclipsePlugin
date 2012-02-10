package org.jpos.jposext.jposworkflow.eclipse.helper;

import org.jpos.jposext.jposworkflow.eclipse.model.NodeDataWrapper;
import org.jpos.jposext.jposworkflow.model.NodeNatureEnum;
import org.jpos.jposext.jposworkflow.model.ParticipantInfo;



public class ModelDataHelper {
	public static String getLabelFromNodeData(Object data) {
		NodeDataWrapper dataWrapper = (NodeDataWrapper) data;
		ParticipantInfo pInfo = dataWrapper.getpInfo();
		String className = pInfo.getClazz();
		int idx = className.lastIndexOf(".");
		String simpleClassName;
		if ((idx > -1) && (idx + 1 < className.length())) {
			simpleClassName = className.substring(idx + 1);
		} else {
			simpleClassName = className;
		}
		String label;
		if (isUndefined(data)) {
			label = String.format("%s:%s", getGroupName(data), simpleClassName);
		} else {
			label = simpleClassName;
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
}
