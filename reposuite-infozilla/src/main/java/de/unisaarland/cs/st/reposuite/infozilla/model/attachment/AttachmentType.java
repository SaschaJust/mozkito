/**
 * 
 */
package de.unisaarland.cs.st.reposuite.infozilla.model.attachment;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public enum AttachmentType implements Annotated {
	ARCHIVE, IMAGE, LOG, PATCH, SOURCECODE, STACKTRACE, UNKNOWN;
}
