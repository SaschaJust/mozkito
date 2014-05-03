/***********************************************************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 **********************************************************************************************************************/
package net.ownhero.dev.andama.threads;

import net.ownhero.dev.hiari.settings.ISettings;

/**
 * The Class Multiplexer.
 * 
 * @param <K>
 *            the key type
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public abstract class Multiplexer<K> extends Node<K, K> {
	
	/**
	 * Instantiates a new multiplexer.
	 * 
	 * @param threadGroup
	 *            the thread group
	 * @param settings
	 *            the settings
	 * @param parallelizable
	 *            the parallelizable
	 */
	public Multiplexer(final Group threadGroup, final ISettings settings, final boolean parallelizable) {
		super(threadGroup, settings, parallelizable);
		
		new ForwardProcessHook<K>(this);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.AndamaThreadable#getBaseType()
	 */
	/**
	 * {@inheritDoc}
	 * 
	 * @see net.ownhero.dev.andama.threads.INode#getBaseType()
	 */
	@SuppressWarnings ("rawtypes")
	@Override
	public final Class<? extends Node> getBaseType() {
		return Multiplexer.class;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.AndamaGeneralThread#hasInputConnector()
	 */
	/**
	 * {@inheritDoc}
	 * 
	 * @see net.ownhero.dev.andama.threads.INode#hasInputConnector()
	 */
	@Override
	public final boolean hasInputConnector() {
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.AndamaGeneralThread#hasOutputConnector()
	 */
	/**
	 * {@inheritDoc}
	 * 
	 * @see net.ownhero.dev.andama.threads.INode#hasOutputConnector()
	 */
	@Override
	public final boolean hasOutputConnector() {
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see net.ownhero.dev.andama.threads.Node#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		
		builder.append('[').append(getThreadGroup().getName()).append("] ");
		
		builder.append(getHandle());
		
		if ((this.getClass().getSuperclass() != null) && Node.class.isAssignableFrom(this.getClass().getSuperclass())) {
			builder.append(' ').append(this.getClass().getSuperclass().getSimpleName());
		}
		
		builder.append(' ');
		builder.append('-').append(getTypeName(getInputClassType())).append('[');
		builder.append(' ');
		
		if (isParallelizable()) {
			builder.append("(parallelizable)");
		}
		
		return builder.toString();
	}
	
}
