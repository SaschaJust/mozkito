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
package org.mozkito.infozilla.model.attachment;

import net.ownhero.dev.ioda.JavaUtils;

import org.mozkito.persistence.Annotated;

/**
 * The Enum AttachmentType.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public enum AttachmentType implements Annotated {
	/** The ARCHIVE. */
	ARCHIVE,
	/** The IMAGE. */
	IMAGE,
	/** The LOG. */
	LOG,
	/** The PATCH. */
	PATCH,
	/** The SOURCECODE. */
	SOURCECODE,
	/** The STACKTRACE. */
	STACKTRACE,
	/** The UNKNOWN. */
	UNKNOWN;
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.persistence.Annotated#getHandle()
	 */
	@Override
	public final String getClassName() {
		return JavaUtils.getHandle(AttachmentType.class);
	}
}
