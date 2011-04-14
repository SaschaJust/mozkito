package de.unisaarland.cs.st.reposuite.ppa.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.kanuni.annotations.simple.NotNegative;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;
import de.unisaarland.cs.st.reposuite.ppa.utils.JavaElementLocations;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * The Class JavaElementDefinitionCache. All instances extending JavaElement
 * must be stored here. Careful! Instances must be created new or fetched within
 * the persister.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class JavaElementCache {
	
	/** The class definitions by name. */
	public static Map<String, JavaClassDefinition>  classDefs           = new HashMap<String, JavaClassDefinition>();
	
	/** The method definitions by name. */
	public static Map<String, JavaMethodDefinition> methodDefs          = new HashMap<String, JavaMethodDefinition>();
	
	/** The method calls by name. */
	public static Map<String, JavaMethodCall>       methodCalls         = new HashMap<String, JavaMethodCall>();
	
	/** The class def locations. */
	private final Set<JavaElementLocation>          classDefLocations   = new HashSet<JavaElementLocation>();
	
	/** The method def locations. */
	private final Set<JavaElementLocation>          methodDefLocations  = new HashSet<JavaElementLocation>();
	
	/** The method call locations. */
	private final Set<JavaElementLocation>          methodCallLocations = new HashSet<JavaElementLocation>();
	
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
	public JavaElementLocation getAnonymousClassDefinition(@NotNull final JavaClassDefinition parent,
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
		JavaElementLocation result = new JavaElementLocation(def, startLine, endLine, position, bodyStartLine, file);
		classDefLocations.add(result);
		
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
	public JavaElementLocation getClassDefinition(@NotNull final String fullQualifiedName,
	                                              @NotNull final String file,
	                                              @NotNegative final int startLine,
	                                              @NotNegative final int endLine,
	                                              @NotNegative final int position,
	                                              @NotNegative final int bodyStartLine) {
		
		JavaClassDefinition def = null;
		if (!classDefs.containsKey(fullQualifiedName)) {
			def = new JavaClassDefinition(fullQualifiedName);
			classDefs.put(fullQualifiedName, def);
		} else {
			def = classDefs.get(fullQualifiedName);
		}
		JavaElementLocation result = new JavaElementLocation(def, startLine, endLine, position, bodyStartLine, file);
		classDefLocations.add(result);
		
		return result;
	}
	
	/**
	 * Gets the java element locations.
	 * 
	 * @return the java element locations
	 */
	public JavaElementLocations getJavaElementLocations() {
		JavaElementLocations result = new JavaElementLocations();
		result.addAllClassDefs(classDefLocations);
		result.addAllMethodCalls(methodCallLocations);
		result.addAllMethodDefs(methodDefLocations);
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
	public JavaElementLocation getMethodCall(@NotNull final String objectName,
	                                         @NotNull final String methodName,
	                                         @NotNull final List<String> signature,
	                                         @NotNull final String file,
	                                         @NotNull final JavaElement parent,
	                                         @NotNegative final int startLine,
	                                         @NotNegative final int endLine,
	                                         @NotNegative final int position) {
		
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
		JavaElementLocation methodCall = new JavaElementLocation(call, startLine, endLine, position, -1, file);
		methodCallLocations.add(methodCall);
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
	public JavaElementLocation getMethodDefinition(@NotNull final String objectName,
	                                               @NotNull final String methodName,
	                                               @NotNull final List<String> signature,
	                                               @NotNull final String file,
	                                               @NotNegative final int startLine,
	                                               @NotNegative final int endLine,
	                                               @NotNegative final int position,
	                                               final int bodyStartLine) {
		
		JavaMethodDefinition def = null;
		String cacheName = JavaMethodCall.composeFullQualifiedName(objectName, methodName, signature);
		if (!methodDefs.containsKey(cacheName)) {
			
			def = new JavaMethodDefinition(objectName, methodName, signature);
			methodDefs.put(cacheName, def);
		} else {
			def = methodDefs.get(cacheName);
		}
		JavaElementLocation result = new JavaElementLocation(def, startLine, endLine, position, bodyStartLine, file);
		
		methodDefLocations.add(result);
		return result;
	}
}
