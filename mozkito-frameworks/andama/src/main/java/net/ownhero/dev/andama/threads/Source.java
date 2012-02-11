/**
 * 
 */
package net.ownhero.dev.andama.threads;

import net.ownhero.dev.andama.settings.Settings;

/**
 * {@link Source}s are the source elements of a tool chain. In general, these are I/O handlers that read data from
 * some source and provide it to the tool chain. All instances of {@link Source}s must have an output connector
 * but must not have an input connector.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public abstract class Source<T> extends Node<T, T> {
	
	/**
	 * @see Node
	 * @param threadGroup
	 * @param name
	 * @param settings
	 */
	public Source(final Group threadGroup, final Settings settings, final boolean parallelizable) {
		super(threadGroup, settings, parallelizable);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.AndamaThreadable#getBaseType()
	 */
	@SuppressWarnings ("unchecked")
	@Override
	public final Class<? extends Node<T, T>> getBaseType() {
		return (Class<? extends Node<T, T>>) Source.class;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread#hasInputConnector()
	 */
	@Override
	public final boolean hasInputConnector() {
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread#hasOutputConnector ()
	 */
	@Override
	public final boolean hasOutputConnector() {
		return true;
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
		        && Node.class.isAssignableFrom(this.getClass().getSuperclass())) {
			builder.append(' ').append(this.getClass().getSuperclass().getSimpleName());
		}
		
		builder.append(' ');
		builder.append(':').append(getTypeName(getOutputClassType()));
		builder.append(' ');
		
		if (isParallelizable()) {
			builder.append("(parallelizable)");
		}
		
		return builder.toString();
	}
}
