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
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.graph.Edge;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.jpos.jposext.jposworkflow.eclipse.figure.CompartmentFigure;
import org.jpos.jposext.jposworkflow.eclipse.figure.EdgeFigure;
import org.jpos.jposext.jposworkflow.eclipse.figure.TransitionAttrsCompartmentFigure;
import org.jpos.jposext.jposworkflow.eclipse.figure.TransitionInfoFigure;
import org.jpos.jposext.jposworkflow.model.Graph;
import org.jpos.jposext.jposworkflow.model.Node;
import org.jpos.jposext.jposworkflow.model.ParticipantInfo;
import org.jpos.jposext.jposworkflow.model.SubFlowInfo;
import org.jpos.jposext.jposworkflow.model.Transition;

/**
 * @author dgrandemange
 * 
 */
public class EdgePart extends AbstractConnectionEditPart {

	private static final ImageData IMAGE_DATA__CTX_ATTR_ADDED_ICON = new ImageData(
			EdgePart.class
					.getResourceAsStream("/org/jpos/jposext/jposworkflow/eclipse/res/img/ctx-attr-put.png"));
	private static final ImageData IMAGE_DATA__TRANSISTION_ICON = new ImageData(
			EdgePart.class
					.getResourceAsStream("/org/jpos/jposext/jposworkflow/eclipse/res/img/transition.png"));
	private static final ImageData IMAGE_DATA__INFO_ICON = new ImageData(
			EdgePart.class
					.getResourceAsStream("/org/jpos/jposext/jposworkflow/eclipse/res/img/information.png"));

	private static final ImageData IMAGE_DATA__CTX_ATTR_GUARANTEED_ICON = new ImageData(
			NodePart.class
					.getResourceAsStream("/org/jpos/jposext/jposworkflow/eclipse/res/img/ctx-attr-guaranteed.png"));

	private static final ImageData IMAGE_DATA__CTX_ATTR_OPTIONAL_ICON = new ImageData(
			NodePart.class
					.getResourceAsStream("/org/jpos/jposext/jposworkflow/eclipse/res/img/ctx-attr-optional.png"));

	private static final int EGDGE_NOT_SELECTED_WIDTH = 1;
	private static final int EGDGE_SELECTED_WIDTH = 3;

	private static final Color DEFAULT_EDGE_COLOR = Color.BLACK;
	private static final Color EDGE_COLOR_ON_MOUSE_HOVER = new Color(0x55,
			0x55, 0xBB);

	@Override
	protected IFigure createFigure() {
		return new EdgeFigure();
	}

	@Override
	protected void createEditPolicies() {
	}

	protected void refreshVisuals() {
		PolylineConnection figure = (PolylineConnection) getFigure();

		figure.addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent evt) {
			}

			public void mouseEntered(MouseEvent evt) {
				PolylineConnection figure = (PolylineConnection) evt
						.getSource();
				Color cl = EDGE_COLOR_ON_MOUSE_HOVER;
				org.eclipse.swt.graphics.Color fg = new org.eclipse.swt.graphics.Color(
						Display.getCurrent(), cl.getRed(), cl.getGreen(), cl
								.getBlue());
				figure.setForegroundColor(fg);
			}

			public void mouseExited(MouseEvent evt) {
				PolylineConnection figure = (PolylineConnection) evt
						.getSource();
				Color cl = DEFAULT_EDGE_COLOR;
				org.eclipse.swt.graphics.Color fg = new org.eclipse.swt.graphics.Color(
						Display.getCurrent(), cl.getRed(), cl.getGreen(), cl
								.getBlue());
				figure.setForegroundColor(fg);
			}

			public void mouseHover(MouseEvent evt) {
			}

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

		Image infoIcon = new Image(Display.getCurrent(), IMAGE_DATA__INFO_ICON);
		Image transitionImg = new Image(Display.getCurrent(),
				IMAGE_DATA__TRANSISTION_ICON);

		Label transitionInfoIndicator = new Label("", infoIcon);
		MidpointLocator locator = new MidpointLocator(figure, 0);
		figure.add(transitionInfoIndicator, locator);

		Label transitionLabel = null;
		String transitionName = t.getName();
		if (!("".equals(transitionName))) {
			transitionLabel = new Label(String.format("%s", transitionName),
					transitionImg);
		} else {
			transitionLabel = new Label(String.format("%s", "<default>"),
					transitionImg);
		}
		Font transitionLabelFont = new Font(null, "Arial", 10, SWT.BOLD);
		transitionLabel.setFont(transitionLabelFont);
		TransitionInfoFigure transitionInfoFigure = new TransitionInfoFigure(
				transitionLabel);

		String transitionDesc = t.getDesc();
		Label transitionDescLabel;
		if ((null != transitionDesc) && (transitionDesc.trim().length() > 0)) {
			transitionDescLabel = new Label(transitionDesc);

		} else {
			transitionDescLabel = new Label("<default>");
		}
		Font transitionDescLabelFont = new Font(null, "Arial", 10, SWT.ITALIC);
		transitionDescLabel.setFont(transitionDescLabelFont);
		transitionInfoFigure.getTransitionDescFigureCompartment().add(
				transitionDescLabel);

		completeTransistionInfoFigureWithAttrInfo(t, transitionInfoFigure);

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
		// EditPart parent = this.getParent();
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
		return super.getContentPane();
	}

	protected void completeTransistionInfoFigureWithAttrInfo(Transition t,
			TransitionInfoFigure transitionInfoFigure) {

		SubFlowInfo subFlowInfo = null;
		ParticipantInfo pInfo = t.getSource().getParticipant();
		if (null != pInfo) {
			if (pInfo instanceof SubFlowInfo) {
				subFlowInfo = (SubFlowInfo) pInfo;
			}
		}

		if (null == subFlowInfo) {
			Image ctxAttrPutImg = new Image(Display.getCurrent(),
					IMAGE_DATA__CTX_ATTR_ADDED_ICON);

			CompartmentFigure compartmentFigure = new CompartmentFigure();
			compartmentFigure.setParent(transitionInfoFigure);

			List<String> attributesAddedOnTransition = t.getAttributesAdded();
			if (null != attributesAddedOnTransition) {
				TransitionAttrsCompartmentFigure attrsCompartmentFigure = new TransitionAttrsCompartmentFigure();
				for (String attr : attributesAddedOnTransition) {
					Label attrLabel = new Label(attr, ctxAttrPutImg);
					attrLabel.setTextPlacement(PositionConstants.WEST);
					attrsCompartmentFigure.add(attrLabel);
				}

				compartmentFigure.add(attrsCompartmentFigure);
			}

			transitionInfoFigure.add(compartmentFigure);
		} else {
			ParticipantInfo subFlowFinalNodeInfo = null;
			Graph subFlowGraph = subFlowInfo.getSubFlowGraph();
			if (null != subFlowGraph) {
				Node subFlowFinalNode = subFlowGraph.getFinalNode();
				subFlowFinalNodeInfo = subFlowFinalNode.getParticipant();
			}

			if (null != subFlowFinalNodeInfo) {

				CompartmentFigure guaranteedAttrsFigureCompartment = new CompartmentFigure();

				Image ctxAttrGuaranteedImg = new Image(Display.getCurrent(),
						IMAGE_DATA__CTX_ATTR_GUARANTEED_ICON);
				if (null != subFlowFinalNodeInfo.getGuaranteedCtxAttributes()
						&& subFlowFinalNodeInfo.getGuaranteedCtxAttributes().size() > 0) {
					for (String guaranteedCtxAttr : subFlowFinalNodeInfo
							.getGuaranteedCtxAttributes()) {
						guaranteedAttrsFigureCompartment.add(new Label(
								guaranteedCtxAttr, ctxAttrGuaranteedImg));
					}
				} else {
					Font nonGuranteedAttrLabelFont = new Font(null, "Arial",
							10, SWT.ITALIC);
					Label nonGuranteedAttrlabel = new Label(
							"<no guaranteed attributes>", ctxAttrGuaranteedImg);
					nonGuranteedAttrlabel.setFont(nonGuranteedAttrLabelFont);
					guaranteedAttrsFigureCompartment.add(nonGuranteedAttrlabel);
				}

				transitionInfoFigure.add(guaranteedAttrsFigureCompartment);

				CompartmentFigure optionalAttrsFigure = new CompartmentFigure();

				Image ctxAttrOptionalImg = new Image(Display.getCurrent(),
						IMAGE_DATA__CTX_ATTR_OPTIONAL_ICON);
				if (null != subFlowFinalNodeInfo.getOptionalCtxAttributes()
						&& subFlowFinalNodeInfo.getOptionalCtxAttributes().size() > 0) {
					for (String optionalCtxAttr : subFlowFinalNodeInfo
							.getOptionalCtxAttributes()) {
						optionalAttrsFigure.add(new Label(optionalCtxAttr,
								ctxAttrOptionalImg));
					}
				} else {
					Font noOptionalAttrlabelFont = new Font(null, "Arial", 10,
							SWT.ITALIC);
					Label noOptionalAttrlabel = new Label(
							"<no optional attributes>", ctxAttrOptionalImg);
					noOptionalAttrlabel.setFont(noOptionalAttrlabelFont);
					optionalAttrsFigure.add(noOptionalAttrlabel);
				}
				transitionInfoFigure.add(optionalAttrsFigure);
			}
		}
	}

}
