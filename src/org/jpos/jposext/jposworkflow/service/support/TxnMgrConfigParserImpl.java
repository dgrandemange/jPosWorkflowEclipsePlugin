package org.jpos.jposext.jposworkflow.service.support;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.FileLocator;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;
import org.jpos.jposext.jposworkflow.io.EnclosedInputStream;
import org.jpos.jposext.jposworkflow.model.ParticipantInfo;
import org.jpos.jposext.jposworkflow.model.SelectCriterion;
import org.jpos.jposext.jposworkflow.service.ITxnMgrConfigParser;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Implémentation d'un parser de config de transaction manager utilisant la
 * librairie JDOM<BR/>
 * Inspiré de la classe TransactionManager JPos mais sans dépendances aucune aux
 * classes JPos
 * 
 * @author dgrandemange
 * 
 */
public class TxnMgrConfigParserImpl implements ITxnMgrConfigParser {

	public static final String DEFAULT_GROUP = "";
	public static final String TXN_MGR_CONFIG__ROOT_ELEMENT = "txnmgr";
	public static final String TXN_MGR_SUBCONFIG__ENTRYPOINTGROUP__NAMEEXTRACTIONREGEXP = "^(.*/)*([^/].*)\\.[^\\.]*$";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mbs.jpos.workflow.service.support.ITxnMgrConfigParser#parse(java.
	 * io.InputStream)
	 */
	public Map<String, List<ParticipantInfo>> parse(InputStream is) {
		Map<String, List<ParticipantInfo>> groups = new HashMap<String, List<ParticipantInfo>>();

		SAXBuilder builder = new SAXBuilder();

		builder.setFeature("http://xml.org/sax/features/namespaces", true);
		builder.setFeature("http://apache.org/xml/features/xinclude", true);

		try {
			Document doc = builder.build(is);
			initParticipants(doc.getRootElement(), groups, null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return groups;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jpos.jposext.jposworkflow.service.ITxnMgrConfigParser#parse(java.
	 * net.URL)
	 */
	public Map<String, List<ParticipantInfo>> parse(URL url) {
		Map<String, List<ParticipantInfo>> groups = new HashMap<String, List<ParticipantInfo>>();

		SAXBuilder builder = new SAXBuilder();

		builder.setFeature("http://xml.org/sax/features/namespaces", true);
		builder.setFeature("http://apache.org/xml/features/xinclude", true);

		String sUrl = url.toString();
		String pathLastToken = null;
		Pattern pattern = Pattern
				.compile(TXN_MGR_SUBCONFIG__ENTRYPOINTGROUP__NAMEEXTRACTIONREGEXP);
		Matcher matcher = pattern.matcher(url.getPath());
		if (matcher.matches()) {
			pathLastToken = matcher.group(2);
		}
		// System.out.println(sUrl);

		URL resolvedUrl = url;
		try {
			resolvedUrl = FileLocator.resolve(url);
		} catch (IOException e) {
			e.printStackTrace();
		}

		String sResolvedUrl = resolvedUrl.toString();
		// System.out.println(sResolvedUrl);

		int lastSep;
		int lastSep1 = sResolvedUrl.lastIndexOf('/');
		int lastSep2 = sResolvedUrl.lastIndexOf('\\');
		lastSep = (lastSep1 > lastSep2) ? lastSep1 : lastSep2;
		final String base = sResolvedUrl.substring(0, lastSep + 1);
		// System.out.println(base);

		builder.setEntityResolver(new EntityResolver() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String,
			 * java.lang.String)
			 */
			public InputSource resolveEntity(String publicId, String systemId)
					throws SAXException, IOException {

				try {
					String entityUrl = null;

					// Si le sytème id était relatif,
					// on doit supprimer le chemin du répertoire courant du
					// systemId
					File currentDir = new File(System.getProperty("user.dir"));
					String sCurrentDirURL = currentDir.toURI().toURL()
							.toString();

					File systemIdFile = new File(new URL(systemId).getFile());
					String sSystemIdFile = systemIdFile.toURI().toURL()
							.toString();
					int lastIndex = sSystemIdFile.lastIndexOf(sCurrentDirURL);

					if (-1 == lastIndex) {
						// On tente un URL decode de sSystemIdFile
						sSystemIdFile = URLDecoder.decode(sSystemIdFile,
								"UTF-8");
						lastIndex = sSystemIdFile.lastIndexOf(sCurrentDirURL);
					}

					if (-1 < lastIndex) {
						entityUrl = base
								+ sSystemIdFile.substring(sCurrentDirURL
										.length());

						URL url = new URL(entityUrl);
						InputStream is = url.openStream();
						InputSource inputSource = new InputSource(is);
						inputSource.setSystemId(entityUrl);
						return inputSource;
					} else {
						return null;
					}
				} catch (Exception e) {
					return null;
				}
			}
		});

		boolean JDOMExceptionOnFirstTry = false;

		try {
			InputStream is = resolvedUrl.openStream();
			Document doc = builder.build(is);
			initParticipants(doc.getRootElement(), groups, pathLastToken);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (JDOMException e) {
			JDOMExceptionOnFirstTry = true;
		}

		if (JDOMExceptionOnFirstTry) {
			try {
				InputStream is = new EnclosedInputStream(String.format("<%s>",
						TXN_MGR_CONFIG__ROOT_ELEMENT).getBytes(),
						resolvedUrl.openStream(), String.format("</%s>",
								TXN_MGR_CONFIG__ROOT_ELEMENT).getBytes());
				Document doc = builder.build(is);
				initParticipants(doc.getRootElement(), groups, pathLastToken);
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} catch (JDOMException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}

		return groups;
	}

	protected void initParticipants(Element config,
			Map<String, List<ParticipantInfo>> groups,
			String entryPointGroupName) {
		boolean entryPointIsAGroup = false;

		groups.put(DEFAULT_GROUP, initGroup(config, DEFAULT_GROUP));

		List<ParticipantInfo> defaultGroup = groups.get(DEFAULT_GROUP);

		if (0 == defaultGroup.size()) {
			if (null != entryPointGroupName) {
				// Try to match a group which is named the same as the
				// entryPointGroupName parameter
				Iterator iter = config.getChildren("group").iterator();
				while (iter.hasNext()) {
					Element e = (Element) iter.next();
					String name = e.getAttributeValue("name");
					if (entryPointGroupName.equals(name)) {
						entryPointIsAGroup = true;
						Iterator iterParticipant = e.getChildren("participant")
								.iterator();
						while (iterParticipant.hasNext()) {
							defaultGroup.add(getParticipantInfo(
									(Element) iterParticipant.next(),
									entryPointGroupName));
						}
						break;
					}
				}
			}
		}

		Iterator iter = config.getChildren("group").iterator();
		while (iter.hasNext()) {
			Element e = (Element) iter.next();
			String name = e.getAttributeValue("name");

			if (entryPointIsAGroup) {
				if (entryPointGroupName.equals(name)) {
					continue;
				}
			}

			if (name == null)
				throw new RuntimeException("missing group name");
			if (groups.get(name) != null) {
				throw new RuntimeException("Group '" + name
						+ "' already defined");
			}
			groups.put(name, initGroup(e, name));
		}
	}

	protected List<ParticipantInfo> initGroup(Element e, String groupName) {
		List<ParticipantInfo> group = new ArrayList<ParticipantInfo>();
		Iterator iter = e.getChildren("participant").iterator();
		while (iter.hasNext()) {
			group.add(getParticipantInfo((Element) iter.next(), groupName));
		}
		return group;
	}

	protected ParticipantInfo getParticipantInfo(Element e, String groupName) {
		ParticipantInfo pInfo = new ParticipantInfo();
		pInfo.setGroupName(groupName);
		pInfo.setClazz((String) e.getAttributeValue("class"));
		Map<String, SelectCriterion> selectCriteria = new HashMap<String, SelectCriterion>();
		pInfo.setSelectCriteria(selectCriteria);
		ElementFilter elementsFilter = new ElementFilter("property");
		for (Iterator descendants = e.getDescendants(elementsFilter); descendants
				.hasNext();) {
			Element propertyElt = (Element) descendants.next();
			Attribute attribute = propertyElt.getAttribute("selectCriterion");
			if (attribute != null) {
				SelectCriterion criterion = new SelectCriterion(
						propertyElt.getAttributeValue("name"),
						propertyElt.getAttributeValue("value"),
						attribute.getValue());
				selectCriteria.put(criterion.getName(), criterion);
			}
		}

		return pInfo;
	}

}
