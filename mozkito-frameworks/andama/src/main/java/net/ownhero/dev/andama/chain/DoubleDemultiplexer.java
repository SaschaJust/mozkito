/**
 * 
 */
package net.ownhero.dev.andama.chain;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaDemultiplexer;
import net.ownhero.dev.andama.threads.AndamaGroup;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class DoubleDemultiplexer extends AndamaDemultiplexer<Double> {
	
	/**
	 * @param threadGroup
	 * @param settings
	 * @param parallelizable
	 */
	public DoubleDemultiplexer(final AndamaGroup threadGroup, final AndamaSettings settings,
	        final boolean parallelizable) {
		super(threadGroup, settings, parallelizable);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.AndamaThreadable#afterExecution()
	 */
	@Override
	public void afterExecution() {
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.AndamaThreadable#afterProcess()
	 */
	@Override
	public void afterProcess() {
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.AndamaThreadable#beforeExecution()
	 */
	@Override
	public void beforeExecution() {
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.AndamaThreadable#beforeProcess()
	 */
	@Override
	public void beforeProcess() {
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * net.ownhero.dev.andama.threads.AndamaThreadable#process(java.lang.Object)
	 */
	@Override
	public Double process(final Double data) {
		return null;
	}
	
}
