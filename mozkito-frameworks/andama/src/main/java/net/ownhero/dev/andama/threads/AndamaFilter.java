/**
 * 
 */
package net.ownhero.dev.andama.threads;

import net.ownhero.dev.andama.settings.AndamaSettings;

/**
 * {@link AndamaFilter}s can be used for two things:
 * <ol>
 * <li>filtering elements out in a tool chain</li>
 * <li>analyzing elements in a tool chain (equals null filtering)</li>
 * </ol>
 * Implementations of {@link AndamaFilter}s must have as well input as
 * output connectors.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public abstract class AndamaFilter<T> extends AndamaThread<T, T> {
	
	/**
	 * @param threadGroup
	 * @param name
	 * @param settings
	 */
	public AndamaFilter(final AndamaGroup threadGroup, final AndamaSettings settings, final boolean parallelizable) {
		super(threadGroup, settings, parallelizable);
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
