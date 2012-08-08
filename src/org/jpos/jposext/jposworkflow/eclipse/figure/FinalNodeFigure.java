package org.jpos.jposext.jposworkflow.eclipse.figure;

import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.swt.graphics.Image;

/**
 * @author dgrandemange
 *
 */
public class FinalNodeFigure extends ImageFigure {
	
    public FinalNodeFigure() {
		super();
		init();
	}

	public FinalNodeFigure(Image image, int alignment) {
		super(image, alignment);
		init();
	}

	public FinalNodeFigure(Image image) {
		super(image);
		init();
	}

	protected void init() {
		Label tipLabel = new Label("Final state");
		this.setToolTip(tipLabel);
	}
		
}
