/**
 * 
 */
package net.ownhero.dev.andama.chain;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaSource;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class IntegerSource extends AndamaSource<Integer> {
	
	/**
	 * @param threadGroup
	 * @param settings
	 * @param parallelizable
	 */
	public IntegerSource(final AndamaGroup threadGroup, final AndamaSettings settings, final boolean parallelizable) {
		super(threadGroup, settings, parallelizable);
		// TODO Auto-generated constructor stub
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.AndamaThreadable#afterExecution()
	 */
	@Override
	public void afterExecution() {
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.AndamaThreadable#afterProcess()
	 */
	@Override
	public void afterProcess() {
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.AndamaThreadable#beforeExecution()
	 */
	@Override
	public void beforeExecution() {
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.AndamaThreadable#beforeProcess()
	 */
	@Override
	public void beforeProcess() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Integer process() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
