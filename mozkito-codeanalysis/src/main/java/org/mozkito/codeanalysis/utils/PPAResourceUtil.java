/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.mozkito.codeanalysis.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import ca.mcgill.cs.swevo.ppa.SnippetUtil;
import ca.mcgill.cs.swevo.ppa.ValidatorUtil;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.dom.PPABindingsUtil;

/**
 * The Class PPAResourceUtil.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class PPAResourceUtil {
	
	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(PPAResourceUtil.class);
	
	/**
	 * <p>
	 * Deletes a file and all the containing folders up to the source folder. Folder containing multiple files are not
	 * deleted and the recursion stops if such folder is encountered.
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
			final IFolder tempFolder = (IFolder) parentFolder.getParent();
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
	public static IFile copyJavaSourceFile(final IProject project,
	                                       final File srcFile,
	                                       final String packageName,
	                                       final String destFileName) throws CoreException, IOException {
		IFile file = null;
		
		final IFolder srcFolder = project.getFolder("src");
		final IFolder packageFolder = getCreatePackageFolder(srcFolder, PPABindingsUtil.getPackageArray(packageName));
		
		file = packageFolder.getFile(destFileName);
		if (!file.exists()) {
			final FileInputStream stream = new FileInputStream(srcFile);
			file.create(stream, IResource.FORCE, new NullProgressMonitor());
			stream.close();
		} else {
			final FileInputStream stream = new FileInputStream(srcFile);
			file.setContents(stream, IResource.FORCE, new NullProgressMonitor());
			stream.close();
		}
		
		return file;
	}
	
	/**
	 * <p>
	 * Creates a Java source file based on the provided Java snippet (contained in a file).
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
	 *            True if the snippet could fit inside a class body (e.g., contains a method declaration). False if it
	 *            could fit inside a method body.
	 * @return the i file
	 * @throws CoreException
	 *             the core exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static IFile copyJavaSourceFileSnippet(final IProject project,
	                                              final File srcFile,
	                                              final String packageName,
	                                              final String destFileName,
	                                              final boolean isTypeBody) throws CoreException, IOException {
		IFile file = null;
		
		final IFolder srcFolder = project.getFolder("src");
		final IFolder packageFolder = getCreatePackageFolder(srcFolder, PPABindingsUtil.getPackageArray(packageName));
		
		file = packageFolder.getFile(destFileName);
		final String content = isTypeBody
		                                 ? SnippetUtil.getTypeBody(getContent(srcFile))
		                                 : SnippetUtil.getMethodBody(getContent(srcFile));
		final InputStream iStream = new ByteArrayInputStream(content.getBytes());
		
		if (!file.exists()) {
			file.create(iStream, IResource.FORCE, new NullProgressMonitor());
		} else {
			file.setContents(iStream, IResource.FORCE, new NullProgressMonitor());
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
	 *            True if the snippet could fit inside a class body (e.g., contains a method declaration). False if it
	 *            could fit inside a method body.
	 * @return the i file
	 * @throws CoreException
	 *             the core exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static IFile copyJavaSourceFileSnippet(final IProject project,
	                                              final String snippetContent,
	                                              final String packageName,
	                                              final String destFileName,
	                                              final boolean isTypeBody) throws CoreException, IOException {
		IFile file = null;
		
		final IFolder srcFolder = project.getFolder("src");
		final IFolder packageFolder = getCreatePackageFolder(srcFolder, PPABindingsUtil.getPackageArray(packageName));
		
		file = packageFolder.getFile(destFileName);
		final String newContent = isTypeBody
		                                    ? SnippetUtil.getTypeBody(snippetContent)
		                                    : SnippetUtil.getMethodBody(snippetContent);
		final InputStream iStream = new ByteArrayInputStream(newContent.getBytes());
		
		if (!file.exists()) {
			file.create(iStream, IResource.FORCE, new NullProgressMonitor());
		} else {
			file.setContents(iStream, IResource.FORCE, new NullProgressMonitor());
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
		final StringBuffer buffer = new StringBuffer();
		
		final BufferedReader br = new BufferedReader(new FileReader(file));
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
	 * For example, if the package is {"com","foo","bar"}, the following folders will be created: src/com/foo/bar.
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
			final int size = packages.length;
			for (int i = 0; i < size; i++) {
				if (!ValidatorUtil.validateEmpty(packages[i], "packages", false)) {
					break;
				}
				final IFolder tempFolder = finalFolder.getFolder(packages[i]);
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
				final IResource[] members = folder.members();
				isEmpty = (members == null) || (members.length == 0);
			}
		} catch (final CoreException ce) {
			PPAResourceUtil.logger.error("Error while checking if folder is empty.", ce);
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
