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
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.mozkito.infozilla.elements.Attachable;
import org.mozkito.infozilla.model.attachment.Attachment;
import org.mozkito.persistence.Annotated;
import org.mozkito.utilities.commons.JavaUtils;
import org.mozkito.utilities.io.CompressionUtils;
import org.mozkito.utilities.io.FileUtils;
import org.mozkito.utilities.io.FileUtils.FileShutdownAction;
import org.mozkito.utilities.io.exceptions.FilePermissionException;

/**
 * The Class Archive.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
@Entity
public class Archive implements Attachable, Annotated {
	
	/**
	 * The Enum Type.
	 */
	public static enum Type {
		
		/** The BZI p2. */
		BZIP2,
		/** The gzip. */
		GZIP,
		/** The lzma. */
		LZMA,
		/** The tar. */
		TAR,
		/** The zip. */
		ZIP,
		/** The rar. */
		RAR;
	}
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7213529287337098350L;
	
	/** The entries. */
	private List<Attachment>  entries          = new LinkedList<>();
	
	/** The id. */
	private int               id;
	
	/** The origin. */
	private Attachment        origin;
	
	/** The type. */
	private Type              type;
	
	/** The target directory. */
	private File              targetDirectory;
	
	/**
	 * Instantiates a new archive.
	 * 
	 * @deprecated must only be used by JPA
	 */
	@Deprecated
	public Archive() {
		// stub
	}
	
	/**
	 * Instantiates a new archive.
	 * 
	 * @param origin
	 *            the origin
	 * @param type
	 *            the type
	 */
	public Archive(final Attachment origin, final Type type) {
		super();
		this.origin = origin;
		this.type = type;
	}
	
	/**
	 * Extract.
	 * 
	 * @return the file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws FilePermissionException
	 *             the file permission exception
	 */
	@Transient
	public synchronized File extractedDataDirectory() throws IOException {
		if (getTargetDirectory() == null) {
			setTargetDirectory(FileUtils.createRandomDir("infozilla_archive_", "_" + getOrigin().getFilename(),
			                                             FileShutdownAction.DELETE));
			CompressionUtils.decompress(getOrigin().getFile(), getTargetDirectory());
		}
		
		return this.targetDirectory;
		
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.persistence.Annotated#getClassName()
	 */
	@Override
	@Transient
	public String getClassName() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return JavaUtils.getHandle(this);
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the entries.
	 * 
	 * @return the entries
	 */
	@OneToMany
	public List<Attachment> getEntries() {
		return this.entries;
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
	 * Gets the origin.
	 * 
	 * @return the origin
	 */
	@ManyToOne (cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	public Attachment getOrigin() {
		return this.origin;
	}
	
	/**
	 * @return the targetDirectory
	 */
	@Transient
	private final File getTargetDirectory() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.targetDirectory;
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
		return this.type;
	}
	
	/**
	 * Sets the entries.
	 * 
	 * @param entries
	 *            the entries to set
	 */
	public void setEntries(final List<Attachment> entries) {
		this.entries = entries;
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
	 * Sets the origin.
	 * 
	 * @param origin
	 *            the origin to set
	 */
	public void setOrigin(final Attachment origin) {
		this.origin = origin;
	}
	
	/**
	 * @param targetDirectory
	 *            the targetDirectory to set
	 */
	private final void setTargetDirectory(final File targetDirectory) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.targetDirectory = targetDirectory;
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
		builder.append("Archive [type=");
		builder.append(getType());
		builder.append("] Entries: ").append(getEntries().size());
		for (final Attachment attachment : getEntries()) {
			builder.append(attachment);
		}
		return builder.toString();
	}
	
}
