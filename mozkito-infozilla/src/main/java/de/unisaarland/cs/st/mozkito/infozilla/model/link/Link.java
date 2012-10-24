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
package de.unisaarland.cs.st.mozkito.infozilla.model.link;

import java.net.URL;

import org.joda.time.DateTime;

import de.unisaarland.cs.st.mozkito.infozilla.model.Attachable;
import de.unisaarland.cs.st.mozkito.infozilla.model.Inlineable;
import de.unisaarland.cs.st.mozkito.infozilla.model.attachment.Attachment;
import de.unisaarland.cs.st.mozkito.issues.tracker.model.TextElement;
import de.unisaarland.cs.st.mozkito.persistence.model.Person;

/**
 * The Class Link.
 */
public class Link implements Attachable, Inlineable {
	
	/**
	 * The Enum Kind.
	 */
	private enum Kind {
		
		/** The WEB. */
		WEB, 
 /** The REPOSITORY. */
 REPOSITORY, 
 /** The TRACKER. */
 TRACKER;
	}
	
	/** The url. */
	private URL           url;
	
	/** The kind. */
	private final Kind    kind  = Kind.WEB;
	
	/** The string representation. */
	private String        stringRepresentation;
	
	/** The valid. */
	private final boolean valid = false;
	
	/** The posted by. */
	private Person        postedBy;
	
	/** The posted on. */
	private DateTime      postedOn;
	
	/** The posted in. */
	private TextElement   postedIn;
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.mozkito.infozilla.model.Attachable#getAttachment()
	 */
	@Override
	public Attachment getAttachment() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.mozkito.infozilla.model.Inlineable#getEndPosition()
	 */
	@Override
	public int getEndPosition() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/**
	 * Gets the kind.
	 *
	 * @return the kind
	 */
	public Kind getKind() {
		return this.kind;
	}
	
	/**
	 * Gets the posted by.
	 *
	 * @return the postedBy
	 */
	public Person getPostedBy() {
		return this.postedBy;
	}
	
	/**
	 * Gets the posted in.
	 *
	 * @return the postedIn
	 */
	public TextElement getPostedIn() {
		return this.postedIn;
	}
	
	/**
	 * Gets the posted on.
	 *
	 * @return the postedOn
	 */
	public DateTime getPostedOn() {
		return this.postedOn;
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.mozkito.infozilla.model.Inlineable#getStartPosition()
	 */
	@Override
	public int getStartPosition() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/**
	 * Gets the string representation.
	 *
	 * @return the stringRepresentation
	 */
	public String getStringRepresentation() {
		return this.stringRepresentation;
	}
	
	/**
	 * Gets the url.
	 *
	 * @return the url
	 */
	public URL getUrl() {
		return this.url;
	}
	
	/**
	 * Checks if is valid.
	 *
	 * @return the valid
	 */
	public boolean isValid() {
		return this.valid;
	}
	
	/**
	 * Sets the posted by.
	 *
	 * @param postedBy the postedBy to set
	 */
	public void setPostedBy(final Person postedBy) {
		this.postedBy = postedBy;
	}
	
	/**
	 * Sets the posted in.
	 *
	 * @param postedIn the postedIn to set
	 */
	public void setPostedIn(final TextElement postedIn) {
		this.postedIn = postedIn;
	}
	
	/**
	 * Sets the posted on.
	 *
	 * @param postedOn the postedOn to set
	 */
	public void setPostedOn(final DateTime postedOn) {
		this.postedOn = postedOn;
	}
	
	/**
	 * Sets the string representation.
	 *
	 * @param stringRepresentation the stringRepresentation to set
	 */
	public void setStringRepresentation(final String stringRepresentation) {
		this.stringRepresentation = stringRepresentation;
	}
	
	/**
	 * Sets the url.
	 *
	 * @param url the url to set
	 */
	public void setUrl(final URL url) {
		this.url = url;
	}
}
