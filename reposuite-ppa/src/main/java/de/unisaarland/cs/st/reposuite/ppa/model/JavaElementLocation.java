package de.unisaarland.cs.st.reposuite.ppa.model;

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

import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;

/**
 * The Class JavaElementLocation.
 * 
 * @param <T>
 *            the generic type
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
@Entity
public class JavaElementLocation<T extends JavaElement> implements Comparable<JavaElementLocation<T>>, Annotated {
	
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
	};
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4858435624572738026L;
	
	/** The id. */
	private long              id;
	
	/** The start line. */
	private int               startLine;
	
	/** The end line. */
	private int               endLine;
	
	/** The position. */
	private int               position;
	
	/** The element. */
	private T                 element;
	
	/** The file path. */
	private String            filePath         = "<unknown>";
	
	/** The body start line. */
	private int               bodyStartLine;
	
	/** The comment lines. */
	private Set<Integer>      commentLines     = new HashSet<Integer>();
	
	/**
	 * Instantiates a new java element location.
	 */
	@SuppressWarnings("unused")
	private JavaElementLocation() {
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
	public JavaElementLocation(@NotNull final T element, @NotNegative final int startLine,
	                           @NotNegative final int endLine, @NotNegative final int position, final int bodyStartLine,
	                           @NotNull final String filePath) {
		Condition.check(startLine <= endLine, "Start line must be smaller or equal than end line");
		if (element instanceof JavaElementDefinition) {
			Condition.check(bodyStartLine <= endLine,
			                "Body start line must be smaller or equal than end line: bodyStartLine=" + bodyStartLine
			                + " startLine=" + endLine);
			Condition.check(bodyStartLine >= startLine,
			                "Body start line must be greater or equal than end line: bodyStartLine=" + bodyStartLine
			                + " startLine=" + startLine);
		}
		this.setElement(element);
		this.setStartLine(startLine);
		this.setEndLine(endLine);
		this.setPosition(position);
		this.setFilePath(filePath);
		this.setBodyStartLine(bodyStartLine);
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
		for(int i = from; i <= to; ++i){
			this.commentLines .add(i);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	@NoneNull
	public int compareTo(final JavaElementLocation<T> other) {
		if (this.getStartLine() < other.getStartLine()) {
			return -1;
		} else if (this.getStartLine() > other.getStartLine()) {
			return 1;
		} else {
			if (this.getPosition() < other.getPosition()) {
				return -1;
			} else if (this.getPosition() > other.getPosition()) {
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
		for (int line : lines) {
			LineCover tmpLC = coversLine(line);
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
				case DEFINITION:
					if (tmpLC.equals(LineCover.BODY)) {
						lc = LineCover.DEF_AND_BODY;
					}
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
		for (int line : lines) {
			LineCover tmpLC = coversLine(line);
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
		if ((getStartLine() <= line) && (getEndLine() >= line) && (!this.commentLines.contains(line))) {
			if (this.getElement() instanceof JavaMethodCall) {
				return LineCover.DEFINITION;
			}
			if ((getBodyStartLine() < 0) || (getBodyStartLine() >= line)) {
				return LineCover.DEFINITION;
			}
			return LineCover.BODY;
		}
		return LineCover.FALSE;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
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
		JavaElementLocation<T> other = (JavaElementLocation<T>) obj;
		if (this.element == null) {
			if (other.element != null) {
				return false;
			}
		} else if (!this.element.equals(other.element)) {
			return false;
		}
		if (this.filePath == null) {
			if (other.filePath != null) {
				return false;
			}
		} else if (!this.filePath.equals(other.filePath)) {
			return false;
		}
		if (!(getElement() instanceof JavaElementDefinition)) {
			if (this.endLine != other.endLine) {
				return false;
			}
			if (this.position != other.position) {
				return false;
			}
			if (this.startLine != other.startLine) {
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
	@Type(type = "de.unisaarland.cs.st.reposuite.ppa.model.JavaElement")
	@ManyToOne(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	public T getElement() {
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
	@Index(name = "idx_elemlocid")
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
	 * @param document
	 *            the document
	 * @return the xML representation
	 */
	@NoneNull
	public Element getXMLRepresentation(final Document document){
		Element thisElement = document.createElement("JavaElementLocation");
		
		thisElement.setAttribute("id", "" + getId());
		thisElement.setAttribute("startline", "" + getStartLine());
		thisElement.setAttribute("endline", "" + getEndLine());
		thisElement.setAttribute("position", "" + getPosition());
		
		Element filePathElement = document.createElement("filePath");
		Text textNode = document.createTextNode(getFilePath());
		filePathElement.appendChild(textNode);
		thisElement.appendChild(filePathElement);
		
		thisElement.appendChild(getElement().getXMLRepresentation(document));
		
		return thisElement;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.element == null) ? 0 : this.element.hashCode());
		result = prime * result + ((this.filePath == null) ? 0 : this.filePath.hashCode());
		if (!(getElement() instanceof JavaElementDefinition)) {
			result = prime * result + this.endLine;
			result = prime * result + this.position;
			result = prime * result + this.startLine;
		}
		return result;
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.persistence.Annotated#saveFirst()
	 */
	@Override
	public Collection<Annotated> saveFirst() {
		return null;
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
	 * Sets the element. Careful! This can have nasty side effects when
	 * persisting the JavaElementLocation using hibernate!
	 * 
	 * @param element
	 *            the new element
	 */
	@NoneNull
	public void setElement(final T element) {
		this.element = element;
	}
	
	/**
	 * Sets the end line.
	 * 
	 * @param endLine
	 *            the new end line
	 */
	private void setEndLine(final int endLine) {
		this.endLine = endLine;
	}
	
	/**
	 * Sets the file path.
	 * 
	 * @param filePath
	 *            the new file path
	 */
	@NoneNull
	private void setFilePath(final String filePath) {
		this.filePath = filePath;
	}
	
	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the new id
	 */
	@SuppressWarnings("unused")
	private void setId(final long id) {
		this.id = id;
	}
	
	
	/**
	 * Sets the position.
	 * 
	 * @param position
	 *            the new position
	 */
	private void setPosition(final int position) {
		this.position = position;
	}
	
	/**
	 * Sets the start line.
	 * 
	 * @param startLine
	 *            the new start line
	 */
	private void setStartLine(final int startLine) {
		this.startLine = startLine;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("element = [");
		sb.append(this.getElement().toString());
		sb.append("]");
		sb.append(", id = ");
		sb.append(this.getId());
		sb.append(", filePath = ");
		sb.append(this.getFilePath());
		sb.append(", position = ");
		sb.append(this.getPosition());
		sb.append(", startLine = ");
		sb.append(this.getStartLine());
		sb.append(", endLine = ");
		sb.append(this.getEndLine());
		sb.append("]");
		return sb.toString();
	}
	
}
