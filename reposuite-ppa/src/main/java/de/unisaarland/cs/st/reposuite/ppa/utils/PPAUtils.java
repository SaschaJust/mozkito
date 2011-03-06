package de.unisaarland.cs.st.reposuite.ppa.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.PPAASTParser;
import org.eclipse.jdt.core.dom.PPAEngine;
import org.eclipse.jdt.core.dom.PPATypeRegistry;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.internal.core.JavaProject;

import ca.mcgill.cs.swevo.ppa.PPAOptions;
import de.unisaarland.cs.st.reposuite.ppa.internal.visitors.ChangeOperationVisitor;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaElementCache;
import de.unisaarland.cs.st.reposuite.ppa.visitors.PPAMethodCallVisitor;
import de.unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor;
import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSRevision;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * The Class PPAUtils.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class PPAUtils {
	
	public static void generateChangeOperations(final Repository repository, final RCSTransaction transaction,
	        final Collection<ChangeOperationVisitor> visitors) {
		
		//JavaElementCache.reset();
		int counter = 1;
		int size = transaction.getRevisions().size();
		for (RCSRevision revision : transaction.getRevisions()) {
			if (Logger.logInfo()) {
				Logger.info("Processing revision " + counter + " / " + size + " ... ");
				++counter;
			}
			switch (revision.getChangeType()) {
				
				case Added:
					ChangeOperationUtils.generateChangeOperationsForAddedFile(repository, revision, visitors);
					break;
					
				case Deleted:
					ChangeOperationUtils.generateChangeOperationsForDeletedFile(repository, revision, visitors);
					break;
					
				case Modified:
					ChangeOperationUtils.generateChangeOperationsForModifiedFile(repository, revision, visitors);
					break;
			}
		}
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
	public static CompilationUnit getCU(final File file, final PPAOptions options, final String requestName) {
		CompilationUnit cu = null;
		String fileName = file.getName();
		
		try {
			String packageName = getPackageFromFile(file);
			IJavaProject javaProject = getProject(requestName);
			IFile newFile = PPAResourceUtil.copyJavaSourceFile(javaProject.getProject(), file, packageName, fileName);
			cu = getCU(newFile, options);
		} catch (Exception e) {
			if (Logger.logError()) {
				Logger.error("Error while getting CU from PPA", e);
			}
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
	public static CompilationUnit getCU(final IFile file, final PPAOptions options) {
		CompilationUnit cu = null;
		try {
			ICompilationUnit icu = JavaCore.createCompilationUnitFrom(file);
			PPATypeRegistry registry = new PPATypeRegistry((JavaProject) JavaCore.create(icu.getUnderlyingResource()
					.getProject()));
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
		} catch (JavaModelException jme) {
			// Retry with the file version.
			if (Logger.logWarn()) {
				Logger.warn("Warning while getting CU from PPA");
			}
			if (Logger.logDebug()) {
				Logger.debug("Exception", jme);
			}
			cu = getCU(file.getLocation().toFile(), options);
		} catch (Exception e) {
			if (Logger.logError()) {
				Logger.error("Error while getting CU from PPA", e);
			}
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
	public static CompilationUnit getCUNoPPA(final IFile file) {
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
			if (Logger.logError()) {
				Logger.error("Error while getting CU without PPA", e);
			}
		}
		
		return cu;
	}
	
	/**
	 * Gets the c us.
	 * 
	 * @param files
	 *            the files
	 * @param options
	 *            the options
	 * @return the c us
	 */
	public static List<CompilationUnit> getCUs(final List<File> files, final PPAOptions options) {
		return getCUs(files, options, Thread.currentThread().getName());
	}
	
	/**
	 * Gets the c us.
	 * 
	 * @param files
	 *            the files
	 * @param options
	 *            the options
	 * @param requestName
	 *            the request name
	 * @return the c us
	 */
	public static List<CompilationUnit> getCUs(final List<File> files, final PPAOptions options,
			final String requestName) {
		List<CompilationUnit> cus = new ArrayList<CompilationUnit>();
		List<IFile> iFiles = new ArrayList<IFile>();
		
		for (File file : files) {
			String fileName = file.getName();
			try {
				String packageName = getPackageFromFile(file);
				IJavaProject javaProject = getProject(requestName);
				IFile newFile = PPAResourceUtil.copyJavaSourceFile(javaProject.getProject(), file, packageName,
						fileName);
				iFiles.add(newFile);
			} catch (Exception e) {
				if (Logger.logError()) {
					Logger.error("Error while getting IFile from PPA", e);
				}
			}
		}
		
		for (IFile file : iFiles) {
			if (Logger.logInfo()) {
				Logger.info("Getting CU for file: " + file.getLocation().toOSString());
			}
			CompilationUnit cu = getCU(file, options);
			if (cu == null) {
				cu = getCUNoPPA(file);
			}
			cus.add(cu);
		}
		
		return cus;
	}
	
	public static JavaElementLocations getJavaElementLocationsByFile(final File file, final String filePrefixPath,
			final String[] packageFilter) {
		PPAMethodCallVisitor methodCallVisitor = new PPAMethodCallVisitor();
		PPAOptions ppaOptions = new PPAOptions();
		
		JavaElementCache elemCache = new JavaElementCache();
		
		CompilationUnit cu = getCU(file, ppaOptions);
		if (cu != null) {
			PPATypeVisitor typeVisitor = new PPATypeVisitor(cu, file, filePrefixPath, packageFilter, elemCache);
			typeVisitor.registerVisitor(methodCallVisitor);
			cu.accept(typeVisitor);
		} else {
			if (Logger.logError()) {
				Logger.error("Could not analyze file " + file.getAbsolutePath()
						+ ". CompilationUnit cannot be created. Skipping ... ");
			}
		}
		return elemCache.getJavaElementLocations();
	}
	
	/**
	 * Gets the java method elements of all java files within
	 * <code>sourceDir</code>.
	 * 
	 * @param sourceDir
	 *            the source dir
	 * @param packageFilter
	 *            the package filter
	 * @param resetDefinitionCache
	 *            the reset definition cache
	 * @return the java method elements
	 */
	public static JavaElementLocations getJavaElementLocationsByFile(final File sourceDir, final String[] packageFilter) {
		
		try {
			Iterator<File> fileIterator = FileUtils.getFileIterator(sourceDir, new String[] { "java" }, true);
			return getJavaElementLocationsByFile(fileIterator, sourceDir.getAbsolutePath(), packageFilter);
		} catch (IOException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		}
		return new JavaElementLocations();
	}
	
	/**
	 * Gets the java method elements of all files covered by the iterator.
	 * 
	 * @param fileIterator
	 *            the file iterator
	 * @param filePrefixPath
	 *            the file prefix path
	 * @param packageFilter
	 *            the package filter
	 * @param resetDefinitionCache
	 *            the reset definition cache
	 * @return the java method elements
	 */
	public static JavaElementLocations getJavaElementLocationsByFile(final Iterator<File> fileIterator,
			final String filePrefixPath, final String[] packageFilter) {
		PPAMethodCallVisitor methodCallVisitor = new PPAMethodCallVisitor();
		PPAOptions ppaOptions = new PPAOptions();
		
		JavaElementCache elemCache = new JavaElementCache();
		
		while (fileIterator.hasNext()) {
			File file = fileIterator.next();
			CompilationUnit cu = getCU(file, ppaOptions);
			if (cu == null) {
				if (Logger.logError()) {
					Logger.error("Could not analyze file " + file.getAbsolutePath()
							+ ". CompilationUnit cannot be created. Skipping ... ");
				}
				continue;
			}
			PPATypeVisitor typeVisitor = new PPATypeVisitor(cu, file, filePrefixPath, packageFilter, elemCache);
			typeVisitor.registerVisitor(methodCallVisitor);
			cu.accept(typeVisitor);
		}
		
		return elemCache.getJavaElementLocations();
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
	public static String getPackageFromFile(final File file) throws IOException {
		String packageName = "";
		PPAASTParser parser2 = new PPAASTParser(AST.JLS3);
		parser2.setStatementsRecovery(true);
		parser2.setResolveBindings(true);
		parser2.setSource(PPAResourceUtil.getContent(file).toCharArray());
		CompilationUnit cu = (CompilationUnit) parser2.createAST(null);
		PackageDeclaration pDec = cu.getPackage();
		if (pDec != null) {
			packageName = pDec.getName().getFullyQualifiedName();
		}
		return packageName;
	}
	
	/**
	 * Gets the project.
	 * 
	 * @param name
	 *            the name
	 * @return the project
	 */
	private static IJavaProject getProject(final String name) {
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
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		}
		return project;
	}
	
}
