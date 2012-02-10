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
package org.jpos.jposext.jposworkflow.eclipse.editpart;

import org.eclipse.core.resources.IProject;
import org.eclipse.draw2d.graph.DirectedGraph;
import org.eclipse.draw2d.graph.Edge;
import org.eclipse.draw2d.graph.Node;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

/**
 * @author dgrandemange
 *
 */
public class AppEditPartFactory implements EditPartFactory {

    private IProject project;
    
    /* (non-Javadoc)
     * @see org.eclipse.gef.EditPartFactory#createEditPart(org.eclipse.gef.EditPart, java.lang.Object)
     */
    public EditPart createEditPart(EditPart editPart, Object modelObject) {
        AbstractGraphicalEditPart part = null;

        if (modelObject instanceof DirectedGraph) {
            part = new DirectedGraphPart(project);
        } else if (modelObject instanceof Node) {
            part = new NodePart();
        } else if (modelObject instanceof Edge) {
            part = new EdgePart();
        }

        part.setModel(modelObject);
        return part;
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
