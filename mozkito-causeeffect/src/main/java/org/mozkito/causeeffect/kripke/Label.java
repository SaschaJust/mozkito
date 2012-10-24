/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
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

package org.mozkito.causeeffect.kripke;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.mozkito.versions.model.RCSFile;


/**
 * Instances of this class represent labels used by Kripke structures to label the states.
 * 
 * @author Andrzej Wasylkowski
 */
public class Label {
	
	/** Mapping (content => label that is associated with that content). */
	private static Map<String, Label> content2label = new HashMap<String, Label>();
	
	/**
	 * Returns a label that is associated with the given content. This method ensures that at any point in time there
	 * exists at most one label object associated with any given content.
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
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder(3, 37).append(this.content).toHashCode();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Label: " + this.content;
	}
}
