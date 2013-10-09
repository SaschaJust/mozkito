/***********************************************************************************************************************
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
 **********************************************************************************************************************/

package org.mozkito.infozilla.model.patch;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.mozkito.persistence.Annotated;
import org.mozkito.utilities.commons.JavaUtils;

/**
 * The Class PatchTextElement.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
@Entity
public class PatchTextElement implements Annotated {
	
	/**
	 * The Enum Type.
	 */
	public static enum Type {
		
		/** The added. */
		ADDED,
		/** The context. */
		CONTEXT,
		/** The removed. */
		REMOVED;
		
		/**
		 * {@inheritDoc}
		 * 
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			switch (this) {
				case ADDED:
					return "+";
				case CONTEXT:
					return NBSP + "";
				case REMOVED:
					return "-";
				default:
					assert false;
			}
			return null;
		}
	}
	
	/** The Constant non-breakable space. */
	public static final char  NBSP             = 160;
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2187090612470054309L;
	
	/** The id. */
	private int               id;
	
	/** The text. */
	private String            text;
	
	/** The type. */
	private Type              type;
	
	/**
	 * Instantiates a new patch text element.
	 * 
	 * @deprecated must only be used by JPA
	 */
	@Deprecated
	public PatchTextElement() {
		// stub
	}
	
	/**
	 * Instantiates a new patch text element.
	 * 
	 * @param type
	 *            the type
	 * @param text
	 *            the text
	 */
	public PatchTextElement(final Type type, final String text) {
		super();
		
		PRECONDITIONS: {
			if (type == null) {
				throw new NullPointerException();
			}
			if (text == null) {
				throw new NullPointerException();
			}
		}
		
		this.type = type;
		this.text = text;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.persistence.Annotated#getClassName()
	 */
	@Override
	@Transient
	public String getClassName() {
		return JavaUtils.getHandle(this);
	}
	
	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Id
	@GeneratedValue (strategy = GenerationType.AUTO)
	public int getId() {
		return this.id;
	}
	
	/**
	 * Gets the text.
	 * 
	 * @return the text
	 */
	@Basic
	public String getText() {
		return this.text;
	}
	
	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	@Enumerated (EnumType.STRING)
	public Type getType() {
		return this.type;
		
	}
	
	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the id to set
	 */
	public void setId(final int id) {
		this.id = id;
	}
	
	/**
	 * Sets the text.
	 * 
	 * @param text
	 *            the text to set
	 */
	public void setText(final String text) {
		
		this.text = text;
		
	}
	
	/**
	 * Sets the type.
	 * 
	 * @param type
	 *            the type to set
	 */
	public void setType(final Type type) {
		this.type = type;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		
		builder.append(getType());
		builder.append(getText());
		
		return builder.toString();
	}
}
