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
/**
 * 
 */
package org.mozkito.infozilla.model.archive;

import java.io.File;
import java.io.IOException;
import java.util.List;

import net.ownhero.dev.ioda.exceptions.FilePermissionException;

import org.mozkito.infozilla.model.Attachable;
import org.mozkito.infozilla.model.attachment.Attachment;

/**
 * The Class Archive.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public abstract class Archive implements Attachable {
	
	/** The attachables. */
	private List<Attachable> attachables;
	
	/** The attachment. */
	private Attachment       attachment;
	
	/**
	 * Instantiates a new archive.
	 * 
	 * @param attachment
	 *            the attachment
	 */
	public Archive(final Attachment attachment) {
		setAttachment(attachment);
	}
	
	/**
	 * Extract.
	 *
	 * @return the file
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws FilePermissionException the file permission exception
	 */
	public abstract File extract() throws IOException, FilePermissionException;
	
	/**
	 * Gets the attachables.
	 * 
	 * @return the attachables
	 */
	public List<Attachable> getAttachables() {
		return this.attachables;
	}
	
	/**
	 * Gets the attachment.
	 * 
	 * @return the attachment
	 */
	@Override
	public Attachment getAttachment() {
		return this.attachment;
	}
	
	/**
	 * Gets the date.
	 * 
	 * @return the date
	 */
	public byte[] getDate() {
		return getAttachment().getData();
	}
	
	/**
	 * Sets the attachables.
	 * 
	 * @param attachables
	 *            the attachables to set
	 */
	public void setAttachables(final List<Attachable> attachables) {
		this.attachables = attachables;
	}
	
	/**
	 * Sets the attachment.
	 * 
	 * @param attachment
	 *            the attachment to set
	 */
	public void setAttachment(final Attachment attachment) {
		this.attachment = attachment;
	}
}
