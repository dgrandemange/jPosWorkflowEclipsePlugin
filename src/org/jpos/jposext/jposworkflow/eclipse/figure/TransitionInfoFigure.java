package org.jpos.jposext.jposworkflow.eclipse.figure;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.swt.graphics.Color;

/**
 * @author dgrandemange
 * 
 */
public class TransitionInfoFigure extends Figure {
	public static Color classColor = new Color(null, 255, 255, 206);
	
	private CompartmentFigure transitionDescFigure = new CompartmentFigure();
	
	public TransitionInfoFigure(Label name) {
		ToolbarLayout layout = new ToolbarLayout();
		setLayoutManager(layout);
		setBorder(new LineBorder(ColorConstants.black, 1));
		setBackgroundColor(classColor);
		setOpaque(true);

		if (null != name) {
			add(name);
			add(transitionDescFigure);
		}
	}

	public CompartmentFigure getTransitionDescFigureCompartment() {
		return transitionDescFigure;
	}
	
}