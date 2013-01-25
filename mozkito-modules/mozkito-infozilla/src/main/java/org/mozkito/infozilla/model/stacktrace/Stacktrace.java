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
package org.mozkito.infozilla.model.stacktrace;

import java.util.List;

import org.mozkito.infozilla.model.Attachable;
import org.mozkito.infozilla.model.Inlineable;
import org.mozkito.infozilla.model.attachment.Attachment;

/**
 * The Class Stacktrace.
 */
public abstract class Stacktrace implements Attachable, Inlineable {
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.infozilla.model.Attachable#getAttachment()
	 */
	@Override
	public Attachment getAttachment() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Gets the entries.
	 * 
	 * @return the entries
	 */
	public abstract List<? extends StacktraceEntry> getEntries();
	
}
