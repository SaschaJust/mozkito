/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.infozilla.model.link;

import java.net.URL;

import org.joda.time.DateTime;

import de.unisaarland.cs.st.reposuite.bugs.tracker.model.TextElement;
import de.unisaarland.cs.st.reposuite.infozilla.model.Attachable;
import de.unisaarland.cs.st.reposuite.infozilla.model.Inlineable;
import de.unisaarland.cs.st.reposuite.infozilla.model.attachment.Attachment;
import de.unisaarland.cs.st.reposuite.persistence.model.Person;

public class Link implements Attachable, Inlineable {
	
	private enum Kind {
		WEB, REPOSITORY, TRACKER;
	}
	
	private URL           url;
	
	private final Kind    kind  = Kind.WEB;
	
	private String        stringRepresentation;
	
	private final boolean valid = false;
	
	private Person        postedBy;
	
	private DateTime      postedOn;
	
	private TextElement   postedIn;
	
	@Override
	public Attachment getAttachment() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int getEndPosition() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/**
	 * @return the kind
	 */
	public Kind getKind() {
		return this.kind;
	}
	
	/**
	 * @return the postedBy
	 */
	public Person getPostedBy() {
		return this.postedBy;
	}
	
	/**
	 * @return the postedIn
	 */
	public TextElement getPostedIn() {
		return this.postedIn;
	}
	
	/**
	 * @return the postedOn
	 */
	public DateTime getPostedOn() {
		return this.postedOn;
	}
	
	@Override
	public int getStartPosition() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/**
	 * @return the stringRepresentation
	 */
	public String getStringRepresentation() {
		return this.stringRepresentation;
	}
	
	/**
	 * @return the url
	 */
	public URL getUrl() {
		return this.url;
	}
	
	/**
	 * @return the valid
	 */
	public boolean isValid() {
		return this.valid;
	}
	
	/**
	 * @param postedBy the postedBy to set
	 */
	public void setPostedBy(final Person postedBy) {
		this.postedBy = postedBy;
	}
	
	/**
	 * @param postedIn the postedIn to set
	 */
	public void setPostedIn(final TextElement postedIn) {
		this.postedIn = postedIn;
	}
	
	/**
	 * @param postedOn the postedOn to set
	 */
	public void setPostedOn(final DateTime postedOn) {
		this.postedOn = postedOn;
	}
	
	/**
	 * @param stringRepresentation the stringRepresentation to set
	 */
	public void setStringRepresentation(final String stringRepresentation) {
		this.stringRepresentation = stringRepresentation;
	}
	
	/**
	 * @param url the url to set
	 */
	public void setUrl(final URL url) {
		this.url = url;
	}
}
