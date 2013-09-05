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
package org.mozkito.mappings.splitters;

import java.util.List;

import org.mozkito.mappings.messages.Messages;
import org.mozkito.mappings.register.Node;
import org.mozkito.persistence.Persistent;
import org.mozkito.persistence.PersistenceUtil;

/**
 * The Class MappingSplitter.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public abstract class Splitter extends Node {
	
	/** The Constant DESCRIPTION. */
	public static final String DESCRIPTION = Messages.getString("Splitter.description"); //$NON-NLS-1$
	                                                                                     
	/** The Constant TAG. */
	public static final String TAG         = "splitters";                               //$NON-NLS-1$
	                                                                                     
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.register.Registered#getDescription ()
	 */
	@Override
	public abstract String getDescription();
	
	/**
	 * Process.
	 * 
	 * @param util
	 *            the util
	 * @return the list
	 */
	public abstract List<Persistent> process(PersistenceUtil util);
}
