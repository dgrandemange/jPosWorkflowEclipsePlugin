/*
 * Created on 24 juil. 08 by dgrandemange
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
package org.jpos.jposext.jposworkflow.eclipse.popup.actions;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.osgi.framework.Bundle;

public class Utils {

	public static ClassLoader getClassLoader(IProject project) {
		return getClassLoader(project, true);
	}

	public static ClassLoader getClassLoader(IProject project,
			boolean useParentClassLoader) {
		List<URL> paths = getClassPathURLs(project, useParentClassLoader);
		if (useParentClassLoader) {
			return new URLClassLoader(paths.toArray(new URL[paths.size()]),
					Thread.currentThread().getContextClassLoader());
		} else {
			return new URLClassLoader(paths.toArray(new URL[paths.size()]));
		}
	}

	public static ClassLoader getClassLoader(IResource resource) {
		return getClassLoader(resource.getProject());
	}

	private static List<URL> getBundleClassPath(String bundleId) {
		List<URL> paths = new ArrayList<URL>();
		try {
			Bundle bundle = Platform.getBundle(bundleId);
			if (bundle != null) {
				String bundleClassPath = (String) bundle.getHeaders().get(
						org.osgi.framework.Constants.BUNDLE_CLASSPATH);
				if (bundleClassPath != null) {
					String[] classPathEntries = delimitedListToStringArray(
							bundleClassPath, ",");
					for (String classPathEntry : classPathEntries) {
						if (".".equals(classPathEntry.trim())) {
							paths.add(FileLocator.toFileURL(bundle
									.getEntry("/")));
						} else {
							paths.add(FileLocator
									.toFileURL(new URL(bundle.getEntry("/"),
											"/" + classPathEntry.trim())));
						}
					}
				} else {
					paths.add(FileLocator.toFileURL(bundle.getEntry("/")));
				}
			}
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return paths;
	}

	public static String[] delimitedListToStringArray(String bundleClassPath,
			String string) {
		List<String> lstString = new ArrayList<String>();
		StringTokenizer tokenizer = new StringTokenizer(bundleClassPath, string);
		while (tokenizer.hasMoreTokens()) {
			lstString.add(tokenizer.nextToken());
		}
		String[] tab = new String[lstString.size()];
		return lstString.toArray(tab);
	}

	public static List<URL> getClassPathURLs(IProject project,
			boolean useParentClassLoader) {
		List<URL> paths = new ArrayList<URL>();

		if (!useParentClassLoader) {
			// add required libraries from osgi bundles
			paths.addAll(getBundleClassPath("org.springframework"));
			paths.addAll(getBundleClassPath("org.aspectj.aspectjweaver"));
			paths.addAll(getBundleClassPath("jakarta.commons.logging"));
			paths.addAll(getBundleClassPath("org.objectweb.asm"));
		}

		try {
			if (isJavaProject(project)) {
				IJavaProject jp = JavaCore.create(project);
				// configured classpath
				IClasspathEntry[] classpath = jp.getResolvedClasspath(true);
				// build output, relative to project
				IPath location = getProjectLocation(project.getProject());
				IPath outputPath = location.append(jp.getOutputLocation()
						.removeFirstSegments(1));

				for (int i = 0; i < classpath.length; i++) {
					IClasspathEntry path = classpath[i];
					if (path.getEntryKind() == IClasspathEntry.CPE_LIBRARY) {
						File file = path.getPath().toFile();
						if (file.exists()) {
							URL url = path.getPath().toFile().toURL();
							paths.add(url);
						} else {
							String projectName = path.getPath().segment(0);
							IProject pathProject = ResourcesPlugin
									.getWorkspace().getRoot().getProject(
											projectName);
							IPath pathLocation = getProjectLocation(pathProject);
							IPath relPath = path.getPath().removeFirstSegments(
									1);
							URL url = new URL("file:" + pathLocation
									+ File.separator + relPath.toOSString());
							paths.add(url);
						}
					} else if (path.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
						// add source path as well for non java resources
						IPath sourcePath = path.getPath();
						if (sourcePath != null) {
							sourcePath = location.append(sourcePath
									.removeFirstSegments(1));
							paths.add(sourcePath.toFile().toURL());
						}
						// add source output locations for different source
						// folders
						IPath sourceOutputPath = path.getOutputLocation();
						if (sourceOutputPath != null) {
							sourceOutputPath = location.append(sourceOutputPath
									.removeFirstSegments(1));
							paths.add(sourceOutputPath.toFile().toURL());
						}
					}
				}
				// add all depending java projects
				for (IJavaProject p : getAllDependingJavaProjects(jp)) {
					paths.addAll(getClassPathURLs(p.getProject(), true));
				}
				paths.add(outputPath.toFile().toURL());
			} else {
				for (IProject p : project.getReferencedProjects()) {
					getClassPathURLs(p, useParentClassLoader);
				}
			}
		} catch (Exception e) {
			// ignore
		}
		return paths;
	}

	public static List<IJavaProject> getAllDependingJavaProjects(
			IJavaProject project) {
		List<IJavaProject> javaProjects = new ArrayList<IJavaProject>();
		IJavaModel model = JavaCore.create(ResourcesPlugin.getWorkspace()
				.getRoot());
		if (model != null) {
			try {
				String[] names = project.getRequiredProjectNames();
				IJavaProject[] projects = model.getJavaProjects();
				for (int index = 0; index < projects.length; index++) {
					for (int offset = 0; offset < names.length; offset++) {
						String name = projects[index].getProject().getName();
						if (name.equals(names[offset])) {
							javaProjects.add(projects[index]);
						}
					}
				}
			} catch (JavaModelException exception) {
			}
		}
		return javaProjects;
	}

	/**
	 * Returns true if given resource's project is a Java project.
	 */
	public static boolean isJavaProject(IResource resource) {
		if (resource != null && resource.isAccessible()) {
			IProject project = resource.getProject();
			if (project != null) {
				try {
					return project.hasNature(JavaCore.NATURE_ID);
				} catch (CoreException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return false;
	}

	public static IPath getProjectLocation(IProject project) {
		return (project.getRawLocation() != null ? project.getRawLocation()
				: project.getLocation());
	}
}
