package org.jpos.jposext.jposworkflow.eclipse.figure;

import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.swt.graphics.Image;

/**
 * @author dgrandemange
 *
 */
public class InitialNodeFigure extends ImageFigure {
	
    public InitialNodeFigure() {
		super();
		init();
	}

	public InitialNodeFigure(Image image, int alignment) {
		super(image, alignment);
		init();
	}

	public InitialNodeFigure(Image image) {
		super(image);
		init();
	}

	protected void init() {
		Label tipLabel = new Label("Initial state");
		this.setToolTip(tipLabel);
	}
		
}
