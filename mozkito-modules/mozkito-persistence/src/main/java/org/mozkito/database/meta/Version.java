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

package org.mozkito.database.meta;

import org.mozkito.database.Entity;

/**
 * The Class Version.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 *         e.g. 1.12.0123847-alpha 1.12.0123847-r2 1.12.0123847-SNAPSHOT
 */
public class Version implements Entity {
	
	/**
	 * The Enum RELEASE_LEVEL.
	 */
	public static enum RELEASE_LEVEL {
		
		/** The alpha. */
		ALPHA,
		/** The beta. */
		BETA,
		/** The snapshot. */
		SNAPSHOT,
		/** The release. */
		RELEASE;
	}
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2988984884406861856L;
	
	/** The major. */
	private short             major;
	
	/** The minor. */
	private short             minor;
	
	/** The revision. */
	private short             revision;
	
	/** The release level. */
	private RELEASE_LEVEL     releaseLevel;
	
	/** The release. */
	private short             release;
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.Entity#getClassName()
	 */
	public String getClassName() {
		return getClass().getSimpleName();
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.Artifact#getId()
	 */
	@Override
	public Object getId() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// TODO Auto-generated method stub
			// return null;
			throw new RuntimeException("Method 'getId' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the major.
	 * 
	 * @return the major
	 */
	public short getMajor() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.major;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the minor.
	 * 
	 * @return the minor
	 */
	public short getMinor() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.minor;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the release.
	 * 
	 * @return the release
	 */
	public short getRelease() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.release;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the release level.
	 * 
	 * @return the releaseLevel
	 */
	public RELEASE_LEVEL getReleaseLevel() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.releaseLevel;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the revision.
	 * 
	 * @return the revision
	 */
	public short getRevision() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.revision;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Sets the major.
	 * 
	 * @param major
	 *            the major to set
	 */
	public void setMajor(final short major) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.major = major;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Sets the minor.
	 * 
	 * @param minor
	 *            the minor to set
	 */
	public void setMinor(final short minor) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.minor = minor;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Sets the release.
	 * 
	 * @param release
	 *            the release to set
	 */
	public void setRelease(final short release) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.release = release;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Sets the release level.
	 * 
	 * @param releaseLevel
	 *            the releaseLevel to set
	 */
	public void setReleaseLevel(final RELEASE_LEVEL releaseLevel) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.releaseLevel = releaseLevel;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Sets the revision.
	 * 
	 * @param revision
	 *            the revision to set
	 */
	public void setRevision(final short revision) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.revision = revision;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
}
