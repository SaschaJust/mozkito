package de.unisaarland.cs.st.reposuite.ppa.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.dom.ASTNode;

import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * The Class ClassContext.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
@Deprecated
public class ClassContext {
	
	private final static String       anonCheck = ".*\\$\\d+";
	private int                       anonCounter;
	private boolean                   anonymClass;
	private final int                       endLine;
	private MethodContext             method    = null;
	private final String                    name;
	private String                    filename;
	
	private final HashMap<ASTNode, Integer> nodes     = new HashMap<ASTNode, Integer>();
	
	private final ClassContext              parent;
	private final int                       startLine;
	private String                    superclass;
	
	/**
	 * Instantiates a new class context.
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
	public ClassContext(final String name, final ClassContext parent, final int startLine, final int endLine, final String filename) {
		this.name = name;
		this.startLine = startLine;
		this.endLine = endLine;
		this.parent = parent;
		anonCounter = 0;
		superclass = null;
		if (Pattern.matches(anonCheck, name)) {
			anonymClass = true;
		} else {
			anonymClass = false;
		}
	}
	
	/**
	 * Adds the method.
	 * 
	 * @param methodName
	 *            the method name
	 * @param params
	 *            the params
	 * @param startline
	 *            the startline
	 * @param endline
	 *            the endline
	 * @return the method context
	 */
	public MethodContext addMethod(final String methodName, final List<String> params, final int startline, final int endline) {
		if (method != null) {
			if (!removeMethod(startline)) {
				
				Logger.error("Cannot add method `" + methodName + "` context to class `" + getFullQualifiedName()
						+ "` in line " + startline + " when method `" + getCurrentMethod().getFullQualifiedName()
						+ "` is set already!");
				return null;
			}
		}
		method = new MethodContext(MethodContext.getMethodName(getFullQualifiedName(), methodName, params), this,
				startline,
				endline, getFilename());
		return method;
	}
	
	/**
	 * Adds the node.
	 * 
	 * @param exp
	 *            the exp
	 * @param line
	 *            the line
	 */
	public void addNode(final ASTNode exp, final int line) {
		nodes.put(exp, line);
	}
	
	/**
	 * Gets the current method.
	 * 
	 * @return the current method
	 */
	public MethodContext getCurrentMethod() {
		return method;
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
	 * Gets the filename.
	 * 
	 * @return the filename
	 */
	private String getFilename() {
		return filename;
	}
	
	/**
	 * Return the full qualified name of the corresponding class.
	 * 
	 * @return the full qualified name
	 */
	public String getFullQualifiedName() {
		return name;
	}
	
	/**
	 * Retuns the class name without package information.
	 * 
	 * @return the name
	 */
	public String getName() {
		String[] nameParts = name.split("\\.");
		return nameParts[nameParts.length - 1];
	}
	
	/**
	 * Gets the nodes.
	 * 
	 * @return the nodes
	 */
	public Map<ASTNode, Integer> getNodes() {
		return nodes;
	}
	
	/**
	 * Gets the start line.
	 * 
	 * @return the start line
	 */
	public int getStartLine() {
		return startLine;
	}
	
	/**
	 * Gets the superclass.
	 * 
	 * @return the superclass
	 */
	public String getSuperclass() {
		return superclass;
	}
	
	/**
	 * Next anon counter.
	 * 
	 * @return the int
	 */
	public int nextAnonCounter() {
		if (anonymClass) {
			return parent.nextAnonCounter();
		} else {
			return ++anonCounter;
		}
	}
	
	/**
	 * Removes the method if it is set.
	 * 
	 * @param line
	 *            the line
	 * @return true, if successful
	 */
	public boolean removeMethod(final int line) {
		if ((method != null) && (method.getEndLine() < line)) {
			method = null;
			return true;
		}
		return false;
	}
	
	/**
	 * Sets the superclass.
	 * 
	 * @param superclass
	 *            the new superclass
	 */
	public void setSuperclass(final String superclass) {
		this.superclass = superclass;
	}
}
