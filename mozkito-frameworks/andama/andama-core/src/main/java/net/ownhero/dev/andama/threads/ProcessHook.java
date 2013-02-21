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

import net.ownhero.dev.kisa.Logger;

/**
 * The Class ProcessHook.
 * 
 * @param <K>
 *            the key type
 * @param <V>
 *            the value type
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public abstract class ProcessHook<K, V> extends Hook<K, V> {
	
	/**
	 * Instantiates a new process hook.
	 * 
	 * @param thread
	 *            the thread
	 */
	public ProcessHook(final Node<K, V> thread) {
		super(thread);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.Hook#execute()
	 */
	/**
	 * {@inheritDoc}
	 * @see net.ownhero.dev.andama.threads.IHook#execute()
	 */
	@Override
	public final void execute() throws InterruptedException {
		getThread().setOutputData(null);
		process();
	}
	
	/**
	 * Guard source termination.
	 */
	private final void guardSourceTermination() {
		// skipping output data should not terminate sources
		if (Source.class.isAssignableFrom(getThread().getClass())) {
			unsetCompleted();
		}
	}
	
	/**
	 * Process.
	 */
	public abstract void process();
	
	/**
	 * Provide output data.
	 * 
	 * @param data
	 *            the data
	 */
	public final void provideOutputData(final V data) {
		if (Source.class.isAssignableFrom(getThread().getClass())) {
			if (Logger.logWarn()) {
				Logger.warn("You are calling `provideOutputData` within a ProcessHook of an AndamaSource node. This will cause the node to provide no more data. You might wanna use `providePartialOutputData` or `provideOutputData(data, false)` instead.");
			}
		}
		provideOutputData(data, true);
	}
	
	/**
	 * Provide output data.
	 * 
	 * @param data
	 *            the data
	 * @param fetchNext
	 *            the fetch next
	 */
	public final void provideOutputData(final V data,
	                                    final boolean fetchNext) {
		if (fetchNext) {
			getThread().setOutputData(data);
			setCompleted();
		} else {
			getThread().setOutputData(data);
			unsetCompleted();
		}
	}
	
	/**
	 * Provide partial output data.
	 * 
	 * @param data
	 *            the data
	 */
	public final void providePartialOutputData(final V data) {
		provideOutputData(data, false);
	}
	
	/**
	 * Skip output data.
	 */
	public final void skipOutputData() {
		getThread().setSkipData(true);
		guardSourceTermination();
	}
	
	/**
	 * Skip output data.
	 * 
	 * @param data
	 *            the data
	 */
	public final void skipOutputData(final V data) {
		getThread().setOutputData(data);
		getThread().setSkipData(true);
		guardSourceTermination();
	}
	
}
