/**
 * 
 */
package net.ownhero.dev.andama.threads;

import net.ownhero.dev.andama.settings.AndamaSettings;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public abstract class AndamaMultiplexer<K> extends AndamaThread<K, K> {
	
	/**
	 * @param threadGroup
	 * @param name
	 * @param settings
	 */
	public AndamaMultiplexer(final AndamaGroup threadGroup, final AndamaSettings settings, final boolean parallelizable) {
		super(threadGroup, settings, parallelizable);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.AndamaGeneralThread#hasInputConnector()
	 */
	@Override
	public final boolean hasInputConnector() {
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.AndamaGeneralThread#hasOutputConnector()
	 */
	@Override
	public final boolean hasOutputConnector() {
		return true;
	}
	
}
