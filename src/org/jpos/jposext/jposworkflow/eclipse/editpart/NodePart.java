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
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.jpos.jposext.jposworkflow.eclipse.MyEditorInput;
import org.jpos.jposext.jposworkflow.eclipse.figure.FinalNodeFigure;
import org.jpos.jposext.jposworkflow.eclipse.figure.InitialNodeFigure;
import org.jpos.jposext.jposworkflow.eclipse.figure.NodeFigure;
import org.jpos.jposext.jposworkflow.eclipse.figure.NodeInfoFigure;
import org.jpos.jposext.jposworkflow.eclipse.helper.ModelDataHelper;
import org.jpos.jposext.jposworkflow.eclipse.model.NodeDataWrapper;
import org.jpos.jposext.jposworkflow.model.NodeNatureEnum;
import org.jpos.jposext.jposworkflow.model.ParticipantInfo;

/**
 * @author dgrandemange
 * 
 */
public class NodePart extends AbstractGraphicalEditPart implements NodeEditPart {

	private static final ImageData IMAGE_DATA__DEFINED_PARTICIPANT_ICON = new ImageData(
			NodePart.class.getResourceAsStream("/org/jpos/jposext/jposworkflow/eclipse/res/img/participant.png"));

	private static final ImageData IMAGE_DATA__UNDEF_PARTICIPANT_ICON = new ImageData(
			NodePart.class.getResourceAsStream("/org/jpos/jposext/jposworkflow/eclipse/res/img/participant-undef.png"));
	
	private static final ImageData IMAGE_DATA__DEFINED_GROUP_ICON = new ImageData(
			NodePart.class
					.getResourceAsStream("/org/jpos/jposext/jposworkflow/eclipse/res/img/group.png"));

	private static final ImageData IMAGE_DATA__UNDEF_GROUP_ICON = new ImageData(
			NodePart.class
					.getResourceAsStream("/org/jpos/jposext/jposworkflow/eclipse/res/img/group-undef.png"));
	
	private static final ImageData IMAGE_DATA__CTX_ATTR_OPTIONAL_ICON = new ImageData(
			NodePart.class
					.getResourceAsStream("/org/jpos/jposext/jposworkflow/eclipse/res/img/ctx-attr-optional.png"));
	
	private static final ImageData IMAGE_DATA__CTX_ATTR_GUARANTEED_ICON = new ImageData(
			NodePart.class
					.getResourceAsStream("/org/jpos/jposext/jposworkflow/eclipse/res/img/ctx-attr-guaranteed.png"));
	
	private static final ImageData IMAGE_DATA__FINAL_STATE_ICON = new ImageData(
			NodePart.class
					.getResourceAsStream("/org/jpos/jposext/jposworkflow/eclipse/res/img/final-state.gif"));
	
	private static final ImageData IMAGE_DATA__INITIAL_STATE_ICON = new ImageData(
			NodePart.class
					.getResourceAsStream("/org/jpos/jposext/jposworkflow/eclipse/res/img/initial-state.gif"));

	@Override
	protected IFigure createFigure() {
		Node model = (Node) getModel();
		NodeNatureEnum nodeNature = ModelDataHelper.getNodeNature(model.data);
		IFigure figure;

		if (NodeNatureEnum.INITIAL.equals(nodeNature)) {
			ImageFigure imgFigure = new InitialNodeFigure();
			Image img = new Image(Display.getCurrent(),
					IMAGE_DATA__INITIAL_STATE_ICON);
			imgFigure.setImage(img);
			figure = imgFigure;
		} else if (NodeNatureEnum.FINAL.equals(nodeNature)) {
			ImageFigure imgFigure = new FinalNodeFigure();
			Image img = new Image(Display.getCurrent(),
					IMAGE_DATA__FINAL_STATE_ICON);
			imgFigure.setImage(img);
			figure = imgFigure;
		} else {
			figure = new NodeFigure();
		}

		return figure;
	}

	@Override
	protected void createEditPolicies() {
	}

	@Override
	protected List getModelSourceConnections() {
		Node model = (Node) getModel();
		return model.outgoing;
	}

	@Override
	protected List getModelTargetConnections() {
		Node model = (Node) getModel();
		return model.incoming;
	}

	@Override
	protected void refreshVisuals() {
		IFigure figure = this.getFigure();
		Node model = (Node) getModel();

		if (figure instanceof NodeFigure) {
			NodeFigure nodeFigure = (NodeFigure) figure;
			Font labelIdFont = new Font(null, "Arial", 10, SWT.BOLD);
			Font classFont = new Font(null, "Arial", 10, SWT.ITALIC);

			Label labelId;
			Image img;

			NodeInfoFigure nodeInfoFigure;
			if (ModelDataHelper.isGroup(model.data)
					&& (!(ModelDataHelper.isDynaGroup(model.data)))) {
				nodeInfoFigure = new NodeInfoFigure(true);

				ImageData imgData;
				if (ModelDataHelper.isUndefined(model.data)) {
					imgData = IMAGE_DATA__UNDEF_GROUP_ICON;					
				}
				else {
					imgData = IMAGE_DATA__DEFINED_GROUP_ICON;
				}
				
				img = new Image(Display.getCurrent(), imgData);
				labelId = new Label(
						ModelDataHelper.getLabelFromNodeData(model.data), img);

				String groupName = ModelDataHelper.getGroupName(model.data);
				nodeInfoFigure.getGroupLabel().setText(
						String.format("group [%s]", groupName));
				nodeInfoFigure.getGroupLabel().setFont(labelIdFont);
			} else {
				nodeInfoFigure = new NodeInfoFigure(false);

				ImageData imgData;
				if (ModelDataHelper.isUndefined(model.data)) {
					imgData = IMAGE_DATA__UNDEF_PARTICIPANT_ICON;					
				}
				else {
					imgData = IMAGE_DATA__DEFINED_PARTICIPANT_ICON;
				}				
				
				img = new Image(Display.getCurrent(), imgData);

				labelId = new Label(
						ModelDataHelper.getLabelFromNodeData(model.data), img);
			}
			String className = ModelDataHelper.getClassName(model.data);
			nodeInfoFigure.getClassLabel().setText(className);
			nodeInfoFigure.getClassLabel().setFont(classFont);

			nodeInfoFigure.getName().setText(labelId.getText());
			nodeInfoFigure.getName().setIcon(img);
			nodeInfoFigure.getName().setFont(labelIdFont);

			ParticipantInfo pInfo = ModelDataHelper
					.getWrappedParticipantInfo(model.data);

			Image ctxAttrGuaranteedImg = new Image(Display.getCurrent(),
					IMAGE_DATA__CTX_ATTR_GUARANTEED_ICON);
			if (null != pInfo.getGuaranteedCtxAttributes()
					&& pInfo.getGuaranteedCtxAttributes().size() > 0) {
				for (String guaranteedCtxAttr : pInfo
						.getGuaranteedCtxAttributes()) {
					nodeInfoFigure.getGuaranteedAttrsFigureCompartment().add(
							new Label(guaranteedCtxAttr, ctxAttrGuaranteedImg));
				}
			} else {
				Font nonGuranteedAttrLabelFont = new Font(null, "Arial", 10,
						SWT.ITALIC);
				Label nonGuranteedAttrlabel = new Label(
						"<no guaranteed attributes>", ctxAttrGuaranteedImg);
				nonGuranteedAttrlabel.setFont(nonGuranteedAttrLabelFont);
				nodeInfoFigure.getGuaranteedAttrsFigureCompartment().add(
						nonGuranteedAttrlabel);
			}

			Image ctxAttrOptionalImg = new Image(Display.getCurrent(),
					IMAGE_DATA__CTX_ATTR_OPTIONAL_ICON);
			if (null != pInfo.getOptionalCtxAttributes()
					&& pInfo.getOptionalCtxAttributes().size() > 0) {
				for (String optionalCtxAttr : pInfo.getOptionalCtxAttributes()) {
					nodeInfoFigure.getOptionalAttrsFigureCompartment().add(
							new Label(optionalCtxAttr, ctxAttrOptionalImg));
				}
			} else {
				Font noOptionalAttrlabelFont = new Font(null, "Arial", 10,
						SWT.ITALIC);
				Label noOptionalAttrlabel = new Label(
						"<no optional attributes>", ctxAttrOptionalImg);
				noOptionalAttrlabel.setFont(noOptionalAttrlabelFont);
				nodeInfoFigure.getOptionalAttrsFigureCompartment().add(
						noOptionalAttrlabel);
			}

			nodeFigure.setToolTip(nodeInfoFigure);

			labelId.setFont(labelIdFont);

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

	@Override
	public void performRequest(Request req) {
		/*
		 * MessageBox msgBox = new MessageBox(new Shell(), SWT.OK |
		 * SWT.ICON_INFORMATION); msgBox.setMessage(req.getType().toString());
		 * msgBox.open();
		 */

		if (req.getType().equals(RequestConstants.REQ_OPEN)) {

			try {
				IWorkbenchWindow window = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow();
				IWorkbenchPage page = window.getActivePage();

				IProject project = ((DirectedGraphPart) getParent())
						.getProject();

				if (null == project) {
					project = getCurrentProject();
					if (null == project) {
						return;
					}
				}

				NodeDataWrapper ndw = (NodeDataWrapper) ((Node) this.getModel()).data;

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

				IJavaProject jProject = (IJavaProject) project
						.getNature(JavaCore.NATURE_ID);
				IJavaElement jElt = jProject.findElement(path);
				IFile fileToBeOpened = null;

				if (null != jElt) {
					IPath path2 = jElt.getPath();

					fileToBeOpened = project.getWorkspace().getRoot()
							.getFile(path2);
				}

				// if (null == fileToBeOpened) {
				// String value = className;
				// value = NodePart.trimNonAlphaChars(value);
				// IJavaProject javaProject = JavaCore.create(project);
				// IPackageFragmentRoot srcEntryDft = null;
				// IPackageFragmentRoot[] roots = javaProject
				// .getPackageFragmentRoots();
				// for (int i = 0; i < roots.length; i++) {
				// if (roots[i].getKind() == IPackageFragmentRoot.K_SOURCE) {
				// srcEntryDft = roots[i];
				// break;
				// }
				// }
				//
				// IPackageFragmentRoot packageRoot;
				//
				// if (srcEntryDft != null)
				// packageRoot = srcEntryDft;
				// else
				// packageRoot = javaProject
				// .getPackageFragmentRoot(javaProject
				// .getResource());
				//
				// String packageNameString = null;
				//					int index = value.lastIndexOf("."); //$NON-NLS-1$
				// if (index == -1) {
				// className = value;
				// } else {
				// className = value.substring(index + 1);
				// packageNameString = value.substring(0, index);
				// }
				// IPackageFragment packageName = null;
				// if (packageNameString != null && packageRoot != null) {
				// IFolder packageFolder = project
				// .getFolder(packageNameString);
				// packageName = packageRoot
				// .getPackageFragment(packageFolder
				// .getProjectRelativePath().toOSString());
				// }
				//
				// NewClassWizardPage wzPage = new NewClassWizardPage();
				// wzPage.init(StructuredSelection.EMPTY);
				// wzPage.setTypeName(className, true);
				// wzPage.setPackageFragmentRoot(packageRoot, true);
				// wzPage.setPackageFragment(packageName, true);
				//
				// Wizard wizard = new Wizard() {
				//
				// @Override
				// public boolean performFinish() {
				// return true;
				// }
				// };
				// wizard.setNeedsProgressMonitor(false);
				// wizard.addPage(wzPage);
				//
				// WizardDialog wd = new WizardDialog(getDisplay()
				// .getActiveShell(), wizard);
				// wd.setTitle(wizard.getWindowTitle());
				// wd.open();
				//
				//
				// return;
				// }

				if (null != fileToBeOpened) {
					IEditorInput editorInput = new FileEditorInput(
							fileToBeOpened);
					IEditorDescriptor desc = PlatformUI.getWorkbench()
							.getEditorRegistry()
							.getDefaultEditor(fileToBeOpened.getName());

					page.openEditor(editorInput, desc.getId());
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}

		}
	}

	final protected String getJavaClassPathFromFullClassName(String className) {
		return className.replaceAll("\\.", "/") + ".java";
	}

	// public void openWizard(String id) {
	// // First see if this is a "new wizard".
	// IWizardDescriptor descriptor = PlatformUI.getWorkbench()
	// .getNewWizardRegistry().findWizard(id);
	// // If not check if it is an "import wizard".
	// if (descriptor == null) {
	// descriptor = PlatformUI.getWorkbench().getImportWizardRegistry()
	// .findWizard(id);
	// }
	// // Or maybe an export wizard
	// if (descriptor == null) {
	// descriptor = PlatformUI.getWorkbench().getExportWizardRegistry()
	// .findWizard(id);
	// }
	// try {
	// // Then if we have a wizard, open it.
	// if (descriptor != null) {
	// IWizard wizard = descriptor.createWizard();
	// WizardDialog wd = new WizardDialog(getDisplay()
	// .getActiveShell(), wizard);
	// wd.setTitle(wizard.getWindowTitle());
	// wd.open();
	// }
	// } catch (CoreException e) {
	// e.printStackTrace();
	// }
	// }

	public static Display getDisplay() {
		Display display = Display.getCurrent();
		// may be null if outside the UI thread
		if (display == null)
			display = Display.getDefault();
		return display;
	}

	public static String trimNonAlphaChars(String value) {
		value = value.trim();
		while (value.length() > 0 && !Character.isLetter(value.charAt(0)))
			value = value.substring(1, value.length());
		int loc = value.indexOf(":"); //$NON-NLS-1$
		if (loc != -1 && loc > 0)
			value = value.substring(0, loc);
		else if (loc == 0)
			value = ""; //$NON-NLS-1$
		return value;
	}

	public IProject getCurrentProject() {
		IEditorPart editorPart = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (editorPart != null) {
			MyEditorInput input = (MyEditorInput) editorPart.getEditorInput();
			IProject activeProject = input.getProject();
			return activeProject;
		}
		return null;
	}

}
