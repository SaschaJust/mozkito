/*******************************************************************************
 * Copyright (c) 2011 Kim Herzig, Sascha Just.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Kim Herzig, Sascha Just - initial API and implementation
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.ppa.visitors;

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
		if (binding == null) {
			return;
		}
		if (binding.getKind() == IBinding.VARIABLE) {
			IVariableBinding vBinding = (IVariableBinding) binding;
			if (!vBinding.isParameter()) {
				// FIELDS and LOCAL VARIABLES
				int currentLine = cu.getLineNumber(sn.getStartPosition());
				if (!lineVariables.containsKey(vBinding.getVariableId())) {
					lineVariables.put(vBinding.getVariableId(), new HashSet<Integer>());
				}
				lineVariables.get(vBinding.getVariableId()).add(currentLine);
			}
		}
	}
	
	public Map<Integer, Set<Integer>> getVariableAccessesPerLine(){
		return lineVariables;
	}
	
}
