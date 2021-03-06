/***********************************************************************************************************************
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
 **********************************************************************************************************************/

package org.mozkito.genealogies.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.codeanalysis.model.JavaElement;
import org.mozkito.codeanalysis.model.JavaElementLocation;
import org.mozkito.codeanalysis.model.JavaMethodCall;
import org.mozkito.codeanalysis.model.JavaMethodDefinition;
import org.mozkito.genealogies.core.CoreChangeGenealogy;

/**
 * The Class JavaMethodRegistry.
 * 
 * @author kim
 */
public class JavaMethodRegistry {
	
	// map signature <-> definitions
	/** The method definitions. */
	protected HashMap<String, JavaChangeOperation>                             methodDefinitions;
	
	/** The method definition deletions. */
	protected HashMap<String, ArrayList<JavaChangeOperation>>                  methodDefinitionDeletions;
	
	// map calling class name . signature <-> invocations
	/** The method invocations. */
	protected HashMap<String, HashMap<String, ArrayList<JavaChangeOperation>>> methodInvocations;
	
	/** The method invocation deletions. */
	protected HashMap<String, HashMap<String, ArrayList<JavaChangeOperation>>> methodInvocationDeletions;
	
	/**
	 * Instantiates a new java method registry.
	 * 
	 * @param coreGenealogy
	 *            the core genealogy
	 */
	public JavaMethodRegistry(final CoreChangeGenealogy coreGenealogy) {
		
		// TODO fill collections with data from change genealogy
		
		this.methodDefinitions = new HashMap<String, JavaChangeOperation>();
		this.methodDefinitionDeletions = new HashMap<String, ArrayList<JavaChangeOperation>>();
		this.methodInvocations = new HashMap<String, HashMap<String, ArrayList<JavaChangeOperation>>>();
		this.methodInvocationDeletions = new HashMap<String, HashMap<String, ArrayList<JavaChangeOperation>>>();
	}
	
	/**
	 * Adds a method invocation to the list of active invocations.
	 * 
	 * @param call
	 *            the call
	 * @return <code>true</code> if element could be added; <code>false</code> if invocation with same signature in same
	 *         line exists
	 */
	public boolean addCall(final JavaChangeOperation call) {
		final JavaElement element = call.getChangedElementLocation().getElement();
		
		Condition.check(element instanceof JavaMethodCall,
		                "JavaChangeOperations not touching JavaMethodCalls cannot be added as JavaMethodCall");
		
		final JavaMethodCall methodCall = ((JavaMethodCall) element);
		
		final String fullQualifiedName = methodCall.getFullQualifiedName();
		final String shortClassName = methodCall.getCalledClassNameShort();
		final String callingPosition = call.getChangedElementLocation().getFilePath();
		
		if (fullQualifiedName.contains("UNKNOWN(") || shortClassName.contains("UNKNOWN")) {
			if (Logger.logWarn()) {
				Logger.warn("Ignoring method call containing UNKNOWN name segments.");
			}
			return false;
		}
		if (!this.methodInvocations.containsKey(callingPosition)) {
			this.methodInvocations.put(callingPosition, new HashMap<String, ArrayList<JavaChangeOperation>>());
		}
		final HashMap<String, ArrayList<JavaChangeOperation>> invocationsInClass = this.methodInvocations.get(callingPosition);
		if (!invocationsInClass.containsKey(fullQualifiedName)) {
			invocationsInClass.put(fullQualifiedName, new ArrayList<JavaChangeOperation>());
		}
		final ArrayList<JavaChangeOperation> invList = invocationsInClass.get(fullQualifiedName);
		// if ((!invList.isEmpty()) && (call.isBefore(invList.get(invList.size() - 1)))) {
		// if (Logger.logError()) {
		// Logger.error("Attempt to add an earlier JavaChangeOperation to the "
		// + "JavaMethodRegistry than the previous registered event. "
		// + "This might lead to serious problems. Abort! "
		// + "Please ensure to process JavaChangeOperations following repository timeline: previousCall="
		// + invList.get(invList.size() - 1) + " tried to add=" + call);
		// }
		// return;
		// }
		return invList.add(call);
	}
	
	/**
	 * Adds a definition to the set of active definitions.
	 * 
	 * @param def
	 *            the definition to add
	 * @return <code>true</code> if element could be added; <code>false</code> if element with same signature already
	 *         exists
	 */
	public boolean addMethodDefiniton(final JavaChangeOperation def) {
		final String fullQualifiedName = def.getChangedElementLocation().getElement().getFullQualifiedName();
		if (!this.methodDefinitions.containsKey(fullQualifiedName)) {
			this.methodDefinitions.put(fullQualifiedName, def);
			return true;
		}
		return false;
	}
	
	/**
	 * Checks for a registered method definition that matches the signature of the given definition.
	 * 
	 * @param element
	 *            the element
	 * @param includeDeletions
	 *            Set to <code>true</code> if you want to search for definition deletions too.
	 * @return <code>true</code> when coresponding method definition was found.
	 */
	public boolean existsDefinition(final JavaElement element,
	                                final boolean includeDeletions) {
		final boolean directSuccess = this.methodDefinitions.containsKey(element.getFullQualifiedName());
		if ((!directSuccess) && includeDeletions) {
			return (findPreviousDefinitionDeletion(element) != null);
		}
		return directSuccess;
	}
	
	/**
	 * Checks if a method invocation that has the same signature and origin class is already registered.
	 * 
	 * @param call
	 *            the call
	 * @param location
	 *            the location of the JavaMethodCall
	 * @param matchLines
	 *            set to <code>true</code> if you want to check for method calls in the same line.
	 * @return <code>true</code> if an matching method invocation was found.
	 */
	public boolean existsInvocation(final JavaMethodCall call,
	                                final JavaElementLocation location,
	                                final boolean matchLines) {
		
		final String callingLocation = location.getFilePath();
		if (!this.methodInvocations.containsKey(callingLocation)) {
			return false;
		}
		if (!this.methodInvocations.get(callingLocation).containsKey(call.getFullQualifiedName())) {
			return false;
		}
		final ArrayList<JavaChangeOperation> invocations = this.methodInvocations.get(callingLocation)
		                                                                         .get(call.getFullQualifiedName());
		
		if (invocations.isEmpty()) {
			return false;
		}
		
		if (!matchLines) {
			return true;
		}
		final Iterator<JavaChangeOperation> invIter = invocations.iterator();
		while (invIter.hasNext()) {
			final JavaChangeOperation i = invIter.next();
			final JavaElementLocation l = i.getChangedElementLocation();
			if (l.getStartLine() == location.getStartLine()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Find closest method call.
	 * 
	 * @param methodCall
	 *            the method call
	 * @param location
	 *            the location
	 * @param invocations
	 *            the invocations
	 * @return the java change operation
	 */
	private JavaChangeOperation findClosestMethodCall(final JavaMethodCall methodCall,
	                                                  final JavaElementLocation location,
	                                                  final ArrayList<JavaChangeOperation> invocations) {
		JavaChangeOperation closest = null;
		int distance = -1;
		final Iterator<JavaChangeOperation> opIter = invocations.iterator();
		while (opIter.hasNext()) {
			final JavaChangeOperation tmpOperation = opIter.next();
			final JavaElementLocation tmpLocation = tmpOperation.getChangedElementLocation();
			if (location.getStartLine() == location.getStartLine()) {
				return tmpOperation;
			}
			final int d = Math.abs(location.getStartLine() - tmpLocation.getStartLine());
			if (distance == -1) {
				distance = d;
				closest = tmpOperation;
			} else {
				if (d < distance) {
					distance = d;
					closest = tmpOperation;
				}
			}
		}
		return closest;
	}
	
	/**
	 * Finds and returns the method definition that matches the signature of the given method expression.
	 * 
	 * @param element
	 *            the element
	 * @param includeDeletions
	 *            Set to <code>true</code> if you want to search for definition deletions too.
	 * @return The method definition matching the signature of the given method expression. If no invocation is found an
	 *         exception is thrown. Return <code>null</code> if no such element could be found.
	 */
	public JavaChangeOperation findPreviousDefinition(final JavaElement element,
	                                                  final boolean includeDeletions) {
		
		final String signature = element.getFullQualifiedName();
		
		if (!this.methodDefinitions.containsKey(signature)) {
			if (includeDeletions) {
				return findPreviousDefinitionDeletion(element);
			}
			return null;
		}
		return this.methodDefinitions.get(signature);
	}
	
	/**
	 * Finds and returns the last method definition deletion matching the signature of the given MethodExpression.
	 * 
	 * @param element
	 *            the element
	 * @return The last MethodDefinitionDeletion object that matches the signature of the given MethodExpression.
	 *         Returns <code>null</code> if no element could be found.
	 */
	public JavaChangeOperation findPreviousDefinitionDeletion(final JavaElement element) {
		if (!this.methodDefinitionDeletions.containsKey(element.getFullQualifiedName())) {
			return null;
		}
		final int index = this.methodDefinitionDeletions.get(element.getFullQualifiedName()).size() - 1;
		if (index < 0) {
			return null;
		}
		return this.methodDefinitionDeletions.get(element.getFullQualifiedName()).get(index);
	}
	
	/**
	 * Searches and returns a registered method invocation that has the same signature and same origin class as the
	 * given method invocation. Note: Type and TDGDelta objects are not compared.
	 * 
	 * @param call
	 *            the call
	 * @param location
	 *            the location
	 * @param matchLine
	 *            Set to <code>true</code> if you want to compare line numbers and return only hits that were registered
	 *            for the same line in the calling object.
	 * @return The method invocation matching the signature and origin class of the given method invocation. If no
	 *         invocation is found an exception is thrown. Return <code>null</code> if no such element could be found.
	 */
	public JavaChangeOperation findPreviousInvocation(final JavaMethodCall call,
	                                                  final JavaElementLocation location,
	                                                  final boolean matchLine) {
		
		final String callingLocation = location.getFilePath();
		final String signature = call.getFullQualifiedName();
		
		if (!this.methodInvocations.containsKey(callingLocation)) {
			return null;
		}
		final HashMap<String, ArrayList<JavaChangeOperation>> signatures = this.methodInvocations.get(callingLocation);
		if (!signatures.containsKey(signature)) {
			
			// check for method calls with same method name and args but
			// different type of object method called on. If only one different
			// type existed, take it.
			return findPreviousSimilarCall(call, location, matchLine);
			
		}
		final ArrayList<JavaChangeOperation> invocations = signatures.get(signature);
		if (invocations.isEmpty()) {
			return null;
		}
		if (matchLine) {
			for (final JavaChangeOperation tmpOperation : invocations) {
				final JavaElementLocation tmpLocation = tmpOperation.getChangedElementLocation();
				if (tmpLocation.getStartLine() == location.getStartLine()) {
					return tmpOperation;
				}
			}
			return null;
		}
		final JavaChangeOperation closest = findClosestMethodCall(call, location, invocations);
		return closest;
		
	}
	
	/**
	 * Find previous similar call.
	 * 
	 * @param call
	 *            the call
	 * @param location
	 *            the location
	 * @param matchLine
	 *            the match line
	 * @return the java change operation
	 */
	private JavaChangeOperation findPreviousSimilarCall(final JavaMethodCall call,
	                                                    final JavaElementLocation location,
	                                                    final boolean matchLine) {
		
		final String callingLocation = location.getFilePath();
		final String callSignature = call.getFullQualifiedName();
		
		if (!this.methodInvocations.containsKey(callingLocation)) {
			return null;
		}
		
		final HashMap<String, ArrayList<JavaChangeOperation>> signatures = this.methodInvocations.get(callingLocation);
		ArrayList<JavaChangeOperation> candidates = null;
		
		for (final String signature : signatures.keySet()) {
			
			final boolean sameMethodName = JavaElement.extractMethodName(signature)
			                                          .equals(JavaElement.extractMethodName(callSignature));
			if (signatures.get(signature).isEmpty()) {
				continue;
			}
			final boolean sameArguments = ((JavaMethodCall) signatures.get(signature).get(0)
			                                                          .getChangedElementLocation().getElement()).getSignature()
			                                                                                                    .equals(call.getSignature());
			
			if (sameMethodName && sameArguments) {
				candidates = signatures.get(signature);
				break;
			}
		}
		if ((candidates == null) || (candidates.size() < 1)) {
			return null;
		}
		return findClosestMethodCall(call, location, candidates);
		
	}
	
	// /**
	// * Finds and returns the only method definition that matches one of the
	// * following criteria (with respect of given criteria order) iff there
	// * exists only one match.
	// *
	// * @param element
	// * the element
	// * @param includeDeletions
	// * Set to <code>true</code> if you want to search for definition
	// * deletions too.
	// * @param anyInRevision
	// * Set to <code>true</code> this method returns any method
	// * definition that matches the number of arguments iff all
	// * candidates were added in the same revision.
	// * @return The only method definition that matches one of the following
	// * criteria (with respect of given criteria order) iff there exists
	// * only one match
	// */
	// public JavaChangeOperation findPreviousSimilarDefinition(JavaElement element, boolean includeDeletions,
	// boolean anyInRevision) {
	//
	//
	// HashSet<JavaChangeOperation> candidates = new HashSet<JavaChangeOperation>();
	// HashSet<JavaChangeOperation> tmpCandidates = new HashSet<JavaChangeOperation>();
	//
	// String elementMethodName = JavaElement.extractMethodName(element.getFullQualifiedName());
	// List<String> elementArgs = null;
	//
	// if(element instanceof JavaMethodCall){
	// elementArgs = ((JavaMethodCall) element).getSignature();
	// }else if(element instanceof JavaMethodDefinition){
	// elementArgs = ((JavaMethodDefinition) element).getSignature();
	// }else{
	// return null;
	// }
	//
	// // get all signatures with same class name and number of arguments
	// // that were added in previous transactions
	//
	// for (String signature : methodDefinitions.keySet()) {
	//
	// JavaChangeOperation tmpOp = methodDefinitions.get(signature);
	// JavaMethodDefinition tmpElem = (JavaMethodDefinition) tmpOp.getChangedElementLocation().getElement();
	//
	//
	// if (elementArgs.size() == tmpElem.getSignature().size()) {
	// if (elementMethodName.equals(JavaElement.extractMethodName(tmpElem.getFullQualifiedName()))) {
	// tmpCandidates.add(this.methodDefinitions.get(signature));
	// } else {
	// candidates.add(this.methodDefinitions.get(signature));
	// }
	// }
	// }
	//
	// // if we found matching class name, we continue with the set of
	// // definitions
	// // matching the class name
	//
	// //prefer the candidates that match the number of arguments
	// if (tmpCandidates.size() > 0) {
	// candidates = tmpCandidates;
	// } else if (candidates.size() < 1) {
	// //there is no single candidate
	// return null;
	// }
	//
	// if (candidates.size() == 1) {
	// //there is onlye one candidate.
	// return candidates.iterator().next();
	// } else if (anyInRevision) {
	//
	// //check if all candidates are in one revision.
	// HashSet<String> revisions = new HashSet<String>();
	// for (JavaChangeOperation candidate : candidates) {
	// revisions.add(candidate.getRevision().getTransaction().getId());
	// }
	// if ((revisions.size() == 1) && (candidates.iterator().hasNext())) {
	// return candidates.iterator().next();
	// }
	// }
	//
	// // check for candidates that match exactly the argument types.
	// tmpCandidates = new HashSet<MethodDefinition>();
	// Iterator<MethodDefinition> defIter = candidates.iterator();
	// while (defIter.hasNext()) {
	// MethodDefinition def = defIter.next();
	// if (expSig.getArguments().equals(def.getSignature().getArguments())) {
	// tmpCandidates.add(def);
	// }
	// }
	//
	// if (tmpCandidates.size() > 0) {
	// candidates = tmpCandidates;
	// }
	// if (candidates.size() == 1) {
	// return candidates.iterator().next();
	// }
	//
	// // check for candidates that match the method name (iff not UNKNONW).
	// if (!exp.getSignature().getMethodName().equals("UNKNOWN")) {
	// tmpCandidates = new HashSet<MethodDefinition>();
	// defIter = candidates.iterator();
	// while (defIter.hasNext()) {
	// MethodDefinition def = defIter.next();
	// if (def.getSignature().getMethodName().equals(expSig.getMethodName())) {
	// tmpCandidates.add(def);
	// }
	// }
	// if (tmpCandidates.size() > 0) {
	// candidates = tmpCandidates;
	// }
	// }
	//
	// if (candidates.size() == 1) {
	// return candidates.iterator().next();
	// }
	//
	// if (includeDeletions) {
	// return this.findPrevSimDefDelByClassArgs(exp);
	// } else {
	// throw new NoSuchDefinitionException("Could not find any similar " + "previous method definition.", expSig);
	// }
	//
	// }
	
	// private MethodDefinition findPrevSimDefDelByClassArgs(MethodExpression exp) throws NoSuchDefinitionException {
	//
	// MethodSignature expSig = exp.getSignature();
	//
	// HashSet<MethodDefinitionDeletion> candidates = new HashSet<MethodDefinitionDeletion>();
	// HashSet<MethodDefinitionDeletion> tmpCandidates = new HashSet<MethodDefinitionDeletion>();
	//
	// // get all signatures with same class name and number of arguments
	// Iterator<MethodSignature> sigIter = this.methodDefinitionDeletions.keySet().iterator();
	// while (sigIter.hasNext()) {
	// MethodSignature signature = sigIter.next();
	// if (expSig.getArguments().size() == signature.getArguments().size()) {
	// if (this.methodDefinitionDeletions.get(signature).size() > 0) {
	// if (expSig.getClassName().equals(signature.getClassName())) {
	// tmpCandidates.add(this.methodDefinitionDeletions.get(signature).get(
	// this.methodDefinitionDeletions.size() - 1));
	// } else {
	// candidates.add(this.methodDefinitionDeletions.get(signature).get(
	// this.methodDefinitionDeletions.size() - 1));
	// }
	// }
	// }
	// }
	//
	// // if we found matching class name, we continue with the set of
	// // definitions
	// // mathing the class name
	// if (tmpCandidates.size() > 0) {
	// candidates = tmpCandidates;
	// } else if (candidates.size() < 1) {
	// throw new NoSuchDefinitionException("Could not find any similar " + "previous method definition.", expSig);
	// }
	// if (candidates.size() == 1) {
	// return candidates.iterator().next();
	// }
	//
	// // check for candidates that match exactly the argument types.
	// tmpCandidates = new HashSet<MethodDefinitionDeletion>();
	// Iterator<MethodDefinitionDeletion> defIter = candidates.iterator();
	// while (defIter.hasNext()) {
	// MethodDefinitionDeletion def = defIter.next();
	// if (expSig.getArguments().equals(def.getSignature().getArguments())) {
	// tmpCandidates.add(def);
	// }
	// }
	//
	// if (tmpCandidates.size() > 0) {
	// candidates = tmpCandidates;
	// }
	// if (candidates.size() == 1) {
	// return candidates.iterator().next();
	// }
	//
	// // check for candidates that match exactly the argument types.
	//
	// if (!exp.getSignature().getMethodName().equals("UNKNOWN")) {
	// tmpCandidates = new HashSet<MethodDefinitionDeletion>();
	// defIter = candidates.iterator();
	// while (defIter.hasNext()) {
	// MethodDefinitionDeletion def = defIter.next();
	// if (def.getSignature().getMethodName().equals(expSig.getMethodName())) {
	// tmpCandidates.add(def);
	// }
	// }
	// if (tmpCandidates.size() > 0) {
	// candidates = tmpCandidates;
	// }
	// }
	//
	// if (candidates.size() == 1) {
	// return candidates.iterator().next();
	// }
	//
	// throw new NoSuchDefinitionException("Could not find any similar " + "previous method definition.", expSig);
	// }
	
	// public MethodDefinition findPrevSimDefDelByMethArgs(MethodExpression exp) throws NoSuchDefinitionException {
	//
	// MethodDefinition candidate = null;
	//
	// for (MethodSignature signature : this.methodDefinitions.keySet()) {
	// if ((signature.getMethodName().equals(exp.getSignature().getMethodName()))
	// && (signature.getArguments().equals(exp.getSignature().getArguments()))) {
	// if (candidate != null) {
	// throw new NoSuchDefinitionException(
	// "Could not find similar method definition by comparing method name and args. Too many options.",
	// exp.getSignature());
	// }
	// candidate = this.methodDefinitions.get(signature);
	//
	// }
	// }
	// if (candidate != null) {
	// return candidate;
	// }
	// throw new NoSuchDefinitionException(
	// "Could not find similar method definition by comparing method name and args!", exp.getSignature());
	// }
	
	/**
	 * Removes the method definition that matches the signature of the given method definition and adds the method
	 * definition deletion event.
	 * 
	 * @param del
	 *            The method definition deletion event specifying the method definition to be deleted
	 * @return The removed method definition iff found
	 */
	public JavaChangeOperation removeDefiniton(final JavaChangeOperation del) {
		Condition.check(del.getChangedElementLocation().getElement() instanceof JavaMethodDefinition,
		                "You try to remove a java method definition that is no definition.");
		final String fullName = del.getChangedElementLocation().getElement().getFullQualifiedName();
		if (!this.methodDefinitions.containsKey(fullName)) {
			return null;
		}
		final JavaChangeOperation def = this.methodDefinitions.get(fullName);
		this.methodDefinitions.remove(fullName);
		if (!this.methodDefinitionDeletions.containsKey(fullName)) {
			this.methodDefinitionDeletions.put(fullName, new ArrayList<JavaChangeOperation>());
		}
		this.methodDefinitionDeletions.get(fullName).add(del);
		if (def.getRevision().getChangeSet().getTimestamp()
		       .isAfter(del.getRevision().getChangeSet().getTimestamp())) {
			if (Logger.logError()) {
				Logger.error("Found method definition that was added after the deletion! Should not happen!");
			}
			return null;
		}
		return def;
	}
	
	/**
	 * Removes the method invocation that matches the signature of the given method definition and that is the closest
	 * to the line of the deletion event. Additionally, the deletion even it registered. (If no perfect match can be
	 * found, use possible old variable types to find match)
	 * 
	 * @param del
	 *            The method invocation deletion event specifying the method invocation to be deleted
	 * @return The removed method invocation iff found
	 */
	public JavaChangeOperation removeMethodCall(final JavaChangeOperation del) {
		Condition.check(del.getChangedElementLocation().getElement() instanceof JavaMethodCall,
		                "You try to remove a java method call that is no call.");
		
		final JavaElementLocation location = del.getChangedElementLocation();
		final JavaMethodCall element = (JavaMethodCall) location.getElement();
		final String fullName = element.getFullQualifiedName();
		
		final String callingPosition = location.getFilePath();
		
		final JavaChangeOperation toDelete = findPreviousInvocation(element, location, false);
		
		if (toDelete == null) {
			return null;
		}
		
		// if (del.isBefore(toDelete)) {
		// if (Logger.logWarn()) {
		// Logger.warn("Fatal error occured. Trying to delete method call that was added by later operation than the current operation: current operation="
		// + del + ", previous definition=" + toDelete);
		// }
		// return null;
		// }
		
		if (!this.methodInvocations.containsKey(callingPosition)) {
			return null;
		}
		if (!this.methodInvocations.get(callingPosition).containsKey(fullName)) {
			return null;
		}
		this.methodInvocations.get(callingPosition).get(fullName).remove(toDelete);
		if (!this.methodInvocationDeletions.containsKey(callingPosition)) {
			this.methodInvocationDeletions.put(callingPosition, new HashMap<String, ArrayList<JavaChangeOperation>>());
		}
		final HashMap<String, ArrayList<JavaChangeOperation>> deletions = this.methodInvocationDeletions.get(callingPosition);
		if (!deletions.containsKey(fullName)) {
			deletions.put(fullName, new ArrayList<JavaChangeOperation>());
		}
		deletions.get(fullName).add(del);
		
		return toDelete;
	}
	
	/**
	 * Reset.
	 */
	public void reset() {
		this.methodDefinitions.clear();
		this.methodDefinitionDeletions.clear();
		this.methodInvocations.clear();
		this.methodInvocationDeletions.clear();
	}
	
}
