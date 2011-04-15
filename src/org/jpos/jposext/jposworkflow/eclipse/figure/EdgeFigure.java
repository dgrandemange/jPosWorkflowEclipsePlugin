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

import java.awt.Color;

import org.eclipse.draw2d.ConnectionRouter;
import org.eclipse.draw2d.FanRouter;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.swt.widgets.Display;

public class EdgeFigure extends PolylineConnection {
	
	private static final Color DEFAULT_EDGE_COLOR = Color.BLACK;
	
    public EdgeFigure() {
		ConnectionRouter cr = new FanRouter();
		this.setConnectionRouter(cr);
		
		PolygonDecoration decoration = new PolygonDecoration();
		this.setTargetDecoration(decoration);
		
		org.eclipse.swt.graphics.Color fg = new org.eclipse.swt.graphics.Color(
				Display.getCurrent(), DEFAULT_EDGE_COLOR.getRed(), DEFAULT_EDGE_COLOR.getGreen(), DEFAULT_EDGE_COLOR.getBlue());
		this.setForegroundColor(fg);
				
    }
}
