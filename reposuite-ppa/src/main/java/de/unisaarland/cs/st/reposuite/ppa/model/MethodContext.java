package de.unisaarland.cs.st.reposuite.ppa.model;

import java.util.List;

import de.unisaarland.cs.st.reposuite.utils.specification.NoneNull;

/**
 * The Class MethodContext.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
@Deprecated
public class MethodContext {
	
	@NoneNull
	public static String getMethodName(final String fullQualifiedName, final String methodName, final List<String> params) {
		StringBuilder builder = new StringBuilder();
		builder.append(fullQualifiedName);
		builder.append(".");
		builder.append(methodName);
		builder.append("(");
		if (params.size() > 0) {
			builder.append(params.get(0));
		}
		for (String param : params) {
			builder.append(",");
			builder.append(param);
		}
		builder.append(")");
		return builder.toString();
	}
	private final int          endLine;
	private final String       name;
	private final ClassContext parent;
	
	private final int          startLine;
	
	/**
	 * Instantiates a new method context.
	 * 
	 * @param name
	 *            the name
	 * @param parent
	 *            the parent
	 * @param startLine
	 *            the start line
	 * @param endLine
	 *            the end line
	 * @param filename
	 *            the filename
	 */
	protected MethodContext(final String name, final ClassContext parent, final int startLine, final int endLine, final String filename) {
		this.name = name;
		this.startLine = startLine;
		this.endLine = endLine;
		this.parent = parent;
	}
	
	/**
	 * Gets the end line.
	 * 
	 * @return the end line
	 */
	public int getEndLine() {
		return endLine;
	}
	
	/**
	 * Gets the full qualified name.
	 * 
	 * @return the full qualified name
	 */
	public String getFullQualifiedName() {
		return name;
	}
	
	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		String[] nameParts = name.split("\\.");
		return nameParts[nameParts.length - 1];
	}
	
	/**
	 * Gets the parent.
	 * 
	 * @return the parent
	 */
	public ClassContext getParent() {
		return parent;
	}
	
	/**
	 * Gets the start line.
	 * 
	 * @return the start line
	 */
	public int getStartLine() {
		return startLine;
	}
}
