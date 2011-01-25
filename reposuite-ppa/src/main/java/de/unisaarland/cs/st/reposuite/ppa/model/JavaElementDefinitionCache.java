package de.unisaarland.cs.st.reposuite.ppa.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;

import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.specification.NonNegative;
import de.unisaarland.cs.st.reposuite.utils.specification.NotNull;

/**
 * The Class JavaElementDefinitionCache.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class JavaElementDefinitionCache {
	
	/** The class definitions by name. */
	private static Map<String, JavaClassDefinition>  classDefsByName = new HashMap<String, JavaClassDefinition>();
	
	/** The class definitions by file. */
	private static Map<String, Set<JavaClassDefinition>>  classDefsByFile  = new HashMap<String, Set<JavaClassDefinition>>();
	
	/** The method definitions by name. */
	private static Map<String, JavaMethodDefinition> methodDefsByName = new HashMap<String, JavaMethodDefinition>();
	
	/** The method definitions by file. */
	private static Map<String, Set<JavaMethodDefinition>> methodDefsByFile = new HashMap<String, Set<JavaMethodDefinition>>();
	
	
	/**
	 * Gets the class definition.
	 * 
	 * @param fullQualifiedName
	 *            the full qualified name
	 * @param file
	 *            the file
	 * @param timestamp
	 *            the timestamp
	 * @param parent
	 *            the parent
	 * @param startLine
	 *            the start line
	 * @param endLine
	 *            the end line
	 * @param packageName
	 *            the package name
	 * @return the class definition
	 */
	public static JavaClassDefinition getClassDefinition(@NotNull final String fullQualifiedName,
			@NotNull final String file,
			@NotNull final DateTime timestamp, final JavaClassDefinition parent, @NonNegative final int startLine,
			@NonNegative final int endLine, @NotNull final String packageName) {
		
		if (!classDefsByName.containsKey(fullQualifiedName)) {
			classDefsByName.put(fullQualifiedName, new JavaClassDefinition(fullQualifiedName, file, timestamp, parent,
					startLine, endLine, packageName));
			if (!classDefsByFile.containsKey(file)) {
				classDefsByFile.put(file, new HashSet<JavaClassDefinition>());
			}
			classDefsByFile.get(file)
			.add(new JavaClassDefinition(fullQualifiedName, file, timestamp, parent, startLine,
					endLine, packageName));
		}
		return classDefsByName.get(fullQualifiedName);
	}
	
	/**
	 * Gets the class defs by file.
	 * 
	 * @return the class defs by file
	 */
	public static Map<String,Set<JavaClassDefinition>> getClassDefsByFile(){
		return classDefsByFile;
	}
	
	/**
	 * Gets the class definitions by name.
	 * 
	 * @return the class definitions by name
	 */
	public static Map<String, JavaClassDefinition> getClassDefsByName() {
		return classDefsByName;
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
	 * @param timestamp
	 *            the timestamp
	 * @param parent
	 *            the parent
	 * @param startLine
	 *            the start line
	 * @param endLine
	 *            the end line
	 * @return the method definition
	 */
	public static JavaMethodDefinition getMethodDefinition(@NotNull final String fullQualifiedName,
			@NotNull final List<String> signature, @NotNull final String file, @NotNull final DateTime timestamp,
			@NotNull final JavaClassDefinition parent, @NonNegative final int startLine, @NonNegative final int endLine) {
		
		String cacheName = JavaMethodDefinition.composeFullQualifiedName(fullQualifiedName, signature);
		JavaMethodDefinition methDef = null;
		if (!methodDefsByName.containsKey(cacheName)) {
			methDef = new JavaMethodDefinition(fullQualifiedName, signature, file, timestamp, parent, startLine,
					endLine);
			methodDefsByName.put(cacheName, methDef);
			if (!methodDefsByFile.containsKey(file)) {
				methodDefsByFile.put(file, new HashSet<JavaMethodDefinition>());
			}
			methodDefsByFile.get(file).add(methDef);
		} else {
			methDef = methodDefsByName.get(cacheName);
			if (methDef.getParent() != parent) {
				if (methDef.getParent() == null) {
					methDef.setParent(parent);
				} else {
					if (Logger.logError()) {
						Logger.error(
								"Trying to override parent of JavaMethodDefinition. This is not possible. Create new MethodDefinition first.",
								new RuntimeException());
					}
				}
			}
		}
		return methDef;
	}
	
	/**
	 * Gets the method defs by file.
	 * 
	 * @return the method defs by file
	 */
	public static Map<String, Set<JavaMethodDefinition>> getMethodDefsByFile() {
		return methodDefsByFile;
	}
	
	/**
	 * Gets the method definitions by name.
	 * 
	 * @return the method definitions by name
	 */
	public static Map<String,JavaMethodDefinition> getMethodDefsByName(){
		return methodDefsByName;
	}
	
	public static void reset() {
		if (Logger.logWarn()) {
			Logger.warn("Resetting the JavaElementDefinitionCache!");
		}
		classDefsByFile.clear();
		classDefsByName.clear();
		methodDefsByFile.clear();
		methodDefsByName.clear();
	}
	
}
