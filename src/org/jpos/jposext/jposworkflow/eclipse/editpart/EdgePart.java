package org.jpos.jposext.jposworkflow.eclipse.editpart;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MidpointLocator;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.graph.Edge;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.jpos.jposext.jposworkflow.eclipse.figure.EdgeFigure;
import org.jpos.jposext.jposworkflow.eclipse.figure.TransitionInfoFigure;
import org.jpos.jposext.jposworkflow.model.Transition;

/**
 * @author dgrandemange
 *
 */
public class EdgePart extends AbstractConnectionEditPart {

	//private IFigure contentPane;
	
	private static final int EGDGE_NOT_SELECTED_WIDTH = 1;
	private static final int EGDGE_SELECTED_WIDTH = 3;
	
	private static final Color DEFAULT_EDGE_COLOR = Color.BLACK;
	private static final Color EDGE_COLOR_ON_MOUSE_HOVER = new Color(0x55,0x55,0xBB);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		return new EdgeFigure();
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

	protected void refreshVisuals() {
		PolylineConnection figure = (PolylineConnection) getFigure();
		
		figure.addMouseMotionListener(new MouseMotionListener() {

			/* (non-Javadoc)
			 * @see org.eclipse.draw2d.MouseMotionListener#mouseDragged(org.eclipse.draw2d.MouseEvent)
			 */			
			public void mouseDragged(MouseEvent evt) {
			}

			/* (non-Javadoc)
			 * @see org.eclipse.draw2d.MouseMotionListener#mouseEntered(org.eclipse.draw2d.MouseEvent)
			 */			
			public void mouseEntered(MouseEvent evt) {
				PolylineConnection figure = (PolylineConnection) evt.getSource();
				Color cl = EDGE_COLOR_ON_MOUSE_HOVER;
				org.eclipse.swt.graphics.Color fg = new org.eclipse.swt.graphics.Color(
						Display.getCurrent(), cl.getRed(), cl.getGreen(), cl.getBlue());
				figure.setForegroundColor(fg);
			}

			/* (non-Javadoc)
			 * @see org.eclipse.draw2d.MouseMotionListener#mouseExited(org.eclipse.draw2d.MouseEvent)
			 */			
			public void mouseExited(MouseEvent evt) {
				PolylineConnection figure = (PolylineConnection) evt.getSource();
				Color cl = DEFAULT_EDGE_COLOR;
				org.eclipse.swt.graphics.Color fg = new org.eclipse.swt.graphics.Color(
						Display.getCurrent(), cl.getRed(), cl.getGreen(), cl.getBlue());
				figure.setForegroundColor(fg);			
			}

			/* (non-Javadoc)
			 * @see org.eclipse.draw2d.MouseMotionListener#mouseHover(org.eclipse.draw2d.MouseEvent)
			 */			
			public void mouseHover(MouseEvent evt) {
			}

			/* (non-Javadoc)
			 * @see org.eclipse.draw2d.MouseMotionListener#mouseMoved(org.eclipse.draw2d.MouseEvent)
			 */			
			public void mouseMoved(MouseEvent evt) {
			}
			
		});
		
		Edge model = (Edge) getModel();
		
		Transition t = (Transition) model.data;
		
		Color cl = DEFAULT_EDGE_COLOR;
			
		// ConnectionEndpointLocator locator = new
		// ConnectionEndpointLocator(figure, true);
		// locator.setUDistance(10);
		// locator.setVDistance(0);

		ImageData imgData = new ImageData(this.getClass()
				.getResourceAsStream("/org/jpos/jposext/jposworkflow/eclipse/res/img/information.png"));
		Image img = new Image(Display.getCurrent(), imgData);

		Label transitionInfoIndicator = new Label("", img);
		MidpointLocator locator = new MidpointLocator(figure, 0);
		figure.add(transitionInfoIndicator, locator);

		ImageData transitionImgData = new ImageData(
				this.getClass()
						.getResourceAsStream("/org/jpos/jposext/jposworkflow/eclipse/res/img/transition.png"));
		Image transitionImg = new Image(Display.getCurrent(), transitionImgData);			
		
		Label transitionLabel = null;
		String transitionName = t.getName();
		if (!("".equals(transitionName))) {
			transitionLabel = new Label(String.format("%s", transitionName), transitionImg);
		} else {
			transitionLabel = new Label(String.format("%s", "<default>"), transitionImg);
		}
		Font transitionLabelFont = new Font(null, "Arial", 10, SWT.BOLD);
		transitionLabel.setFont(transitionLabelFont);
		TransitionInfoFigure transitionInfoFigure = new TransitionInfoFigure(transitionLabel);
		
		String transitionDesc = t.getDesc();
		Label transitionDescLabel;
		if ((null != transitionDesc) && (transitionDesc.trim().length() > 0)) {
			transitionDescLabel = new Label(transitionDesc);
			
		} else {
			transitionDescLabel = new Label("<default>");
		}
		Font transitionDescLabelFont = new Font(null, "Arial", 10, SWT.ITALIC);
		transitionDescLabel.setFont(transitionDescLabelFont);
		transitionInfoFigure.getTransitionDescFigureCompartment().add(transitionDescLabel);
		
		ImageData ctxAttrGuaranteedImgData = new ImageData(
				this.getClass()
						.getResourceAsStream("/org/jpos/jposext/jposworkflow/eclipse/res/img/ctx-attr-guaranteed.png"));
		Image ctxAttrGuaranteedImg = new Image(Display.getCurrent(), ctxAttrGuaranteedImgData);
		if (null != t.getGuaranteedCtxAttributes() && t.getGuaranteedCtxAttributes().size() > 0) {
			for (String guaranteedCtxAttr : t.getGuaranteedCtxAttributes()) {
				transitionInfoFigure.getGuaranteedAttrsFigureCompartment().add(new Label(guaranteedCtxAttr, ctxAttrGuaranteedImg));
			}
		}
		else {
			Font nonGuranteedAttrLabelFont = new Font(null, "Arial", 10, SWT.ITALIC);
			Label nonGuranteedAttrlabel = new Label("<no guaranteed attributes>", ctxAttrGuaranteedImg);
			nonGuranteedAttrlabel.setFont(nonGuranteedAttrLabelFont);
			transitionInfoFigure.getGuaranteedAttrsFigureCompartment().add(nonGuranteedAttrlabel);
		}
		
		ImageData ctxAttrOptionalImgData = new ImageData(
				this.getClass()
						.getResourceAsStream("/org/jpos/jposext/jposworkflow/eclipse/res/img/ctx-attr-optional.png"));
		Image ctxAttrOptionalImg = new Image(Display.getCurrent(), ctxAttrOptionalImgData);		
		if (null != t.getOptionalCtxAttributes() && t.getOptionalCtxAttributes().size() > 0) {
			for (String optionalCtxAttr : t.getOptionalCtxAttributes()) {
				transitionInfoFigure.getOptionalAttrsFigureCompartment().add(new Label(optionalCtxAttr, ctxAttrOptionalImg));
			}
		}
		else {
			Font noOptionalAttrlabelFont = new Font(null, "Arial", 10, SWT.ITALIC);
			Label noOptionalAttrlabel = new Label("<no optional attributes>", ctxAttrOptionalImg);
			noOptionalAttrlabel.setFont(noOptionalAttrlabelFont);
			transitionInfoFigure.getOptionalAttrsFigureCompartment().add(noOptionalAttrlabel);
		}
		
		figure.setToolTip(transitionInfoFigure);
		
		org.eclipse.swt.graphics.Color fg = new org.eclipse.swt.graphics.Color(
				Display.getCurrent(), cl.getRed(), cl.getGreen(), cl.getBlue());
		figure.setForegroundColor(fg);

	}

	public List<Object> getModelChildren() {
		return new ArrayList<Object>();
	}

	@Override
	public void performRequest(Request req) {
		super.performRequest(req);
		//EditPart parent = this.getParent();
		if (req.getType().equals(RequestConstants.REQ_OPEN)) {
			PolylineConnection figure = (PolylineConnection) getFigure();			
			int currentLineWidth = figure.getLineWidth();
			switch (currentLineWidth) {
			case EGDGE_SELECTED_WIDTH:
				figure.setLineWidth(EGDGE_NOT_SELECTED_WIDTH);
				break;
			case EGDGE_NOT_SELECTED_WIDTH:
				figure.setLineWidth(EGDGE_SELECTED_WIDTH);
				break;
			default:
				figure.setLineWidth(EGDGE_NOT_SELECTED_WIDTH);
			}
		}
		
	}

	@Override
	public IFigure getContentPane() {
		//return contentPane;
		return super.getContentPane();
	}
/*
	public void setContentPane(IFigure contentPane) {
		this.contentPane = contentPane;
	}
*/
}
