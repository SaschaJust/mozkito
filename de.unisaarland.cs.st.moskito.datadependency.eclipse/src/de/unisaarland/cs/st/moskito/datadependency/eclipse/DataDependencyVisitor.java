/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
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


package de.unisaarland.cs.st.moskito.datadependency.eclipse;

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
	
	private final Map<Integer, Set<Integer>> lineVariables = new HashMap<Integer, Set<Integer>>();
	private final CompilationUnit            cu;
	
	public DataDependencyVisitor(final CompilationUnit cu) {
		this.cu = cu;
	}
	
	@Override
	public void endVisit(final SimpleName sn) {
		IBinding binding = sn.resolveBinding();
		if (binding.getKind() == IBinding.VARIABLE) {
			IVariableBinding vBinding = (IVariableBinding) binding;
			if (!vBinding.isParameter()) {
				// FIELDS and LOCAL VARIABLES
				int currentLine = cu.getLineNumber(sn.getStartPosition());
				if (!lineVariables.containsKey(currentLine)) {
					lineVariables.put(currentLine, new HashSet<Integer>());
				}
				lineVariables.get(currentLine).add(vBinding.getVariableId());
				System.err.println("add");
			}
		}
	}
	
	public Map<Integer, Set<Integer>> getVariableAccessesPerLine(){
		return lineVariables;
	}
	
}
