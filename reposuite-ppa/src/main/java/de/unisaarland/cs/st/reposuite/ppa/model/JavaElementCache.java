package de.unisaarland.cs.st.reposuite.ppa.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
		if (Logger.logWarn()) {
			Logger.warn("Resetting the JavaElementDefinitionCache!");
		}
		classDefs.clear();
		methodDefs.clear();
		methodCalls.clear();
	}
	
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
			@NotNull final String file,
			final JavaClassDefinition parent, @NonNegative final int startLine, @NonNegative final int endLine,
			@NonNegative final int position, @NonNegative final int bodyStartLine, @NotNull final String packageName) {
		
		if (!classDefs.containsKey(fullQualifiedName)) {
			classDefs.put(fullQualifiedName, new JavaClassDefinition(fullQualifiedName, parent, packageName));
		}
		JavaElementLocation<JavaClassDefinition> result = new JavaElementLocation<JavaClassDefinition>(classDefs.get(fullQualifiedName), startLine, endLine, position,
				bodyStartLine, file);
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
			@NotNull final List<String> signature,
			@NotNull final String file, @NotNull final JavaElementDefinition parent, @NonNegative final int startLine,
			@NonNegative final int endLine, @NonNegative final int position) {
		
		String cacheName = JavaMethodCall.composeFullQualifiedName(fullQualifiedName, signature);
		if (!methodCalls.containsKey(cacheName)) {
			methodCalls.put(cacheName, new JavaMethodCall(fullQualifiedName, signature));
		}
		//TODO add parent to location
		JavaElementLocation<JavaMethodCall> methodCall = new JavaElementLocation<JavaMethodCall>(
				methodCalls.get(cacheName), startLine, endLine, position, -1, file);
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
		if (!methodDefs.containsKey(cacheName)) {
			methodDefs.put(cacheName, new JavaMethodDefinition(fullQualifiedName, signature, parent));
		}
		JavaElementLocation<JavaMethodDefinition> result = new JavaElementLocation<JavaMethodDefinition>(
		        methodDefs.get(cacheName), startLine, endLine, position, bodyStartLine, file);
		
		if (result.getElement().getParent() != parent) {
			if (result.getElement().getParent() == null) {
				result.getElement().setParent(parent);
			} else {
				if (Logger.logError()) {
					Logger.error("Trying to override parent of '" + result.getElement().getFullQualifiedName()
							+ "'. This is not possible (already set to `" + result.getElement().getParent().fullQualifiedName
							+ "`). Create new MethodDefinition first.", new RuntimeException());
				}
			}
		}
		
		this.methodDefLocations.add(result);
		return result;
	}
}
