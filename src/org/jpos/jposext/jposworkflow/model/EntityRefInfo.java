package org.jpos.jposext.jposworkflow.model;

import java.net.URL;

/**
 * @author dgrandemange
 *
 */
public class EntityRefInfo {
	private String name;
	
	private URL url;

	public EntityRefInfo(String name, URL url) {
		super();
		this.name = name;
		this.url = url;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the systemID
	 */
	public URL getUrl() {
		return url;
	}
	
	
}
