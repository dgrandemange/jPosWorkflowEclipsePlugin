package org.jpos.jposext.jposworkflow.eclipse;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.jpos.jposext.jposworkflow.model.Graph;

/**
 * @author dgrandemange
 *
 */
public class MyEditorInput implements IEditorInput {

	public String name = null;

	private Graph graph;
	
	private IProject project;

	public MyEditorInput(String name,
	Graph graph) {
		this.name = name;
		this.graph = graph;
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
		return null;
	}

	public String getName() {
		return name;
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getToolTipText() {
		return name;
	}

	public Object getAdapter(Class adapter) {
		return null;
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

	/**
	 * @return the graph
	 */
	public Graph getGraph() {
		return graph;
	}

}
