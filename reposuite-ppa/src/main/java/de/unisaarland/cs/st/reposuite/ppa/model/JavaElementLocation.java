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

import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.utils.Condition;
import de.unisaarland.cs.st.reposuite.utils.specification.NonNegative;
import de.unisaarland.cs.st.reposuite.utils.specification.NotNull;

@Entity
public class JavaElementLocation<T extends JavaElement> implements Comparable<JavaElementLocation<T>>, Annotated {
	
	public static enum LineCover {
		DEFINITION, BODY, DEF_AND_BODY, FALSE
	};
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4858435624572738026L;
	
	private long              id;
	private int               startLine;
	private int               endLine;
	private int               position;
	private T                 element;
	private String            filePath         = "<unknown>";
	private int               bodyStartLine;
	private Set<Integer>      commentLines     = new HashSet<Integer>();
	
	@SuppressWarnings("unused")
	private JavaElementLocation() {
	}
	
	public JavaElementLocation(@NotNull final T element, @NonNegative final int startLine,
			@NonNegative final int endLine, @NonNegative final int position, final int bodyStartLine,
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
	
	@Transient
	public void addCommentLines(final int from, final int to){
		Condition.check(from <= to, "You must supply a closed interval.");
		for(int i = from; i <= to; ++i){
			this.commentLines .add(i);
		}
	}
	
	@Override
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
	
	public LineCover coversAllLines(final Collection<Integer> lines) {
		//TODO add test case
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
	
	public LineCover coversAnyLine(final Collection<Integer> lines) {
		//TODO add test case
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
	
	public LineCover coversLine(final int line) {
		//TODO add test case
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
	
	public int getBodyStartLine() {
		return this.bodyStartLine;
	}
	
	@ElementCollection
	public Set<Integer> getCommentLines() {
		return this.commentLines;
	}
	
	@Type(type = "de.unisaarland.cs.st.reposuite.ppa.model.JavaElement")
	@ManyToOne(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	public T getElement() {
		return this.element;
	}
	
	public int getEndLine() {
		return this.endLine;
	}
	
	public String getFilePath() {
		return this.filePath;
	}
	
	@Index(name = "idx_elemlocid")
	@Id
	@GeneratedValue
	public long getId() {
		return this.id;
	}
	
	public int getPosition() {
		return this.position;
	}
	
	
	public int getStartLine() {
		return this.startLine;
	}
	
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
	
	@Override
	public Collection<Annotated> saveFirst() {
		Set<Annotated> set = new HashSet<Annotated>();
		set.add(this.getElement());
		return set;
	}
	
	protected void setBodyStartLine(final int bodyStartLine) {
		this.bodyStartLine = bodyStartLine;
	}
	
	protected void setCommentLines(final Set<Integer> commentLines) {
		this.commentLines = commentLines;
	}
	
	@Deprecated
	public void setElement(final T element) {
		this.element = element;
	}
	
	private void setEndLine(final int endLine) {
		this.endLine = endLine;
	}
	
	private void setFilePath(final String filePath) {
		this.filePath = filePath;
	}
	
	@SuppressWarnings("unused")
	private void setId(final long id) {
		this.id = id;
	}
	
	private void setPosition(final int position) {
		this.position = position;
	}
	
	private void setStartLine(final int startLine) {
		this.startLine = startLine;
	}
	
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
