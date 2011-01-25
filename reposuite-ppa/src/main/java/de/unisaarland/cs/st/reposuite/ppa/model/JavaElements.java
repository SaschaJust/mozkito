package de.unisaarland.cs.st.reposuite.ppa.model;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


public class JavaElements {
	
	List<JavaMethodDefinition> methodDefs = new LinkedList<JavaMethodDefinition>();
	
	List<JavaClassDefinition>  classDefs   = new LinkedList<JavaClassDefinition>();
	
	List<JavaMethodCall>       methodCalls = new LinkedList<JavaMethodCall>();
	
	public JavaElements() {
		
	}
	
	public boolean add(final JavaClassDefinition classDef) {
		return classDefs.add(classDef);
	}
	
	public boolean add(final JavaMethodCall methodCall) {
		return methodCalls.add(methodCall);
	}
	
	public boolean add(final JavaMethodDefinition methodDef) {
		return methodDefs.add(methodDef);
	}
	
	public boolean addAllClassDefs(final Collection<JavaClassDefinition> classDef) {
		return classDefs.addAll(classDef);
	}
	
	public boolean addAllMethodCalls(final Collection<JavaMethodCall> methodCall) {
		return methodCalls.addAll(methodCall);
	}
	
	public boolean addAllMethodDefs(final Collection<JavaMethodDefinition> methodDef) {
		return methodDefs.addAll(methodDef);
	}
	
	public Collection<JavaClassDefinition> getClassDefs() {
		return classDefs;
	}
	
	public Collection<JavaElementDefinition> getJavaDefs(){
		List<JavaElementDefinition> defs = new LinkedList<JavaElementDefinition>();
		defs.addAll(classDefs);
		defs.addAll(methodDefs);
		return defs;
	}
	
	public Collection<JavaMethodCall> getMethodCalls() {
		return methodCalls;
	}
	
	public Collection<JavaMethodDefinition> getMethodDefs() {
		return methodDefs;
	}

}
