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
package org.eclipse.jdt.core.dom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import de.unisaarland.cs.st.reposuite.ppa.PPAIndex;
import de.unisaarland.cs.st.reposuite.ppa.PPAIndexer;
import de.unisaarland.cs.st.reposuite.ppa.PPAOptions;
import de.unisaarland.cs.st.reposuite.ppa.TypeFact;
import de.unisaarland.cs.st.reposuite.ppa.TypeFactMerger;
import de.unisaarland.cs.st.reposuite.ppa.utils.PPALoggerUtil;

public class PPAEngine {
	
	// private final static PPAEngine instance = new PPAEngine();
	
	private final List<TypeFact> worklist = new LinkedList<TypeFact>();
	
	private final Map<PPAIndex, List<ASTNode>> unsafeNodes = new HashMap<PPAIndex, List<ASTNode>>();
	
	private final PPAIndexer indexer;
	
	private final TypeFactMerger merger = new TypeFactMerger();
	
	private final Set<ASTNode> ambiguousNodes = new HashSet<ASTNode>();
	
	private final List<ASTNode> unitsToProcess = new ArrayList<ASTNode>();
	
	private final PPATypeRegistry typeRegistry;
	
	private boolean allowMemberInference = true;
	
	private boolean allowCollectiveMode = false;
	
	private boolean allowTypeInferenceMode = true;
	
	private boolean allowMethodBindingMode = true;
	
	private int maxMISize = -1;
	
	private final int MAX_TURN = 1000000;
	
	private TypeFact currentFact;
	
	private boolean isInMethodBindingPass = false;
	
	private final Logger logger = PPALoggerUtil.getLogger(PPAEngine.class);
	
	public PPAEngine(final PPATypeRegistry typeRegistry, final PPAOptions options) {
		this.indexer = new PPAIndexer(this);
		this.typeRegistry = typeRegistry;
		this.setAllowMemberInference(options.isAllowMemberInference());
		this.setAllowCollectiveMode(options.isAllowCollectiveMode());
		this.setAllowTypeInferenceMode(options.isAllowTypeInferenceMode());
		this.setAllowMethodBindingMode(options.isAllowMethodBindingMode());
		this.setMaxMISize(options.getMaxMISize());
	}
	
	// public static PPAEngine getInstance() {
	// return instance;
	// }
	
	public void addAmbiguousNodes(final Set<ASTNode> nodes) {
		this.ambiguousNodes.addAll(nodes);
	}
	
	public void addUnitToProcess(final ASTNode node) {
		this.unitsToProcess.add(node);
	}
	
	public boolean allowCollectiveMode() {
		return this.allowCollectiveMode;
	}
	
	public boolean allowMemberInference() {
		return this.allowMemberInference;
	}
	
	public boolean allowMethodBindingMode() {
		return this.allowMethodBindingMode;
	}
	
	public boolean allowTypeInferenceMode() {
		return this.allowTypeInferenceMode;
	}
	
	public void doPPA() {
		if (this.allowCollectiveMode) {
			seedPass();
			if (this.allowTypeInferenceMode) {
				typeInferencePass();
				if (this.allowMethodBindingMode) {
					methodBindingPass();
				}
			}
		} else {
			for (ASTNode node : this.unitsToProcess) {
				seedPass(node);
				if (this.allowTypeInferenceMode) {
					typeInferencePass();
					if (this.allowMethodBindingMode) {
						methodBindingPass();
					}
				}
				reset();
			}
		}
	}
	
	private List<MethodInvocation> getAmbiguousMethodInvocations() {
		List<MethodInvocation> mis = new ArrayList<MethodInvocation>();
		for (ASTNode node : this.ambiguousNodes) {
			if (node instanceof SimpleName) {
				SimpleName sName = (SimpleName) node;
				ASTNode parent = sName.getParent();
				if (parent instanceof MethodInvocation) {
					MethodInvocation mi = (MethodInvocation) parent;
					if (sName == mi.getName()) {
						mis.add(mi);
					}
				}
			}
		}
		return mis;
	}
	
	public Set<ASTNode> getAmbiguousNodes() {
		return Collections.unmodifiableSet(this.ambiguousNodes);
	}
	
	public int getMaxMISize() {
		return this.maxMISize;
	}
	
	public PPATypeRegistry getRegistry() {
		return this.typeRegistry;
	}
	
	public List<ASTNode> getUnitsToProcess() {
		return this.unitsToProcess;
	}
	
	// For debugging purpose only
	public List<TypeFact> getWorklist() {
		return this.worklist;
	}
	
	public boolean isInMethodBindingPass() {
		return this.isInMethodBindingPass;
	}
	
	private boolean isSecondaryIndex(final PPAIndex index, final List<PPAIndex> secondaryIndexes) {
		boolean found = false;
		
		for (PPAIndex tempIndex : secondaryIndexes) {
			if (tempIndex.equals(index)) {
				found = true;
				break;
			}
		}
		
		return found;
	}
	
	public void methodBindingPass() {
		this.isInMethodBindingPass = true;
		List<MethodInvocation> mis = getAmbiguousMethodInvocations();
		int size = mis.size();
		if ((this.maxMISize == -1) || (mis.size() < this.maxMISize)) {
			for (MethodInvocation mi : mis) {
				IMethodBinding currentBinding = mi.resolveMethodBinding();
				PPABindingsUtil.fixMethod(mi.getName(), currentBinding.getReturnType(), this.typeRegistry,
						(PPADefaultBindingResolver) mi.getName().ast.getBindingResolver(), this.indexer, this,
						false, true);
				recomputeIndexes(this.unsafeNodes.get(this.indexer.getMainIndex(mi)));
			}
		} else {
			this.logger.info("Skipping method binding pass because too many ambiguous methods: " + size);
		}
		
		processWorklist();
		this.isInMethodBindingPass = false;
	}
	
	public void processWorklist() {
		int turn = 0;
		try {
			while (!this.worklist.isEmpty()) {
				turn++;
				
				if (turn > this.MAX_TURN) {
					assert false;
				}
				
				List<ASTNode> visitInSecond = new ArrayList<ASTNode>();
				this.currentFact = this.worklist.get(0);
				this.worklist.remove(0);
				
				// For debugging purpose.
				// System.out.println(currentFact);
				
				PPAIndex index = this.currentFact.getIndex();
				
				if (this.unsafeNodes.containsKey(index)) {
					for (ASTNode unsafeNode : this.unsafeNodes.get(index)) {
						PPAIndex mainIndex = this.indexer.getMainIndex(unsafeNode);
						List<PPAIndex> secondaryIndexes = this.indexer.getSecondaryIndexes(unsafeNode);
						if (mainIndex.equals(index)) {
							this.indexer.makeSafe(unsafeNode, this.currentFact);
						}
						
						// This is in case the type refer both to a main index AND a secondary
						// index.
						// For example, infinite fields a.a.a.a
						if (isSecondaryIndex(index, secondaryIndexes)) {
							visitInSecond.add(unsafeNode);
						}
					}
				} else {
					// We will try to avoid that case...
					assert false;
				}
				
				for (ASTNode unsafeNode : visitInSecond) {
					// This is to enforce the fact that secondary indexes should NEVER use the new
					// type in the type fact, but should rather seek the actual fact in the primary
					// indexes.
					TypeFact tFact = (TypeFact) this.currentFact.clone();
					tFact.setNewType(null);
					this.indexer.makeSafeSecondary(unsafeNode, tFact);
				}
				
				recomputeIndexes(this.unsafeNodes.get(index));
			}
			
		} catch (Exception e) {
			this.logger.error("Error in PPAEngine", e);
			assert false;
		}
	}
	
	private void putUnsafeNode(final PPAIndex index, final ASTNode node) {
		List<ASTNode> nodes = this.unsafeNodes.get(index);
		if (nodes == null) {
			nodes = new ArrayList<ASTNode>();
			this.unsafeNodes.put(index, nodes);
		}
		
		if (!nodes.contains(node)) {
			nodes.add(node);
		}
		
	}
	
	public void recomputeIndexes(final List<ASTNode> nodes) {
		if (nodes == null) {
			return;
		}
		for (ASTNode node : nodes) {
			reportUnsafe(node);
		}
	}
	
	public boolean reportTypeFact(final TypeFact typeFact) {
		assert typeFact.getNewType() != null;
		
		if (typeFact.getIndex() == null) {
			// This is to prevent future problem.
			return false;
		} else if (!this.merger.isValuableTypeFact(typeFact) || this.merger.similarTypeFacts(typeFact, this.currentFact)) {
			return false;
		}
		
		TypeFact similarFact = this.merger.findTypeFact(typeFact, this.worklist);
		if (similarFact == null) {
			this.worklist.add(typeFact);
		} else {
			this.worklist.remove(similarFact);
			TypeFact newFact = this.merger.merge(similarFact, typeFact);
			assert newFact.getNewType() != null;
			this.worklist.add(newFact);
		}
		
		return true;
	}
	
	public void reportUnsafe(final ASTNode node) {
		for (PPAIndex index : this.indexer.getAllIndexes(node)) {
			putUnsafeNode(index, node);
		}
	}
	
	public void reset() {
		this.worklist.clear();
		this.unsafeNodes.clear();
		this.ambiguousNodes.clear();
		this.typeRegistry.clear();
	}
	
	public void seedPass() {
		for (ASTNode node : this.unitsToProcess) {
			seedPass(node);
		}
	}
	
	public void seedPass(final ASTNode node) {
		if (node.ast.getBindingResolver() instanceof DefaultBindingResolver) {
			PPADefaultBindingResolver resolver = new PPADefaultBindingResolver(
					(DefaultBindingResolver) node.ast.getBindingResolver(), this.typeRegistry);
			if (node instanceof CompilationUnit) {
				resolver.setCurrentCu((CompilationUnit) node);
			}
			node.ast.setBindingResolver(resolver);
			
			// Syntax Disambiguation
			SyntaxDisambiguation sDisambiguation = new SyntaxDisambiguation(resolver);
			node.accept(sDisambiguation);
			sDisambiguation.postProcess();
			addAmbiguousNodes(sDisambiguation.getAmbiguousNodes());
			
			if (this.allowMemberInference) {
				MemberInferencer mInferencer = new MemberInferencer(this.indexer, resolver, this);
				mInferencer.processMembers();
			}
			
			// Initial inference
			SeedVisitor sVisitor = new SeedVisitor(this.indexer, this);
			node.accept(sVisitor);
		}
	}
	
	public void setAllowCollectiveMode(final boolean allowCollectiveMode) {
		this.allowCollectiveMode = allowCollectiveMode;
	}
	
	public void setAllowMemberInference(final boolean allowMemberInference) {
		this.allowMemberInference = allowMemberInference;
	}
	
	public void setAllowMethodBindingMode(final boolean allowMethodBindingMode) {
		this.allowMethodBindingMode = allowMethodBindingMode;
	}
	
	public void setAllowTypeInferenceMode(final boolean allowTypeInferenceMode) {
		this.allowTypeInferenceMode = allowTypeInferenceMode;
	}
	
	public void setMaxMISize(final int maxMISize) {
		this.maxMISize = maxMISize;
	}
	
	public void typeInferencePass() {
		processWorklist();
	}
	
}
