/**
 * 
 */
package de.unisaarland.cs.st.reposuite.infozilla.model.archive;

import java.io.File;
import java.io.IOException;

import de.unisaarland.cs.st.reposuite.infozilla.model.Attachable;
import de.unisaarland.cs.st.reposuite.infozilla.model.attachment.Attachment;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public abstract class Archive implements Attachable {
	
	private Attachment attachment;
	
	/**
	 * 
	 */
	public Archive(final Attachment attachment) {
		setAttachment(attachment);
	}
	
	public abstract File extract() throws IOException;
	
	/**
	 * @return the attachment
	 */
	public Attachment getAttachment() {
		return this.attachment;
	}
	
	/**
	 * @param attachment the attachment to set
	 */
	public void setAttachment(final Attachment attachment) {
		this.attachment = attachment;
	}
	
}
