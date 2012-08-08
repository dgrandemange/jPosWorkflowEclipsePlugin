package org.jpos.jposext.jposworkflow.model;

/**
 * @author dgrandemange
 *
 */
public class SelectCriterion {
	private String name;
	
	private String value;

	private String desc;
	
	public SelectCriterion(String name, String value, String desc) {
		super();
		this.name = name;
		this.value = value;
		this.desc = desc;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
}
