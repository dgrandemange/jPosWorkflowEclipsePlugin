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
public class NodeInfoFigure extends Figure {
	public static Color classColor = new Color(null, 255, 255, 206);	
	
	private Label name = new Label();
	private Label groupLabel = new Label();
	private Label classLabel = new Label();
	private CompartmentFigure transitionsInfoFigureCompartment = new CompartmentFigure();

	public NodeInfoFigure(boolean isGroup) {
		ToolbarLayout layout = new ToolbarLayout();
		setLayoutManager(layout);
		setBorder(new LineBorder(ColorConstants.black, 1));
		setBackgroundColor(classColor);
		setOpaque(true);		

		add(name);
		if (isGroup) {
			add(groupLabel);
		}
		add(classLabel);
		add(transitionsInfoFigureCompartment);
	}

	/**
	 * @return
	 */
	public CompartmentFigure getTransitionsInfoFigureCompartment() {
		return transitionsInfoFigureCompartment;
	}

	/**
	 * @return the name
	 */
	public Label getName() {
		return name;
	}

	/**
	 * @return the groupLabel
	 */
	public Label getGroupLabel() {
		return groupLabel;
	}

	/**
	 * @return the classLabel
	 */
	public Label getClassLabel() {
		return classLabel;
	}

}