package org.jpos.jposext.jposworkflow.eclipse.figure;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;

/**
 * @author dgrandemange
 *
 */
public class NodeFigure extends Figure {

    public static Color classColor = new Color(null, 255, 255, 206);

    public NodeFigure() {
        ToolbarLayout layout = new ToolbarLayout();
        setLayoutManager(layout);
        setBorder(new LineBorder(ColorConstants.black, 1));
        setBackgroundColor(classColor);
        setOpaque(true);
    }

    public void setLayout(Rectangle rect) {
        getParent().setConstraint(this, rect);
    }

}
