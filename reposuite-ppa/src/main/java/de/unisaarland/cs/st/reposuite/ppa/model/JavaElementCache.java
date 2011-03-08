package de.unisaarland.cs.st.reposuite.ppa.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.unisaarland.cs.st.reposuite.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.persistence.HibernateUtil;
import de.unisaarland.cs.st.reposuite.persistence.PPAHibernateUtil;
import de.unisaarland.cs.st.reposuite.ppa.utils.JavaElementLocations;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.specification.NonNegative;
import de.unisaarland.cs.st.reposuite.utils.specification.NotNull;

/**
 * The Class JavaElementDefinitionCache.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class JavaElementCache {
	
	/** The class definitions by name. */
	private static Map<String, JavaClassDefinition>  classDefs   = new HashMap<String, JavaClassDefinition>();
	
	/** The method definitions by name. */
	private static Map<String, JavaMethodDefinition> methodDefs  = new HashMap<String, JavaMethodDefinition>();
	
	/** The method calls by name. */
	private static Map<String, JavaMethodCall>       methodCalls = new HashMap<String, JavaMethodCall>();
	
	/**
	 * Reset.
	 */
	public static void reset() {
		if (Logger.logDebug()) {
			Logger.debug("Resetting the JavaElementDefinitionCache!");
		}
		classDefs.clear();
		methodDefs.clear();
		methodCalls.clear();
	}
	
	private final HibernateUtil                                  hibernateUtil;
	
	/** The class def locations. */
	private final Set<JavaElementLocation<JavaClassDefinition>>  classDefLocations   = new HashSet<JavaElementLocation<JavaClassDefinition>>();
	
	/** The method def locations. */
	private final Set<JavaElementLocation<JavaMethodDefinition>> methodDefLocations  = new HashSet<JavaElementLocation<JavaMethodDefinition>>();
	
	/** The method call locations. */
	private final Set<JavaElementLocation<JavaMethodCall>>       methodCallLocations = new HashSet<JavaElementLocation<JavaMethodCall>>();
	
	/**
	 * Instantiates a new java element cache.
	 */
	public JavaElementCache() {
		try {
			this.hibernateUtil = HibernateUtil.getInstance(false);
		} catch (UninitializedDatabaseException e) {
			throw new UnrecoverableError(e.getMessage(), e);
		}
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
	 * @param packageName
	 *            the package name
	 * @return the class definition
	 */
	public JavaElementLocation<JavaClassDefinition> getClassDefinition(@NotNull final String fullQualifiedName,
			@NotNull final String file, final JavaClassDefinition parent, @NonNegative final int startLine,
			@NonNegative final int endLine, @NonNegative final int position, @NonNegative final int bodyStartLine,
			@NotNull final String packageName) {
		
		JavaClassDefinition def = null;
		if (!classDefs.containsKey(fullQualifiedName)) {
			def = new JavaClassDefinition(fullQualifiedName, parent, packageName);
			JavaClassDefinition cachedDef = (JavaClassDefinition) PPAHibernateUtil.getSessionJavaElement(
					this.hibernateUtil, def);
			if (cachedDef != null) {
				def = cachedDef;
			} else {
				cachedDef = (JavaClassDefinition) PPAHibernateUtil.getJavaElement(this.hibernateUtil, def);
				if (cachedDef != null) {
					def = cachedDef;
				}
			}
			
			if (classDefs.size() > 10000) {
				classDefs.clear();
			}
			
			classDefs.put(fullQualifiedName, def);
		} else {
			def = classDefs.get(fullQualifiedName);
		}
		JavaElementLocation<JavaClassDefinition> result = new JavaElementLocation<JavaClassDefinition>(def, startLine,
				endLine, position, bodyStartLine, file);
		this.classDefLocations.add(result);
		
		return result;
	}
	
	/**
	 * Gets the java element locations.
	 * 
	 * @return the java element locations
	 */
	public JavaElementLocations getJavaElementLocations() {
		JavaElementLocations result = new JavaElementLocations();
		result.addAllClassDefs(this.classDefLocations);
		result.addAllMethodCalls(this.methodCallLocations);
		result.addAllMethodDefs(this.methodDefLocations);
		return result;
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
	public JavaElementLocation<JavaMethodCall> getMethodCall(@NotNull final String fullQualifiedName,
			@NotNull final List<String> signature, @NotNull final String file,
			@NotNull final JavaElementDefinition parent, @NonNegative final int startLine,
			@NonNegative final int endLine, @NonNegative final int position) {
		
		String cacheName = JavaMethodCall.composeFullQualifiedName(fullQualifiedName, signature);
		JavaMethodCall call = null;
		if (!methodCalls.containsKey(cacheName)) {
			call = new JavaMethodCall(fullQualifiedName, signature);
			JavaMethodCall cachedCall = (JavaMethodCall) PPAHibernateUtil.getSessionJavaElement(this.hibernateUtil,
					call);
			if (cachedCall != null) {
				call = cachedCall;
			} else {
				cachedCall = (JavaMethodCall) PPAHibernateUtil.getJavaElement(this.hibernateUtil, call);
				if (cachedCall != null) {
					call = cachedCall;
				}
			}
			
			if (methodCalls.size() > 10000) {
				methodCalls.clear();
			}
			
			methodCalls.put(cacheName, call);
		} else {
			call = methodCalls.get(cacheName);
		}
		JavaElementLocation<JavaMethodCall> methodCall = new JavaElementLocation<JavaMethodCall>(call, startLine,
				endLine, position, -1, file);
		this.methodCallLocations.add(methodCall);
		return methodCall;
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
	 * @return the method definition
	 */
	public JavaElementLocation<JavaMethodDefinition> getMethodDefinition(@NotNull final String fullQualifiedName,
			@NotNull final List<String> signature, @NotNull final String file,
			@NotNull final JavaClassDefinition parent, @NonNegative final int startLine,
			@NonNegative final int endLine, @NonNegative final int position, final int bodyStartLine) {
		
		String cacheName = JavaMethodDefinition.composeFullQualifiedName(parent, fullQualifiedName, signature);
		JavaMethodDefinition def = null;
		if (!methodDefs.containsKey(cacheName)) {
			def = new JavaMethodDefinition(fullQualifiedName, signature, parent);
			JavaMethodDefinition cachedDef = (JavaMethodDefinition) PPAHibernateUtil.getSessionJavaElement(
			        this.hibernateUtil, def);
			if (cachedDef != null) {
				def = cachedDef;
			} else {
				cachedDef = (JavaMethodDefinition) PPAHibernateUtil.getJavaElement(this.hibernateUtil, def);
				if (cachedDef != null) {
					def = cachedDef;
				}
			}
			
			if (methodDefs.size() > 10000) {
				methodDefs.clear();
			}
			
			methodDefs.put(cacheName, def);
		} else {
			def = methodDefs.get(cacheName);
		}
		JavaElementLocation<JavaMethodDefinition> result = new JavaElementLocation<JavaMethodDefinition>(def,
				startLine, endLine, position, bodyStartLine, file);
		
		if (result.getElement().getParent() != parent) {
			if (result.getElement().getParent() == null) {
				result.getElement().setParent(parent);
			} else {
				if (Logger.logError()) {
					Logger.error("Trying to override parent of '" + result.getElement().getFullQualifiedName()
							+ "'. This is not possible (already set to `"
							+ result.getElement().getParent().getFullQualifiedName()
							+ "`). Create new MethodDefinition first.", new RuntimeException());
				}
			}
		}
		
		this.methodDefLocations.add(result);
		return result;
	}
}
