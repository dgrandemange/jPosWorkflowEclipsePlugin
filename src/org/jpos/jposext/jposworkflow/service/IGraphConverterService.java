package org.jpos.jposext.jposworkflow.service;

import java.io.PrintWriter;

import org.jpos.jposext.jposworkflow.model.Graph;


public interface IGraphConverterService {
	
	/**
	 * @param graph
	 * @param os
	 */
	public void convertGraphToDOT(String name, Graph graph, PrintWriter pw);
}
