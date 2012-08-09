package org.jpos.jposext.jposworkflow.eclipse.figure;

import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Insets;

/**
 * @author dgrandemange
 *
 */
public class TransitionAttrsCompartmentFigure extends Figure {

	public TransitionAttrsCompartmentFigure() {
		ToolbarLayout layout = new ToolbarLayout();
		layout.setMinorAlignment(ToolbarLayout.ALIGN_TOPLEFT);
		layout.setStretchMinorAxis(false);
		layout.setSpacing(2);
		setLayoutManager(layout);
		setBorder(new CompartmentFigureBorder());
	}

	public class CompartmentFigureBorder extends AbstractBorder {
		public Insets getInsets(IFigure figure) {
			return new Insets(0, 10, 0, 10);
		}

		public void paint(IFigure figure, Graphics graphics, Insets insets) {
//			graphics.drawLine(getPaintRectangle(figure, insets).getTopLeft(),
//					tempRect.getTopRight());
		}
	}
}
