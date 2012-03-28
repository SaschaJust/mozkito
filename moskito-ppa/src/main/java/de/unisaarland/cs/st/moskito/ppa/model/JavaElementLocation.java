/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package de.unisaarland.cs.st.moskito.ppa.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotNegative;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

import org.apache.openjpa.persistence.Type;
import org.apache.openjpa.persistence.jdbc.Index;
import org.jdom.Attribute;
import org.jdom.Element;

import de.unisaarland.cs.st.moskito.persistence.Annotated;

/**
 * The Class JavaElementLocation.
 * 
 * @param <T>
 *            the generic type
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
@Entity
public class JavaElementLocation implements Comparable<JavaElementLocation>, Annotated {
	
	/**
	 * The Enum LineCover.
	 * 
	 * @author Kim Herzig <herzig@cs.uni-saarland.de>
	 */
	public static enum LineCover {
		
		/** The DEFINITION. */
		DEFINITION,
		/** The BODY. */
		BODY,
		/** The DE f_ an d_ body. */
		DEF_AND_BODY,
		/** The FALSE. */
		FALSE
	}
	
	public static String      JAVA_ELEMENT_LOCATION_TAG             = "JavaElementLocation";
	public static String      JAVA_ELEMENT_LOCATION_ID_ATTR         = "id";
	public static String      JAVA_ELEMENT_LOCATION_START_LINE_ATTR = "startline";
	public static String      JAVA_ELEMENT_LOCATION_END_LINE_ATTR   = "endline";
	public static String      JAVA_ELEMENT_LOCATION_POSITION_ATTR   = "position";
	public static String      JAVA_ELEMENT_LOCATION_PATH_TAG        = "filePath";
	public static String      JAVA_ELEMENT_LOCATION_BODY_START_ATTR = "bodystartline";
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID                      = -4858435624572738026L;
	
	/**
	 * Creates a JavaElementLocation instance from XML.
	 * 
	 * @param element
	 *            the element
	 * @return the java element location if successful. Returns <code>null</code> otherwise.
	 */
	public static JavaElementLocation fromXMLRepresentation(final org.jdom.Element element) {
		
		if (!element.getName().equals(JAVA_ELEMENT_LOCATION_TAG)) {
			if (Logger.logWarn()) {
				Logger.warn("Unrecognized JavaElementLocation tag. Expected <" + JAVA_ELEMENT_LOCATION_TAG
				        + "> but got <" + element.getName() + ">. Retuning null.");
			}
			return null;
		}
		
		final Attribute idAttr = element.getAttribute(JAVA_ELEMENT_LOCATION_ID_ATTR);
		if (idAttr == null) {
			if (Logger.logWarn()) {
				Logger.warn("Could not extract JavaElementLocation.id from XML. Returning null.");
			}
			return null;
		}
		Long id = -1l;
		try {
			id = new Long(idAttr.getValue());
		} catch (final NumberFormatException e) {
			if (Logger.logWarn()) {
				Logger.warn("Could not extract JavaElementLocation.id from XML. Returning null.");
			}
			return null;
		}
		
		final Attribute slAttr = element.getAttribute(JAVA_ELEMENT_LOCATION_START_LINE_ATTR);
		Integer startline = -1;
		if (slAttr == null) {
			if (Logger.logWarn()) {
				Logger.warn("Could not extract JavaElementLocation.startline from XML. Returning null.");
			}
			return null;
		}
		try {
			startline = new Integer(slAttr.getValue());
		} catch (final NumberFormatException e) {
			if (Logger.logWarn()) {
				Logger.warn("Could not extract JavaElementLocation.startline from XML. Returning null.");
			}
			return null;
		}
		
		final Attribute elAttr = element.getAttribute(JAVA_ELEMENT_LOCATION_END_LINE_ATTR);
		Integer endline = -1;
		if (elAttr == null) {
			if (Logger.logWarn()) {
				Logger.warn("Could not extract JavaElementLocation.endline from XML. Returning null.");
			}
			return null;
		}
		try {
			endline = new Integer(elAttr.getValue());
		} catch (final NumberFormatException e) {
			if (Logger.logWarn()) {
				Logger.warn("Could not extract JavaElementLocation.endline from XML. Returning null.");
			}
			return null;
		}
		
		final Attribute posAttr = element.getAttribute(JAVA_ELEMENT_LOCATION_POSITION_ATTR);
		Integer position = -1;
		if (posAttr == null) {
			if (Logger.logWarn()) {
				Logger.warn("Could not extract JavaElementLocation.position from XML. Returning null.");
			}
			return null;
		}
		try {
			position = new Integer(posAttr.getValue());
		} catch (final NumberFormatException e) {
			if (Logger.logWarn()) {
				Logger.warn("Could not extract JavaElementLocation.position from XML. Returning null.");
			}
			return null;
		}
		
		final Attribute bsAttr = element.getAttribute(JAVA_ELEMENT_LOCATION_BODY_START_ATTR);
		Integer bodystart = -1;
		if (bsAttr == null) {
			if (Logger.logWarn()) {
				Logger.warn("Could not extract JavaElementLocation.bodystartline from XML. Returning null.");
			}
			return null;
		}
		try {
			bodystart = new Integer(bsAttr.getValue());
		} catch (final NumberFormatException e) {
			if (Logger.logWarn()) {
				Logger.warn("Could not extract JavaElementLocation.bodystartline from XML. Returning null.");
			}
			return null;
		}
		
		final org.jdom.Element pathElement = element.getChild(JAVA_ELEMENT_LOCATION_PATH_TAG);
		if (pathElement == null) {
			if (Logger.logWarn()) {
				Logger.warn("Could not extract JavaElementLocation.path from XML. Returning null.");
			}
			return null;
		}
		final String path = pathElement.getText();
		
		JavaElement javaElement = null;
		
		org.jdom.Element elementChild = element.getChild(JavaClassDefinition.JAVA_CLASS_DEFINITION);
		if (elementChild != null) {
			javaElement = JavaClassDefinition.fromXMLRepresentation(elementChild);
		} else {
			elementChild = element.getChild(JavaMethodDefinition.JAVA_METHOD_DEFINITION);
			if (elementChild != null) {
				javaElement = JavaMethodDefinition.fromXMLRepresentation(elementChild);
			} else {
				elementChild = element.getChild(JavaMethodCall.JAVA_METHOD_CALL);
				if (elementChild != null) {
					javaElement = JavaMethodCall.fromXMLRepresentation(elementChild);
				} else {
					if (Logger.logWarn()) {
						Logger.warn("Could not extract JavaElement from XML. Returning null.");
					}
					return null;
				}
			}
		}
		
		if (javaElement == null) {
			if (Logger.logWarn()) {
				Logger.warn("Could not extract JavaElement from XML. Returning null.");
			}
			return null;
		}
		
		final JavaElementLocation javaElementLocation = new JavaElementLocation(javaElement, startline, endline,
		                                                                        position, bodystart, path);
		javaElementLocation.setId(id);
		return javaElementLocation;
	}
	
	/** The id. */
	private long         id;
	
	/** The start line. */
	private int          startLine;
	
	/** The end line. */
	private int          endLine;
	
	/** The position. */
	private int          position;
	
	/** The element. */
	private JavaElement  element;
	
	/** The file path. */
	private String       filePath     = "<unknown>";
	
	/** The body start line. */
	private int          bodyStartLine;
	
	/** The comment lines. */
	private Set<Integer> commentLines = new HashSet<Integer>();
	
	/**
	 * Instantiates a new java element location.
	 */
	protected JavaElementLocation() {
	}
	
	/**
	 * Instantiates a new java element location.
	 * 
	 * @param element
	 *            the element
	 * @param startLine
	 *            the start line
	 * @param endLine
	 *            the end line
	 * @param position
	 *            the position
	 * @param bodyStartLine
	 *            the body start line
	 * @param filePath
	 *            the file path
	 */
	public JavaElementLocation(@NotNull final JavaElement element, @NotNegative final int startLine,
	        @NotNegative final int endLine, @NotNegative final int position, final int bodyStartLine,
	        @NotNull final String filePath) {
		Condition.check(startLine <= endLine, "Start line must be smaller or equal than end line");
		
		if ((element instanceof JavaClassDefinition) || (element instanceof JavaMethodDefinition)) {
			Condition.check(bodyStartLine <= endLine,
			                "Body start line must be smaller or equal than end line: bodyStartLine=" + bodyStartLine
			                        + " startLine=" + endLine);
			Condition.check(bodyStartLine >= startLine,
			                "Body start line must be greater or equal than end line: bodyStartLine=" + bodyStartLine
			                        + " startLine=" + startLine);
		}
		setElement(element);
		setStartLine(startLine);
		setEndLine(endLine);
		setPosition(position);
		setFilePath(filePath);
		setBodyStartLine(bodyStartLine);
	}
	
	/**
	 * Adds the comment lines.
	 * 
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 */
	@Transient
	public void addCommentLines(@NotNegative final int from,
	                            @NotNegative final int to) {
		Condition.check(from <= to, "You must supply a closed interval.");
		for (int i = from; i <= to; ++i) {
			getCommentLines().add(i);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	@NoneNull
	public int compareTo(final JavaElementLocation other) {
		if (getStartLine() < other.getStartLine()) {
			return -1;
		} else if (getStartLine() > other.getStartLine()) {
			return 1;
		} else {
			if (getPosition() < other.getPosition()) {
				return -1;
			} else if (getPosition() > other.getPosition()) {
				return 1;
			} else {
				return 0;
			}
		}
	}
	
	/**
	 * Covers all lines.
	 * 
	 * @param lines
	 *            the lines
	 * @return the line cover
	 */
	@NoneNull
	public LineCover coversAllLines(final Collection<Integer> lines) {
		LineCover lc = LineCover.FALSE;
		for (final int line : lines) {
			final LineCover tmpLC = coversLine(line);
			if (tmpLC.equals(LineCover.FALSE)) {
				return tmpLC;
			}
			switch (lc) {
				case DEF_AND_BODY:
					break;
				case BODY:
					if (tmpLC.equals(LineCover.DEFINITION)) {
						lc = LineCover.DEF_AND_BODY;
					}
					break;
				case DEFINITION:
					if (tmpLC.equals(LineCover.BODY)) {
						lc = LineCover.DEF_AND_BODY;
					}
					break;
				default:
					lc = tmpLC;
					break;
			}
		}
		return lc;
	}
	
	/**
	 * Covers any line.
	 * 
	 * @param lines
	 *            the lines
	 * @return the line cover
	 */
	@NoneNull
	public LineCover coversAnyLine(final Collection<Integer> lines) {
		LineCover lc = LineCover.FALSE;
		for (final int line : lines) {
			final LineCover tmpLC = coversLine(line);
			if (!tmpLC.equals(LineCover.FALSE)) {
				switch (lc) {
					case DEF_AND_BODY:
						return lc;
					case BODY:
						if (tmpLC.equals(LineCover.DEFINITION)) {
							return LineCover.DEF_AND_BODY;
						}
						break;
					case DEFINITION:
						if (tmpLC.equals(LineCover.BODY)) {
							return LineCover.DEF_AND_BODY;
						}
						break;
					default:
						lc = tmpLC;
						break;
				}
			}
		}
		return lc;
	}
	
	/**
	 * Covers line.
	 * 
	 * @param line
	 *            the line
	 * @return the line cover
	 */
	public LineCover coversLine(@NotNegative final int line) {
		if ((getStartLine() <= line) && (getEndLine() >= line) && (!getCommentLines().contains(line))) {
			if (getElement() instanceof JavaMethodCall) {
				return LineCover.DEFINITION;
			}
			if ((getBodyStartLine() < 0) || (getBodyStartLine() >= line)) {
				return LineCover.DEFINITION;
			}
			return LineCover.BODY;
		}
		return LineCover.FALSE;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final JavaElementLocation other = (JavaElementLocation) obj;
		if (getElement() == null) {
			if (other.getElement() != null) {
				return false;
			}
		} else if (!getElement().equals(other.getElement())) {
			return false;
		}
		if (getFilePath() == null) {
			if (other.getFilePath() != null) {
				return false;
			}
		} else if (!getFilePath().equals(other.getFilePath())) {
			return false;
		}
		if (getElement() instanceof JavaMethodCall) {
			if (getEndLine() != other.getEndLine()) {
				return false;
			}
			if (getPosition() != other.getPosition()) {
				return false;
			}
			if (getStartLine() != other.getStartLine()) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Gets the body start line.
	 * 
	 * @return the body start line
	 */
	public int getBodyStartLine() {
		return this.bodyStartLine;
	}
	
	/**
	 * Gets the comment lines.
	 * 
	 * @return the comment lines
	 */
	@ElementCollection
	public Set<Integer> getCommentLines() {
		return this.commentLines;
	}
	
	/**
	 * Gets the element.
	 * 
	 * @return the element
	 */
	@Type (JavaElement.class)
	@ManyToOne (cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	public JavaElement getElement() {
		return this.element;
	}
	
	/**
	 * Gets the end line.
	 * 
	 * @return the end line
	 */
	public int getEndLine() {
		return this.endLine;
	}
	
	/**
	 * Gets the file path.
	 * 
	 * @return the file path
	 */
	public String getFilePath() {
		return this.filePath;
	}
	
	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Index (name = "idx_elemlocid")
	@Id
	@GeneratedValue
	public long getId() {
		return this.id;
	}
	
	/**
	 * Gets the position.
	 * 
	 * @return the position
	 */
	public int getPosition() {
		return this.position;
	}
	
	/**
	 * Gets the start line.
	 * 
	 * @return the start line
	 */
	public int getStartLine() {
		return this.startLine;
	}
	
	/**
	 * Gets the xML representation.
	 * 
	 * @return the xML representation
	 */
	@NoneNull
	@Transient
	public Element getXMLRepresentation() {
		final Element thisElement = new Element(JAVA_ELEMENT_LOCATION_TAG);
		
		thisElement.setAttribute(JAVA_ELEMENT_LOCATION_ID_ATTR, "" + getId());
		thisElement.setAttribute(JAVA_ELEMENT_LOCATION_START_LINE_ATTR, "" + getStartLine());
		thisElement.setAttribute(JAVA_ELEMENT_LOCATION_END_LINE_ATTR, "" + getEndLine());
		thisElement.setAttribute(JAVA_ELEMENT_LOCATION_POSITION_ATTR, "" + getPosition());
		thisElement.setAttribute(JAVA_ELEMENT_LOCATION_BODY_START_ATTR, "" + getBodyStartLine());
		
		final Element filePathElement = new Element(JAVA_ELEMENT_LOCATION_PATH_TAG);
		filePathElement.setText(getFilePath());
		thisElement.addContent(filePathElement);
		
		thisElement.addContent(getElement().getXMLRepresentation());
		
		return thisElement;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((getElement() == null)
		                                                   ? 0
		                                                   : getElement().hashCode());
		result = (prime * result) + ((getFilePath() == null)
		                                                    ? 0
		                                                    : getFilePath().hashCode());
		if (getElement() instanceof JavaMethodCall) {
			result = (prime * result) + getEndLine();
			result = (prime * result) + getPosition();
			result = (prime * result) + getStartLine();
		}
		return result;
	}
	
	/**
	 * Sets the body start line.
	 * 
	 * @param bodyStartLine
	 *            the new body start line
	 */
	protected void setBodyStartLine(final int bodyStartLine) {
		this.bodyStartLine = bodyStartLine;
	}
	
	/**
	 * Sets the comment lines.
	 * 
	 * @param commentLines
	 *            the new comment lines
	 */
	@NoneNull
	protected void setCommentLines(final Set<Integer> commentLines) {
		this.commentLines = commentLines;
	}
	
	/**
	 * Sets the element. Careful! This can have nasty side effects when persisting the JavaElementLocation using
	 * persistence middleware!
	 * 
	 * @param element
	 *            the new element
	 */
	public void setElement(final JavaElement element) {
		this.element = element;
	}
	
	/**
	 * Sets the end line.
	 * 
	 * @param endLine
	 *            the new end line
	 */
	protected void setEndLine(final int endLine) {
		this.endLine = endLine;
	}
	
	/**
	 * Sets the file path.
	 * 
	 * @param filePath
	 *            the new file path
	 */
	protected void setFilePath(final String filePath) {
		this.filePath = filePath;
	}
	
	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the new id
	 */
	protected void setId(final long id) {
		this.id = id;
	}
	
	/**
	 * Sets the position.
	 * 
	 * @param position
	 *            the new position
	 */
	protected void setPosition(final int position) {
		this.position = position;
	}
	
	/**
	 * Sets the start line.
	 * 
	 * @param startLine
	 *            the new start line
	 */
	protected void setStartLine(final int startLine) {
		this.startLine = startLine;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("element = [");
		sb.append(getElement().toString());
		sb.append("]");
		sb.append(", id = ");
		sb.append(getId());
		sb.append(", filePath = ");
		sb.append(getFilePath());
		sb.append(", position = ");
		sb.append(getPosition());
		sb.append(", startLine = ");
		sb.append(getStartLine());
		sb.append(", endLine = ");
		sb.append(getEndLine());
		sb.append("]");
		return sb.toString();
	}
	
}
