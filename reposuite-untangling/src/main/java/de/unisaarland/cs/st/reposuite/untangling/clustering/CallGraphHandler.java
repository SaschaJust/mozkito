package de.unisaarland.cs.st.reposuite.untangling.clustering;

import de.unisaarland.cs.st.reposuite.clustering.MultilevelClusteringScoreVisitor;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaClassDefinition;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaElement;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaMethodCall;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaMethodDefinition;

public class CallGraphHandler implements MultilevelClusteringScoreVisitor<JavaChangeOperation> {
	
	@Override
	public double getScore(JavaChangeOperation op1,
	                       JavaChangeOperation op2,
	                       double oldScore) {
		
		JavaElement e1 = op1.getChangedElementLocation().getElement();
		JavaElement e2 = op2.getChangedElementLocation().getElement();
		
		if (e1 instanceof JavaClassDefinition) {
			if (e2 instanceof JavaClassDefinition) {
				return oldScore += handleClassOnClass((JavaClassDefinition) e1, (JavaClassDefinition) e2);
			}else if (e2 instanceof JavaMethodDefinition) {
				return oldScore += handleMethodOnClass((JavaMethodDefinition) e2, (JavaClassDefinition) e1);
			} else if (e2 instanceof JavaMethodCall) {
				return oldScore += handleCallOnClass((JavaMethodCall) e2, (JavaClassDefinition) e1);
			}
		} else if (e1 instanceof JavaMethodDefinition) {
			if (e2 instanceof JavaClassDefinition) {
				return oldScore += handleMethodOnClass((JavaMethodDefinition) e1, (JavaClassDefinition) e2);
			} else if (e2 instanceof JavaMethodDefinition) {
				return oldScore += handleMethodOnMethod((JavaMethodDefinition) e1, (JavaMethodDefinition) e2);
			} else if (e2 instanceof JavaMethodCall) {
				return oldScore += handleCallOnMethod((JavaMethodCall) e2, (JavaMethodDefinition) e1);
			}
		} else if (e1 instanceof JavaMethodCall) {
			if (e2 instanceof JavaClassDefinition) {
				return oldScore += handleCallOnClass((JavaMethodCall) e1, (JavaClassDefinition) e2);
			} else if (e2 instanceof JavaMethodDefinition) {
				return oldScore += handleCallOnMethod((JavaMethodCall) e1, (JavaMethodDefinition) e2);
			} else if (e2 instanceof JavaMethodCall) {
				return oldScore += handleCallOnCall((JavaMethodCall) e1, (JavaMethodCall) e2);
			}
		}
		return oldScore;
	}
	
	private double handleCallOnCall(JavaMethodCall call1,
	                                JavaMethodCall call2) {
		return 0d;
	}
	
	private double handleCallOnClass(JavaMethodCall call,
	                                 JavaClassDefinition clazz) {
		return 0d;
	}
	
	private double handleCallOnMethod(JavaMethodCall call,
	                                  JavaMethodDefinition method) {
		return 0d;
	}
	
	private double handleClassOnClass(JavaClassDefinition class1,
	                                  JavaClassDefinition class2) {
		// if class1 contains method call on class2: +0.5
		
		// if class2 contains method call on class1: +0.5
		return 0d;
	}
	
	private double handleMethodOnClass(JavaMethodDefinition method,
	                                   JavaClassDefinition clazz) {
		return 0d;
	}
	
	private double handleMethodOnMethod(JavaMethodDefinition method1,
	                                    JavaMethodDefinition method2) {
		return 0d;
	}
	
	@Override
	public double getMaxPossibleScore() {
		return 1;
	}
}
