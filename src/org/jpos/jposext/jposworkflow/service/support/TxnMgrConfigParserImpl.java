package org.jpos.jposext.jposworkflow.service.support;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
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
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.EntityRef;
import org.jdom.JDOMException;
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;
import org.jpos.jposext.jposworkflow.io.EnclosedInputStream;
import org.jpos.jposext.jposworkflow.model.EntityRefInfo;
import org.jpos.jposext.jposworkflow.model.Graph;
import org.jpos.jposext.jposworkflow.model.ParticipantInfo;
import org.jpos.jposext.jposworkflow.model.SelectCriterion;
import org.jpos.jposext.jposworkflow.model.SubFlowInfo;
import org.jpos.jposext.jposworkflow.service.ITxnMgrConfigParser;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * JDOM based implementation of a jPos transaction manager XML config parser
 * 
 * @author dgrandemange
 * 
 */
public class TxnMgrConfigParserImpl implements ITxnMgrConfigParser {

	private static final String REGEXP_PATERN__DTD_EXTENSION = "^.*\\.[dD][tT][dD]$";
	public static final String DEFAULT_GROUP = "";
	public static final String TXN_MGR_CONFIG__ROOT_ELEMENT = "txnmgr";
	public static final String TXN_MGR_SUBCONFIG__ENTRYPOINTGROUP__NAMEEXTRACTIONREGEXP = "^(.*/)*([^/].*)\\.[^\\.]*$";

	public class EntityResolverImpl implements EntityResolver {

		private String base;		
		
		public EntityResolverImpl(String base) {
			this.base = base;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String,
		 * java.lang.String)
		 */
		public InputSource resolveEntity(String publicId, String systemId)
				throws SAXException, IOException {

			try {
				String entityUrl = fixSystemId(systemId);

				if (null != entityUrl) {
					InputStream is = new URL(entityUrl).openStream();
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

		public String fixSystemId(String systemId)
				throws MalformedURLException, UnsupportedEncodingException {
			String entityUrl = null;

			// If system ID was a relative path, me must remove the
			// current dir prefixing the system id
			File currentDir = new File(System.getProperty("user.dir"));
			String sCurrentDirURL = currentDir.toURI().toURL().toString();

			File systemIdFile = new File(new URL(systemId).getFile());
			String sSystemIdFile = systemIdFile.toURI().toURL().toString();
			int lastIndex = sSystemIdFile.lastIndexOf(sCurrentDirURL);

			if (-1 == lastIndex) {
				// We try an URL decode of sSystemIdFile
				sSystemIdFile = URLDecoder.decode(sSystemIdFile, "UTF-8");
				lastIndex = sSystemIdFile.lastIndexOf(sCurrentDirURL);
			}

			if (-1 < lastIndex) {
				entityUrl = base
						+ sSystemIdFile.substring(sCurrentDirURL.length());

			}
			return entityUrl;
		}
	}

	private Map<String, Graph> graphByEntityRef;

	private DocType defaultDocType;
	
	private boolean expanded;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jpos.jposext.jposworkflow.service.ITxnMgrConfigParser#parse(java.
	 * net.URL)
	 */
	public Map<String, List<ParticipantInfo>> parse(URL url) {
		Map<String, List<ParticipantInfo>> groups = new HashMap<String, List<ParticipantInfo>>();

		EntityResolver entityResolver = getEntityResolver(url);

		Document doc = getDocument(url, entityResolver, defaultDocType);

		if (null != doc) {
			String pathLastToken = null;
			Pattern pattern = Pattern
					.compile(TXN_MGR_SUBCONFIG__ENTRYPOINTGROUP__NAMEEXTRACTIONREGEXP);
			Matcher matcher = pattern.matcher(url.getPath());
			if (matcher.matches()) {
				pathLastToken = matcher.group(2);
			}

			initParticipants(doc.getRootElement(), groups, pathLastToken);
		}

		return groups;
	}

	protected EntityResolverImpl getEntityResolver(URL url) {
		URL resolvedUrl = url;
		try {
			resolvedUrl = FileLocator.resolve(url);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		String sResolvedUrl = resolvedUrl.toString();

		int lastSep;
		int lastSep1 = sResolvedUrl.lastIndexOf('/');
		int lastSep2 = sResolvedUrl.lastIndexOf('\\');
		lastSep = (lastSep1 > lastSep2) ? lastSep1 : lastSep2;
		final String base = sResolvedUrl.substring(0, lastSep + 1);

		EntityResolverImpl entityResolver = new EntityResolverImpl(base);
		return entityResolver;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jpos.jposext.jposworkflow.service.ITxnMgrConfigParser#
	 * entityRefsTopologicalSort(java.net.URL)
	 */
	public List<EntityRefInfo> entityRefsTopologicalSort(URL url) {
		Map<String, EntityRefInfo> entityRefs = new HashMap<String, EntityRefInfo>();
		
		Map<String, List<String>> entityDeps = listEntityRefsInterDependencies(
				url, entityRefs);

		return sortEntityRefsTopologicalOrder(entityRefs, entityDeps);
	}

	public List<EntityRefInfo> sortEntityRefsTopologicalOrder(
			Map<String, EntityRefInfo> entityRefs,
			Map<String, List<String>> entityDeps) {
		List<String> sortedEntities = new ArrayList<String>();
		sortEntities("", entityDeps, sortedEntities);

		List<EntityRefInfo> res = new ArrayList<EntityRefInfo>();
		for (String entity : sortedEntities) {
			res.add(entityRefs.get(entity));
		}

		return res;
	}

	public Map<String, List<String>> listEntityRefsInterDependencies(
			URL url, Map<String, EntityRefInfo> entityRefs) {
		Map<String, List<String>> entityDeps = new HashMap<String, List<String>>();

		EntityResolverImpl entityResolver = getEntityResolver(url);

		listEntitiesInterDependencies(url, null, entityResolver, entityRefs,
				entityDeps, "", true);
		return entityDeps;
	}

	protected void sortEntities(String entity,
			Map<String, List<String>> entityDeps, List<String> sortedEntities) {
		if (sortedEntities.contains(entity)) {
			return;
		}

		List<String> currEntityDeps = entityDeps.get(entity);

		for (String entityDep : currEntityDeps) {
			sortEntities(entityDep, entityDeps, sortedEntities);
		}

		if (!("".equals(entity))) {
			sortedEntities.add(entity);
		}
	}

	protected void listEntitiesInterDependencies(URL url, DocType docType,
			EntityResolverImpl entityResolver,
			Map<String, EntityRefInfo> entityRefs,
			Map<String, List<String>> entityDeps, String currentEntityName,
			boolean rootDocument) {
		List<String> currentEntityDeps = entityDeps.get(currentEntityName);

		if (null == currentEntityDeps) {
			currentEntityDeps = new ArrayList<String>();
			entityDeps.put(currentEntityName, currentEntityDeps);
		}

		Document doc = getDocument(url, entityResolver, docType);
		if (rootDocument) {
			docType = doc.getDocType();
		}

		Element config = doc.getRootElement();

		// Looking for entity references
		List content = config.getContent();
		Iterator iterEntityRef = content.listIterator();
		while (iterEntityRef.hasNext()) {
			Object o = iterEntityRef.next();
			if (o instanceof EntityRef) {
				EntityRef entityRef = (EntityRef) o;
				String ERname = entityRef.getName();
				String ERsystemID = entityRef.getSystemID();

				if (!(ERsystemID.matches(REGEXP_PATERN__DTD_EXTENSION))) {
					currentEntityDeps.add(ERname);
					if (!(entityRefs.containsKey(ERname))) {
						try {
							String fixedSystemId = entityResolver
									.fixSystemId(ERsystemID);
							listEntitiesInterDependencies(
									new URL(fixedSystemId), docType,
									entityResolver, entityRefs, entityDeps,
									ERname, false);
							EntityRefInfo entityRefInfo = new EntityRefInfo(
									ERname, new URL(fixedSystemId));
							entityRefs.put(ERname, entityRefInfo);
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					}
				}
			}
		}
	}

	public void useXmlDocType(URL url) {
		Document doc = getDocument(url, null, null);
		DocType docType = doc.getDocType();
		if (null != docType) {
			this.defaultDocType = (DocType) docType.clone();
		}
	}

	protected Document getDocument(URL url, EntityResolver entityResolver,
			DocType inheritedDocType) {
		SAXBuilder builder = new SAXBuilder();

		builder.setValidation(false);
		builder.setExpandEntities(expanded);

		builder.setFeature("http://xml.org/sax/features/namespaces", true);
		builder.setFeature("http://apache.org/xml/features/xinclude", true);

		builder.setEntityResolver(entityResolver);

		boolean JDOMExceptionOnFirstTry = false;

		Document doc = null;

		URL resolvedUrl = url;

		InputStream is = null;
		try {
			is = resolvedUrl.openStream();
			doc = builder.build(is);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (JDOMException e) {
			JDOMExceptionOnFirstTry = true;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {
					// We've done our best
				}
			}
		}

		if (JDOMExceptionOnFirstTry) {
			try {
				String docTypePart = "";
				if (null != inheritedDocType) {
					docTypePart = String.format("<!DOCTYPE %s [\n%s\n]>\n",
							inheritedDocType.getElementName(),
							inheritedDocType.getInternalSubset());
				}

				String prefix = String.format("%s<%s>", docTypePart,
						TXN_MGR_CONFIG__ROOT_ELEMENT);

				String suffix = String.format("</%s>",
						TXN_MGR_CONFIG__ROOT_ELEMENT);

				is = new EnclosedInputStream(prefix.getBytes(),
						resolvedUrl.openStream(), suffix.getBytes());
				doc = builder.build(is);
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} catch (JDOMException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (Exception e) {
						// We've done our best
					}
				}
			}
		}

		return doc;
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

		// Looking for groups
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

		// Looking for entity references
		List content = config.getContent();
		Iterator iterEentityRef = content.listIterator();
		while (iterEentityRef.hasNext()) {
			Object o = iterEentityRef.next();
			if (o instanceof EntityRef) {
				EntityRef entityRef = (EntityRef) o;
				String ERname = entityRef.getName();

				Graph subFlowGraph = null;
				if (null != graphByEntityRef) {
					subFlowGraph = graphByEntityRef.get(ERname);
				}

				SubFlowInfo pInfo = new SubFlowInfo(ERname, subFlowGraph,
						new HashMap<String, SelectCriterion>());

				List<String> guaranteedCtxAttributes = new ArrayList<String>();
				pInfo.setGuaranteedCtxAttributes(guaranteedCtxAttributes);

				List<String> optionalCtxAttributes = new ArrayList<String>();
				pInfo.setOptionalCtxAttributes(optionalCtxAttributes);

				List<ParticipantInfo> group = new ArrayList<ParticipantInfo>();
				group.add(pInfo);

				groups.put(ERname, group);

			}
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

	public void setGraphByEntityRef(Map<String, Graph> graphByEntityRef) {
		this.graphByEntityRef = graphByEntityRef;
	}

	/**
	 * @return the defaultDocType
	 */
	public DocType getDefaultDocType() {
		return defaultDocType;
	}

	/**
	 * @param defaultDocType
	 *            the defaultDocType to set
	 */
	public void setDefaultDocType(DocType defaultDocType) {
		this.defaultDocType = defaultDocType;
	}

	/**
	 * @param expanded the expanded to set
	 */
	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

}
