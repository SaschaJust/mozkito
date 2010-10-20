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
package de.unisaarland.cs.st.reposuite.ppa;

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

public class MyPPAResourceUtil {
	
	private final static Logger logger = Logger.getLogger(MyPPAResourceUtil.class);
	
	/**
	 * <p>
	 * Deletes a file and all the containing folders up to the source folder.
	 * Folder containing multiple files are not deleted and the recursion stops
	 * if such folder is encountered.
	 * </p>
	 * 
	 * @param file
	 * @throws CoreException
	 */
	public static void cleanUp(IFile file) throws CoreException {
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
	 * @param srcFile
	 * @param packageName
	 *            Name of the package (e.g., com.foo)
	 * @param destFileName
	 *            Name of the compilation unit (e.g., Bar)
	 * @return
	 * @throws CoreException
	 * @throws IOException
	 */
	public static IFile copyJavaSourceFile(IProject project, File srcFile, String packageName, String destFileName)
	        throws CoreException, IOException {
		IFile file = null;
		
		IFolder srcFolder = project.getFolder("src");
		IFolder packageFolder = getCreatePackageFolder(srcFolder, PPABindingsUtil.getPackageArray(packageName));
		
		file = packageFolder.getFile(destFileName);
		if (!file.exists()) {
			file.create(new FileInputStream(srcFile), IFile.FORCE, new NullProgressMonitor());
		} else {
			file.setContents(new FileInputStream(srcFile), IFile.FORCE, new NullProgressMonitor());
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
	 * @param srcFile
	 * @param packageName
	 *            Name of the package (e.g., com.foo)
	 * @param destFileName
	 *            Name of the compilation unit (e.g., Bar)
	 * @param isTypeBody
	 *            True if the snippet could fit inside a class body (e.g.,
	 *            contains a method declaration). False if it could fit inside a
	 *            method body.
	 * @return
	 * @throws CoreException
	 * @throws IOException
	 */
	public static IFile copyJavaSourceFileSnippet(IProject project, File srcFile, String packageName,
	        String destFileName, boolean isTypeBody) throws CoreException, IOException {
		IFile file = null;
		
		IFolder srcFolder = project.getFolder("src");
		IFolder packageFolder = getCreatePackageFolder(srcFolder, PPABindingsUtil.getPackageArray(packageName));
		
		file = packageFolder.getFile(destFileName);
		String content = isTypeBody ? SnippetUtil.getTypeBody(getContent(srcFile)) : SnippetUtil
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
	 * @param snippetContent
	 * @param packageName
	 *            Name of the package (e.g., com.foo)
	 * @param destFileName
	 *            Name of the compilation unit (e.g., Bar)
	 * @param isTypeBody
	 *            True if the snippet could fit inside a class body (e.g.,
	 *            contains a method declaration). False if it could fit inside a
	 *            method body.
	 * @return
	 * @throws CoreException
	 * @throws IOException
	 */
	public static IFile copyJavaSourceFileSnippet(IProject project, String snippetContent, String packageName,
	        String destFileName, boolean isTypeBody) throws CoreException, IOException {
		IFile file = null;
		
		IFolder srcFolder = project.getFolder("src");
		IFolder packageFolder = getCreatePackageFolder(srcFolder, PPABindingsUtil.getPackageArray(packageName));
		
		file = packageFolder.getFile(destFileName);
		String newContent = isTypeBody ? SnippetUtil.getTypeBody(snippetContent) : SnippetUtil
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
	 * @return
	 * @throws IOException
	 *             If there were any IO related errors while reading the file.
	 */
	public static String getContent(File file) throws IOException {
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
	 * @param packages
	 * @return The last folder (e.g., bar) created.
	 * @throws CoreException
	 */
	public static IFolder getCreatePackageFolder(IFolder srcFolder, String[] packages) throws CoreException {
		IFolder finalFolder = srcFolder;
		
		if (packages != null) {
			int size = packages.length;
			for (int i = 0; i < size; i++) {
				if (!ValidatorUtil.validateEmpty(packages[i], "packages", false)) {
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
	 * 
	 * @param folder
	 * @return true if the folder does not contain any resource and exists.
	 */
	public static boolean isEmpty(IFolder folder) {
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
	
	private static boolean isSrcFolder(IFolder parentFolder) {
		return parentFolder.getName().equals("src");
	}
	
}
