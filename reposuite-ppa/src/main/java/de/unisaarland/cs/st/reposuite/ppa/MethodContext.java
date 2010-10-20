package de.unisaarland.cs.st.reposuite.ppa;

/**
 * The Class MethodContext.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class MethodContext {
	
	private int          endLine;
	private String       name;
	private ClassContext parent;
	private int          startLine;
	
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
	protected MethodContext(String name, ClassContext parent, int startLine, int endLine, String filename) {
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
