package de.unisaarland.cs.st.reposuite.ppa.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.kanuni.annotations.simple.NotNegative;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import de.unisaarland.cs.st.reposuite.ppa.utils.JavaElementLocations;

/**
 * The Class JavaElementDefinitionCache. All instances extending JavaElement
 * must be stored here. Careful! Instances must be created new or fetched within
 * the persister.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class JavaElementCache {
	
	/** The class definitions by name. */
	public static Map<String, JavaClassDefinition>               classDefs           = new HashMap<String, JavaClassDefinition>();
	
	/** The method definitions by name. */
	public static Map<String, JavaMethodDefinition>              methodDefs          = new HashMap<String, JavaMethodDefinition>();
	
	/** The method calls by name. */
	public static Map<String, JavaMethodCall>                    methodCalls         = new HashMap<String, JavaMethodCall>();
	
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
	 * @param bodyStartLine
	 *            the body start line
	 * @param packageName
	 *            the package name
	 * @return the class definition
	 */
	public JavaElementLocation<JavaClassDefinition> getAnonymousClassDefinition(@NotNull final JavaClassDefinition parent,
	                                                                            @NotNull final String fullQualifiedName,
	                                                                            @NotNull final String file,
	                                                                            @NotNegative final int startLine,
	                                                                            @NotNegative final int endLine,
	                                                                            @NotNegative final int position,
	                                                                            @NotNegative final int bodyStartLine) {
		
		JavaClassDefinition def = null;
		if (!classDefs.containsKey(fullQualifiedName)) {
			def = new JavaClassDefinition(parent, fullQualifiedName);
			classDefs.put(fullQualifiedName, def);
		} else {
			def = classDefs.get(fullQualifiedName);
		}
		JavaElementLocation<JavaClassDefinition> result = new JavaElementLocation<JavaClassDefinition>(def, startLine,
				endLine,
				position,
				bodyStartLine,
				file);
		this.classDefLocations.add(result);
		
		return result;
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
	public JavaElementLocation<JavaClassDefinition> getClassDefinition(@NotNull final String fullQualifiedName,
	                                                                   @NotNull final String file,
	                                                                   @NotNegative final int startLine,
	                                                                   @NotNegative final int endLine,
	                                                                   @NotNegative final int position,
	                                                                   @NotNegative final int bodyStartLine,
	                                                                   @NotNull final String packageName) {
		
		JavaClassDefinition def = null;
		if (!classDefs.containsKey(fullQualifiedName)) {
			def = new JavaClassDefinition(fullQualifiedName, packageName);
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
	                                                         @NotNull final JavaElementDefinition parent,
	                                                         @NotNegative final int startLine,
	                                                         @NotNegative final int endLine,
	                                                         @NotNegative final int position) {
		
		String cacheName = JavaMethodCall.composeFullQualifiedName(fullQualifiedName, signature);
		JavaMethodCall call = null;
		if (!methodCalls.containsKey(cacheName)) {
			call = new JavaMethodCall(cacheName, signature);
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
	 * @param bodyStartLine
	 *            the body start line
	 * @return the method definition
	 */
	public JavaElementLocation<JavaMethodDefinition> getMethodDefinition(@NotNull final String fullQualifiedName,
	                                                                     @NotNull final List<String> signature, @NotNull final String file,
	                                                                     @NotNegative final int startLine,
	                                                                     @NotNegative final int endLine,
	                                                                     @NotNegative final int position,
	                                                                     final int bodyStartLine) {
		
		JavaMethodDefinition def = null;
		if (!methodDefs.containsKey(fullQualifiedName)) {
			
			def = new JavaMethodDefinition(fullQualifiedName, signature);
			methodDefs.put(fullQualifiedName, def);
		} else {
			def = methodDefs.get(fullQualifiedName);
		}
		JavaElementLocation<JavaMethodDefinition> result = new JavaElementLocation<JavaMethodDefinition>(def,
				startLine, endLine, position, bodyStartLine, file);
		
		this.methodDefLocations.add(result);
		return result;
	}
}