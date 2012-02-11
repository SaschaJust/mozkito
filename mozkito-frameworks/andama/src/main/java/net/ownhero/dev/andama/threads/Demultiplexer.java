/**
 * 
 */
package net.ownhero.dev.andama.threads;

import net.ownhero.dev.andama.settings.Settings;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public abstract class Demultiplexer<K> extends Node<K, K> {
	
	/**
	 * 
	 */
	public Demultiplexer(final Group threadGroup, final Settings settings,
	        final boolean parallelizable) {
		super(threadGroup, settings, parallelizable);
		
		new ForwardProcessHook<K>(this);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.AndamaThreadable#getBaseType()
	 */
	@SuppressWarnings ("unchecked")
	@Override
	public final Class<? extends Node<K, K>> getBaseType() {
		return (Class<? extends Node<K, K>>) Demultiplexer.class;
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
		builder.append(']').append(getTypeName(getOutputClassType())).append('-');
		builder.append(' ');
		
		if (isParallelizable()) {
			builder.append("(parallelizable)");
		}
		
		return builder.toString();
	}
	
}
