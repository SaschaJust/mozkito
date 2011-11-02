/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.unisaarland.cs.st.moskito.ppa.utils;

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
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.Tuple;
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
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.PPAASTParser;
import org.eclipse.jdt.core.dom.PackageDeclaration;

import ca.mcgill.cs.swevo.ppa.PPAOptions;
import de.unisaarland.cs.st.moskito.persistence.Criteria;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.ppa.internal.visitors.ChangeOperationVisitor;
import de.unisaarland.cs.st.moskito.ppa.model.ChangeOperations;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.moskito.ppa.model.JavaElement;
import de.unisaarland.cs.st.moskito.ppa.model.JavaElementLocation;
import de.unisaarland.cs.st.moskito.ppa.model.JavaElementLocationSet;
import de.unisaarland.cs.st.moskito.ppa.model.JavaElementLocation_;
import de.unisaarland.cs.st.moskito.ppa.model.JavaMethodCall;
import de.unisaarland.cs.st.moskito.ppa.model.JavaMethodDefinition;
import de.unisaarland.cs.st.moskito.ppa.model.JavaMethodDefinition_;
import de.unisaarland.cs.st.moskito.ppa.model.JavaElementLocation.LineCover;
import de.unisaarland.cs.st.moskito.ppa.visitors.PPAMethodCallVisitor;
import de.unisaarland.cs.st.moskito.ppa.visitors.PPATypeVisitor;
import de.unisaarland.cs.st.moskito.rcs.Repository;
import de.unisaarland.cs.st.moskito.rcs.elements.ChangeType;
import de.unisaarland.cs.st.moskito.rcs.model.RCSRevision;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.moskito.utils.DiffUtils;
import difflib.DeleteDelta;
import difflib.Delta;
import difflib.InsertDelta;

/**
 * The Class PPAUtils.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class PPAUtils {
	
	private static class CopyThread extends Thread {
		
		private final IProject   project;
		private final File       file;
		private final String     packagename;
		private final String     filename;
		private final PPAOptions options;
		private CompilationUnit  cu;
		private IFile            iFile;
		
		public CopyThread(final IProject project, final File file, final String packagename, final String filename,
				final PPAOptions options) {
			this.project = project;
			this.file = file;
			this.packagename = packagename;
			this.filename = filename;
			this.options = options;
		}
		
		public CompilationUnit getCompilationUnit() {
			return cu;
		}
		
		public IFile getIFile() {
			return iFile;
		}
		
		@Override
		public void run() {
			try {
				iFile = PPAResourceUtil.copyJavaSourceFile(project, file, packagename, filename);
				if (iFile == null) {
					if (Logger.logError()) {
						Logger.error("Error while getting CU from PPA. Timeout copy to workspace exceeded.");
					}
					return;
				}
				cu = getCU(iFile, options);
			} catch (CoreException e) {
				if (Logger.logError()) {
					Logger.error("Could not import file into eclipse workspace", e);
				}
			} catch (IOException e) {
				if (Logger.logError()) {
					Logger.error("Could not import file into eclipse workspace", e);
				}
			}
		}
		
	}
	
	public static void cleanupWorkspace() throws CoreException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.getRoot().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		workspace.getRoot().clearHistory(new NullProgressMonitor());
	}
	
	/**
	 * Find previous call corresponding to the supplied JavaChangeOperation that
	 * must be a JavaMethodCall delete operation.
	 * 
	 * @param persistenceUtil
	 *            the persistence util
	 * @param op
	 *            the JavaChangeOperation to base the search on
	 * @return the previous JavaChangeOperation on the very same JavaMethodCall
	 *         within the same file, same method and closest location. Returns
	 *         null if no such operation exists or if supplied
	 *         JavaChangeOperation is not based on a JavaMethodCall or not a
	 *         delete operation.
	 */
	@NoneNull
	public static JavaChangeOperation findPreviousCall(final PersistenceUtil persistenceUtil,
			final JavaChangeOperation op) {
		
		JavaElementLocation location = op.getChangedElementLocation();
		JavaElement element = location.getElement();
		
		if ((!(element instanceof JavaMethodCall)) || (!op.getChangeType().equals(ChangeType.Deleted))) {
			if (Logger.logDebug()) {
				Logger.debug("Cannot find previous JMethodCall operation based on a non method call delete operation!");
			}
			return null;
		}
		
		Criteria<JavaElementLocation> criteria = persistenceUtil.createCriteria(JavaElementLocation.class);
		CriteriaBuilder cb = criteria.getBuilder();
		Root<JavaElementLocation> root = criteria.getRoot();
		Predicate eq = cb.equal(root.get("element"), element);
		Predicate eq1 = cb.equal(root.get("filePath"), location.getFilePath());
		
		Predicate lt = cb.lt(criteria.getRoot().get(JavaElementLocation_.id), location.getId());
		criteria.getQuery().where(cb.and(cb.and(eq, eq1), lt));
		criteria.getQuery().orderBy(cb.asc(root.get("id")));
		List<JavaElementLocation> hits = persistenceUtil.load(criteria);
		
		if (hits.isEmpty()) {
			return null;
		}
		
		//should be in same method
		//get the javaelement location of surrounding method definition
		String defIdQueryStr = "select * from javaelementlocation WHERE id = (select max(l.id) from javaelementlocation l, javaelement e, javachangeoperation o, rcsrevision r where filepath = '"
				+ location.getFilePath()
				+ "' AND startline <= "
				+ location.getStartLine()
				+ " and endline >= "
				+ location.getStartLine()
				+ " AND l.element_generatedid = e.generatedid AND e.type = 'JAVAMETHODDEFINITION' AND l.id = o.changedelementlocation_id AND o.revision_revisionid = r.revisionid AND (l.id < "
				+ location.getId() + " OR transaction_id = '" + op.getRevision().getTransaction().getId() + "'))";
		Query defIdQuery = persistenceUtil.createNativeQuery(defIdQueryStr, JavaElementLocation.class);
		
		@SuppressWarnings("unchecked") List<JavaElementLocation> methodDefHits = defIdQuery.getResultList();
		
		if (!methodDefHits.isEmpty()) {
			List<JavaElementLocation> strongCandidates = new LinkedList<JavaElementLocation>();
			JavaElementLocation methodDefinition = methodDefHits.get(0);
			//check	which hit is in the same method
			for (JavaElementLocation tmpLoc : hits) {
				if (!methodDefinition.coversLine(tmpLoc.getStartLine()).equals(LineCover.FALSE)) {
					strongCandidates.add(tmpLoc);
				}
			}
			hits = strongCandidates;
		}
		
		//now search for the closest call
		int minDistance = Integer.MAX_VALUE;
		JavaElementLocation bestHit = null;
		for (JavaElementLocation loc : hits) {
			int distance = Math.abs(loc.getStartLine() - location.getStartLine());
			if (distance < minDistance) {
				minDistance = distance;
				bestHit = loc;
			}
		}
		
		if (bestHit == null) {
			return null;
		}
		
		//get the corresponding javachangeoperation
		
		Criteria<JavaChangeOperation> operationCriteria = persistenceUtil.createCriteria(JavaChangeOperation.class);
		operationCriteria.eq("changedElementLocation", bestHit);
		List<JavaChangeOperation> result = persistenceUtil.load(operationCriteria, 1);
		if (result.isEmpty()) {
			return null;
		}
		return result.get(0);
		
	}
	
	/**
	 * Finds the change operation that touched the definition corresponding to
	 * the JavaElement definition.
	 * 
	 * @param persistenceUtil
	 *            the persistence util to use for accessing the relational
	 *            database
	 * @param op
	 *            the operation containing the JavaElement to search for
	 * @return the JavaChangeOperation that touched the definition corresponding
	 *         to the JavaElement associated to <code>op</code>. If no such
	 *         operation exists, returns <code>null</code>.
	 */
	@NoneNull
	public static JavaChangeOperation findPreviousDefinition(final PersistenceUtil persistenceUtil,
			final JavaChangeOperation op) {
		
		JavaElementLocation location = op.getChangedElementLocation();
		JavaElement element = location.getElement();
		JavaElement searchElement = element;
		RCSTransaction opTransaction = op.getRevision().getTransaction();
		
		//distinguish between definitions and calls
		if (element instanceof JavaMethodCall) {
			String[] defNames = getDefinitionNamesForCallName((JavaMethodCall) element);
			
			for (String elementName : defNames) {
				Criteria<JavaMethodDefinition> elementCriteria = persistenceUtil
						.createCriteria(JavaMethodDefinition.class);
				
				CriteriaBuilder cb = elementCriteria.getBuilder();
				Predicate like = cb.like(elementCriteria.getRoot().get(JavaMethodDefinition_.fullQualifiedName),
						elementName);
				elementCriteria.getQuery().where(like);
				List<JavaMethodDefinition> defHits = persistenceUtil.load(elementCriteria, 1);
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
		JavaChangeOperation result = getLastOperationOnElement(persistenceUtil, searchElement, opTransaction, location);
		if ((result == null) || result.equals(op)) {
			return null;
		}
		return result;
	}
	
	/**
	 * Generate change operations. The generated change operations will be
	 * passed to the given visitors.
	 * 
	 * @param repository
	 *            the repository
	 * @param transaction
	 *            the transaction
	 * @param visitors
	 *            the visitors
	 * @param usePPA
	 */
	public static void generateChangeOperations(final Repository repository, final RCSTransaction transaction,
			final Collection<ChangeOperationVisitor> visitors) {
		
		Map<RCSRevision, String> addRevs = new HashMap<RCSRevision, String>();
		Map<RCSRevision, String> deleteRevs = new HashMap<RCSRevision, String>();
		Map<RCSRevision, String> modifyRevs = new HashMap<RCSRevision, String>();
		
		String requestName = Thread.currentThread().getName();
		IJavaProject javaProject = getProject(requestName);
		RCSTransaction parentTransaction = transaction.getParent(transaction.getBranch());
		
		for (RCSRevision rev : transaction.getRevisions()) {
			
			String changedPath = rev.getChangedFile().getPath(transaction);
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
			if (changedPath.startsWith("/")) {
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
			}
		}
		
		// handle the removed files first
		if (parentTransaction != null) {
			File oldCheckoutFile = repository.checkoutPath("/", parentTransaction.getId());
			if (!oldCheckoutFile.exists()) {
				throw new UnrecoverableError("Could not access checkout directory: "
						+ oldCheckoutFile.getAbsolutePath() + ". Ignoring!");
			}
			
			// first copy files!
			Map<RCSRevision, Tuple<IFile, String>> iFiles = new HashMap<RCSRevision, Tuple<IFile, String>>();
			for (Entry<RCSRevision, String> entry : deleteRevs.entrySet()) {
				File file = new File(oldCheckoutFile.getAbsolutePath() + FileUtils.fileSeparator + entry.getValue());
				if (!file.exists()) {
					if (Logger.logDebug()) {
						Logger.debug("Could not find checked out file " + file.getAbsolutePath()
								+ " (might be added in next revision?)");
					}
					continue;
				}
				try {
					String packageName = getPackageFromFile(file);
					IFile newFile = PPAResourceUtil.copyJavaSourceFile(javaProject.getProject(), file, packageName,
							file.getName());
					iFiles.put(entry.getKey(), new Tuple<IFile, String>(newFile, entry.getValue()));
				} catch (Exception e) {
					if (Logger.logError()) {
						Logger.error("Error while getting IFile from PPA for revision " + entry.getKey().toString(), e);
					}
				}
			}
			for (Entry<RCSRevision, Tuple<IFile, String>> entry : iFiles.entrySet()) {
				CompilationUnit cu = getCU(entry.getValue().getFirst(), new PPAOptions());
				generateChangeOperationsForDeletedFile(repository, transaction, entry.getKey(), cu, entry.getValue()
						.getSecond(), visitors);
			}
		}
		
		// handle added files
		File newCheckoutFile = repository.checkoutPath("/", transaction.getId());
		if (!newCheckoutFile.exists()) {
			throw new UnrecoverableError("Could not access checkout directory: " + newCheckoutFile.getAbsolutePath()
					+ ". Ignoring!");
		}
		
		// first copy files!
		Map<RCSRevision, Tuple<IFile, String>> iFiles = new HashMap<RCSRevision, Tuple<IFile, String>>();
		for (Entry<RCSRevision, String> entry : addRevs.entrySet()) {
			File file = new File(newCheckoutFile.getAbsolutePath() + FileUtils.fileSeparator + entry.getValue());
			if (!file.exists()) {
				if (Logger.logDebug()) {
					Logger.debug("Could not find checked out file " + file.getAbsolutePath()
							+ " (might be added in next revision?)");
				}
				continue;
			}
			try {
				String packageName = getPackageFromFile(file);
				IFile newFile = PPAResourceUtil.copyJavaSourceFile(javaProject.getProject(), file, packageName,
						file.getName());
				iFiles.put(entry.getKey(), new Tuple<IFile, String>(newFile, entry.getValue()));
			} catch (Exception e) {
				if (Logger.logError()) {
					Logger.error("Error while getting IFile from PPA for revision " + entry.getKey().toString(), e);
				}
			}
		}
		for (Entry<RCSRevision, Tuple<IFile, String>> entry : iFiles.entrySet()) {
			CompilationUnit cu = getCU(entry.getValue().getFirst(), new PPAOptions());
			generateChangeOperationsForAddedFile(repository, transaction, entry.getKey(), cu, entry.getValue()
					.getSecond(), visitors);
		}
		
		// handle modified files
		for (Entry<RCSRevision, String> entry : modifyRevs.entrySet()) {
			
			Condition.notNull(parentTransaction, "If files got modified there must exist an parent transaction");
			
			File oldCheckoutFile = repository.checkoutPath("/", parentTransaction.getId());
			if (!oldCheckoutFile.exists()) {
				throw new UnrecoverableError("Could not access checkout directory: "
						+ oldCheckoutFile.getAbsolutePath() + ". Ignoring!");
			}
			File oldFile = new File(oldCheckoutFile.getAbsolutePath() + FileUtils.fileSeparator + entry.getValue());
			CompilationUnit oldCU = getCU(oldFile, new PPAOptions());
			JavaElementLocations oldElems = PPAUtils
					.getJavaElementLocationsByCU(oldCU, entry.getValue(), new String[0]);
			
			newCheckoutFile = repository.checkoutPath("/", transaction.getId());
			if (!newCheckoutFile.exists()) {
				throw new UnrecoverableError("Could not access checkout directory: "
						+ newCheckoutFile.getAbsolutePath() + ". Ignoring!");
			}
			File newFile = new File(newCheckoutFile.getAbsolutePath() + FileUtils.fileSeparator + entry.getValue());
			CompilationUnit newCU = getCU(newFile, new PPAOptions());
			JavaElementLocations newElems = PPAUtils
					.getJavaElementLocationsByCU(newCU, entry.getValue(), new String[0]);
			generateChangeOperationsForModifiedFile(repository, transaction, entry.getKey(), oldElems, newElems,
					entry.getValue(), visitors);
		}
	}
	
	private static void generateChangeOperationsForAddedFile(final Repository repository,
			final RCSTransaction transaction, final RCSRevision revision, final CompilationUnit cu,
			final String changedPath, final Collection<ChangeOperationVisitor> visitors) {
		ChangeOperations operations = new ChangeOperations();
		JavaElementLocations newElems = PPAUtils.getJavaElementLocationsByCU(cu, changedPath, new String[0]);
		for (JavaElementLocation classDef : newElems.getClassDefs(changedPath)) {
			JavaChangeOperation op = new JavaChangeOperation(ChangeType.Added, classDef, revision);
			operations.add(op);
			
		}
		for (JavaElementLocation methDef : newElems.getMethodDefs(changedPath)) {
			JavaChangeOperation op = new JavaChangeOperation(ChangeType.Added, methDef, revision);
			operations.add(op);
		}
		for (JavaElementLocation methCall : newElems.getMethodCalls(changedPath)) {
			JavaChangeOperation op = new JavaChangeOperation(ChangeType.Added, methCall, revision);
			operations.add(op);
		}
		for (JavaChangeOperation op : operations.getOperations()) {
			for (ChangeOperationVisitor visitor : visitors) {
				visitor.visit(op);
			}
		}
		
	}
	
	protected static void generateChangeOperationsForDeletedFile(final Repository repository,
			final RCSTransaction transaction, final RCSRevision revision, final CompilationUnit cu,
			final String changedPath, final Collection<ChangeOperationVisitor> visitors) {
		
		ChangeOperations operations = new ChangeOperations();
		
		JavaElementLocations oldElems = PPAUtils.getJavaElementLocationsByCU(cu, changedPath, new String[0]);
		
		for (JavaElementLocation classDef : oldElems.getClassDefs(changedPath)) {
			JavaChangeOperation op = new JavaChangeOperation(ChangeType.Deleted, classDef, revision);
			operations.add(op);
		}
		for (JavaElementLocation methDef : oldElems.getMethodDefs(changedPath)) {
			JavaChangeOperation op = new JavaChangeOperation(ChangeType.Deleted, methDef, revision);
			operations.add(op);
		}
		for (JavaElementLocation methCall : oldElems.getMethodCalls(changedPath)) {
			JavaChangeOperation op = new JavaChangeOperation(ChangeType.Deleted, methCall, revision);
			operations.add(op);
		}
		for (JavaChangeOperation op : operations.getOperations()) {
			for (ChangeOperationVisitor visitor : visitors) {
				visitor.visit(op);
			}
		}
	}
	
	/**
	 * Generate change operations for modified file. The generated change
	 * operations will be passed to the given visitors.
	 * 
	 * @param repository
	 *            the repository
	 * @param revision
	 *            the revision
	 * @param visitors
	 *            the visitors
	 */
	@SuppressWarnings({ "unchecked" })
	@NoneNull
	protected static void generateChangeOperationsForModifiedFile(final Repository repository,
			final RCSTransaction transaction, final RCSRevision revision, final JavaElementLocations oldElems,
			final JavaElementLocations newElems, final String changedPath,
			final Collection<ChangeOperationVisitor> visitors) {
		
		// generate diff
		Collection<Delta> diff = repository.diff(changedPath, transaction.getParent(transaction.getBranch()).getId(),
				transaction.getId());
		ChangeOperations operations = new ChangeOperations();
		for (Delta delta : diff) {
			
			if (delta instanceof DeleteDelta) {
				// corresponding elements to be removed
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
				// corresponding elements to be added
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
				// make a collection diff
				HashSet<Integer> oldLines = DiffUtils.getLineNumbers(delta.getOriginal());
				HashSet<Integer> newLines = DiffUtils.getLineNumbers(delta.getRevised());
				
				Collection<JavaElementLocation> modifiedDefCandidates = new HashSet<JavaElementLocation>();
				
				Collection<JavaElementLocation> addDefCandidates = new HashSet<JavaElementLocation>();
				for (JavaElementLocation def : newElems.getElements(changedPath)) {
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
				Collection<JavaElementLocation> removeDefCandidates = new HashSet<JavaElementLocation>();
				for (JavaElementLocation def : oldElems.getElements(changedPath)) {
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
				
				Collection<JavaElementLocation> defsToRemove = new HashSet<JavaElementLocation>();
				Collection<JavaElementLocation> defsToAdd = new HashSet<JavaElementLocation>();
				
				defsToRemove.addAll(CollectionUtils.subtract(removeDefCandidates, addDefCandidates));
				defsToAdd.addAll(CollectionUtils.subtract(addDefCandidates, removeDefCandidates));
				
				Map<String, TreeSet<JavaElementLocation>> removeCallCandidates = new HashMap<String, TreeSet<JavaElementLocation>>();
				if (oldElems.getMethodCalls(changedPath) != null) {
					for (JavaElementLocation call : oldElems.getMethodCalls(changedPath)) {
						LineCover cover = call.coversAnyLine(oldLines);
						if (!cover.equals(LineCover.FALSE)) {
							if (!removeCallCandidates.containsKey(call.getElement().getFullQualifiedName())) {
								removeCallCandidates.put(call.getElement().getFullQualifiedName(),
										new TreeSet<JavaElementLocation>());
							}
							removeCallCandidates.get(call.getElement().getFullQualifiedName()).add(call);
						}
					}
				}
				Map<String, TreeSet<JavaElementLocation>> addCallCandidates = new HashMap<String, TreeSet<JavaElementLocation>>();
				if (newElems.getMethodCalls(changedPath) != null) {
					for (JavaElementLocation call : newElems.getMethodCalls(changedPath)) {
						LineCover cover = call.coversAnyLine(newLines);
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
				for (String addedCallId : addCallCandidates.keySet()) {
					if (removeCallCandidates.containsKey(addedCallId)) {
						TreeSet<JavaElementLocation> delSet = removeCallCandidates.get(addedCallId);
						TreeSet<JavaElementLocation> addSet = addCallCandidates.get(addedCallId);
						for (JavaElementLocation addedCall : new TreeSet<JavaElementLocation>(
								addCallCandidates.get(addedCallId))) {
							JavaElementLocation ceiling = delSet.ceiling(addedCall);
							JavaElementLocation floor = delSet.floor(addedCall);
							if ((ceiling != null) || (floor != null)) {
								JavaElementLocation correspondent = null;
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
				for (TreeSet<JavaElementLocation> methodCallsToDelete : removeCallCandidates.values()) {
					for (JavaElementLocation methodCall : methodCallsToDelete) {
						operations.add(new JavaChangeOperation(ChangeType.Deleted, methodCall, revision));
					}
				}
				for (TreeSet<JavaElementLocation> methodCallsToAdd : addCallCandidates.values()) {
					for (JavaElementLocation methodCall : methodCallsToAdd) {
						operations.add(new JavaChangeOperation(ChangeType.Added, methodCall, revision));
					}
				}
				for (JavaElementLocation methodDefToDelete : defsToRemove) {
					operations.add(new JavaChangeOperation(ChangeType.Deleted, methodDefToDelete, revision));
				}
				for (JavaElementLocation methodDefToAdd : defsToAdd) {
					operations.add(new JavaChangeOperation(ChangeType.Added, methodDefToAdd, revision));
				}
				for (JavaElementLocation methodDefModified : modifiedDefCandidates) {
					operations.add(new JavaChangeOperation(ChangeType.Modified, methodDefModified, revision));
				}
			}
		}
		for (JavaChangeOperation op : operations.getOperations()) {
			for (ChangeOperationVisitor visitor : visitors) {
				visitor.visit(op);
			}
		}
	}
	
	public static void generateChangeOperationsNOPPA(final Repository repository, final RCSTransaction transaction,
			final Collection<ChangeOperationVisitor> visitors) {
		
		Map<RCSRevision, String> addRevs = new HashMap<RCSRevision, String>();
		Map<RCSRevision, String> deleteRevs = new HashMap<RCSRevision, String>();
		Map<RCSRevision, String> modifyRevs = new HashMap<RCSRevision, String>();
		
		RCSTransaction parentTransaction = transaction.getParent(transaction.getBranch());
		
		for (RCSRevision rev : transaction.getRevisions()) {
			
			String changedPath = rev.getChangedFile().getPath(transaction);
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
			if (changedPath.startsWith("/")) {
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
			}
		}
		
		// handle the removed files first
		if (parentTransaction != null) {
			File oldCheckoutFile = repository.checkoutPath("/", parentTransaction.getId());
			if (!oldCheckoutFile.exists()) {
				throw new UnrecoverableError("Could not access checkout directory: "
						+ oldCheckoutFile.getAbsolutePath() + ". Ignoring!");
			}
			
			for (Entry<RCSRevision, String> entry : deleteRevs.entrySet()) {
				File file = new File(oldCheckoutFile.getAbsolutePath() + FileUtils.fileSeparator + entry.getValue());
				CompilationUnit cu = getCUNoPPA(file);
				generateChangeOperationsForDeletedFile(repository, transaction, entry.getKey(), cu, entry.getValue(),
						visitors);
			}
		}
		
		// handle added files
		File newCheckoutFile = repository.checkoutPath("/", transaction.getId());
		if (!newCheckoutFile.exists()) {
			throw new UnrecoverableError("Could not access checkout directory: " + newCheckoutFile.getAbsolutePath()
					+ ". Ignoring!");
		}
		
		for (Entry<RCSRevision, String> entry : addRevs.entrySet()) {
			File file = new File(newCheckoutFile.getAbsolutePath() + FileUtils.fileSeparator + entry.getValue());
			CompilationUnit cu = getCUNoPPA(file);
			generateChangeOperationsForAddedFile(repository, transaction, entry.getKey(), cu, entry.getValue(),
					visitors);
		}
		
		// handle modified files
		for (Entry<RCSRevision, String> entry : modifyRevs.entrySet()) {
			
			Condition.notNull(parentTransaction, "If files got modified there must exist an parent transaction");
			
			File oldCheckoutFile = repository.checkoutPath("/", parentTransaction.getId());
			if (!oldCheckoutFile.exists()) {
				throw new UnrecoverableError("Could not access checkout directory: "
						+ oldCheckoutFile.getAbsolutePath() + ". Ignoring!");
			}
			File oldFile = new File(oldCheckoutFile.getAbsolutePath() + FileUtils.fileSeparator + entry.getValue());
			CompilationUnit oldCU = getCUNoPPA(oldFile);
			JavaElementLocations oldElems = PPAUtils
					.getJavaElementLocationsByCU(oldCU, entry.getValue(), new String[0]);
			
			newCheckoutFile = repository.checkoutPath("/", transaction.getId());
			if (!newCheckoutFile.exists()) {
				throw new UnrecoverableError("Could not access checkout directory: "
						+ newCheckoutFile.getAbsolutePath() + ". Ignoring!");
			}
			File newFile = new File(newCheckoutFile.getAbsolutePath() + FileUtils.fileSeparator + entry.getValue());
			CompilationUnit newCU = getCUNoPPA(newFile);
			JavaElementLocations newElems = PPAUtils
					.getJavaElementLocationsByCU(newCU, entry.getValue(), new String[0]);
			generateChangeOperationsForModifiedFile(repository, transaction, entry.getKey(), oldElems, newElems,
					entry.getValue(), visitors);
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
			
			CopyThread copyThread = new CopyThread(javaProject.getProject(), file, packageName, fileName, options);
			
			// IFile newFile =
			// PPAResourceUtil.copyJavaSourceFile(javaProject.getProject(),
			// file, packageName, fileName);
			
			copyThread.start();
			
			copyThread.join(120000);
			cu = copyThread.getCompilationUnit();
			if (cu == null) {
				IFile ifile = copyThread.getIFile();
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
			PPACUThread ppacuThread = new PPACUThread(file, options);
			Thread t = new Thread(ppacuThread);
			t.run();
			t.join(30000);
			cu = ppacuThread.getCU();
		} catch (InterruptedException e1) {
			if (Logger.logError()) {
				Logger.error("Error while getting CU using PPA.", e1);
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
	 * Gets the cU no ppa.
	 * 
	 * @param file
	 *            the file
	 * @return the cU no ppa
	 */
	public static CompilationUnit getCUNoPPA(final File file) {
		CompilationUnit cu = null;
		try {
			String content = FileUtils.readFileToString(file);
			ASTNode node = null;
			ASTParser parser = ASTParser.newParser(AST.JLS3);
			parser.setStatementsRecovery(true);
			parser.setResolveBindings(true);
			parser.setSource(content.toCharArray());
			node = parser.createAST(null);
			cu = (CompilationUnit) node;
		} catch (Exception e) {
			if (Logger.logError()) {
				Logger.error("Error while getting CU without PPA", e);
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
			node = parser2.createAST(false, new NullProgressMonitor());
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
	public static Map<File, CompilationUnit> getCUs(final Collection<File> files, final PPAOptions options) {
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
	public static Map<File, CompilationUnit> getCUs(final Collection<File> files, final PPAOptions options,
			final String requestName) {
		Map<File, CompilationUnit> cus = new HashMap<File, CompilationUnit>();
		Map<IFile, File> iFiles = new HashMap<IFile, File>();
		try {
			cleanupWorkspace();
		} catch (CoreException e1) {
			if (Logger.logWarn()) {
				Logger.warn(e1.getMessage(), e1);
			}
		}
		for (File file : files) {
			String fileName = file.getName();
			try {
				String packageName = getPackageFromFile(file);
				IJavaProject javaProject = getProject(requestName);
				IFile newFile = PPAResourceUtil.copyJavaSourceFile(javaProject.getProject(), file, packageName,
						fileName);
				iFiles.put(newFile, file);
			} catch (Exception e) {
				if (Logger.logError()) {
					Logger.error("Error while getting IFile from PPA", e);
				}
			}
		}
		
		for (IFile iFile : iFiles.keySet()) {
			if (Logger.logDebug()) {
				Logger.debug("Getting CU for file: " + iFile.getLocation().toOSString());
			}
			// PPACUThread ppacuThread = new PPACUThread(iFile, options);
			// Thread t = new Thread(ppacuThread);
			// t.run();
			// try {
			// t.join(30000);
			// } catch (InterruptedException e) {
			// if (Logger.logWarn()) {
			// Logger.warn(e.getMessage(), e);
			// }
			// }
			// CompilationUnit cu = ppacuThread.getCU();
			CompilationUnit cu = getCU(iFile, options);
			if (cu == null) {
				cu = getCUNoPPA(iFile);
			}
			cus.put(iFiles.get(iFile), cu);
		}
		
		return cus;
	}
	
	protected static Map<RCSRevision, CompilationUnit> getCUsForTransaction(final Repository repository,
			final RCSTransaction transaction, final ChangeType changeType) {
		
		Map<RCSRevision, IFile> ifiles = new HashMap<RCSRevision, IFile>();
		Map<RCSRevision, CompilationUnit> result = new HashMap<RCSRevision, CompilationUnit>();
		
		File oldCheckoutFile = repository.checkoutPath("/", transaction.getId());
		if (!oldCheckoutFile.exists()) {
			if (Logger.logError()) {
				Logger.error("Could not access checkout directory: " + oldCheckoutFile.getAbsolutePath()
						+ ". Ignoring!");
			}
			return result;
		}
		
		String requestName = Thread.currentThread().getName();
		
		Map<File, RCSRevision> filesToAnalyze = new HashMap<File, RCSRevision>();
		for (RCSRevision revision : transaction.getRevisions()) {
			
			if (changeType.equals(ChangeType.Added)) {
				if (revision.getChangeType().equals(ChangeType.Deleted)) {
					continue;
				}
			} else if (changeType.equals(ChangeType.Deleted)) {
				if (revision.getChangeType().equals(ChangeType.Added)) {
					continue;
				}
			}
			
			String changedPath = revision.getChangedFile().getPath(transaction);
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
			if (changedPath.startsWith("/")) {
				changedPath = changedPath.substring(1);
			}
			
			File file = new File(oldCheckoutFile.getAbsolutePath() + FileUtils.fileSeparator + changedPath);
			if (!file.exists()) {
				if (Logger.logDebug()) {
					Logger.debug("Could not find checked out file " + file.getAbsolutePath()
							+ " (might be added in next revision?)");
				}
				continue;
			}
			filesToAnalyze.put(file, revision);
			
			String fileName = file.getName();
			try {
				String packageName = getPackageFromFile(file);
				IJavaProject javaProject = getProject(requestName);
				IFile newFile = PPAResourceUtil.copyJavaSourceFile(javaProject.getProject(), file, packageName,
						fileName);
				ifiles.put(revision, newFile);
			} catch (Exception e) {
				if (Logger.logError()) {
					Logger.error("Error while getting IFile from PPA for revision " + revision.toString(), e);
				}
			}
		}
		
		for (Entry<RCSRevision, IFile> entry : ifiles.entrySet()) {
			CompilationUnit cu = getCU(entry.getValue(), new PPAOptions());
			result.put(entry.getKey(), cu);
		}
		return result;
	}
	
	/**
	 * Computes the possible matching method definition names based on the full
	 * qualified name of the supplied JavaMethodCall; The returned array of
	 * Strings is sorted in descending importance order.
	 * 
	 * @param call
	 *            the JavaMethodCall to base the method definition name
	 *            generation on.
	 * @return the definition names corresponding to the supplied
	 *         JavaMethodCall. Sorted in descending importance and likelyhood
	 *         order.
	 */
	protected static String[] getDefinitionNamesForCallName(final JavaMethodCall call) {
		String name = String.valueOf(call.getFullQualifiedName());
		
		//replace "<init>" from name
		if (name.contains("<init>()")) {
			int index = 0;
			String[] nameParts = name.split("\\.");
			for (String namePart : nameParts) {
				if (namePart.equals("<init>()")) {
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
			String className = nameParts[index - 1];
			name = name.replace("<init>()", className + "()");
		}
		//if it starts with src. get rid of prefix
		if (name.startsWith("src.")) {
			name = name.substring(4);
		}
		//the all might call an inner class
		StringBuilder alternativeName = new StringBuilder();
		String[] nameParts = name.split("\\.");
		if (nameParts.length > 3) {
			nameParts[nameParts.length - 3] = "";
			for (int i = 0; i < nameParts.length; ++i) {
				if (!nameParts[i].equals("")) {
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
	 * @param file
	 *            the file
	 * @param filePrefixPath
	 *            the file prefix path
	 * @param packageFilter
	 *            the package filter
	 * @return the java element locations by file
	 */
	@NoneNull
	public static JavaElementLocations getJavaElementLocationsByCU(final CompilationUnit cu, final String relativePath,
			final String[] packageFilter) {
		PPAMethodCallVisitor methodCallVisitor = new PPAMethodCallVisitor();
		
		JavaElementLocationSet locationSet = new JavaElementLocationSet();
		
		if (cu != null) {
			PPATypeVisitor typeVisitor = new PPATypeVisitor(cu, relativePath, packageFilter, locationSet);
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
	 * @return the java element locations by file
	 */
	public static JavaElementLocations getJavaElementLocationsByFile(final File file, final String filePrefixPath,
			final String[] packageFilter) {
		PPAMethodCallVisitor methodCallVisitor = new PPAMethodCallVisitor();
		PPAOptions ppaOptions = new PPAOptions();
		
		JavaElementLocationSet locationSet = new JavaElementLocationSet();
		
		if (!file.getAbsolutePath().startsWith(filePrefixPath)) {
			if (Logger.logError()) {
				Logger.error("File name does not start with specified filePrefixPath");
			}
		} else {
			
			CompilationUnit cu = getCU(file, ppaOptions);
			if (cu != null) {
				
				String relativePath = file.getAbsolutePath().substring(filePrefixPath.length());
				if (relativePath.startsWith(FileUtils.fileSeparator)) {
					relativePath = relativePath.substring(1);
				}
				
				PPATypeVisitor typeVisitor = new PPATypeVisitor(cu, relativePath, packageFilter, locationSet);
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
	 * Gets the java method elements of all java files within
	 * <code>sourceDir</code>.
	 * 
	 * @param sourceDir
	 *            the source dir
	 * @param packageFilter
	 *            the package filter
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
	 * @return the java method elements
	 */
	public static JavaElementLocations getJavaElementLocationsByFile(final Iterator<File> fileIterator,
			final String filePrefixPath, final String[] packageFilter) {
		PPAMethodCallVisitor methodCallVisitor = new PPAMethodCallVisitor();
		PPAOptions ppaOptions = new PPAOptions();
		
		JavaElementLocationSet locationSet = new JavaElementLocationSet();
		
		while (fileIterator.hasNext()) {
			File file = fileIterator.next();
			CompilationUnit cu = getCU(file, ppaOptions);
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
			
			PPATypeVisitor typeVisitor = new PPATypeVisitor(cu, relativePath, packageFilter, locationSet);
			typeVisitor.registerVisitor(methodCallVisitor);
			cu.accept(typeVisitor);
		}
		
		return locationSet.getJavaElementLocations();
	}
	
	private static JavaChangeOperation getLastOperationOnElement(final PersistenceUtil persistenceUtil,
			JavaElement searchElement, RCSTransaction opTransaction, JavaElementLocation notMatch) {
		Criteria<JavaElementLocation> criteria = persistenceUtil.createCriteria(JavaElementLocation.class);
		CriteriaBuilder cb = criteria.getBuilder();
		Root<JavaElementLocation> root = criteria.getRoot();
		Predicate eq = cb.equal(root.get("element"), searchElement);
		
		//it might be an operation within the same transaction having a larger id.
		
		//get the highest location id within the same transaction
		
		@SuppressWarnings("unchecked") List<Long> maxIdResultList = persistenceUtil
				.executeNativeSelectQuery("select max(l.id) from javaelementlocation l, javachangeoperation o WHERE l.id = o.changedelementlocation_id and o.revision_revisionid IN (select revisionid from rcsrevision where transaction_id = '"
						+ opTransaction.getId() + "')");
		
		Long maxId = maxIdResultList.get(0);
		
		Predicate lt = cb.lt(criteria.getRoot().get(JavaElementLocation_.id), maxId);
		
		Predicate ne = cb.notEqual(criteria.getRoot().get(JavaElementLocation_.id), notMatch.getId());
		
		criteria.getQuery().where(cb.and(cb.and(eq, lt), ne));
		criteria.getQuery().orderBy(cb.desc(root.get("id")));
		
		List<JavaElementLocation> hits = persistenceUtil.load(criteria, 1);
		if (hits.isEmpty()) {
			return null;
		}
		
		JavaElementLocation hitLocation = hits.get(0);
		
		//search for corresponding JavaChangeOperation
		Criteria<JavaChangeOperation> operationCriteria = persistenceUtil.createCriteria(JavaChangeOperation.class);
		operationCriteria.eq("changedElementLocation", hitLocation);
		List<JavaChangeOperation> operationHits = persistenceUtil.load(operationCriteria, 1);
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
		PPAASTParser parser2 = new PPAASTParser(AST.JLS3);
		parser2.setStatementsRecovery(true);
		parser2.setResolveBindings(true);
		parser2.setSource(PPAResourceUtil.getContent(file).toCharArray());
		CompilationUnit cu = (CompilationUnit) parser2.createAST(false, new NullProgressMonitor());
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
