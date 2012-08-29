package org.jpos.jposext.jposworkflow.model;

import java.util.List;
import java.util.Map;

/**
 * @author dgrandemange
 * 
 */
public class ParticipantInfo {

	public static final String UNDEFINED_CLAZZ = "<undefined>";

	public static final String DYNAGROUP_PREFIXE = "DYNAGROUP";

	private String clazz;

	private String groupName;

	private Map<String, SelectCriterion> selectCriteria;

	private List<String> guaranteedCtxAttributes;

	private List<String> optionalCtxAttributes;

	/**
	 * Updated context attributes by transition id
	 */
	private Map<String, String[]> updCtxAttrByTransId;

	public ParticipantInfo() {
		super();
	}

	public ParticipantInfo(String clazz, String groupName,
			Map<String, SelectCriterion> selectCriteria) {
		super();
		this.groupName = groupName;
		this.clazz = clazz;
		this.selectCriteria = selectCriteria;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public boolean isGroup() {
		if (null == selectCriteria) {
			return false;
		}
		return (selectCriteria.size() > 0);
	}

	public Map<String, SelectCriterion> getSelectCriteria() {
		return selectCriteria;
	}

	public void setSelectCriteria(Map<String, SelectCriterion> selectCriteria) {
		this.selectCriteria = selectCriteria;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	/**
	 * @return the updCtxAttrByTransId
	 */
	public Map<String, String[]> getUpdCtxAttrByTransId() {
		return updCtxAttrByTransId;
	}

	/**
	 * @param updCtxAttrByTransId
	 *            the updCtxAttrByTransId to set
	 */
	public void setUpdCtxAttrByTransId(Map<String, String[]> updCtxAttrByTransId) {
		this.updCtxAttrByTransId = updCtxAttrByTransId;
	}

	/**
	 * @return the guaranteedCtxAttributes
	 */
	public List<String> getGuaranteedCtxAttributes() {
		return guaranteedCtxAttributes;
	}

	/**
	 * @param guaranteedCtxAttributes
	 *            the guaranteedCtxAttributes to set
	 */
	public void setGuaranteedCtxAttributes(List<String> guaranteedCtxAttributes) {
		this.guaranteedCtxAttributes = guaranteedCtxAttributes;
	}

	/**
	 * @return the optionalCtxAttributes
	 */
	public List<String> getOptionalCtxAttributes() {
		return optionalCtxAttributes;
	}

	/**
	 * @param optionalCtxAttributes
	 *            the optionalCtxAttributes to set
	 */
	public void setOptionalCtxAttributes(List<String> optionalCtxAttributes) {
		this.optionalCtxAttributes = optionalCtxAttributes;
	}

}
