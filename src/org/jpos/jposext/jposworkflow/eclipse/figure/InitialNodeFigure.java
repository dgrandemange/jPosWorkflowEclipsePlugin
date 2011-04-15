/*
 * Created on 17 juil. 08 by dgrandemange
 *
 * Copyright (c) 2005 Setib
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Setib ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Setib.
 */
package org.jpos.jposext.jposworkflow.eclipse.figure;

import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.swt.graphics.Image;

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
