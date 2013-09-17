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
package org.mozkito.infozilla.model.image;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.mozkito.infozilla.elements.Attachable;
import org.mozkito.infozilla.model.attachment.Attachment;
import org.mozkito.persistence.Annotated;

/**
 * The Class Image.
 */
@Entity
public abstract class Image implements Attachable, Annotated {
	
	/**
	 * The Enum Type.
	 */
	public static enum Type {
		
		/** The bmp. */
		BMP,
		/** The jpg. */
		JPG,
		/** The png. */
		PNG;
	}
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8243454338111223835L;
	
	/** The data. */
	private byte[]            data;
	
	/** The id. */
	private int               id;
	
	/** The origin. */
	private Attachment        origin;
	
	/** The type. */
	private Type              type;
	
	/**
	 * Instantiates a new image.
	 * 
	 * @param id
	 *            the id
	 * @param origin
	 *            the origin
	 * @param type
	 *            the type
	 * @param data
	 *            the data
	 */
	public Image(final int id, final Attachment origin, final Type type, final byte[] data) {
		super();
		this.id = id;
		this.origin = origin;
		this.type = type;
		this.data = data;
	}
	
	/**
	 * Gets the data.
	 * 
	 * @return the data
	 */
	@Basic
	public byte[] getData() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.data;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Id
	@GeneratedValue (strategy = GenerationType.AUTO)
	public int getId() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.id;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the origin.
	 * 
	 * @return the origin
	 */
	@ManyToOne (cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	public Attachment getOrigin() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.origin;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	@Enumerated (EnumType.STRING)
	public Type getType() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.type;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Sets the data.
	 * 
	 * @param data
	 *            the data to set
	 */
	public void setData(final byte[] data) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.data = data;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the id to set
	 */
	public void setId(final int id) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.id = id;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Sets the origin.
	 * 
	 * @param origin
	 *            the origin to set
	 */
	public void setOrigin(final Attachment origin) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.origin = origin;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Sets the type.
	 * 
	 * @param type
	 *            the type to set
	 */
	public void setType(final Type type) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.type = type;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
}
