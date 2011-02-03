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
	public JavaClassDefinition getClassDefinition(@NotNull final String fullQualifiedName, @NotNull final String file,
			final JavaClassDefinition parent, @NonNegative final int startLine, @NonNegative final int endLine,
			@NonNegative final int position, @NotNull final String packageName) {
		
		if (!classDefs.containsKey(fullQualifiedName)) {
			classDefs.put(fullQualifiedName, new JavaClassDefinition(fullQualifiedName, parent, packageName));
		}
		JavaClassDefinition result = classDefs.get(fullQualifiedName);
		this.classDefLocations.add(new JavaElementLocation<JavaClassDefinition>(result, startLine, endLine, position,
				file));
		return result;
	}
	
	/**
	 * Gets the java element locations.
	 * 
	 * @return the java element locations
	 */
	public JavaElementLocations getJavaElementLocations(){
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
	public JavaMethodCall getMethodCall(@NotNull final String fullQualifiedName, @NotNull final List<String> signature,
			@NotNull final String file, @NotNull final JavaElementDefinition parent, @NonNegative final int startLine,
			@NonNegative final int endLine, @NonNegative final int position) {
		
		String cacheName = JavaMethodCall.composeFullQualifiedName(fullQualifiedName, signature);
		if (!methodCalls.containsKey(cacheName)) {
			methodCalls.put(cacheName, new JavaMethodCall(fullQualifiedName, signature));
		}
		//TODO add parent to location
		JavaMethodCall methodCall = methodCalls.get(cacheName);
		this.methodCallLocations.add(new JavaElementLocation<JavaMethodCall>(methodCall, startLine, endLine, position,
				file));
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
	public JavaMethodDefinition getMethodDefinition(@NotNull final String fullQualifiedName,
			@NotNull final List<String> signature, @NotNull final String file,
			@NotNull final JavaClassDefinition parent, @NonNegative final int startLine,
			@NonNegative final int endLine, @NonNegative final int position) {
		
		String cacheName = JavaMethodDefinition.composeFullQualifiedName(fullQualifiedName, signature);
		if (!methodDefs.containsKey(cacheName)) {
			methodDefs.put(cacheName, new JavaMethodDefinition(fullQualifiedName, signature, parent));
		}
		JavaMethodDefinition result = methodDefs.get(cacheName);
		
		if (result.getParent() != parent) {
			if (result.getParent() == null) {
				result.setParent(parent);
			} else {
				if (Logger.logError()) {
					Logger.error(
							"Trying to override parent of JavaMethodDefinition. This is not possible. Create new MethodDefinition first.",
							new RuntimeException());
				}
			}
		}
		
		this.methodDefLocations.add(new JavaElementLocation<JavaMethodDefinition>(result, startLine, endLine, position,
				file));
		return result;
	}
}
