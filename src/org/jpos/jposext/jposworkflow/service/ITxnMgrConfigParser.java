package org.jpos.jposext.jposworkflow.service;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.jpos.jposext.jposworkflow.model.ParticipantInfo;


/**
 * Interface de parsing d'une configuration de transaction manager JPos
 * 
 * @author dgrandemange
 *
 */
public interface ITxnMgrConfigParser {

	public Map<String, List<ParticipantInfo>> parse(InputStream is);

	public Map<String, List<ParticipantInfo>> parse(URL url);
	
}