/**
 * 
 */
package de.unisaarland.cs.st.reposuite.infozilla.model;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public enum AttachmentType implements Annotated {
	ARCHIVE, IMAGE, LOG, PATCH, STACKTRACE, UNKNOWN;
}
