package de.unisaarland.cs.st.reposuite.ppa.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
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
import de.unisaarland.cs.st.reposuite.ppa.model.ChangeOperations;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaClassDefinition;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaElementCache;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaElementDefinition;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaElementLocation;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaElementLocation.LineCover;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaElementLocations;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaMethodCall;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaMethodDefinition;
import de.unisaarland.cs.st.reposuite.ppa.visitors.PPAMethodCallVisitor;
import de.unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor;
import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.elements.ChangeType;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSFile;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSRevision;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.utils.Condition;
import de.unisaarland.cs.st.reposuite.utils.DiffUtils;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import difflib.DeleteDelta;
import difflib.Delta;
import difflib.InsertDelta;

/**
 * The Class PPAUtils.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class PPAUtils {
	
	/**
	 * Gets the change operations.
	 * 
	 * @param repository
	 *            the repository
	 * @param transaction
	 *            the transaction
	 * @return the change operations
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Collection<JavaChangeOperation> getChangeOperations(final Repository repository,
			final RCSTransaction transaction) {
		
		ChangeOperations operations = new ChangeOperations();
		
		String transactionID = transaction.getId();
		RCSTransaction parentTransaction = transaction.getParent(transaction.getBranch());
		String parentTransactionID = parentTransaction.getId();
		
		Collection<RCSRevision> revisions = transaction.getRevisions();
		Map<String, RCSRevision> addedRCSRevs = new HashMap<String, RCSRevision>();
		Map<String, RCSRevision> removedRCSRevs = new HashMap<String, RCSRevision>();
		Map<String, RCSRevision> changedRCSRevs = new HashMap<String, RCSRevision>();
		
		for (RCSRevision revision : revisions) {
			RCSFile rcsFile = revision.getChangedFile();
			String tmpPath = rcsFile.getPath(transaction);
			if (!tmpPath.endsWith(".java")) {
				if (Logger.logDebug()) {
					Logger.debug("Ignoring non-Java file " + tmpPath);
				}
				continue;
			}
			if (tmpPath.startsWith("/")) {
				tmpPath = tmpPath.substring(1);
			}
			if (revision.getChangeType().equals(ChangeType.Added)) {
				addedRCSRevs.put(tmpPath, revision);
			} else if (revision.getChangeType().equals(ChangeType.Deleted)) {
				removedRCSRevs.put(tmpPath, revision);
			} else if (revision.getChangeType().equals(ChangeType.Modified)) {
				changedRCSRevs.put(tmpPath, revision);
			}
			//TODO what's with renamed files?
		}
		
		//update working copy to parent transaction
		File checkoutPath = repository.checkoutPath("/", parentTransactionID);
		
		//handle old files BEGIN
		Set<File> oldFiles = new HashSet<File>();
		for (String filePath : removedRCSRevs.keySet()) {
			oldFiles.add(new File(checkoutPath + FileUtils.fileSeparator + filePath));
		}
		for (String filePath : changedRCSRevs.keySet()) {
			oldFiles.add(new File(checkoutPath + FileUtils.fileSeparator + filePath));
		}
		
		JavaElementLocations oldElems = PPAUtils.getJavaElementLocationsByFile(oldFiles.iterator(),
				checkoutPath.getAbsolutePath(), new String[0], false);
		//handle old files END
		
		//update working copy
		checkoutPath = repository.checkoutPath("/", transactionID);
		
		//handle new files BEGIN
		Set<File> newFiles = new HashSet<File>();
		for (String filePath : addedRCSRevs.keySet()) {
			newFiles.add(new File(checkoutPath + FileUtils.fileSeparator + filePath));
		}
		for (String filePath : changedRCSRevs.keySet()) {
			newFiles.add(new File(checkoutPath + FileUtils.fileSeparator + filePath));
		}
		
		JavaElementLocations newElems = PPAUtils.getJavaElementLocationsByFile(newFiles.iterator(),
				checkoutPath.getAbsolutePath(), new String[0], false);
		//handle new files END
		
		//for added and deleted files, we can just add an change operation
		for (String addedPath : addedRCSRevs.keySet()) {
			if (!newElems.containsFilePath(addedPath)) {
				if (Logger.logWarn()) {
					Logger.warn("Found file `" + addedPath + "` for which no new JavaElements were found!");
				}
				continue;
			}
			RCSRevision revision = addedRCSRevs.get(addedPath);
			for (JavaElementLocation<JavaClassDefinition> classDef : newElems.getClassDefs(addedPath)) {
				operations.add(new JavaChangeOperation(ChangeType.Added, classDef, revision));
			}
			for (JavaElementLocation<JavaMethodDefinition> methDef : newElems.getMethodDefs(addedPath)) {
				operations.add(new JavaChangeOperation(ChangeType.Added, methDef, revision));
			}
			for (JavaElementLocation<JavaMethodCall> methCall : newElems.getMethodCalls(addedPath)) {
				operations.add(new JavaChangeOperation(ChangeType.Added, methCall, revision));
			}
		}
		for (String removedPath : removedRCSRevs.keySet()) {
			if (!oldElems.containsFilePath(removedPath)) {
				if (Logger.logWarn()) {
					Logger.warn("Found file `" + removedPath + "` for which no old JavaElements were found!");
				}
				continue;
			}
			RCSRevision revision = removedRCSRevs.get(removedPath);
			for (JavaElementLocation<JavaClassDefinition> classDef : oldElems.getClassDefs(removedPath)) {
				operations.add(new JavaChangeOperation(ChangeType.Deleted, classDef, revision));
			}
			for (JavaElementLocation<JavaMethodDefinition> methDef : oldElems.getMethodDefs(removedPath)) {
				operations.add(new JavaChangeOperation(ChangeType.Deleted, methDef, revision));
			}
			for (JavaElementLocation<JavaMethodCall> methCall : oldElems.getMethodCalls(removedPath)) {
				operations.add(new JavaChangeOperation(ChangeType.Deleted, methCall, revision));
			}
		}
		
		//handle changed files
		for (String changedPath : changedRCSRevs.keySet()) {
			
			//generate diff
			if (changedPath.startsWith("/")) {
				changedPath = changedPath.substring(1);
			}
			Collection<Delta> diff = repository.diff(changedPath, parentTransactionID, transactionID);
			RCSRevision revision = changedRCSRevs.get(changedPath);
			
			for (Delta delta : diff) {
				if (delta instanceof DeleteDelta) {
					//corresponding elements to be removed
					HashSet<Integer> lines = DiffUtils.getLineNumbers(delta.getOriginal());
					for (JavaElementLocation javaElem : oldElems.getElements(changedPath)) {
						LineCover cover = javaElem.coversAnyLine(lines);
						switch (cover) {
							case DEFINITION:
							case DEF_AND_BODY:
								operations.add(new JavaChangeOperation(ChangeType.Deleted, javaElem, revision));
								break;
							case BODY:
								operations.add(new JavaChangeOperation(ChangeType.Modified, javaElem, revision));
								break;
						}
					}
				} else if (delta instanceof InsertDelta) {
					//corresponding elements to be added
					HashSet<Integer> lines = DiffUtils.getLineNumbers(delta.getRevised());
					for (JavaElementLocation javaElem : newElems.getElements(changedPath)) {
						LineCover cover = javaElem.coversAnyLine(lines);
						switch (cover) {
							case DEFINITION:
							case DEF_AND_BODY:
								operations.add(new JavaChangeOperation(ChangeType.Added, javaElem, revision));
								break;
							case BODY:
								operations.add(new JavaChangeOperation(ChangeType.Modified, javaElem, revision));
								break;
						}
					}
				} else {
					//make a collection diff
					HashSet<Integer> oldLines = DiffUtils.getLineNumbers(delta.getOriginal());
					HashSet<Integer> newLines = DiffUtils.getLineNumbers(delta.getRevised());
					
					Collection<JavaElementLocation<JavaElementDefinition>> modifiedDefCandidates = new HashSet<JavaElementLocation<JavaElementDefinition>>();
					
					Collection<JavaElementLocation<JavaElementDefinition>> removeDefCandidates = new HashSet<JavaElementLocation<JavaElementDefinition>>();
					for (JavaElementLocation def : oldElems.getDefs(changedPath)) {
						LineCover cover = def.coversAnyLine(oldLines);
						switch (cover) {
							case BODY:
								modifiedDefCandidates.add(def);
								break;
							case DEF_AND_BODY:
							case DEFINITION:
								removeDefCandidates.add(def);
								break;
						}
					}
					Collection<JavaElementLocation<JavaElementDefinition>> addDefCandidates = new HashSet<JavaElementLocation<JavaElementDefinition>>();
					for (JavaElementLocation def : newElems.getDefs(changedPath)) {
						LineCover cover = def.coversAnyLine(newLines);
						switch (cover) {
							case BODY:
								modifiedDefCandidates.add(def);
								break;
							case DEF_AND_BODY:
							case DEFINITION:
								addDefCandidates.add(def);
								break;
						}
					}
					
					Collection<JavaElementLocation<JavaElementDefinition>> defsToRemove = new HashSet<JavaElementLocation<JavaElementDefinition>>();
					Collection<JavaElementLocation<JavaElementDefinition>> defsToAdd = new HashSet<JavaElementLocation<JavaElementDefinition>>();
					
					
					defsToRemove.addAll(CollectionUtils.subtract(removeDefCandidates, addDefCandidates));
					defsToAdd.addAll(CollectionUtils.subtract(addDefCandidates, removeDefCandidates));
					
					Map<String, TreeSet<JavaElementLocation<JavaMethodCall>>> removeCallCandidates = new HashMap<String, TreeSet<JavaElementLocation<JavaMethodCall>>>();
					if (oldElems.getMethodCalls(changedPath) != null) {
						for (JavaElementLocation<JavaMethodCall> call : oldElems.getMethodCalls(changedPath)) {
							LineCover cover = call.coversAnyLine(oldLines);
							if (!cover.equals(LineCover.FALSE)) {
								if (!removeCallCandidates.containsKey(call.getElement().getFullQualifiedName())) {
									removeCallCandidates.put(call.getElement().getFullQualifiedName(),
											new TreeSet<JavaElementLocation<JavaMethodCall>>());
								}
								removeCallCandidates.get(call.getElement().getFullQualifiedName()).add(call);
							}
						}
					}
					Map<String, TreeSet<JavaElementLocation<JavaMethodCall>>> addCallCandidates = new HashMap<String, TreeSet<JavaElementLocation<JavaMethodCall>>>();
					if (newElems.getMethodCalls(changedPath) != null) {
						for (JavaElementLocation<JavaMethodCall> call : newElems.getMethodCalls(changedPath)) {
							LineCover cover = call.coversAnyLine(newLines);
							if (!cover.equals(LineCover.FALSE)) {
								if (!addCallCandidates.containsKey(call.getElement().getFullQualifiedName())) {
									addCallCandidates.put(call.getElement().getFullQualifiedName(),
											new TreeSet<JavaElementLocation<JavaMethodCall>>());
								}
								addCallCandidates.get(call.getElement().getFullQualifiedName()).add(call);
							}
						}
					}
					
					//if a line was changed: detect added and removed calls that are most likely corresponding
					for (String addedCallId : addCallCandidates.keySet()) {
						if (removeCallCandidates.containsKey(addedCallId)) {
							TreeSet<JavaElementLocation<JavaMethodCall>> delSet = removeCallCandidates.get(addedCallId);
							TreeSet<JavaElementLocation<JavaMethodCall>> addSet = addCallCandidates.get(addedCallId);
							for (JavaElementLocation<JavaMethodCall> addedCall : new TreeSet<JavaElementLocation<JavaMethodCall>>(
									addCallCandidates.get(addedCallId))) {
								JavaElementLocation<JavaMethodCall> ceiling = delSet.ceiling(addedCall);
								JavaElementLocation<JavaMethodCall> floor = delSet.floor(addedCall);
								Condition.check((ceiling != null) || (floor != null), "Must find a nearest element!");
								JavaElementLocation<JavaMethodCall> correspondent = null;
								if (ceiling == null) {
									correspondent = floor;
								} else if (floor == null) {
									correspondent = ceiling;
								} else {
									int floorDist = Math.abs(addedCall.getPosition() - floor.getPosition());
									int ceilingDist = Math.abs(addedCall.getPosition() - ceiling.getPosition());
									if (floorDist < ceilingDist) {
										correspondent = floor;
									} else {
										correspondent = ceiling;
									}
								}
								Condition.check(correspondent != null, "Must have found correspondent");
								delSet.remove(correspondent);
								addSet.remove(addedCall);
							}
						}
					}

					//method calls that are still present in their collections make up the operations
					for (TreeSet<JavaElementLocation<JavaMethodCall>> methodCallsToDelete : removeCallCandidates
							.values()) {
						for (JavaElementLocation<JavaMethodCall> methodCall : methodCallsToDelete) {
							operations.add(new JavaChangeOperation(ChangeType.Deleted, methodCall, revision));
						}
					}
					for (TreeSet<JavaElementLocation<JavaMethodCall>> methodCallsToAdd : addCallCandidates.values()) {
						for (JavaElementLocation<JavaMethodCall> methodCall : methodCallsToAdd) {
							operations.add(new JavaChangeOperation(ChangeType.Added, methodCall, revision));
						}
					}
					for (JavaElementLocation<JavaElementDefinition> methodDefToDelete : defsToRemove) {
						operations.add(new JavaChangeOperation(ChangeType.Deleted, methodDefToDelete, revision));
					}
					for (JavaElementLocation<JavaElementDefinition> methodDefToAdd : defsToAdd) {
						operations.add(new JavaChangeOperation(ChangeType.Added, methodDefToAdd, revision));
					}
					for (JavaElementLocation<JavaElementDefinition> methodDefModified : modifiedDefCandidates) {
						operations.add(new JavaChangeOperation(ChangeType.Modified, methodDefModified, revision));
					}
				}
			}
		}
		return operations.getOperations();
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
	public static JavaElementLocations getJavaElementLocationsByFile(final File sourceDir,
			final String[] packageFilter, final boolean resetDefinitionCache) {
		
		try {
			Iterator<File> fileIterator = FileUtils.getFileIterator(sourceDir, new String[] { "java" }, true);
			return getJavaElementLocationsByFile(fileIterator, sourceDir.getAbsolutePath(), packageFilter,
					resetDefinitionCache);
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
			final String filePrefixPath, final String[] packageFilter, final boolean resetDefinitionCache) {
		if (resetDefinitionCache) {
			JavaElementCache.reset();
		}
		PPAMethodCallVisitor methodCallVisitor = new PPAMethodCallVisitor();
		PPAOptions ppaOptions = new PPAOptions();
		
		JavaElementCache elemCache = new JavaElementCache();
		
		while (fileIterator.hasNext()) {
			File file = fileIterator.next();
			CompilationUnit cu = getCU(file, ppaOptions);
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
