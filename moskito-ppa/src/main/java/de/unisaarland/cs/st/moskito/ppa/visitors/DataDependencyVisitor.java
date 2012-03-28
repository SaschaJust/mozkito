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
package de.unisaarland.cs.st.moskito.ppa.visitors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.SimpleName;

public class DataDependencyVisitor extends ASTVisitor {
	
	private final Map<Integer, Set<Integer>> lineFields    = new HashMap<Integer, Set<Integer>>();
	private final Map<Integer, Set<Integer>> lineVariables = new HashMap<Integer, Set<Integer>>();
	private final CompilationUnit            cu;
	
	public DataDependencyVisitor(final CompilationUnit cu) {
		this.cu = cu;
	}
	
	@Override
	public void endVisit(final SimpleName sn) {
		final IBinding binding = sn.resolveBinding();
		if (binding == null) {
			return;
		}
		if (binding.getKind() == IBinding.VARIABLE) {
			final IVariableBinding vBinding = (IVariableBinding) binding;
			if (vBinding.isField()) {
				// FIELDS and LOCAL VARIABLES
				final int currentLine = this.cu.getLineNumber(sn.getStartPosition());
				if (!this.lineFields.containsKey(vBinding.getVariableId())) {
					this.lineFields.put(vBinding.getVariableId(), new HashSet<Integer>());
				}
				this.lineFields.get(vBinding.getVariableId()).add(currentLine);
			} else if (!vBinding.isParameter()) {
				final int currentLine = this.cu.getLineNumber(sn.getStartPosition());
				if (!this.lineVariables.containsKey(vBinding.getVariableId())) {
					this.lineVariables.put(vBinding.getVariableId(), new HashSet<Integer>());
				}
				this.lineVariables.get(vBinding.getVariableId()).add(currentLine);
			}
		}
	}
	
	public Map<Integer, Set<Integer>> getFieldAccessesPerLine() {
		return this.lineFields;
	}
	
	public Map<Integer, Set<Integer>> getVariableAccessesPerLine() {
		return this.lineVariables;
	}
	
}
