/*******************************************************************************
 * Copyright (c) 2011 Kim Herzig, Sascha Just.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Kim Herzig, Sascha Just - initial API and implementation
 ******************************************************************************/
/**
 * 
 */
package de.unisaarland.cs.st.reposuite.toolchain;

import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;

/**
 * {@link RepoSuiteSourceThread}s are the source elements of a tool chain. In
 * general, these are I/O handlers that read data from some source and provide
 * it to the tool chain. All instances of {@link RepoSuiteSourceThread}s must
 * have an output connector but must not have an input connector.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepoSuiteSourceThread<T> extends RepoSuiteThread<T, T> {
	
	/**
	 * @see RepoSuiteThread
	 * @param threadGroup
	 * @param name
	 * @param settings
	 */
	public RepoSuiteSourceThread(final RepoSuiteThreadGroup threadGroup, final String name,
	        final RepoSuiteSettings settings) {
		super(threadGroup, name, settings);
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread#hasInputConnector()
	 */
	@Override
	public boolean hasInputConnector() {
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread#hasOutputConnector
	 * ()
	 */
	@Override
	public boolean hasOutputConnector() {
		return true;
	}
	
}
