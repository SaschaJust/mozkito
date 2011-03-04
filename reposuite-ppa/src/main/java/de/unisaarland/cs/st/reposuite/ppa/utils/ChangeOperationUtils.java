package de.unisaarland.cs.st.reposuite.ppa.utils;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;

import de.unisaarland.cs.st.reposuite.ppa.internal.visitors.ChangeOperationVisitor;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaClassDefinition;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaElementDefinition;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaElementLocation;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaElementLocation.LineCover;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaMethodCall;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaMethodDefinition;
import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.elements.ChangeType;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSRevision;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.utils.Condition;
import de.unisaarland.cs.st.reposuite.utils.DiffUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.specification.NoneNull;
import difflib.DeleteDelta;
import difflib.Delta;
import difflib.InsertDelta;

public class ChangeOperationUtils {
	
	@NoneNull
	private static String filterChangedPath(final String changedPath) {
		if (!changedPath.endsWith(".java")) {
			if (Logger.logWarn()) {
				Logger.warn("Ignoring non-Java file: " + changedPath);
			}
			return null;
		}
		if (changedPath.startsWith("/")) {
			return changedPath.substring(1);
		}
		return changedPath;
	}
	
	@NoneNull
	protected static void generateChangeOperationsForAddedFile(final Repository repository, final RCSRevision revision,
			final Collection<ChangeOperationVisitor> visitors) {
		
		String changedPath = filterChangedPath(revision.getChangedFile().getPath(revision.getTransaction()));
		if (changedPath == null) {
			return;
		}
		
		//checkout repo to new revision
		File checkoutPath = repository.checkoutPath("/", revision.getTransaction().getId());
		if (!checkoutPath.exists()) {
			if (Logger.logError()) {
				Logger.error("Could not access checkout directory: " + checkoutPath.getAbsolutePath() + ". Ignoring!");
			}
			return;
		}
		
		File file = new File(checkoutPath.getAbsolutePath() + changedPath);
		if (!file.exists()) {
			if (Logger.logError()) {
				Logger.error("Could not access added file for analysis: " + file.getAbsolutePath() + ". Ignoring!");
			}
			return;
		}
		
		//get the new elements
		JavaElementLocations newElems = PPAUtils.getJavaElementLocationsByFile(file, checkoutPath.getAbsolutePath(),
				new String[0]);
		
		for (JavaElementLocation<JavaClassDefinition> classDef : newElems.getClassDefs(changedPath)) {
			JavaChangeOperation op = new JavaChangeOperation(ChangeType.Added, classDef, revision);
			for (ChangeOperationVisitor visitor : visitors) {
				visitor.visit(op);
			}
		}
		for (JavaElementLocation<JavaMethodDefinition> methDef : newElems.getMethodDefs(changedPath)) {
			JavaChangeOperation op = new JavaChangeOperation(ChangeType.Added, methDef, revision);
			for (ChangeOperationVisitor visitor : visitors) {
				visitor.visit(op);
			}
		}
		for (JavaElementLocation<JavaMethodCall> methCall : newElems.getMethodCalls(changedPath)) {
			JavaChangeOperation op = new JavaChangeOperation(ChangeType.Added, methCall, revision);
			for (ChangeOperationVisitor visitor : visitors) {
				visitor.visit(op);
			}
		}
	}
	
	@NoneNull
	protected static void generateChangeOperationsForDeletedFile(final Repository repository,
			final RCSRevision revision, final Collection<ChangeOperationVisitor> visitors) {
		
		String changedPath = filterChangedPath(revision.getChangedFile().getPath(revision.getTransaction()));
		if (changedPath == null) {
			return;
		}
		
		RCSTransaction transaction = revision.getTransaction();
		RCSTransaction parentTransaction = transaction.getParent(transaction.getBranch());
		if (parentTransaction == null) {
			if (Logger.logError()) {
				Logger.error("Could not generate ChangeOperations for deleted file " + changedPath + " in transation "
						+ transaction.getId() + ". The parent transaction could not be found! Ignoring!");
			}
			return;
		}
		
		//checkout repo to new revision
		File checkoutPath = repository.checkoutPath("/", parentTransaction.getId());
		if (!checkoutPath.exists()) {
			if (Logger.logError()) {
				Logger.error("Could not access checkout directory: " + checkoutPath.getAbsolutePath() + ". Ignoring!");
			}
			return;
		}
		
		File file = new File(checkoutPath.getAbsolutePath() + changedPath);
		if (!file.exists()) {
			if (Logger.logError()) {
				Logger.error("Could not access added file for analysis: " + file.getAbsolutePath() + ". Ignoring!");
			}
			return;
		}
		
		//get the old elements
		JavaElementLocations oldElems = PPAUtils.getJavaElementLocationsByFile(file, checkoutPath.getAbsolutePath(),
				new String[0]);
		
		for (JavaElementLocation<JavaClassDefinition> classDef : oldElems.getClassDefs(changedPath)) {
			JavaChangeOperation op = new JavaChangeOperation(ChangeType.Deleted, classDef, revision);
			for (ChangeOperationVisitor visitor : visitors) {
				visitor.visit(op);
			}
		}
		for (JavaElementLocation<JavaMethodDefinition> methDef : oldElems.getMethodDefs(changedPath)) {
			JavaChangeOperation op = new JavaChangeOperation(ChangeType.Deleted, methDef, revision);
			for (ChangeOperationVisitor visitor : visitors) {
				visitor.visit(op);
			}
		}
		for (JavaElementLocation<JavaMethodCall> methCall : oldElems.getMethodCalls(changedPath)) {
			JavaChangeOperation op = new JavaChangeOperation(ChangeType.Deleted, methCall, revision);
			for (ChangeOperationVisitor visitor : visitors) {
				visitor.visit(op);
			}
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@NoneNull
	protected static void generateChangeOperationsForModifiedFile(final Repository repository,
			final RCSRevision revision, final Collection<ChangeOperationVisitor> visitors) {
		
		String changedPath = filterChangedPath(revision.getChangedFile().getPath(revision.getTransaction()));
		if (changedPath == null) {
			return;
		}
		
		RCSTransaction transaction = revision.getTransaction();
		RCSTransaction parentTransaction = transaction.getParent(transaction.getBranch());
		if (parentTransaction == null) {
			if (Logger.logError()) {
				Logger.error("Could not generate ChangeOperations for deleted file " + changedPath + " in transation "
						+ transaction.getId() + ". The parent transaction could not be found! Ignoring!");
			}
			return;
		}
		
		//update working copy to parent transaction
		File oldCheckoutFile = repository.checkoutPath("/", parentTransaction.getId());
		if (!oldCheckoutFile.exists()) {
			if (Logger.logError()) {
				Logger.error("Could not access checkout directory: " + oldCheckoutFile.getAbsolutePath()
						+ ". Ignoring!");
			}
			return;
		}
		
		File oldFile = new File(oldCheckoutFile.getAbsoluteFile() + changedPath);
		if (!oldFile.exists()) {
			if (Logger.logError()) {
				Logger.error("Could not access old version of modified file for analysis: " + oldFile.getAbsolutePath()
						+ ". Ignoring!");
			}
			return;
		}
		
		JavaElementLocations oldElems = PPAUtils.getJavaElementLocationsByFile(oldFile,
				oldCheckoutFile.getAbsolutePath(), new String[0]);
		
		//update working copy to current transaction
		File checkoutFile = repository.checkoutPath("/", transaction.getId());
		if (!checkoutFile.exists()) {
			if (Logger.logError()) {
				Logger.error("Could not access checkout directory: " + checkoutFile.getAbsolutePath() + ". Ignoring!");
			}
			return;
		}
		
		File newFile = new File(checkoutFile.getAbsoluteFile() + changedPath);
		if (!newFile.exists()) {
			if (Logger.logError()) {
				Logger.error("Could not access old version of modified file for analysis: " + newFile.getAbsolutePath()
						+ ". Ignoring!");
			}
			return;
		}
		
		JavaElementLocations newElems = PPAUtils.getJavaElementLocationsByFile(newFile,
				oldCheckoutFile.getAbsolutePath(), new String[0]);
		
		//generate diff
		Collection<Delta> diff = repository.diff(changedPath, parentTransaction.getId(), transaction.getId());
		List<JavaChangeOperation> operations = new LinkedList<JavaChangeOperation>();
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
				for (TreeSet<JavaElementLocation<JavaMethodCall>> methodCallsToDelete : removeCallCandidates.values()) {
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
		for (JavaChangeOperation op : operations) {
			for (ChangeOperationVisitor visitor : visitors) {
				visitor.visit(op);
			}
		}
	}
}
