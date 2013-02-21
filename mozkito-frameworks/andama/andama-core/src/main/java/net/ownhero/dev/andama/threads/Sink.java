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
 * The Class Sink.
 *
 * @param <T> the generic type
 * {@link Sink}s are the end points of a tool chain. In general, they provide a connection to a database back-end (e.g.
 * {@link RepositoryPersister} ). There can also be void sinks in case you don't want to store anything, e.g. if you
 * just want to do some analysis. {@link RepositoryVoidSink} is an example for this. All instances of {@link Sink} must
 * have an input connector, but must not have an output connector.
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public abstract class Sink<T> extends Node<T, T> {
	
	/**
	 * Instantiates a new sink.
	 *
	 * @param threadGroup the thread group
	 * @param settings the settings
	 * @param parallelizable the parallelizable
	 * @see Node
	 */
	public Sink(final Group threadGroup, final ISettings settings, final boolean parallelizable) {
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
	@SuppressWarnings ({ "rawtypes" })
	@Override
	public final Class<? extends Node> getBaseType() {
		return Sink.class;
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
		return true;
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
		return false;
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
		builder.append(getTypeName(getInputClassType())).append(':');
		builder.append(' ');
		
		if (isParallelizable()) {
			builder.append("(parallelizable)");
		}
		
		return builder.toString();
	}
}
