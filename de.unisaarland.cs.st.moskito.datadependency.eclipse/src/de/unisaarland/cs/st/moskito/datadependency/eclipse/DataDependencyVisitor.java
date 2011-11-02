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
