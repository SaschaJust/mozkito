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
 * 
 * Contributors:
 *     Puneet Kapur - 2009
 *******************************************************************************/
package de.unisaarland.cs.st.reposuite.ppa;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTRequestor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.PPAASTParser;
import org.eclipse.jdt.core.dom.PPAEngine;
import org.eclipse.jdt.core.dom.PPATypeRegistry;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.internal.core.JavaProject;

import ca.mcgill.cs.swevo.ppa.PPAOptions;

// TODO: Try to get rid of Eclipse IProjects and IFiles
/**
 * <p>
 * Collection of utility methods to get compilation units from partial programs.
 * </p>
 * 
 * @author Barthelemy Dagenais
 * 
 */
public class MyPPAUtil {
	
	private final static Logger logger = Logger.getLogger(MyPPAUtil.class);
	
	/**
	 * Clean up.
	 * 
	 * @param cu
	 *            the cu
	 */
	public static void cleanUp(CompilationUnit cu) {
		cleanUp(cu, Thread.currentThread().getName());
	}
	
	/**
	 * Clean up.
	 * 
	 * @param cu
	 *            the cu
	 * @param requestName
	 *            the request name
	 */
	public static void cleanUp(CompilationUnit cu, String requestName) {
		ICompilationUnit icu = (ICompilationUnit) cu.getJavaElement();
		IFile iFile = (IFile) icu.getResource();
		IProject project = iFile.getProject();
		if (project.equals(ResourcesPlugin.getWorkspace().getRoot().getProject(getPPAProjectName(requestName)))) {
			try {
				MyPPAResourceUtil.cleanUp(iFile);
			} catch (CoreException ce) {
				logger.error("Error during file cleanup.", ce);
			}
		}
	}
	
	/**
	 * Clean up all.
	 */
	public static void cleanUpAll() {
		cleanUpAll(Thread.currentThread().getName());
	}
	
	/**
	 * Clean up all.
	 * 
	 * @param project
	 *            the project
	 */
	public static void cleanUpAll(IProject project) {
		if (project.exists()) {
			int deleted = 0;
			IFolder src = project.getFolder("src");
			final List<IFile> toDelete = new ArrayList<IFile>();
			try {
				src.accept(new IResourceVisitor() {
					
					@Override
					public boolean visit(IResource resource) throws CoreException {
						if (resource.getType() == IResource.FILE) {
							toDelete.add((IFile) resource);
						}
						return true;
					}
					
				});
			} catch (Exception e) {
				logger.error("Error while cleaning up PPA project", e);
			}
			
			for (IFile file : toDelete) {
				try {
					file.delete(true, new NullProgressMonitor());
					deleted++;
				} catch (Exception e) {
					logger.error("Error while cleaning up PPA project", e);
				}
			}
			// PPAUIActivator.getDefault().releaseId(requestName);
			// logger.info("DELETED: " + deleted + " files deleted out of " +
			// toDelete.size());
		}
	}
	
	/**
	 * Clean up all.
	 * 
	 * @param requestName
	 *            the request name
	 */
	public static void cleanUpAll(String requestName) {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(getPPAProjectName(requestName));
		cleanUpAll(project);
	}
	
	/**
	 * Gets the cU.
	 * 
	 * @param file
	 *            the file
	 * @param options
	 *            the options
	 * @return the cU
	 */
	public static CompilationUnit getCU(final File file, final PPAOptions options) {
		return getCU(file, options, Thread.currentThread().getName());
	}
	
	/**
	 * Gets the cU.
	 * 
	 * @param file
	 *            the file
	 * @param options
	 *            the options
	 * @param requestName
	 *            the request name
	 * @return the cU
	 */
	public static CompilationUnit getCU(File file, PPAOptions options, String requestName) {
		CompilationUnit cu = null;
		String fileName = file.getName();
		
		try {
			String packageName = getPackageFromFile(file);
			
			IJavaProject iJavaProject = getProject(requestName);
			
			IFile newFile = MyPPAResourceUtil
			        .copyJavaSourceFile(iJavaProject.getProject(), file, packageName, fileName);
			cu = getCU(newFile, options);
		} catch (Exception e) {
			logger.error("Error while getting CU from PPA", e);
		}
		
		return cu;
	}
	
	/**
	 * Gets the cU.
	 * 
	 * @param file
	 *            the file
	 * @param options
	 *            the options
	 * @return the cU
	 */
	public static CompilationUnit getCU(IFile file, PPAOptions options) {
		CompilationUnit cu = null;
		try {
			
			ICompilationUnit icu = JavaCore.createCompilationUnitFrom(file);
			
			// System.out.println(file.getProject().exists());
			
			JavaProject javaProject = (JavaProject) JavaCore.create(file.getProject());
			
			// System.out.println(javaProject.toString());
			
			PPATypeRegistry registry = new PPATypeRegistry(javaProject);
			ASTNode node = null;
			PPAASTParser parser2 = new PPAASTParser(AST.JLS3);
			parser2.setStatementsRecovery(true);
			parser2.setResolveBindings(true);
			parser2.setSource(icu);
			node = parser2.createAST(null);
			PPAEngine ppaEngine = new PPAEngine(registry, options);
			
			cu = (CompilationUnit) node;
			
			ppaEngine.addUnitToProcess(cu);
			ppaEngine.doPPA();
			ppaEngine.reset();
		} catch (Exception e) {
			logger.error("Error while getting CU from PPA", e);
		}
		
		return cu;
	}
	
	/**
	 * Gets the cU no ppa.
	 * 
	 * @param file
	 *            the file
	 * @return the cU no ppa
	 */
	public static CompilationUnit getCUNoPPA(IFile file) {
		CompilationUnit cu = null;
		try {
			ICompilationUnit icu = JavaCore.createCompilationUnitFrom(file);
			ASTNode node = null;
			PPAASTParser parser2 = new PPAASTParser(AST.JLS3);
			parser2.setStatementsRecovery(true);
			parser2.setResolveBindings(true);
			parser2.setSource(icu);
			node = parser2.createAST(null);
			cu = (CompilationUnit) node;
		} catch (Exception e) {
			logger.error("Error while getting CU without PPA", e);
		}
		
		return cu;
	}
	
	/**
	 * Gets the node.
	 * 
	 * @param file
	 *            the file
	 * @param options
	 *            the options
	 * @param kind
	 *            the kind
	 * @return the node
	 */
	public static ASTNode getNode(IFile file, PPAOptions options, int kind) {
		ASTNode node = null;
		try {
			ICompilationUnit icu = JavaCore.createCompilationUnitFrom(file);
			PPATypeRegistry registry = new PPATypeRegistry((JavaProject) JavaCore.create(icu.getUnderlyingResource()
			        .getProject()));
			PPAASTParser parser2 = new PPAASTParser(AST.JLS3);
			parser2.setStatementsRecovery(true);
			parser2.setResolveBindings(true);
			parser2.setSource(icu);
			parser2.setKind(kind);
			node = parser2.createAST(null);
			PPAEngine ppaEngine = new PPAEngine(registry, options);
			
			ppaEngine.addUnitToProcess(node);
			ppaEngine.doPPA();
			ppaEngine.reset();
		} catch (Exception e) {
			logger.error("Error while getting CU from PPA", e);
		}
		
		return node;
	}
	
	/**
	 * Gets the package from file.
	 * 
	 * @param file
	 *            the file
	 * @return the package from file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static String getPackageFromFile(File file) throws IOException {
		String packageName = "";
		PPAASTParser parser2 = new PPAASTParser(AST.JLS3);
		parser2.setStatementsRecovery(true);
		parser2.setResolveBindings(true);
		parser2.setSource(MyPPAResourceUtil.getContent(file).toCharArray());
		CompilationUnit cu = (CompilationUnit) parser2.createAST(null);
		PackageDeclaration pDec = cu.getPackage();
		if (pDec != null) {
			packageName = pDec.getName().getFullyQualifiedName();
		}
		return packageName;
	}
	
	/**
	 * Gets the pPA project name.
	 * 
	 * @param requestName
	 *            the request name
	 * @return the pPA project name
	 */
	public static String getPPAProjectName(String requestName) {
		// PPAUIActivator activator = PPAUIActivator.getDefault();
		String projectName = "__PPA_PROJECT0";
		
		// projectName += activator.acquireId(requestName);
		
		return projectName;
	}
	
	/**
	 * Gets the project.
	 * 
	 * @param name
	 *            the name
	 * @return the project
	 */
	public static IJavaProject getProject(String name) {
		IJavaProject project = null;
		try {
			IProject javaProject = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
			if (!javaProject.exists()) {
				javaProject.create(new NullProgressMonitor());
				javaProject.open(IResource.BACKGROUND_REFRESH, new NullProgressMonitor());
				IProjectDescription description = javaProject.getDescription();
				String[] natures = description.getNatureIds();
				String[] newNatures = new String[natures.length + 1];
				System.arraycopy(natures, 0, newNatures, 0, natures.length);
				newNatures[natures.length] = JavaCore.NATURE_ID;
				description.setNatureIds(newNatures);
				javaProject.setDescription(description, new NullProgressMonitor());
				
				project = JavaCore.create(javaProject);
			} else {
				project = (IJavaProject) javaProject.getNature(JavaCore.NATURE_ID);
			}
			if (!javaProject.isOpen()) {
				javaProject.open(IResource.BACKGROUND_REFRESH, new NullProgressMonitor());
			}
			IFolder folder = javaProject.getFolder("src");
			if (!folder.exists()) {
				folder.create(true, true, new NullProgressMonitor());
			}
		} catch (CoreException e) {
			Logger.getLogger(MyPPAUtil.class).error(e.getMessage(), e);
		}
		return project;
	}
	
	/**
	 * <p>
	 * </p>
	 * .
	 * 
	 * @param units
	 *            the units
	 * @return the c us with one ppa pass
	 */
	public List<CompilationUnit> getCUsWithOnePPAPass(List<ICompilationUnit> units) {
		
		if (units.size() == 0) {
			return new ArrayList<CompilationUnit>();
		}
		
		final List<CompilationUnit> astList = new ArrayList<CompilationUnit>();
		try {
			
			// a hack to get the current project.
			JavaProject jproject = (JavaProject) JavaCore.create(units.get(0).getUnderlyingResource().getProject());
			PPATypeRegistry registry = new PPATypeRegistry(jproject);
			final PPAEngine ppaEngine = new PPAEngine(registry, new PPAOptions());
			
			PPAASTParser parser2 = new PPAASTParser(AST.JLS3);
			parser2.setStatementsRecovery(true);
			parser2.setResolveBindings(true);
			parser2.setProject(jproject);
			
			ASTRequestor requestor = new ASTRequestor() {
				
				@Override
				public void acceptAST(ICompilationUnit source, CompilationUnit ast) {
					astList.add(ast);
					ppaEngine.addUnitToProcess(ast);
				}
			};
			
			parser2.createASTs(units.toArray(new ICompilationUnit[units.size()]), new String[0], requestor, null);
			
			ppaEngine.doPPA();
			ppaEngine.reset();
			
		} catch (JavaModelException jme) {
			jme.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return astList;
	}
}
