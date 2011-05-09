package de.unisaarland.cs.st.reposuite.ppa.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.persistence.Criteria;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;

public class JavaElementFactory {
	
	/** The class definitions by name. */
	public static Map<String, JavaClassDefinition>  classDefs   = new HashMap<String, JavaClassDefinition>();
	
	/** The method definitions by name. */
	public static Map<String, JavaMethodDefinition> methodDefs  = new HashMap<String, JavaMethodDefinition>();
	
	/** The method calls by name. */
	public static Map<String, JavaMethodCall>       methodCalls = new HashMap<String, JavaMethodCall>();
	
	private static boolean                          isInit      = false;
	
	/**
	 * Gets the class definition.
	 * 
	 * @param fullQualifiedName
	 *            the full qualified name
	 * @param file
	 *            the file
	 * @param parent
	 *            the parent
	 * @param startLine
	 *            the start line
	 * @param endLine
	 *            the end line
	 * @param position
	 *            the position
	 * @param bodyStartLine
	 *            the body start line
	 * @param packageName
	 *            the package name
	 * @return the class definition
	 */
	public static JavaClassDefinition getAnonymousClassDefinition(@NotNull final JavaClassDefinition parent,
	                                                              @NotNull final String fullQualifiedName) {
		
		Condition.check(isInit, "You must call init() before accessing the factory!");
		
		JavaClassDefinition def = null;
		if (!classDefs.containsKey(fullQualifiedName)) {
			def = new JavaClassDefinition(parent, fullQualifiedName);
			classDefs.put(fullQualifiedName, def);
		} else {
			def = classDefs.get(fullQualifiedName);
		}
		return def;
	}
	
	/**
	 * Gets the class definition.
	 * 
	 * @param fullQualifiedName
	 *            the full qualified name
	 * @param file
	 *            the file
	 * @param parent
	 *            the parent
	 * @param startLine
	 *            the start line
	 * @param endLine
	 *            the end line
	 * @param position
	 *            the position
	 * @param bodyStartLine
	 *            the body start line
	 * @param packageName
	 *            the package name
	 * @return the class definition
	 */
	public static JavaClassDefinition getClassDefinition(@NotNull final String fullQualifiedName,
	                                                     @NotNull final String file) {
		
		Condition.check(isInit, "You must call init() before accessing the factory!");
		
		JavaClassDefinition def = null;
		if (!classDefs.containsKey(fullQualifiedName)) {
			def = new JavaClassDefinition(fullQualifiedName);
			classDefs.put(fullQualifiedName, def);
		} else {
			def = classDefs.get(fullQualifiedName);
		}
		return def;
	}
	
	/**
	 * Gets the method call.
	 * 
	 * @param fullQualifiedName
	 *            the full qualified name
	 * @param signature
	 *            the signature
	 * @param file
	 *            the file
	 * @param parent
	 *            the parent
	 * @param startLine
	 *            the start line
	 * @param endLine
	 *            the end line
	 * @param position
	 *            the position
	 * @return the method call
	 */
	public static JavaMethodCall getMethodCall(@NotNull final String objectName,
	                                           @NotNull final String methodName,
	                                           @NotNull final List<String> signature,
	                                           @NotNull final JavaElement parent) {
		
		Condition.check(isInit, "You must call init() before accessing the factory!");
		
		boolean parentPass = ((parent instanceof JavaClassDefinition) || (parent instanceof JavaMethodDefinition));
		Condition.check(parentPass,
		                "The parent of a JavaMethodCall has to be a JavaMethodDefinition or a JavaClassDefinition");
		if (!parentPass) {
			if (Logger.logError()) {
				Logger.error("The parent of a JavaMethodCall has to be a JavaMethodDefinition or a JavaClassDefinition");
			}
		}
		
		String cacheName = JavaMethodCall.composeFullQualifiedName(objectName, methodName, signature);
		JavaMethodCall call = null;
		if (!methodCalls.containsKey(cacheName)) {
			call = new JavaMethodCall(objectName, methodName, signature);
			methodCalls.put(cacheName, call);
		} else {
			call = methodCalls.get(cacheName);
		}
		return call;
	}
	
	/**
	 * Gets the method definition.
	 * 
	 * @param fullQualifiedName
	 *            the full qualified name
	 * @param signature
	 *            the signature
	 * @param file
	 *            the file
	 * @param parent
	 *            the parent
	 * @param startLine
	 *            the start line
	 * @param endLine
	 *            the end line
	 * @param position
	 *            the position
	 * @param bodyStartLine
	 *            the body start line
	 * @return the method definition
	 */
	public static JavaMethodDefinition getMethodDefinition(@NotNull final String objectName,
	                                                       @NotNull final String methodName,
	                                                       @NotNull final List<String> signature) {
		
		Condition.check(isInit, "You must call init() before accessing the factory!");
		
		JavaMethodDefinition def = null;
		String cacheName = JavaMethodCall.composeFullQualifiedName(objectName, methodName, signature);
		if (!methodDefs.containsKey(cacheName)) {
			
			def = new JavaMethodDefinition(objectName, methodName, signature);
			methodDefs.put(cacheName, def);
		} else {
			def = methodDefs.get(cacheName);
		}
		return def;
	}
	
	@SuppressWarnings ({ "rawtypes", "unchecked" })
	public static void init(final PersistenceUtil persistenceUtil) {
		if (isInit) {
			return;
		}
		Criteria criteria = persistenceUtil.createCriteria(JavaClassDefinition.class);
		List<JavaClassDefinition> defs = persistenceUtil.load(criteria);
		for (JavaClassDefinition def : defs) {
			classDefs.put(def.getFullQualifiedName(), def);
		}
		criteria = persistenceUtil.createCriteria(JavaMethodDefinition.class);
		List<JavaMethodDefinition> mDefs = persistenceUtil.load(criteria);
		for (JavaMethodDefinition def : mDefs) {
			methodDefs.put(def.getFullQualifiedName(), def);
		}
		criteria = persistenceUtil.createCriteria(JavaMethodCall.class);
		List<JavaMethodCall> calls = persistenceUtil.load(criteria);
		for (JavaMethodCall call : calls) {
			methodCalls.put(call.getFullQualifiedName(), call);
		}
		isInit = true;
	}
}
