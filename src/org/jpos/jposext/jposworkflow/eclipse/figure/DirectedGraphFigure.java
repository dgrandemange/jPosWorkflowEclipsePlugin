package org.jpos.jposext.jposworkflow.eclipse.figure;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * @author dgrandemange
 *
 */
public class DirectedGraphFigure extends Figure {
    private Label labelId = new Label();

    private XYLayout layout;

    public DirectedGraphFigure() {
        layout = new XYLayout();
        setLayoutManager(layout);

        /*
        labelId.setForegroundColor(ColorConstants.blue);
        add(labelId);
        setConstraint(labelId, new Rectangle(5, 5, -1, -1));
        */
        setForegroundColor(ColorConstants.black);
        setBorder(new LineBorder(5));
    }

    public void setLayout(Rectangle rect) {
        setBounds(rect);
    }

    public void setId(String text) {
        labelId.setText(text);
    }

}
