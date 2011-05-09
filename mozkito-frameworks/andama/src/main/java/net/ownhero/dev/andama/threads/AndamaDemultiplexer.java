/**
 * 
 */
package net.ownhero.dev.andama.threads;

import net.ownhero.dev.andama.settings.AndamaSettings;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public abstract class AndamaDemultiplexer<K> extends AndamaThread<K, K> {
	
	/**
	 * 
	 */
	public AndamaDemultiplexer(final AndamaGroup threadGroup, final AndamaSettings settings,
	        final boolean parallelizable) {
		super(threadGroup, settings, parallelizable);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.AndamaGeneralThread#hasInputConnector()
	 */
	@Override
	public boolean hasInputConnector() {
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.AndamaGeneralThread#hasOutputConnector()
	 */
	@Override
	public boolean hasOutputConnector() {
		return true;
	}
	
}
