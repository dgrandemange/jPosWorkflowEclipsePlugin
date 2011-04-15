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

import java.util.ArrayList;
import java.util.List;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.graph.Node;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.jpos.jposext.jposworkflow.eclipse.figure.FinalNodeFigure;
import org.jpos.jposext.jposworkflow.eclipse.figure.InitialNodeFigure;
import org.jpos.jposext.jposworkflow.eclipse.figure.NodeFigure;
import org.jpos.jposext.jposworkflow.eclipse.helper.ModelDataHelper;
import org.jpos.jposext.jposworkflow.eclipse.model.NodeDataWrapper;
import org.jpos.jposext.jposworkflow.model.NodeNatureEnum;
import org.jpos.jposext.jposworkflow.model.ParticipantInfo;


public class NodePart extends AbstractGraphicalEditPart implements NodeEditPart {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		Node model = (Node) getModel();
		NodeNatureEnum nodeNature = ModelDataHelper.getNodeNature(model.data);
		IFigure figure;

		if (NodeNatureEnum.INITIAL.equals(nodeNature)) {
			ImageFigure imgFigure = new InitialNodeFigure();
			ImageData imgData = new ImageData(this.getClass()
					.getResourceAsStream("/org/jpos/jposext/jposworkflow/eclipse/res/img/initial-state.gif"));
			Image img = new Image(Display.getCurrent(), imgData);
			imgFigure.setImage(img);
			figure = imgFigure;
		} else if (NodeNatureEnum.FINAL.equals(nodeNature)) {
			ImageFigure imgFigure = new FinalNodeFigure();
			ImageData imgData = new ImageData(this.getClass()
					.getResourceAsStream("/org/jpos/jposext/jposworkflow/eclipse/res/img/final-state.gif"));
			Image img = new Image(Display.getCurrent(), imgData);
			imgFigure.setImage(img);
			figure = imgFigure;
		} else {
			figure = new NodeFigure();
		}

		return figure;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		// TODO Auto-generated method stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelSourceConnections
	 * ()
	 */
	@Override
	protected List getModelSourceConnections() {
		Node model = (Node) getModel();
		return model.outgoing;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelTargetConnections
	 * ()
	 */
	@Override
	protected List getModelTargetConnections() {
		Node model = (Node) getModel();
		return model.incoming;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	protected void refreshVisuals() {
		IFigure figure = this.getFigure();
		Node model = (Node) getModel();

		if (figure instanceof NodeFigure) {
			NodeFigure nodeFigure = (NodeFigure) figure;
			Font classFont = new Font(null, "Arial", 10, SWT.BOLD);
			Label labelId;

			String undef = ModelDataHelper.isUndefined(model.data) ? "-undef"
					: "";

			if (ModelDataHelper.isGroup(model.data)
					&& (!(ModelDataHelper.isDynaGroup(model.data)))) {
				ImageData imgData = new ImageData(this.getClass()
						.getResourceAsStream(
								String.format("/org/jpos/jposext/jposworkflow/eclipse/res/img/group%s.png", undef)));
				Image img = new Image(Display.getCurrent(), imgData);
				labelId = new Label(ModelDataHelper
						.getLabelFromNodeData(model.data), img);

				String groupName = ModelDataHelper.getGroupName(model.data);
				String className = ModelDataHelper.getClassName(model.data);
				Label tipLabel = new Label(String.format(
						"Participant : group=%s, class=%s", groupName,
						className));
				nodeFigure.setToolTip(tipLabel);
			} else {
				ImageData imgData = new ImageData(this.getClass()
						.getResourceAsStream(
								String.format("/org/jpos/jposext/jposworkflow/eclipse/res/img/participant%s.png",
										undef)));
				Image img = new Image(Display.getCurrent(), imgData);

				labelId = new Label(ModelDataHelper
						.getLabelFromNodeData(model.data), img);
				String className = ModelDataHelper.getClassName(model.data);
				Label tipLabel = new Label(String.format(
						"Participant : class=%s", className));
				nodeFigure.setToolTip(tipLabel);
			}

			labelId.setFont(classFont);

			nodeFigure.add(labelId);
		}

		Rectangle r = new Rectangle(model.x, model.y, -1, -1);

		((GraphicalEditPart) getParent()).setLayoutConstraint(this, figure, r);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
	 */
	public List<Object> getModelChildren() {
		return new ArrayList<Object>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef
	 * .ConnectionEditPart)
	 */
	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart arg0) {
		return new ChopboxAnchor(getFigure());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef
	 * .Request)
	 */
	public ConnectionAnchor getSourceConnectionAnchor(Request arg0) {
		return new ChopboxAnchor(getFigure());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef
	 * .ConnectionEditPart)
	 */
	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart arg0) {
		return new ChopboxAnchor(getFigure());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef
	 * .Request)
	 */
	public ConnectionAnchor getTargetConnectionAnchor(Request arg0) {
		return new ChopboxAnchor(getFigure());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.editparts.AbstractEditPart#performRequest(org.eclipse
	 * .gef.Request)
	 */
	@Override
	public void performRequest(Request req) {
		/*
		MessageBox msgBox = new MessageBox(new Shell(), SWT.OK
				| SWT.ICON_INFORMATION);
		msgBox.setMessage(req.getType().toString());
		msgBox.open();		
		*/
		
		if (req.getType().equals(RequestConstants.REQ_OPEN)) {

			try {
				IWorkbenchWindow window = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow();
				IWorkbenchPage page = window.getActivePage();

				NodeDataWrapper ndw = (NodeDataWrapper) ((Node) this
						.getModel()).data;				

				if (!(NodeNatureEnum.COMMON.equals(ndw.getNodeNature()))) {
					return;
				}
				
				ParticipantInfo pInfo = ndw.getpInfo();
				String className = pInfo.getClazz();

				String javaClassPathFromFullClassName = getJavaClassPathFromFullClassName(className);
				IPath path = new Path(javaClassPathFromFullClassName);

				if (null == path) {
					return;
				}
				
				IProject project = ((DirectedGraphPart) getParent())
						.getProject();
				
				if (null == project) {
					return;
				}
				
				IJavaProject jProject = (IJavaProject) project
						.getNature(JavaCore.NATURE_ID);
				IJavaElement jElt = jProject.findElement(path);

				IPath path2 = jElt.getPath();

				IFile fileToBeOpened = project.getWorkspace().getRoot()
						.getFile(path2);

				if (null == fileToBeOpened) {
					return;
				}											
				
				IEditorInput editorInput = new FileEditorInput(fileToBeOpened);
				IEditorDescriptor desc = PlatformUI.getWorkbench()
						.getEditorRegistry().getDefaultEditor(
								fileToBeOpened.getName());

				page.openEditor(editorInput, desc.getId());
			} catch (CoreException e) {
				e.printStackTrace();
			}

		}
	}

	final protected String getJavaClassPathFromFullClassName(String className) {
		return className.replaceAll("\\.", "/") + ".java";
	}

}
