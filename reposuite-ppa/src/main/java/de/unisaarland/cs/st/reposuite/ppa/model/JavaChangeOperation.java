package de.unisaarland.cs.st.reposuite.ppa.model;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.utils.specification.NonNegative;
import de.unisaarland.cs.st.reposuite.utils.specification.NoneNull;

/**
 * The Class JavaChangeOperation.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
@Entity
public class JavaChangeOperation implements Annotated {
	
	/** The Constant serialVersionUID. */
	private static final long       serialVersionUID = 8988140924725401608L;
	
	/** The id. */
	private long                    id;
	
	/** The change type. */
	private JavaChangeOperationType changeType;
	
	/** The changed element. */
	private JavaElement             changedElement;
	
	/** The changed line. */
	private int                     changedLine;
	
	@SuppressWarnings("unused")
	private JavaChangeOperation() {
	}
	
	/**
	 * Instantiates a new java change operation.
	 * 
	 * @param type
	 *            the type
	 * @param element
	 *            the element
	 * @param line
	 *            the line
	 */
	@NoneNull
	public JavaChangeOperation(final JavaChangeOperationType type, final JavaElement element,
			@NonNegative final int line) {
		setChangeType(type);
		setChangedElement(element);
		setChangedLine(line);
	}
	
	/**
	 * Gets the changed element.
	 * 
	 * @return the changed element
	 */
	public JavaElement getChangedElement() {
		return changedElement;
	}
	
	/**
	 * Gets the changed file.
	 * 
	 * @return the changed file
	 */
	@Transient
	public String getChangedFile(){
		return changedElement.getFilePath();
	}
	
	
	/**
	 * Gets the changed line.
	 * 
	 * @return the changed line
	 */
	public int getChangedLine() {
		return changedLine;
	}
	
	/**
	 * Gets the change type.
	 * 
	 * @return the change type
	 */
	public JavaChangeOperationType getChangeType() {
		return changeType;
	}
	
	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Id
	@GeneratedValue (strategy = GenerationType.SEQUENCE)
	public long getId() {
		return id;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unisaarland.cs.st.reposuite.persistence.Annotated#saveFirst()
	 */
	@Override
	public Collection<Annotated> saveFirst() {
		HashSet<Annotated> set = new HashSet<Annotated>();
		set.add(getChangedElement());
		return set;
	}
	
	/**
	 * Sets the changed element.
	 * 
	 * @param changedElement
	 *            the new changed element
	 */
	private void setChangedElement(final JavaElement changedElement) {
		this.changedElement = changedElement;
	}
	
	/**
	 * Sets the changed line.
	 * 
	 * @param changedLine
	 *            the new changed line
	 */
	private void setChangedLine(final int changedLine) {
		this.changedLine = changedLine;
	}
	
	/**
	 * Sets the change type.
	 * 
	 * @param changeType
	 *            the new change type
	 */
	private void setChangeType(final JavaChangeOperationType changeType) {
		this.changeType = changeType;
	}
	
	private void setId(final long id) {
		this.id = id;
	}
	
}
