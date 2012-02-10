/*
 * Created on 17 juil. 08 by dgrandemange
 *
 * Copyright (c) 2005 Setib
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Setib ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Setib.
 */
package org.jpos.jposext.jposworkflow.eclipse;

import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.jpos.jposext.jposworkflow.model.ParticipantInfo;


public class MyEditorInput implements IEditorInput {

	public String name = null;

	private Map<String, List<ParticipantInfo>> jposTxnMgrGroups;

	private IProject project;

	public MyEditorInput(String name,
			Map<String, List<ParticipantInfo>> jposTxnMgrGroups) {
		this.name = name;
		this.jposTxnMgrGroups = jposTxnMgrGroups;
	}

	public boolean equals(Object o) {
		if (!(o instanceof MyEditorInput))
			return false;
		return ((MyEditorInput) o).getName().equals(getName());
	}

	public boolean exists() {
		return (name != null);
	}

	public ImageDescriptor getImageDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		return name;
	}

	public IPersistableElement getPersistable() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getToolTipText() {
		return name;
	}

	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return the springContext
	 */
	public Map<String, List<ParticipantInfo>> getJPosTxnMgrGroups() {
		return jposTxnMgrGroups;
	}

	/**
	 * @return the project
	 */
	public IProject getProject() {
		return project;
	}

	/**
	 * @param project
	 *            the project to set
	 */
	public void setProject(IProject project) {
		this.project = project;
	}

}
