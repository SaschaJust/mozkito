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

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.kisa.Logger;

/**
 * The Class DefaultOutputHook.
 *
 * @param <K> the key type
 * @param <V> the value type
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class DefaultOutputHook<K, V> extends OutputHook<K, V> {
	
	/**
	 * Instantiates a new default output hook.
	 *
	 * @param thread the thread
	 */
	public DefaultOutputHook(final Node<K, V> thread) {
		super(thread);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.OutputHook#output()
	 */
	/**
	 * {@inheritDoc}
	 * @see net.ownhero.dev.andama.threads.OutputHook#output()
	 */
	@Override
	public void output() {
		try {
			
			if (Logger.logDebug()) {
				Logger.debug("Providing output data: " + getThread().getOutputData());
			}
			
			getThread().write(getThread().getOutputData());
			setCompleted();
		} catch (final InterruptedException e) {
			throw new UnrecoverableError(e);
		}
	}
	
}
