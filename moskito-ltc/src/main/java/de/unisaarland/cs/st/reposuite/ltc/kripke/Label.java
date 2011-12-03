package de.unisaarland.cs.st.reposuite.ltc.kripke;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import de.unisaarland.cs.st.moskito.rcs.model.RCSFile;

/**
 * Instances of this class represent labels used by Kripke structures to label
 * the states.
 * 
 * @author Andrzej Wasylkowski
 */
public class Label {
	
	/** Mapping (content => label that is associated with that content). */
	private static Map<String, Label> content2label = new HashMap<String, Label>();
	
	/**
	 * Returns a label that is associated with the given content. This method
	 * ensures that at any point in time there exists at most one label object
	 * associated with any given content.
	 * 
	 * @param content
	 *            Content to be associated with the label.
	 * @return Label associated with the given content.
	 */
	public static Label getLabel(RCSFile content) {
		if (!content2label.containsKey(content.toString())) {
			content2label.put(content.toString(), new Label(content));
		}
		return content2label.get(content.toString());
	}
	
	/** Content of this label. Different labels have different contents. */
	private Long content;
	
	/**
	 * Creates a new label and associates it with the given content.
	 * 
	 * @param event
	 *            Content to attach to the label created.
	 */
	private Label(RCSFile content) {
		this.content = content.getGeneratedId();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o instanceof Label) {
			Label other = (Label) o;
			return new EqualsBuilder().append(this.content, other.content).isEquals();
		}
		return false;
	}
	
	/**
	 * Returns the content of this label.
	 * 
	 * @return Content of this label.
	 */
	public Long getContent() {
		return this.content;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder(3, 37).append(this.content).toHashCode();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Label: " + this.content;
	}
}
