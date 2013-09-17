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
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.mozkito.infozilla.elements.Attachable;
import org.mozkito.infozilla.model.attachment.Attachment;
import org.mozkito.persistence.Annotated;
import org.mozkito.utilities.commons.JavaUtils;
import org.mozkito.utilities.io.FileUtils;
import org.mozkito.utilities.io.FileUtils.FileShutdownAction;
import org.mozkito.utilities.io.exceptions.FilePermissionException;

/**
 * The Class Archive.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
@Entity
public abstract class Archive implements Attachable, Annotated {
	
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
		ZIP;
	}
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7213529287337098350L;
	
	/** The entries. */
	private List<String>      entries          = new LinkedList<>();
	
	/** The id. */
	private int               id;
	
	/** The origin. */
	private Attachment        origin;
	
	private Type              type;
	
	/**
	 * Instantiates a new archive.
	 * 
	 * @param origin
	 *            the origin
	 */
	public Archive(final Attachment origin) {
		super();
		this.origin = origin;
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
	public File extract() throws IOException, FilePermissionException {
		switch (getType()) {
			case BZIP2:
				return extractBzip2();
			case GZIP:
				return extractGzip();
			case LZMA:
				return extractLzma();
			case TAR:
				return extractTar();
			case ZIP:
				return extractZip();
			default:
				throw new IOException("Unsupported archive type: " + getType().name());
		}
	}
	
	/**
	 * @return
	 */
	private File extractBzip2() throws IOException, FilePermissionException {
		PRECONDITIONS: {
			// none
		}
		
		try {
			throw new UnsupportedOperationException("not yet implemented");
			// final File file = FileUtils.createRandomFile(FileShutdownAction.DELETE);
			// FileUtils.dump(getOrigin().getData(), file);
			// final File dir = FileUtils.createRandomDir("test", "bleh", FileShutdownAction.DELETE);
			// FileUtils.bunzip2(file, dir);
			// return dir;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * @return
	 */
	private File extractGzip() throws IOException, FilePermissionException {
		PRECONDITIONS: {
			// none
		}
		
		try {
			final File file = FileUtils.createRandomFile(FileShutdownAction.DELETE);
			FileUtils.dump(getOrigin().getData(), file);
			final File dir = FileUtils.createRandomDir("test", "bleh", FileShutdownAction.DELETE);
			FileUtils.gunzip(file, dir);
			return dir;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * @return
	 */
	private File extractLzma() throws IOException, FilePermissionException {
		PRECONDITIONS: {
			// none
		}
		
		try {
			final File file = FileUtils.createRandomFile(FileShutdownAction.DELETE);
			FileUtils.dump(getOrigin().getData(), file);
			final File dir = FileUtils.createRandomDir("test", "bleh", FileShutdownAction.DELETE);
			FileUtils.unlzma(file, dir);
			return dir;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * @return
	 */
	private File extractTar() throws IOException, FilePermissionException {
		PRECONDITIONS: {
			// none
		}
		
		try {
			final File file = FileUtils.createRandomFile(FileShutdownAction.DELETE);
			FileUtils.dump(getOrigin().getData(), file);
			final File dir = FileUtils.createRandomDir("test", "bleh", FileShutdownAction.DELETE);
			FileUtils.untar(file, dir);
			return dir;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * @return
	 */
	private File extractZip() throws IOException, FilePermissionException {
		PRECONDITIONS: {
			// none
		}
		
		try {
			final File file = FileUtils.createRandomFile(FileShutdownAction.DELETE);
			FileUtils.dump(getOrigin().getData(), file);
			final File dir = FileUtils.createRandomDir("test", "bleh", FileShutdownAction.DELETE);
			FileUtils.unzip(file, dir);
			return dir;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
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
	 * Gets the date.
	 * 
	 * @return the date
	 */
	@Transient
	public byte[] getData() {
		return getOrigin().getData();
	}
	
	/**
	 * Gets the entries.
	 * 
	 * @return the entries
	 */
	@ElementCollection
	public List<String> getEntries() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.entries;
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
	 * Sets the entries.
	 * 
	 * @param entries
	 *            the entries to set
	 */
	public void setEntries(final List<String> entries) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.entries = entries;
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
