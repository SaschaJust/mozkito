/**
 * 
 */
package de.unisaarland.cs.st.reposuite.infozilla.model.archive;

import java.io.File;
import java.io.IOException;
import java.util.List;

import de.unisaarland.cs.st.reposuite.infozilla.model.Attachable;
import de.unisaarland.cs.st.reposuite.infozilla.model.attachment.Attachment;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public abstract class Archive implements Attachable {
	
	private List<Attachable> attachables;
	
	private Attachment       attachment;
	
	/**
	 * 
	 */
	public Archive(final Attachment attachment) {
		setAttachment(attachment);
	}
	
	public abstract File extract() throws IOException;
	
	/**
	 * @return the attachables
	 */
	public List<Attachable> getAttachables() {
		return this.attachables;
	}
	
	/**
	 * @return the attachment
	 */
	public Attachment getAttachment() {
		return this.attachment;
	}
	
	/**
	 * @return
	 */
	public byte[] getDate() {
		return getAttachment().getData();
	}
	
	/**
	 * @param attachables the attachables to set
	 */
	public void setAttachables(final List<Attachable> attachables) {
		this.attachables = attachables;
	}
	
	/**
	 * @param attachment the attachment to set
	 */
	public void setAttachment(final Attachment attachment) {
		this.attachment = attachment;
	}
}
