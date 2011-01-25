package de.unisaarland.cs.st.reposuite.ppa.model;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;

/**
 * The Enum JavaChangeOperationType.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public enum JavaChangeOperationType implements Annotated {
	
	/** The ADD. */
	ADD,
	/** The CHANGE. */
	CHANGE,
	/** The DELETE. */
	DELETE;
	
	/** The id. */
	private long id;
	
	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
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
		return new ArrayList<Annotated>(0);
	}
}
