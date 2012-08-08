package org.jpos.jposext.jposworkflow.eclipse.editpart;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.graph.DirectedGraph;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.jpos.jposext.jposworkflow.eclipse.figure.DirectedGraphFigure;
import org.jpos.jposext.jposworkflow.eclipse.policy.ExportAsDOTPolicy;
import org.jpos.jposext.jposworkflow.eclipse.policy.ExportAsImagePolicy;

/**
 * @author dgrandemange
 *
 */
public class DirectedGraphPart extends AbstractGraphicalEditPart {

    IProject project;
    
    /**
     * @param project
     */
    public DirectedGraphPart(IProject project) {
        super();
        this.project = project;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
     */
    @Override
    protected IFigure createFigure() {
        IFigure figure = new DirectedGraphFigure();
        return figure;
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
     */
    @Override
    protected void createEditPolicies() {
    	installEditPolicy("EDIT_POLICY__EXPORT_AS_IMAGE", new ExportAsImagePolicy());
    	installEditPolicy("EDIT_POLICY__EXPORT_AS_DOT", new ExportAsDOTPolicy());
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
     */
    @Override
    protected void refreshVisuals() {
        DirectedGraphFigure figure = (DirectedGraphFigure) getFigure();

        figure.setId("jPos Workflow");
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Object> getModelChildren() {        
        DirectedGraph graph = (DirectedGraph) getModel();
        List<Object> list = new ArrayList<Object>();
        list.addAll(graph.nodes);        
        return list;
    }

    /**
     * @return the project
     */
    public IProject getProject() {
        return project;
    }

    /**
     * @param project the project to set
     */
    public void setProject(IProject project) {
        this.project = project;
    }

}
