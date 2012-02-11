/**
 * 
 */
package net.ownhero.dev.andama.threads;

import net.ownhero.dev.andama.settings.AndamaSettings;

/**
 * {@link AndamaSink}s are the end points of a tool chain. In general, they provide a connection to a database back-end
 * (e.g. {@link RepositoryPersister} ). There can also be void sinks in case you don't want to store anything, e.g. if
 * you just want to do some analysis. {@link RepositoryVoidSink} is an example for this. All instances of
 * {@link AndamaSink} must have an input connector, but must not have an output connector.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public abstract class AndamaSink<T> extends AndamaThread<T, T> {
	
	/**
	 * @see AndamaThread
	 * @param threadGroup
	 * @param name
	 * @param settings
	 */
	public AndamaSink(final AndamaGroup threadGroup, final AndamaSettings settings, final boolean parallelizable) {
		super(threadGroup, settings, parallelizable);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.AndamaThreadable#getBaseType()
	 */
	@SuppressWarnings ("unchecked")
	@Override
	public final Class<? extends AndamaThread<T, T>> getBaseType() {
		return (Class<? extends AndamaThread<T, T>>) AndamaSink.class;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread#hasInputConnector()
	 */
	@Override
	public final boolean hasInputConnector() {
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread#hasOutputConnector ()
	 */
	@Override
	public final boolean hasOutputConnector() {
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.AndamaThread#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		
		builder.append('[').append(getThreadGroup().getName()).append("] ");
		
		builder.append(getHandle());
		
		if ((this.getClass().getSuperclass() != null)
		        && AndamaThread.class.isAssignableFrom(this.getClass().getSuperclass())) {
			builder.append(' ').append(this.getClass().getSuperclass().getSimpleName());
		}
		
		builder.append(' ');
		builder.append(getTypeName(getInputClassType())).append(':');
		builder.append(' ');
		
		if (isParallelizable()) {
			builder.append("(parallelizable)");
		}
		
		return builder.toString();
	}
}
