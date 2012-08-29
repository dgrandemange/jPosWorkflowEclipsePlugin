package org.jpos.jposext.jposworkflow.eclipse.service.support;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.jpos.jposext.ctxmgmt.annotation.UpdateContextRule;
import org.jpos.jposext.ctxmgmt.annotation.UpdateContextRules;
import org.jpos.jposext.jposworkflow.model.ParticipantInfo;
import org.jpos.jposext.jposworkflow.model.SubFlowInfo;
import org.jpos.jposext.jposworkflow.service.support.ContextMgmtInfoPopulatorAbstractImpl;

/**
 * @author dgrandemange
 * 
 */
public class ContextMgmtInfoPopulatorEclipsePluginImpl extends
		ContextMgmtInfoPopulatorAbstractImpl {

	private IProject project;
	
	public ContextMgmtInfoPopulatorEclipsePluginImpl(IProject project) {
		super();
		this.project = project;
	}

	@Override
	public void processParticipantAnnotations(
			Map<String, List<ParticipantInfo>> jPosTxnMgrGroups) {
		try {
			IJavaProject jProject = (IJavaProject) project
					.getNature(JavaCore.NATURE_ID);

			for (Entry<String, List<ParticipantInfo>> entry : jPosTxnMgrGroups
					.entrySet()) {

				for (ParticipantInfo participantInfo : entry.getValue()) {
					if (participantInfo instanceof SubFlowInfo) {
						continue;
					}
					
					Map<String, String[]> updCtxAttrByTransId = new HashMap<String, String[]>();
					participantInfo.setUpdCtxAttrByTransId(updCtxAttrByTransId);

					String javaClassPathFromFullClassName = getJavaClassPathFromFullClassName(participantInfo
							.getClazz());
					IPath path = new Path(javaClassPathFromFullClassName);

					if (null == path) {
						return;
					}

					IJavaElement jElt = jProject.findElement(path);

					if (null == jElt) {
						continue;
					}

					int elementType = jElt.getElementType();

					if (elementType == IJavaElement.COMPILATION_UNIT) {

						IType[] types = ((ICompilationUnit) jElt).getTypes();
						IType type = types[0];
						IAnnotation[] annotations = type.getAnnotations();
						for (IAnnotation annotation : annotations) {
							if (UpdateContextRules.class.getSimpleName()
									.equals(annotation.getElementName())) {
								IMemberValuePair[] memberValuePairs = annotation
										.getMemberValuePairs();
								for (IMemberValuePair memberValuePair : memberValuePairs) {
									if ("value".equals(memberValuePair
											.getMemberName())) {
										try {
											Object[] memberValues;
											try {
												memberValues = (Object[]) memberValuePair
														.getValue();
											} catch (ClassCastException e) {
												memberValues = new Object[] { memberValuePair
														.getValue() };
											}
											for (Object memberValue : memberValues) {
												if (memberValue instanceof IAnnotation) {
													IAnnotation subAnnotation = (IAnnotation) memberValue;
													if (UpdateContextRule.class
															.getSimpleName()
															.equals(subAnnotation
																	.getElementName())) {
														String id = null;
														String[] attrNames = null;
														IMemberValuePair[] subMemberValuePairs = subAnnotation
																.getMemberValuePairs();
														for (IMemberValuePair subMemberValuePair : subMemberValuePairs) {
															if ("id".equals(subMemberValuePair
																	.getMemberName())) {
																id = renameMeLater(
																		jProject,
																		type,
																		subMemberValuePair);
															} else if ("attrNames"
																	.equals(subMemberValuePair
																			.getMemberName())) {
																Object subMemberValue = subMemberValuePair
																		.getValue();
																if (subMemberValue instanceof String) {
																	String attrName = (String) subMemberValue;
																	attrNames = new String[] { attrName };
																} else if (subMemberValue instanceof Object[]) {
																	attrNames = Arrays
																			.copyOf((Object[]) subMemberValue,
																					((Object[]) subMemberValue).length,
																					String[].class);
																}
															}
														}

														if (null != attrNames) {
															if (null == id) {
																updCtxAttrByTransId
																		.put(UpdateContextRule.DEFAULT_ID,
																				attrNames);
															} else {
																updCtxAttrByTransId
																		.put(id,
																				attrNames);
															}
														}
													}
												}
											}

										} catch (ClassCastException e) {
											e.printStackTrace();
										}
									}
								}
							}
						}
					}

				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected String renameMeLater(IJavaProject jProject, IType type,
			IMemberValuePair subMemberValuePair) throws JavaModelException {
		String res = null;

		Object subMemberValue = subMemberValuePair.getValue();
		int valueKind = subMemberValuePair.getValueKind();
		if (IMemberValuePair.K_STRING == valueKind) {
			res = (String) subMemberValue;
		} else if ((IMemberValuePair.K_SIMPLE_NAME == valueKind)
				|| (IMemberValuePair.K_QUALIFIED_NAME == valueKind)) {

			res = resolveConstantFromQualifiedName(jProject, type,
					subMemberValue);

			if (null == res) {
				res = (String) subMemberValue;
			}

		}
		return res;
	}

	protected String resolveConstantFromQualifiedName(IJavaProject jProject,
			IType type, Object subMemberValue) throws JavaModelException {
		String res = null;

		String name = (String) subMemberValue;
		int i = name.lastIndexOf(".");
		String last = name.substring(i + 1);
		String valueClass = name.substring(0, i);
		String[][] resolvedTypes = type.resolveType(valueClass);
		if (null != resolvedTypes) {
			String packageName = null;
			String className = null;
			for (String[] tab1 : resolvedTypes) {
				packageName = tab1[0];
				className = tab1[1];
				break;
			}
			IType valueType = getCompilationUnitFromFullClassName(jProject,
					packageName + "." + className);
			if (null != valueType) {
				IField valueField = valueType.getField(last);

				Object fldConstant = valueField.getConstant();
				if (null != fldConstant) {
					String constantValue = fldConstant.toString();
					if (('"' == constantValue.charAt(0))
							&& ('"' == constantValue.charAt(constantValue
									.length() - 1))) {
						res = constantValue.substring(1,
								constantValue.length() - 1);
					}
				}
			}

		}
		return res;
	}

	protected IType getCompilationUnitFromFullClassName(IJavaProject jProject,
			String className) throws JavaModelException {
		IType res = null;

		String sTypePath = getJavaClassPathFromFullClassName(className);
		IPath typePath = new Path(sTypePath);

		if (null != typePath) {
			IJavaElement typeJElt = jProject.findElement(typePath);

			if (null != typeJElt) {
				if (typeJElt.getElementType() == IJavaElement.COMPILATION_UNIT) {
					IType[] types = ((ICompilationUnit) typeJElt).getTypes();
					res = types[0];
				}
			}
		}

		return res;
	}

	final protected String getJavaClassPathFromFullClassName(String className) {
		return className.replaceAll("\\.", "/") + ".java";
	}

}
