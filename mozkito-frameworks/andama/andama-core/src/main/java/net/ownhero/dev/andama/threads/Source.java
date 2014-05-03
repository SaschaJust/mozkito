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
 * The Class Source.
 *
 * @param <T> the generic type
 * {@link Source}s are the source elements of a tool chain. In general, these are I/O handlers that read data from some
 * source and provide it to the tool chain. All instances of {@link Source}s must have an output connector but must not
 * have an input connector.
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public abstract class Source<T> extends Node<T, T> {
	
	/**
	 * Instantiates a new source.
	 *
	 * @param threadGroup the thread group
	 * @param settings the settings
	 * @param parallelizable the parallelizable
	 * @see Node
	 */
	public Source(final Group threadGroup, final ISettings settings, final boolean parallelizable) {
		super(threadGroup, settings, parallelizable);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.AndamaThreadable#getBaseType()
	 */
	/**
	 * {@inheritDoc}
	 * @see net.ownhero.dev.andama.threads.INode#getBaseType()
	 */
	@SuppressWarnings ("rawtypes")
	@Override
	public final Class<? extends Node> getBaseType() {
		return Source.class;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread#hasInputConnector()
	 */
	/**
	 * {@inheritDoc}
	 * @see net.ownhero.dev.andama.threads.INode#hasInputConnector()
	 */
	@Override
	public final boolean hasInputConnector() {
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread#hasOutputConnector ()
	 */
	/**
	 * {@inheritDoc}
	 * @see net.ownhero.dev.andama.threads.INode#hasOutputConnector()
	 */
	@Override
	public final boolean hasOutputConnector() {
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.AndamaThread#toString()
	 */
	/**
	 * {@inheritDoc}
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
		builder.append(':').append(getTypeName(getOutputClassType()));
		builder.append(' ');
		
		if (isParallelizable()) {
			builder.append("(parallelizable)");
		}
		
		return builder.toString();
	}
}
