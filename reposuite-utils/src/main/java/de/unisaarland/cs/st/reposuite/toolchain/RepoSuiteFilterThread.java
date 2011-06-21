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
 * {@link RepoSuiteFilterThread}s can be used for two things:
 * <ol>
 * <li>filtering elements out in a tool chain</li>
 * <li>analyzing elements in a tool chain (equals null filtering)</li>
 * </ol>
 * Implementations of {@link RepoSuiteFilterThread}s must have as well input as
 * output connectors.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepoSuiteFilterThread<T> extends RepoSuiteThread<T, T> {
	
	/**
	 * @param threadGroup
	 * @param name
	 * @param settings
	 */
	public RepoSuiteFilterThread(final RepoSuiteThreadGroup threadGroup, final String name,
	        final RepoSuiteSettings settings) {
		super(threadGroup, name, settings);
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread#hasInputConnector()
	 */
	@Override
	public final boolean hasInputConnector() {
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread#hasOutputConnector
	 * ()
	 */
	@Override
	public final boolean hasOutputConnector() {
		return true;
	}
}
