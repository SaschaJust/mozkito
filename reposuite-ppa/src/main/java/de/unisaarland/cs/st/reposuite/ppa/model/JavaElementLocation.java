package de.unisaarland.cs.st.reposuite.ppa.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.utils.specification.NonNegative;
import de.unisaarland.cs.st.reposuite.utils.specification.NotNull;

@Entity
public class JavaElementLocation<T extends JavaElement> implements Comparable<JavaElementLocation<T>>, Annotated {
	
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
	
	@SuppressWarnings("unused")
	private JavaElementLocation() {
	}
	
	public JavaElementLocation(@NotNull final T element, @NonNegative final int startLine,
			@NonNegative final int endLine, @NonNegative final int position, @NotNull final String filePath) {
		this.setElement(element);
		this.setStartLine(startLine);
		this.setEndLine(endLine);
		this.setPosition(position);
		this.setFilePath(filePath);
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
	
	public boolean coversAllLines(final Collection<Integer> lines) {
		for (int line : lines) {
			if (!((getStartLine() <= line) && (getEndLine() >= line))) {
				return false;
			}
		}
		return true;
	}
	
	public boolean coversAnyLine(final Collection<Integer> lines) {
		for (int line : lines) {
			if ((getStartLine() <= line) && (getEndLine() >= line)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean coversLine(final int line) {
		return ((getStartLine() <= line) && (getEndLine() >= line));
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
		if (this.endLine != other.endLine) {
			return false;
		}
		if (this.filePath == null) {
			if (other.filePath != null) {
				return false;
			}
		} else if (!this.filePath.equals(other.filePath)) {
			return false;
		}
		if (this.position != other.position) {
			return false;
		}
		if (this.startLine != other.startLine) {
			return false;
		}
		return true;
	}
	
	@Type(type = "de.unisaarland.cs.st.reposuite.ppa.model.JavaElement")
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
		filePathElement.setNodeValue(getFilePath());
		thisElement.appendChild(filePathElement);
		
		thisElement.appendChild(getElement().getXMLRepresentation(document));
		
		return thisElement;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.element == null) ? 0 : this.element.hashCode());
		result = prime * result + this.endLine;
		result = prime * result + ((this.filePath == null) ? 0 : this.filePath.hashCode());
		result = prime * result + this.position;
		result = prime * result + this.startLine;
		return result;
	}
	
	@Override
	public Collection<Annotated> saveFirst() {
		Set<Annotated> set = new HashSet<Annotated>();
		set.add(this.getElement());
		return set;
	}
	
	private void setElement(final T element) {
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
