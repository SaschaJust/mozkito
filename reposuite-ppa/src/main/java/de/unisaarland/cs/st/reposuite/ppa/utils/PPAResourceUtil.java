/*******************************************************************************
 * PPA - Partial Program Analysis for Java
 * Copyright (C) 2008 Barthelemy Dagenais
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this library. If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.txt>
 *******************************************************************************/
package de.unisaarland.cs.st.reposuite.ppa.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.dom.PPABindingsUtil;

import ca.mcgill.cs.swevo.ppa.SnippetUtil;
import ca.mcgill.cs.swevo.ppa.ValidatorUtil;

/**
 * The Class PPAResourceUtil.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class PPAResourceUtil {
	
	/** The Constant logger. */
	private final static Logger logger = Logger
	.getLogger(PPAResourceUtil.class);
	
	/**
	 * <p>
	 * Deletes a file and all the containing folders up to the source folder.
	 * Folder containing multiple files are not deleted and the recursion stops
	 * if such folder is encountered.
	 * </p>
	 * 
	 * @param file
	 *            the file
	 * @throws CoreException
	 *             the core exception
	 */
	public static void cleanUp(final IFile file) throws CoreException {
		IFolder parentFolder = (IFolder) file.getParent();
		file.delete(true, new NullProgressMonitor());
		while (isEmpty(parentFolder) && !isSrcFolder(parentFolder)) {
			IFolder tempFolder = (IFolder) parentFolder.getParent();
			parentFolder.delete(true, new NullProgressMonitor());
			parentFolder = tempFolder;
		}
	}
	
	/**
	 * <p>
	 * Creates a Java source file based on a complete Java file.
	 * </p>
	 * 
	 * @param project
	 *            the project
	 * @param srcFile
	 *            the src file
	 * @param packageName
	 *            Name of the package (e.g., com.foo)
	 * @param destFileName
	 *            Name of the compilation unit (e.g., Bar)
	 * @return the i file
	 * @throws CoreException
	 *             the core exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static IFile copyJavaSourceFile(final IProject project, final File srcFile,
			final String packageName, final String destFileName) throws CoreException,
			IOException {
		IFile file = null;
		
		IFolder srcFolder = project.getFolder("src");
		IFolder packageFolder = getCreatePackageFolder(srcFolder,
				PPABindingsUtil.getPackageArray(packageName));
		
		file = packageFolder.getFile(destFileName);
		if (!file.exists()) {
			file.create(new FileInputStream(srcFile), IFile.FORCE,
					new NullProgressMonitor());
		} else {
			file.setContents(new FileInputStream(srcFile), IFile.FORCE,
					new NullProgressMonitor());
		}
		
		return file;
	}
	
	/**
	 * <p>
	 * Creates a Java source file based on the provided Java snippet (contained
	 * in a file).
	 * </p>
	 * 
	 * @param project
	 *            the project
	 * @param srcFile
	 *            the src file
	 * @param packageName
	 *            Name of the package (e.g., com.foo)
	 * @param destFileName
	 *            Name of the compilation unit (e.g., Bar)
	 * @param isTypeBody
	 *            True if the snippet could fit inside a class body (e.g.,
	 *            contains a method declaration). False if it could fit inside a
	 *            method body.
	 * @return the i file
	 * @throws CoreException
	 *             the core exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static IFile copyJavaSourceFileSnippet(final IProject project,
			final File srcFile, final String packageName, final String destFileName,
			final boolean isTypeBody) throws CoreException, IOException {
		IFile file = null;
		
		IFolder srcFolder = project.getFolder("src");
		IFolder packageFolder = getCreatePackageFolder(srcFolder,
				PPABindingsUtil.getPackageArray(packageName));
		
		file = packageFolder.getFile(destFileName);
		String content = isTypeBody ? SnippetUtil
				.getTypeBody(getContent(srcFile)) : SnippetUtil
				.getMethodBody(getContent(srcFile));
				InputStream iStream = new ByteArrayInputStream(content.getBytes());
				
				if (!file.exists()) {
					file.create(iStream, IFile.FORCE, new NullProgressMonitor());
				} else {
					file.setContents(iStream, IFile.FORCE, new NullProgressMonitor());
				}
				
				return file;
	}
	
	/**
	 * <p>
	 * Creates a Java source file based on the provided codesnippet.
	 * </p>
	 * 
	 * @param project
	 *            the project
	 * @param snippetContent
	 *            the snippet content
	 * @param packageName
	 *            Name of the package (e.g., com.foo)
	 * @param destFileName
	 *            Name of the compilation unit (e.g., Bar)
	 * @param isTypeBody
	 *            True if the snippet could fit inside a class body (e.g.,
	 *            contains a method declaration). False if it could fit inside a
	 *            method body.
	 * @return the i file
	 * @throws CoreException
	 *             the core exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static IFile copyJavaSourceFileSnippet(final IProject project,
			final String snippetContent, final String packageName, final String destFileName,
			final boolean isTypeBody) throws CoreException, IOException {
		IFile file = null;
		
		IFolder srcFolder = project.getFolder("src");
		IFolder packageFolder = getCreatePackageFolder(srcFolder,
				PPABindingsUtil.getPackageArray(packageName));
		
		file = packageFolder.getFile(destFileName);
		String newContent = isTypeBody ? SnippetUtil
				.getTypeBody(snippetContent) : SnippetUtil
				.getMethodBody(snippetContent);
				InputStream iStream = new ByteArrayInputStream(newContent.getBytes());
				
				if (!file.exists()) {
					file.create(iStream, IFile.FORCE, new NullProgressMonitor());
				} else {
					file.setContents(iStream, IFile.FORCE, new NullProgressMonitor());
				}
				
				return file;
	}
	
	/**
	 * <p>
	 * Retrieves the content of a file as a String.
	 * </p>
	 * 
	 * @param file
	 *            the file
	 * @return the content
	 * @throws IOException
	 *             If there were any IO related errors while reading the file.
	 */
	public static String getContent(final File file) throws IOException {
		StringBuffer buffer = new StringBuffer();
		
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = br.readLine();
		
		while (line != null) {
			buffer.append(line);
			buffer.append("\n");
			line = br.readLine();
		}
		
		br.close();
		
		return buffer.toString();
	}
	
	/**
	 * <p>
	 * Creates all the intermediate folders associated with package fragments.
	 * </p>
	 * <p>
	 * For example, if the package is {"com","foo","bar"}, the following folders
	 * will be created: src/com/foo/bar.
	 * </p>
	 * 
	 * @param srcFolder
	 *            the src folder
	 * @param packages
	 *            the packages
	 * @return The last folder (e.g., bar) created.
	 * @throws CoreException
	 *             the core exception
	 */
	public static IFolder getCreatePackageFolder(final IFolder srcFolder,
			final String[] packages) throws CoreException {
		IFolder finalFolder = srcFolder;
		
		if (packages != null) {
			int size = packages.length;
			for (int i = 0; i < size; i++) {
				if (!ValidatorUtil
						.validateEmpty(packages[i], "packages", false)) {
					break;
				}
				IFolder tempFolder = finalFolder.getFolder(packages[i]);
				if (!tempFolder.exists()) {
					tempFolder.create(true, true, new NullProgressMonitor());
				}
				finalFolder = tempFolder;
			}
		}
		
		return finalFolder;
	}
	
	/**
	 * Checks if is empty.
	 * 
	 * @param folder
	 *            the folder
	 * @return true if the folder does not contain any resource and exists.
	 */
	public static boolean isEmpty(final IFolder folder) {
		boolean isEmpty = false;
		try {
			if ((folder != null) && folder.exists()) {
				IResource[] members = folder.members();
				isEmpty = (members == null) || (members.length == 0);
			}
		} catch (CoreException ce) {
			logger.error("Error while checking if folder is empty.", ce);
		}
		
		return isEmpty;
	}
	
	/**
	 * Checks if is src folder.
	 * 
	 * @param parentFolder
	 *            the parent folder
	 * @return true, if is src folder
	 */
	private static boolean isSrcFolder(final IFolder parentFolder) {
		return parentFolder.getName().equals("src");
	}
	
	
}
