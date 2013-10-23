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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

import org.apache.commons.collections.CollectionUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTRequestor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.PPAASTParser;
import org.eclipse.jdt.core.dom.PPAEngine;
import org.eclipse.jdt.core.dom.PPATypeRegistry;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.internal.core.JavaProject;
import org.mozkito.codeanalysis.internal.visitors.ChangeOperationVisitor;
import org.mozkito.codeanalysis.model.ChangeOperations;
import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.codeanalysis.model.JavaElement;
import org.mozkito.codeanalysis.model.JavaElementFactory;
import org.mozkito.codeanalysis.model.JavaElementLocation;
import org.mozkito.codeanalysis.model.JavaElementLocation.LineCover;
import org.mozkito.codeanalysis.model.JavaElementLocationSet;
import org.mozkito.codeanalysis.model.JavaElementLocation_;
import org.mozkito.codeanalysis.model.JavaElement_;
import org.mozkito.codeanalysis.model.JavaMethodCall;
import org.mozkito.codeanalysis.model.JavaMethodDefinition;
import org.mozkito.codeanalysis.visitors.PPAMethodCallVisitor;
import org.mozkito.codeanalysis.visitors.PPATypeVisitor;
import org.mozkito.persistence.Criteria;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.utililities.diff.DiffUtils;
import org.mozkito.utilities.datastructures.Tuple;
import org.mozkito.utilities.io.FileUtils;
import org.mozkito.versions.Repository;
import org.mozkito.versions.elements.ChangeType;
import org.mozkito.versions.exceptions.NoSuchHandleException;
import org.mozkito.versions.exceptions.RepositoryOperationException;
import org.mozkito.versions.model.ChangeSet;
import org.mozkito.versions.model.Revision;

import ca.mcgill.cs.swevo.ppa.PPAOptions;
import difflib.DeleteDelta;
import difflib.Delta;
import difflib.InsertDelta;

/**
 * The Class PPAUtils.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class PPAUtils {
	
	/**
	 * The Class CopyThread.
	 */
	private static class CopyThread extends Thread {
		
		/** The project. */
		private final IProject   project;
		
		/** The file. */
		private final File       file;
		
		/** The packagename. */
		private final String     packagename;
		
		/** The filename. */
		private final String     filename;
		
		/** The options. */
		private final PPAOptions options;
		
		/** The cu. */
		private CompilationUnit  cu;
		
		/** The i file. */
		private IFile            iFile;
		
		/**
		 * Instantiates a new copy thread.
		 * 
		 * @param project
		 *            the project
		 * @param file
		 *            the file
		 * @param packagename
		 *            the packagename
		 * @param filename
		 *            the filename
		 * @param options
		 *            the options
		 */
		public CopyThread(final IProject project, final File file, final String packagename, final String filename,
		        final PPAOptions options) {
			this.project = project;
			this.file = file;
			this.packagename = packagename;
			this.filename = filename;
			this.options = options;
		}
		
		/**
		 * Gets the compilation unit.
		 * 
		 * @return the compilation unit
		 */
		public CompilationUnit getCompilationUnit() {
			return this.cu;
		}
		
		/**
		 * Gets the i file.
		 * 
		 * @return the i file
		 */
		public IFile getIFile() {
			return this.iFile;
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			try {
				this.iFile = PPAResourceUtil.copyJavaSourceFile(this.project, this.file, this.packagename,
				                                                this.filename);
				if (this.iFile == null) {
					if (Logger.logError()) {
						Logger.error("Error while getting CU from PPA. Timeout copy to workspace exceeded.");
					}
					return;
				}
				this.cu = getCU(this.iFile, this.options);
			} catch (final CoreException e) {
				if (Logger.logError()) {
					Logger.error(e, "Could not import file into eclipse workspace.");
				}
			} catch (final IOException e) {
				if (Logger.logError()) {
					Logger.error(e, "Could not import file into eclipse workspace.");
				}
			}
		}
		
	}
	
	/** The Constant DIRECTORY_PREFIX. */
	private static final String DIRECTORY_PREFIX = "/";
	
	/**
	 * Cleanup workspace.
	 * 
	 * @throws CoreException
	 *             the core exception
	 */
	public static void cleanupWorkspace() throws CoreException {
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.getRoot().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		workspace.getRoot().clearHistory(new NullProgressMonitor());
	}
	
	/**
	 * Find previous call corresponding to the supplied JavaChangeOperation that must be a JavaMethodCall delete
	 * operation.
	 * 
	 * @param persistenceUtil
	 *            the persistence util
	 * @param op
	 *            the JavaChangeOperation to base the search on
	 * @return the previous JavaChangeOperation on the very same JavaMethodCall within the same file, same method and
	 *         closest location. Returns null if no such operation exists or if supplied JavaChangeOperation is not
	 *         based on a JavaMethodCall or not a delete operation.
	 */
	@NoneNull
	public static JavaChangeOperation findPreviousCall(final PersistenceUtil persistenceUtil,
	                                                   final JavaChangeOperation op) {
		
		final JavaElementLocation location = op.getChangedElementLocation();
		final JavaElement element = location.getElement();
		
		if ((!(element instanceof JavaMethodCall)) || (!op.getChangeType().equals(ChangeType.Deleted))) {
			if (Logger.logDebug()) {
				Logger.debug("Cannot find previous JMethodCall operation based on a non method call delete operation!");
			}
			return null;
		}
		
		final Criteria<JavaElementLocation> criteria = persistenceUtil.createCriteria(JavaElementLocation.class);
		final CriteriaBuilder cb = criteria.getBuilder();
		final Root<JavaElementLocation> root = criteria.getRoot();
		final Predicate eq = cb.equal(root.get("element"), element);
		final Predicate eq1 = cb.equal(root.get("filePath"), location.getFilePath());
		
		final Predicate lt = cb.lt(criteria.getRoot().get(JavaElementLocation_.id), location.getId());
		criteria.getQuery().where(cb.and(cb.and(eq, eq1), lt));
		criteria.getQuery().orderBy(cb.asc(root.get("id")));
		List<JavaElementLocation> hits = persistenceUtil.load(criteria);
		
		if (hits.isEmpty()) {
			return null;
		}
		
		// should be in same method
		// get the javaelement location of surrounding method definition
		final String defIdQueryStr = "select * from javaelementlocation WHERE id = (select max(l.id) from javaelementlocation l, javaelement e, javachangeoperation o, revision r where filepath = '"
		        + location.getFilePath()
		        + "' AND startline <= "
		        + location.getStartLine()
		        + " and endline >= "
		        + location.getStartLine()
		        + " AND l.element_generatedid = e.generatedid AND e.type = 'JAVAMETHODDEFINITION' AND l.id = o.changedelementlocation_id AND o.revision_revisionid = r.revisionid AND (l.id < "
		        + location.getId() + " OR transaction_id = '" + op.getRevision().getChangeSet().getId() + "'))";
		final Query defIdQuery = persistenceUtil.createNativeQuery(defIdQueryStr, JavaElementLocation.class);
		
		@SuppressWarnings ("unchecked")
		final List<JavaElementLocation> methodDefHits = defIdQuery.getResultList();
		
		if (!methodDefHits.isEmpty()) {
			final List<JavaElementLocation> strongCandidates = new LinkedList<JavaElementLocation>();
			final JavaElementLocation methodDefinition = methodDefHits.get(0);
			// check which hit is in the same method
			for (final JavaElementLocation tmpLoc : hits) {
				if (!methodDefinition.coversLine(tmpLoc.getStartLine()).equals(LineCover.FALSE)) {
					strongCandidates.add(tmpLoc);
				}
			}
			hits = strongCandidates;
		}
		
		// now search for the closest call
		int minDistance = Integer.MAX_VALUE;
		JavaElementLocation bestHit = null;
		for (final JavaElementLocation loc : hits) {
			final int distance = Math.abs(loc.getStartLine() - location.getStartLine());
			if (distance < minDistance) {
				minDistance = distance;
				bestHit = loc;
			}
		}
		
		if (bestHit == null) {
			return null;
		}
		
		// get the corresponding javachangeoperation
		
		final Criteria<JavaChangeOperation> operationCriteria = persistenceUtil.createCriteria(JavaChangeOperation.class);
		operationCriteria.eq("changedElementLocation", bestHit);
		final List<JavaChangeOperation> result = persistenceUtil.load(operationCriteria, 1);
		if (result.isEmpty()) {
			return null;
		}
		return result.get(0);
		
	}
	
	/**
	 * Finds the change operation that touched the definition corresponding to the JavaElement definition.
	 * 
	 * @param persistenceUtil
	 *            the persistence util to use for accessing the relational database
	 * @param op
	 *            the operation containing the JavaElement to search for
	 * @return the JavaChangeOperation that touched the definition corresponding to the JavaElement associated to
	 *         <code>op</code>. If no such operation exists, returns <code>null</code>.
	 */
	@NoneNull
	public static JavaChangeOperation findPreviousDefinition(final PersistenceUtil persistenceUtil,
	                                                         final JavaChangeOperation op) {
		
		final JavaElementLocation location = op.getChangedElementLocation();
		final JavaElement element = location.getElement();
		JavaElement searchElement = element;
		final ChangeSet opTransaction = op.getRevision().getChangeSet();
		
		// distinguish between definitions and calls
		if (element instanceof JavaMethodCall) {
			final String[] defNames = getDefinitionNamesForCallName((JavaMethodCall) element);
			
			for (final String elementName : defNames) {
				final Criteria<JavaMethodDefinition> elementCriteria = persistenceUtil.createCriteria(JavaMethodDefinition.class);
				
				final CriteriaBuilder cb = elementCriteria.getBuilder();
				final Predicate like = cb.like(elementCriteria.getRoot().get(JavaElement_.fullQualifiedName),
				                               elementName);
				elementCriteria.getQuery().where(like);
				final List<JavaMethodDefinition> defHits = persistenceUtil.load(elementCriteria, 1);
				if (defHits.isEmpty()) {
					continue;
				}
				searchElement = defHits.get(0);
				break;
			}
			
			if (searchElement.equals(element)) {
				return null;
			}
			
		}
		final JavaChangeOperation result = getLastOperationOnElement(persistenceUtil, searchElement, opTransaction,
		                                                             location);
		if ((result == null) || result.equals(op)) {
			return null;
		}
		return result;
	}
	
	/**
	 * Generate change operations. The generated change operations will be passed to the given visitors.
	 * 
	 * @param repository
	 *            the repository
	 * @param changeSet
	 *            the transaction
	 * @param visitors
	 *            the visitors
	 * @param elementFactory
	 *            the element factory
	 * @param packageFilter
	 *            the package filter
	 */
	public static void generateChangeOperations(final Repository repository,
	                                            final ChangeSet changeSet,
	                                            final Collection<ChangeOperationVisitor> visitors,
	                                            final JavaElementFactory elementFactory,
	                                            final String[] packageFilter) {
		try {
			final Map<Revision, String> addRevs = new HashMap<Revision, String>();
			final Map<Revision, String> deleteRevs = new HashMap<Revision, String>();
			final Map<Revision, String> modifyRevs = new HashMap<Revision, String>();
			
			final String requestName = Thread.currentThread().getName();
			final IJavaProject javaProject = getProject(requestName);
			
			final ChangeSet parentTransaction = changeSet.getBranchParent();
			
			for (final Revision rev : changeSet.getRevisions()) {
				
				if (Logger.logDebug()) {
					Logger.debug("PPA parsing revision: " + rev.toString());
				}
				try {
					String changedPath = rev.getChangedFile().getPath(changeSet);
					if (changedPath == null) {
						continue;
					}
					changedPath = new String(changedPath);
					if (!changedPath.endsWith(".java")) {
						if (Logger.logDebug()) {
							Logger.debug("Ignoring non-Java file: " + changedPath);
						}
						continue;
					}
					if (changedPath.startsWith(DIRECTORY_PREFIX)) {
						changedPath = changedPath.substring(1);
					}
					
					switch (rev.getChangeType()) {
						case Added:
							addRevs.put(rev, changedPath);
							break;
						case Modified:
							modifyRevs.put(rev, changedPath);
							break;
						case Deleted:
							deleteRevs.put(rev, changedPath);
							break;
						default:
							break;
					}
				} catch (final NoSuchHandleException e) {
					if (Logger.logWarn()) {
						Logger.warn("Could not determine path name for Handle %s in ChangeSet %s.",
						            rev.getChangedFile().toString(), changeSet.toString());
					}
				}
			}
			
			// handle the removed files first
			if (parentTransaction != null) {
				final File oldCheckoutFile = repository.checkoutPath(DIRECTORY_PREFIX, parentTransaction.getId());
				if (!oldCheckoutFile.exists()) {
					throw new UnrecoverableError("Could not access checkout directory: "
					        + oldCheckoutFile.getAbsolutePath() + ".");
				}
				
				// first copy files!
				final Map<Revision, Tuple<IFile, String>> iFiles = new HashMap<Revision, Tuple<IFile, String>>();
				for (final Entry<Revision, String> entry : deleteRevs.entrySet()) {
					final File file = new File(oldCheckoutFile.getAbsolutePath() + FileUtils.fileSeparator
					        + entry.getValue());
					if (!file.exists()) {
						if (Logger.logDebug()) {
							Logger.debug("Could not find checked out file " + file.getAbsolutePath()
							        + " (might be added in next revision?)");
						}
						continue;
					}
					try {
						final String packageName = getPackageFromFile(file);
						final IFile newFile = PPAResourceUtil.copyJavaSourceFile(javaProject.getProject(), file,
						                                                         packageName, file.getName());
						iFiles.put(entry.getKey(), new Tuple<IFile, String>(newFile, entry.getValue()));
					} catch (final Exception e) {
						if (Logger.logError()) {
							Logger.error(e, "Error while getting IFile from PPA for revision ", entry.getKey()
							                                                                         .toString());
						}
					}
				}
				for (final Entry<Revision, Tuple<IFile, String>> entry : iFiles.entrySet()) {
					final CompilationUnit cu = getCU(entry.getValue().getFirst(), new PPAOptions());
					generateChangeOperationsForDeletedFile(repository, changeSet, entry.getKey(), cu,
					                                       entry.getValue().getSecond(), visitors, elementFactory,
					                                       packageFilter);
				}
			}
			
			// handle added files
			
			File newCheckoutFile = repository.checkoutPath(DIRECTORY_PREFIX, changeSet.getId());
			if (!newCheckoutFile.exists()) {
				throw new UnrecoverableError("Could not access checkout directory: "
				        + newCheckoutFile.getAbsolutePath() + ". Ignoring!");
			}
			
			// first copy files!
			final Map<Revision, Tuple<IFile, String>> iFiles = new HashMap<Revision, Tuple<IFile, String>>();
			for (final Entry<Revision, String> entry : addRevs.entrySet()) {
				final File file = new File(newCheckoutFile.getAbsolutePath() + FileUtils.fileSeparator
				        + entry.getValue());
				if (!file.exists()) {
					if (Logger.logDebug()) {
						Logger.debug("Could not find checked out file " + file.getAbsolutePath()
						        + " (might be added in next revision?)");
					}
					continue;
				}
				try {
					final String packageName = getPackageFromFile(file);
					final IFile newFile = PPAResourceUtil.copyJavaSourceFile(javaProject.getProject(), file,
					                                                         packageName, file.getName());
					iFiles.put(entry.getKey(), new Tuple<IFile, String>(newFile, entry.getValue()));
				} catch (final Exception e) {
					if (Logger.logError()) {
						Logger.error(e, "Error while getting IFile from PPA for revision '%s'.", entry.getKey()
						                                                                              .toString());
					}
				}
			}
			for (final Entry<Revision, Tuple<IFile, String>> entry : iFiles.entrySet()) {
				final CompilationUnit cu = getCU(entry.getValue().getFirst(), new PPAOptions());
				generateChangeOperationsForAddedFile(repository, changeSet, entry.getKey(), cu, entry.getValue()
				                                                                                     .getSecond(),
				                                     visitors, elementFactory, packageFilter);
			}
			
			// handle modified files
			for (final Entry<Revision, String> entry : modifyRevs.entrySet()) {
				
				Condition.notNull(parentTransaction, "If files got modified there must exist an parent transaction");
				
				@SuppressWarnings ("null")
				final File oldCheckoutFile = repository.checkoutPath(DIRECTORY_PREFIX, parentTransaction.getId());
				if (!oldCheckoutFile.exists()) {
					throw new UnrecoverableError("Could not access checkout directory: "
					        + oldCheckoutFile.getAbsolutePath() + ". Ignoring!");
				}
				final File oldFile = new File(oldCheckoutFile.getAbsolutePath() + FileUtils.fileSeparator
				        + entry.getValue());
				final CompilationUnit oldCU = getCU(oldFile, new PPAOptions());
				final JavaElementLocations oldElems = PPAUtils.getJavaElementLocationsByCU(oldCU, entry.getValue(),
				                                                                           packageFilter,
				                                                                           elementFactory);
				
				newCheckoutFile = repository.checkoutPath(DIRECTORY_PREFIX, changeSet.getId());
				if (!newCheckoutFile.exists()) {
					throw new UnrecoverableError("Could not access checkout directory: "
					        + newCheckoutFile.getAbsolutePath() + ". Ignoring!");
				}
				final File newFile = new File(newCheckoutFile.getAbsolutePath() + FileUtils.fileSeparator
				        + entry.getValue());
				final CompilationUnit newCU = getCU(newFile, new PPAOptions());
				final JavaElementLocations newElems = PPAUtils.getJavaElementLocationsByCU(newCU, entry.getValue(),
				                                                                           packageFilter,
				                                                                           elementFactory);
				generateChangeOperationsForModifiedFile(repository, changeSet, entry.getKey(), oldElems, newElems,
				                                        entry.getValue(), visitors);
			}
		} catch (final RepositoryOperationException e) {
			throw new UnrecoverableError(e);
		}
		
	}
	
	/**
	 * Generate change operations for added file.
	 * 
	 * @param repository
	 *            the repository
	 * @param changeSet
	 *            the r cs transaction
	 * @param revision
	 *            the r cs revision
	 * @param cu
	 *            the cu
	 * @param changedPath
	 *            the changed path
	 * @param visitors
	 *            the visitors
	 * @param elementFactory
	 *            the element factory
	 * @param packageFilter
	 *            the package filter
	 */
	private static void generateChangeOperationsForAddedFile(final Repository repository,
	                                                         final ChangeSet changeSet,
	                                                         final Revision revision,
	                                                         final CompilationUnit cu,
	                                                         final String changedPath,
	                                                         final Collection<ChangeOperationVisitor> visitors,
	                                                         final JavaElementFactory elementFactory,
	                                                         final String[] packageFilter) {
		final ChangeOperations operations = new ChangeOperations();
		final JavaElementLocations newElems = PPAUtils.getJavaElementLocationsByCU(cu, changedPath, packageFilter,
		                                                                           elementFactory);
		for (final JavaElementLocation classDef : newElems.getClassDefs(changedPath)) {
			final JavaChangeOperation op = new JavaChangeOperation(ChangeType.Added, classDef, revision);
			operations.add(op);
			
		}
		for (final JavaElementLocation methDef : newElems.getMethodDefs(changedPath)) {
			final JavaChangeOperation op = new JavaChangeOperation(ChangeType.Added, methDef, revision);
			operations.add(op);
		}
		for (final JavaElementLocation methCall : newElems.getMethodCalls(changedPath)) {
			final JavaChangeOperation op = new JavaChangeOperation(ChangeType.Added, methCall, revision);
			operations.add(op);
		}
		for (final JavaChangeOperation op : operations.getOperations()) {
			for (final ChangeOperationVisitor visitor : visitors) {
				visitor.visit(op);
			}
		}
		
	}
	
	/**
	 * Generate change operations for deleted file.
	 * 
	 * @param repository
	 *            the repository
	 * @param changeSet
	 *            the changeset
	 * @param revision
	 *            the revision
	 * @param cu
	 *            the cu
	 * @param changedPath
	 *            the changed path
	 * @param visitors
	 *            the visitors
	 * @param elementFactory
	 *            the element factory
	 * @param packageFilter
	 *            the package filter
	 */
	protected static void generateChangeOperationsForDeletedFile(final Repository repository,
	                                                             final ChangeSet changeSet,
	                                                             final Revision revision,
	                                                             final CompilationUnit cu,
	                                                             final String changedPath,
	                                                             final Collection<ChangeOperationVisitor> visitors,
	                                                             final JavaElementFactory elementFactory,
	                                                             final String[] packageFilter) {
		
		final ChangeOperations operations = new ChangeOperations();
		
		final JavaElementLocations oldElems = PPAUtils.getJavaElementLocationsByCU(cu, changedPath, packageFilter,
		                                                                           elementFactory);
		
		for (final JavaElementLocation classDef : oldElems.getClassDefs(changedPath)) {
			final JavaChangeOperation op = new JavaChangeOperation(ChangeType.Deleted, classDef, revision);
			operations.add(op);
		}
		for (final JavaElementLocation methDef : oldElems.getMethodDefs(changedPath)) {
			final JavaChangeOperation op = new JavaChangeOperation(ChangeType.Deleted, methDef, revision);
			operations.add(op);
		}
		for (final JavaElementLocation methCall : oldElems.getMethodCalls(changedPath)) {
			final JavaChangeOperation op = new JavaChangeOperation(ChangeType.Deleted, methCall, revision);
			operations.add(op);
		}
		for (final JavaChangeOperation op : operations.getOperations()) {
			for (final ChangeOperationVisitor visitor : visitors) {
				visitor.visit(op);
			}
		}
	}
	
	/**
	 * Generate change operations for modified file. The generated change operations will be passed to the given
	 * visitors.
	 * 
	 * @param repository
	 *            the repository
	 * @param changeSet
	 *            the change set
	 * @param revision
	 *            the revision
	 * @param oldElems
	 *            the old elems
	 * @param newElems
	 *            the new elems
	 * @param changedPath
	 *            the changed path
	 * @param visitors
	 *            the visitors
	 */
	@SuppressWarnings ("unchecked")
	protected static void generateChangeOperationsForModifiedFile(final Repository repository,
	                                                              final ChangeSet changeSet,
	                                                              final Revision revision,
	                                                              final JavaElementLocations oldElems,
	                                                              final JavaElementLocations newElems,
	                                                              final String changedPath,
	                                                              final Collection<ChangeOperationVisitor> visitors) {
		
		try {
			final ChangeSet parentTransaction = changeSet.getBranchParent();
			if (parentTransaction == null) {
				return;
			}
			
			// generate diff
			Collection<Delta> diff;
			
			diff = repository.diff(changedPath, parentTransaction.getId(), changeSet.getId());
			final ChangeOperations operations = new ChangeOperations();
			for (final Delta delta : diff) {
				
				if (delta instanceof DeleteDelta) {
					// corresponding elements to be removed
					final HashSet<Integer> lines = DiffUtils.getLineNumbers(delta.getOriginal());
					for (final JavaElementLocation javaElem : oldElems.getElements(changedPath)) {
						final LineCover cover = javaElem.coversAnyLine(lines);
						switch (cover) {
							case DEFINITION:
							case DEF_AND_BODY:
								operations.add(new JavaChangeOperation(ChangeType.Deleted, javaElem, revision));
								break;
							case BODY:
								operations.add(new JavaChangeOperation(ChangeType.Modified, javaElem, revision));
								break;
							default:
								break;
						}
					}
				} else if (delta instanceof InsertDelta) {
					// corresponding elements to be added
					final HashSet<Integer> lines = DiffUtils.getLineNumbers(delta.getRevised());
					for (final JavaElementLocation javaElem : newElems.getElements(changedPath)) {
						final LineCover cover = javaElem.coversAnyLine(lines);
						switch (cover) {
							case DEFINITION:
							case DEF_AND_BODY:
								operations.add(new JavaChangeOperation(ChangeType.Added, javaElem, revision));
								break;
							case BODY:
								operations.add(new JavaChangeOperation(ChangeType.Modified, javaElem, revision));
								break;
							default:
								break;
						}
					}
				} else {
					// make a collection diff
					final HashSet<Integer> oldLines = DiffUtils.getLineNumbers(delta.getOriginal());
					final HashSet<Integer> newLines = DiffUtils.getLineNumbers(delta.getRevised());
					
					final Collection<JavaElementLocation> modifiedDefCandidates = new HashSet<JavaElementLocation>();
					
					final Collection<JavaElementLocation> addDefCandidates = new HashSet<JavaElementLocation>();
					for (final JavaElementLocation def : newElems.getElements(changedPath)) {
						final LineCover cover = def.coversAnyLine(newLines);
						switch (cover) {
							case BODY:
								modifiedDefCandidates.add(def);
								break;
							case DEF_AND_BODY:
							case DEFINITION:
								addDefCandidates.add(def);
								break;
							default:
								break;
						}
					}
					final Collection<JavaElementLocation> removeDefCandidates = new HashSet<JavaElementLocation>();
					for (final JavaElementLocation def : oldElems.getElements(changedPath)) {
						final LineCover cover = def.coversAnyLine(oldLines);
						switch (cover) {
							case BODY:
								modifiedDefCandidates.add(def);
								break;
							case DEF_AND_BODY:
							case DEFINITION:
								removeDefCandidates.add(def);
								break;
							default:
								break;
						}
					}
					
					final Collection<JavaElementLocation> defsToRemove = new HashSet<JavaElementLocation>();
					final Collection<JavaElementLocation> defsToAdd = new HashSet<JavaElementLocation>();
					
					defsToRemove.addAll(CollectionUtils.subtract(removeDefCandidates, addDefCandidates));
					defsToAdd.addAll(CollectionUtils.subtract(addDefCandidates, removeDefCandidates));
					
					final Map<String, TreeSet<JavaElementLocation>> removeCallCandidates = new HashMap<String, TreeSet<JavaElementLocation>>();
					if (oldElems.getMethodCalls(changedPath) != null) {
						for (final JavaElementLocation call : oldElems.getMethodCalls(changedPath)) {
							final LineCover cover = call.coversAnyLine(oldLines);
							if (!cover.equals(LineCover.FALSE)) {
								if (!removeCallCandidates.containsKey(call.getElement().getFullQualifiedName())) {
									removeCallCandidates.put(call.getElement().getFullQualifiedName(),
									                         new TreeSet<JavaElementLocation>());
								}
								removeCallCandidates.get(call.getElement().getFullQualifiedName()).add(call);
							}
						}
					}
					final Map<String, TreeSet<JavaElementLocation>> addCallCandidates = new HashMap<String, TreeSet<JavaElementLocation>>();
					if (newElems.getMethodCalls(changedPath) != null) {
						for (final JavaElementLocation call : newElems.getMethodCalls(changedPath)) {
							final LineCover cover = call.coversAnyLine(newLines);
							if (!cover.equals(LineCover.FALSE)) {
								if (!addCallCandidates.containsKey(call.getElement().getFullQualifiedName())) {
									addCallCandidates.put(call.getElement().getFullQualifiedName(),
									                      new TreeSet<JavaElementLocation>());
								}
								addCallCandidates.get(call.getElement().getFullQualifiedName()).add(call);
							}
						}
					}
					
					// if a line was changed: detect added and removed calls that
					// are most likely corresponding
					for (final String addedCallId : addCallCandidates.keySet()) {
						if (removeCallCandidates.containsKey(addedCallId)) {
							final TreeSet<JavaElementLocation> delSet = removeCallCandidates.get(addedCallId);
							final TreeSet<JavaElementLocation> addSet = addCallCandidates.get(addedCallId);
							for (final JavaElementLocation addedCall : new TreeSet<JavaElementLocation>(
							                                                                            addCallCandidates.get(addedCallId))) {
								final JavaElementLocation ceiling = delSet.ceiling(addedCall);
								final JavaElementLocation floor = delSet.floor(addedCall);
								if ((ceiling != null) || (floor != null)) {
									JavaElementLocation correspondent = null;
									if (ceiling == null) {
										correspondent = floor;
									} else if (floor == null) {
										correspondent = ceiling;
									} else {
										final int floorDist = Math.abs(addedCall.getPosition() - floor.getPosition());
										final int ceilingDist = Math.abs(addedCall.getPosition()
										        - ceiling.getPosition());
										if (floorDist < ceilingDist) {
											correspondent = floor;
										} else {
											correspondent = ceiling;
										}
									}
									Condition.check(correspondent != null, "Must have found correspondent");
									if (correspondent == null) {
										if (Logger.logError()) {
											Logger.error("Must have found correspondent!");
										}
									}
									delSet.remove(correspondent);
									addSet.remove(addedCall);
								}
							}
						}
					}
					
					// method calls that are still present in their collections make
					// up the operations
					for (final TreeSet<JavaElementLocation> methodCallsToDelete : removeCallCandidates.values()) {
						for (final JavaElementLocation methodCall : methodCallsToDelete) {
							operations.add(new JavaChangeOperation(ChangeType.Deleted, methodCall, revision));
						}
					}
					for (final TreeSet<JavaElementLocation> methodCallsToAdd : addCallCandidates.values()) {
						for (final JavaElementLocation methodCall : methodCallsToAdd) {
							operations.add(new JavaChangeOperation(ChangeType.Added, methodCall, revision));
						}
					}
					for (final JavaElementLocation methodDefToDelete : defsToRemove) {
						operations.add(new JavaChangeOperation(ChangeType.Deleted, methodDefToDelete, revision));
					}
					for (final JavaElementLocation methodDefToAdd : defsToAdd) {
						operations.add(new JavaChangeOperation(ChangeType.Added, methodDefToAdd, revision));
					}
					for (final JavaElementLocation methodDefModified : modifiedDefCandidates) {
						operations.add(new JavaChangeOperation(ChangeType.Modified, methodDefModified, revision));
					}
				}
			}
			for (final JavaChangeOperation op : operations.getOperations()) {
				for (final ChangeOperationVisitor visitor : visitors) {
					visitor.visit(op);
				}
			}
		} catch (final RepositoryOperationException e) {
			throw new UnrecoverableError(e);
		}
		
	}
	
	/**
	 * Generate change operations noppa.
	 * 
	 * @param repository
	 *            the repository
	 * @param changeSet
	 *            the r cs transaction
	 * @param visitors
	 *            the visitors
	 * @param elementFactory
	 *            the element factory
	 * @param packageFilter
	 *            the package filter
	 */
	public static void generateChangeOperationsNOPPA(final Repository repository,
	                                                 final ChangeSet changeSet,
	                                                 final Collection<ChangeOperationVisitor> visitors,
	                                                 final JavaElementFactory elementFactory,
	                                                 final String[] packageFilter) {
		
		try {
			final Map<Revision, String> addRevs = new HashMap<Revision, String>();
			final Map<Revision, String> deleteRevs = new HashMap<Revision, String>();
			final Map<Revision, String> modifyRevs = new HashMap<Revision, String>();
			
			final ChangeSet parentTransaction = changeSet.getBranchParent();
			if (parentTransaction == null) {
				return;
			}
			
			for (final Revision rev : changeSet.getRevisions()) {
				try {
					String changedPath = rev.getChangedFile().getPath(changeSet);
					if (changedPath == null) {
						continue;
					}
					changedPath = new String(changedPath);
					if (!changedPath.endsWith(".java")) {
						if (Logger.logDebug()) {
							Logger.debug("Ignoring non-Java file: " + changedPath);
						}
						continue;
					}
					if (changedPath.startsWith(DIRECTORY_PREFIX)) {
						changedPath = changedPath.substring(1);
					}
					
					switch (rev.getChangeType()) {
						case Added:
							addRevs.put(rev, changedPath);
							break;
						case Modified:
							modifyRevs.put(rev, changedPath);
							break;
						case Deleted:
							deleteRevs.put(rev, changedPath);
							break;
						default:
							break;
					}
				} catch (final NoSuchHandleException e) {
					if (Logger.logWarn()) {
						Logger.warn("Could not determine path name for Handle %s in ChangeSet %s.",
						            rev.getChangedFile().toString(), changeSet.toString());
					}
				}
			}
			
			// handle the removed files first
			
			File oldCheckoutFile = null;
			
			oldCheckoutFile = repository.checkoutPath(DIRECTORY_PREFIX, parentTransaction.getId());
			Condition.notNull(oldCheckoutFile, "The oldCheckoutFile must not be null.");
			if (!oldCheckoutFile.exists()) {
				throw new UnrecoverableError("Could not access checkout directory: "
				        + oldCheckoutFile.getAbsolutePath() + ". Ignoring!");
			}
			
			for (final Entry<Revision, String> entry : deleteRevs.entrySet()) {
				final File file = new File(oldCheckoutFile.getAbsolutePath() + FileUtils.fileSeparator
				        + entry.getValue());
				final CompilationUnit cu = getCUNoPPA(file);
				generateChangeOperationsForDeletedFile(repository, changeSet, entry.getKey(), cu, entry.getValue(),
				                                       visitors, elementFactory, packageFilter);
			}
			
			// handle added files
			File newCheckoutFile = null;
			newCheckoutFile = repository.checkoutPath(DIRECTORY_PREFIX, changeSet.getId());
			Condition.notNull(oldCheckoutFile, "The newCheckoutFile must not be null.");
			if (!newCheckoutFile.exists()) {
				throw new UnrecoverableError("Could not access checkout directory: "
				        + newCheckoutFile.getAbsolutePath() + ". Ignoring!");
			}
			
			for (final Entry<Revision, String> entry : addRevs.entrySet()) {
				final File file = new File(newCheckoutFile.getAbsolutePath() + FileUtils.fileSeparator
				        + entry.getValue());
				final CompilationUnit cu = getCUNoPPA(file);
				generateChangeOperationsForAddedFile(repository, changeSet, entry.getKey(), cu, entry.getValue(),
				                                     visitors, elementFactory, packageFilter);
			}
			
			// handle modified files
			for (final Entry<Revision, String> entry : modifyRevs.entrySet()) {
				
				Condition.notNull(parentTransaction, "If files got modified there must exist an parent transaction");
				
				final File oldModifiedCheckoutFile = repository.checkoutPath(DIRECTORY_PREFIX,
				                                                             parentTransaction.getId());
				if (!oldModifiedCheckoutFile.exists()) {
					throw new UnrecoverableError("Could not access checkout directory: "
					        + oldModifiedCheckoutFile.getAbsolutePath() + ". Ignoring!");
				}
				final File oldFile = new File(oldModifiedCheckoutFile.getAbsolutePath() + FileUtils.fileSeparator
				        + entry.getValue());
				final CompilationUnit oldCU = getCUNoPPA(oldFile);
				final JavaElementLocations oldElems = PPAUtils.getJavaElementLocationsByCU(oldCU, entry.getValue(),
				                                                                           packageFilter,
				                                                                           elementFactory);
				
				newCheckoutFile = repository.checkoutPath(DIRECTORY_PREFIX, changeSet.getId());
				if (!newCheckoutFile.exists()) {
					throw new UnrecoverableError("Could not access checkout directory: "
					        + newCheckoutFile.getAbsolutePath() + ". Ignoring!");
				}
				final File newFile = new File(newCheckoutFile.getAbsolutePath() + FileUtils.fileSeparator
				        + entry.getValue());
				final CompilationUnit newCU = getCUNoPPA(newFile);
				final JavaElementLocations newElems = PPAUtils.getJavaElementLocationsByCU(newCU, entry.getValue(),
				                                                                           packageFilter,
				                                                                           elementFactory);
				generateChangeOperationsForModifiedFile(repository, changeSet, entry.getKey(), oldElems, newElems,
				                                        entry.getValue(), visitors);
			}
		} catch (final RepositoryOperationException e) {
			throw new UnrecoverableError(e);
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
	public static CompilationUnit getCU(final File file,
	                                    final PPAOptions options) {
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
	public static CompilationUnit getCU(final File file,
	                                    final PPAOptions options,
	                                    final String requestName) {
		CompilationUnit cu = null;
		final String fileName = file.getName();
		
		try {
			final String packageName = getPackageFromFile(file);
			final IJavaProject javaProject = getProject(requestName);
			
			final CopyThread copyThread = new CopyThread(javaProject.getProject(), file, packageName, fileName, options);
			
			// IFile newFile =
			// PPAResourceUtil.copyJavaSourceFile(javaProject.getProject(),
			// file, packageName, fileName);
			
			copyThread.start();
			
			copyThread.join(120000);
			cu = copyThread.getCompilationUnit();
			if (cu == null) {
				final IFile ifile = copyThread.getIFile();
				if (ifile == null) {
					if (Logger.logError()) {
						Logger.error("Error while getting IFile from PPA. Timeout copy to workspace exceeded.");
					}
				} else {
					if (Logger.logError()) {
						Logger.error("Error while getting CU from PPA. Timeout copy to workspace exceeded. Trying without PPA");
					}
					return getCUNoPPA(ifile);
				}
				return null;
			}
		} catch (final Exception e) {
			if (Logger.logError()) {
				Logger.error(e, "Error while getting CU from PPA");
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
	public static CompilationUnit getCU(final IFile file,
	                                    final PPAOptions options) {
		CompilationUnit cu = null;
		
		try {
			final PPACUThread ppacuThread = new PPACUThread(file, options);
			final Thread t = new Thread(ppacuThread);
			t.run();
			t.join(30000);
			cu = ppacuThread.getCU();
		} catch (final InterruptedException e1) {
			if (Logger.logError()) {
				Logger.error(e1, "Error while getting CU using PPA.");
			}
		}
		// try {
		// ICompilationUnit icu = JavaCore.createCompilationUnitFrom(file);
		// PPATypeRegistry registry = new PPATypeRegistry((JavaProject)
		// JavaCore.create(icu.getUnderlyingResource()
		// .getProject()));
		// ASTNode node = null;
		// PPAASTParser parser2 = new PPAASTParser(AST.JLS3);
		// parser2.setStatementsRecovery(true);
		// parser2.setResolveBindings(true);
		// parser2.setSource(icu);
		// node = parser2.createAST(null);
		// PPAEngine ppaEngine = new PPAEngine(registry, options);
		//
		// cu = (CompilationUnit) node;
		//
		// ppaEngine.addUnitToProcess(cu);
		// ppaEngine.doPPA();
		// ppaEngine.reset();
		// } catch (JavaModelException jme) {
		// // Retry with the file version.
		// if (Logger.logWarn()) {
		// Logger.warn("Warning while getting CU from PPA");
		// }
		// if (Logger.logDebug()) {
		// Logger.debug("Exception", jme);
		// }
		//
		// } catch (Exception e) {
		// if (Logger.logError()) {
		// Logger.error("Error while getting CU from PPA", e);
		// }
		// }
		
		return cu;
	}
	
	/**
	 * Gets the cU no codeanalysis.
	 * 
	 * @param file
	 *            the file
	 * @return the cU no codeanalysis
	 */
	public static CompilationUnit getCUNoPPA(final File file) {
		CompilationUnit cu = null;
		try {
			final String content = FileUtils.readFileToString(file);
			ASTNode node = null;
			final ASTParser parser = ASTParser.newParser(AST.JLS3);
			parser.setStatementsRecovery(true);
			parser.setResolveBindings(true);
			parser.setSource(content.toCharArray());
			node = parser.createAST(null);
			cu = (CompilationUnit) node;
		} catch (final Exception e) {
			if (Logger.logError()) {
				Logger.error(e, "Error while getting CU without PPA");
			}
		}
		
		return cu;
	}
	
	/**
	 * Gets the cU no codeanalysis.
	 * 
	 * @param file
	 *            the file
	 * @return the cU no codeanalysis
	 */
	public static CompilationUnit getCUNoPPA(final IFile file) {
		CompilationUnit cu = null;
		try {
			final ICompilationUnit icu = JavaCore.createCompilationUnitFrom(file);
			ASTNode node = null;
			final PPAASTParser parser2 = new PPAASTParser(AST.JLS3);
			parser2.setStatementsRecovery(true);
			parser2.setResolveBindings(true);
			parser2.setSource(icu);
			node = parser2.createAST(false, new NullProgressMonitor());
			cu = (CompilationUnit) node;
		} catch (final Exception e) {
			if (Logger.logError()) {
				Logger.error(e, "Error while getting CU without PPA");
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
	public static Map<File, CompilationUnit> getCUs(final Collection<File> files,
	                                                final PPAOptions options) {
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
	public static Map<File, CompilationUnit> getCUs(final Collection<File> files,
	                                                final PPAOptions options,
	                                                final String requestName) {
		final Map<File, CompilationUnit> cus = new HashMap<File, CompilationUnit>();
		final Map<ICompilationUnit, File> iCus = new HashMap<ICompilationUnit, File>();
		try {
			cleanupWorkspace();
		} catch (final CoreException e1) {
			if (Logger.logWarn()) {
				Logger.warn(e1);
			}
		}
		final List<ICompilationUnit> iUnits = new LinkedList<>();
		for (final File file : files) {
			final String fileName = file.getName();
			try {
				final String packageName = getPackageFromFile(file);
				final IJavaProject javaProject = getProject(requestName);
				final IFile iFile = PPAResourceUtil.copyJavaSourceFile(javaProject.getProject(), file, packageName,
				                                                       fileName);
				if (Logger.logDebug()) {
					Logger.debug("Getting CU for file: " + iFile.getLocation().toOSString());
				}
				final ICompilationUnit iCu = JavaCore.createCompilationUnitFrom(iFile);
				iCus.put(iCu, file);
				iUnits.add(iCu);
			} catch (final Exception e) {
				if (Logger.logError()) {
					Logger.error(e, "Error while getting IFile from PPA");
				}
			}
		}
		
		final Map<ICompilationUnit, CompilationUnit> cUsWithOnePPAPass = getCUsWithOnePPAPass(iUnits);
		
		for (final ICompilationUnit iUnit : cUsWithOnePPAPass.keySet()) {
			final File f = iCus.get(iUnit);
			final CompilationUnit cu = cus.get(f);
			if (cu != null) {
				cus.put(f, cu);
			}
		}
		
		return cus;
	}
	
	/**
	 * Gets the c us for transaction.
	 * 
	 * @param repository
	 *            the repository
	 * @param changeSet
	 *            the r cs transaction
	 * @param changeType
	 *            the change type
	 * @return the c us for transaction
	 */
	protected static Map<Revision, CompilationUnit> getCUsForTransaction(final Repository repository,
	                                                                     final ChangeSet changeSet,
	                                                                     final ChangeType changeType) {
		try {
			final Map<Revision, IFile> ifiles = new HashMap<Revision, IFile>();
			final Map<Revision, CompilationUnit> result = new HashMap<Revision, CompilationUnit>();
			
			File oldCheckoutFile = null;
			
			oldCheckoutFile = repository.checkoutPath(DIRECTORY_PREFIX, changeSet.getId());
			Condition.notNull(oldCheckoutFile, "Cannot get CUs for transaction.");
			if (!oldCheckoutFile.exists()) {
				if (Logger.logError()) {
					Logger.error("Could not access checkout directory: " + oldCheckoutFile.getAbsolutePath()
					        + ". Ignoring!");
				}
				return result;
			}
			
			final String requestName = Thread.currentThread().getName();
			
			final Map<File, Revision> filesToAnalyze = new HashMap<File, Revision>();
			for (final Revision revision : changeSet.getRevisions()) {
				
				if (changeType.equals(ChangeType.Added)) {
					if (revision.getChangeType().equals(ChangeType.Deleted)) {
						continue;
					}
				} else if (changeType.equals(ChangeType.Deleted)) {
					if (revision.getChangeType().equals(ChangeType.Added)) {
						continue;
					}
				}
				try {
					String changedPath = revision.getChangedFile().getPath(changeSet);
					if (changedPath == null) {
						continue;
					}
					changedPath = new String(changedPath);
					if (!changedPath.endsWith(".java")) {
						if (Logger.logDebug()) {
							Logger.debug("Ignoring non-Java file: " + changedPath);
						}
						continue;
					}
					if (changedPath.startsWith(DIRECTORY_PREFIX)) {
						changedPath = changedPath.substring(1);
					}
					
					final File file = new File(oldCheckoutFile.getAbsolutePath() + FileUtils.fileSeparator
					        + changedPath);
					if (!file.exists()) {
						if (Logger.logDebug()) {
							Logger.debug("Could not find checked out file " + file.getAbsolutePath()
							        + " (might be added in next revision?)");
						}
						continue;
					}
					filesToAnalyze.put(file, revision);
					
					final String fileName = file.getName();
					try {
						final String packageName = getPackageFromFile(file);
						final IJavaProject javaProject = getProject(requestName);
						final IFile newFile = PPAResourceUtil.copyJavaSourceFile(javaProject.getProject(), file,
						                                                         packageName, fileName);
						ifiles.put(revision, newFile);
					} catch (final Exception e) {
						if (Logger.logError()) {
							Logger.error(e, "Error while getting IFile from PPA for revision '%s'.",
							             revision.toString());
						}
					}
				} catch (final NoSuchHandleException e) {
					if (Logger.logWarn()) {
						Logger.warn("Could not determine path name for Handle %s in ChangeSet %s.",
						            revision.getChangedFile().toString(), changeSet.toString());
					}
				}
			}
			
			for (final Entry<Revision, IFile> entry : ifiles.entrySet()) {
				final CompilationUnit cu = getCU(entry.getValue(), new PPAOptions());
				result.put(entry.getKey(), cu);
			}
			return result;
		} catch (final RepositoryOperationException e) {
			throw new UnrecoverableError(e);
		}
		
	}
	
	/**
	 * Gets the c us.
	 * 
	 * @param files
	 *            the files
	 * @return the c us
	 */
	public static Map<File, CompilationUnit> getCUsNoPPA(final Collection<File> files) {
		
		final Map<File, CompilationUnit> cus = new HashMap<File, CompilationUnit>();
		
		for (final File file : files) {
			if (Logger.logDebug()) {
				Logger.debug("Getting CU for file: " + file.getAbsolutePath());
			}
			final CompilationUnit cu = getCUNoPPA(file);
			cus.put(file, cu);
		}
		
		return cus;
	}
	
	/**
	 * <p>
	 * </p>
	 * 
	 * @param units
	 * @return
	 */
	public static Map<ICompilationUnit, CompilationUnit> getCUsWithOnePPAPass(final List<ICompilationUnit> units) {
		
		if (units.size() == 0) {
			return new HashMap<ICompilationUnit, CompilationUnit>();
		}
		
		final Map<ICompilationUnit, CompilationUnit> astList = new HashMap<ICompilationUnit, CompilationUnit>();
		try {
			
			// FIXME a hack to get the current project.
			final JavaProject jproject = (JavaProject) JavaCore.create(units.get(0).getUnderlyingResource()
			                                                                .getProject());
			final PPATypeRegistry registry = new PPATypeRegistry(jproject);
			final PPAEngine ppaEngine = new PPAEngine(registry, new PPAOptions());
			
			final ASTParser parser2 = ASTParser.newParser(AST.JLS3);
			parser2.setStatementsRecovery(true);
			parser2.setResolveBindings(true);
			parser2.setProject(jproject);
			
			final ASTRequestor requestor = new ASTRequestor() {
				
				@Override
				public void acceptAST(final ICompilationUnit source,
				                      final CompilationUnit ast) {
					astList.put(source, ast);
					ppaEngine.addUnitToProcess(ast);
				}
			};
			
			parser2.createASTs(units.toArray(new ICompilationUnit[units.size()]), new String[0], requestor, null);
			
			ppaEngine.doPPA();
			ppaEngine.reset();
			
		} catch (final JavaModelException jme) {
			jme.printStackTrace();
		} catch (final Exception e) {
			e.printStackTrace();
		}
		
		return astList;
	}
	
	/**
	 * Computes the possible matching method definition names based on the full qualified name of the supplied
	 * JavaMethodCall; The returned array of Strings is sorted in descending importance order.
	 * 
	 * @param call
	 *            the JavaMethodCall to base the method definition name generation on.
	 * @return the definition names corresponding to the supplied JavaMethodCall. Sorted in descending importance and
	 *         likelyhood order.
	 */
	protected static String[] getDefinitionNamesForCallName(final JavaMethodCall call) {
		String name = String.valueOf(call.getFullQualifiedName());
		
		// replace "<init>" from name
		if (name.contains("<init>()")) {
			int index = 0;
			final String[] nameParts = name.split("\\.");
			for (final String namePart : nameParts) {
				if ("<init>()".equals(namePart)) {
					break;
				}
				++index;
			}
			if (index < 1) {
				if (Logger.logError()) {
					Logger.error("Could not find the class name for the constructor call. Returning null;");
				}
				return null;
			}
			final String className = nameParts[index - 1];
			name = name.replace("<init>()", className + "()");
		}
		// if it starts with src. get rid of prefix
		if (name.startsWith("src.")) {
			name = name.substring(4);
		}
		// the all might call an inner class
		final StringBuilder alternativeName = new StringBuilder();
		final String[] nameParts = name.split("\\.");
		if (nameParts.length > 3) {
			nameParts[nameParts.length - 3] = "";
			for (int i = 0; i < nameParts.length; ++i) {
				if (!nameParts[i].isEmpty()) {
					alternativeName.append(nameParts[i]);
					if (i != (nameParts.length - 1)) {
						alternativeName.append(".");
					}
				}
			}
			
		}
		
		return new String[] { name, alternativeName.toString(), name.replaceAll("UNKNOWN", "%"),
		        alternativeName.toString().replaceAll("UNKNOWN", "%") };
	}
	
	/**
	 * Gets the java element locations by file.
	 * 
	 * @param cu
	 *            the cu
	 * @param relativePath
	 *            the relative path
	 * @param packageFilter
	 *            the package filter
	 * @param elementFactory
	 *            the element factory
	 * @return the java element locations by file
	 */
	@NoneNull
	public static JavaElementLocations getJavaElementLocationsByCU(final CompilationUnit cu,
	                                                               final String relativePath,
	                                                               final String[] packageFilter,
	                                                               final JavaElementFactory elementFactory) {
		final PPAMethodCallVisitor methodCallVisitor = new PPAMethodCallVisitor();
		
		final JavaElementLocationSet locationSet = new JavaElementLocationSet(elementFactory);
		
		if (cu != null) {
			final PPATypeVisitor typeVisitor = new PPATypeVisitor(cu, relativePath, packageFilter, locationSet);
			typeVisitor.registerVisitor(methodCallVisitor);
			cu.accept(typeVisitor);
		} else {
			if (Logger.logError()) {
				Logger.error("Could not analyze file " + relativePath
				        + ". CompilationUnit cannot be created. Skipping ... ");
			}
		}
		return locationSet.getJavaElementLocations();
	}
	
	/**
	 * Gets the java element locations by file.
	 * 
	 * @param file
	 *            the file
	 * @param filePrefixPath
	 *            the file prefix path
	 * @param packageFilter
	 *            the package filter
	 * @param elementFactory
	 *            the element factory
	 * @return the java element locations by file
	 */
	public static JavaElementLocations getJavaElementLocationsByFile(final File file,
	                                                                 final String filePrefixPath,
	                                                                 final String[] packageFilter,
	                                                                 final JavaElementFactory elementFactory) {
		final PPAMethodCallVisitor methodCallVisitor = new PPAMethodCallVisitor();
		final PPAOptions ppaOptions = new PPAOptions();
		
		final JavaElementLocationSet locationSet = new JavaElementLocationSet(elementFactory);
		
		if (!file.getAbsolutePath().startsWith(filePrefixPath)) {
			if (Logger.logError()) {
				Logger.error("File name does not start with specified filePrefixPath");
			}
		} else {
			
			final CompilationUnit cu = getCU(file, ppaOptions);
			if (cu != null) {
				
				String relativePath = file.getAbsolutePath().substring(filePrefixPath.length());
				if (relativePath.startsWith(FileUtils.fileSeparator)) {
					relativePath = relativePath.substring(1);
				}
				
				final PPATypeVisitor typeVisitor = new PPATypeVisitor(cu, relativePath, packageFilter, locationSet);
				typeVisitor.registerVisitor(methodCallVisitor);
				cu.accept(typeVisitor);
			} else {
				if (Logger.logError()) {
					Logger.error("Could not analyze file " + file.getAbsolutePath()
					        + ". CompilationUnit cannot be created. Skipping ... ");
				}
			}
		}
		return locationSet.getJavaElementLocations();
	}
	
	/**
	 * Gets the java method elements of all java files within <code>sourceDir</code>.
	 * 
	 * @param sourceDir
	 *            the source dir
	 * @param packageFilter
	 *            the package filter
	 * @param elementFactory
	 *            the element factory
	 * @return the java method elements
	 */
	public static JavaElementLocations getJavaElementLocationsByFile(final File sourceDir,
	                                                                 final String[] packageFilter,
	                                                                 final JavaElementFactory elementFactory) {
		
		try {
			final Iterator<File> fileIterator = FileUtils.getFileIterator(sourceDir, new String[] { "java" }, true);
			return getJavaElementLocationsByFile(fileIterator, sourceDir.getAbsolutePath(), packageFilter,
			                                     elementFactory);
		} catch (final IOException e) {
			if (Logger.logError()) {
				Logger.error(e);
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
	 * @param elementFactory
	 *            the element factory
	 * @return the java method elements
	 */
	public static JavaElementLocations getJavaElementLocationsByFile(final Iterator<File> fileIterator,
	                                                                 final String filePrefixPath,
	                                                                 final String[] packageFilter,
	                                                                 final JavaElementFactory elementFactory) {
		final PPAMethodCallVisitor methodCallVisitor = new PPAMethodCallVisitor();
		final PPAOptions ppaOptions = new PPAOptions();
		
		final JavaElementLocationSet locationSet = new JavaElementLocationSet(elementFactory);
		
		while (fileIterator.hasNext()) {
			final File file = fileIterator.next();
			final CompilationUnit cu = getCU(file, ppaOptions);
			if (!file.getAbsolutePath().startsWith(filePrefixPath)) {
				if (Logger.logError()) {
					Logger.error("File name does not start with specified filePrefixPath");
				}
				continue;
			}
			if (cu == null) {
				if (Logger.logError()) {
					Logger.error("Could not analyze file " + file.getAbsolutePath()
					        + ". CompilationUnit cannot be created. Skipping ... ");
				}
				continue;
			}
			
			String relativePath = file.getAbsolutePath().substring(filePrefixPath.length());
			if (relativePath.startsWith(FileUtils.fileSeparator)) {
				relativePath = relativePath.substring(1);
			}
			
			final PPATypeVisitor typeVisitor = new PPATypeVisitor(cu, relativePath, packageFilter, locationSet);
			typeVisitor.registerVisitor(methodCallVisitor);
			cu.accept(typeVisitor);
		}
		
		return locationSet.getJavaElementLocations();
	}
	
	/**
	 * Gets the last operation on element.
	 * 
	 * @param persistenceUtil
	 *            the persistence util
	 * @param searchElement
	 *            the search element
	 * @param opTransaction
	 *            the op transaction
	 * @param notMatch
	 *            the not match
	 * @return the last operation on element
	 */
	private static JavaChangeOperation getLastOperationOnElement(final PersistenceUtil persistenceUtil,
	                                                             final JavaElement searchElement,
	                                                             final ChangeSet opTransaction,
	                                                             final JavaElementLocation notMatch) {
		final Criteria<JavaElementLocation> criteria = persistenceUtil.createCriteria(JavaElementLocation.class);
		final CriteriaBuilder cb = criteria.getBuilder();
		final Root<JavaElementLocation> root = criteria.getRoot();
		final Predicate eq = cb.equal(root.get("element"), searchElement);
		
		// it might be an operation within the same transaction having a larger id.
		
		// get the highest location id within the same transaction
		
		@SuppressWarnings ("unchecked")
		final List<Long> maxIdResultList = persistenceUtil.executeNativeSelectQuery("select max(l.id) from javaelementlocation l, javachangeoperation o WHERE l.id = o.changedelementlocation_id and o.revision_revisionid IN (select revisionid from revision where transaction_id = '"
		        + opTransaction.getId() + "')");
		
		final Long maxId = maxIdResultList.get(0);
		
		final Predicate lt = cb.lt(criteria.getRoot().get(JavaElementLocation_.id), maxId);
		
		final Predicate ne = cb.notEqual(criteria.getRoot().get(JavaElementLocation_.id), notMatch.getId());
		
		criteria.getQuery().where(cb.and(cb.and(eq, lt), ne));
		criteria.getQuery().orderBy(cb.desc(root.get("id")));
		
		final List<JavaElementLocation> hits = persistenceUtil.load(criteria, 1);
		if (hits.isEmpty()) {
			return null;
		}
		
		final JavaElementLocation hitLocation = hits.get(0);
		
		// search for corresponding JavaChangeOperation
		final Criteria<JavaChangeOperation> operationCriteria = persistenceUtil.createCriteria(JavaChangeOperation.class);
		operationCriteria.eq("changedElementLocation", hitLocation);
		final List<JavaChangeOperation> operationHits = persistenceUtil.load(operationCriteria, 1);
		if (operationHits.isEmpty()) {
			return null;
		}
		return operationHits.get(0);
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
		final PPAASTParser parser2 = new PPAASTParser(AST.JLS3);
		parser2.setStatementsRecovery(true);
		parser2.setResolveBindings(true);
		parser2.setSource(PPAResourceUtil.getContent(file).toCharArray());
		final CompilationUnit cu = (CompilationUnit) parser2.createAST(false, new NullProgressMonitor());
		final PackageDeclaration pDec = cu.getPackage();
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
			final IProject javaProject = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
			if (!javaProject.exists()) {
				javaProject.create(new NullProgressMonitor());
				javaProject.open(IResource.BACKGROUND_REFRESH, new NullProgressMonitor());
				final IProjectDescription description = javaProject.getDescription();
				final String[] natures = description.getNatureIds();
				final String[] newNatures = new String[natures.length + 1];
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
			final IFolder folder = javaProject.getFolder("src");
			if (!folder.exists()) {
				folder.create(true, true, new NullProgressMonitor());
			}
		} catch (final CoreException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
		}
		return project;
	}
	
}
