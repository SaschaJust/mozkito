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
 * The Class ForwardProcessHook.
 *
 * @param <K> the key type
 * @author just
 */
public class ForwardProcessHook<K> extends ProcessHook<K, K> {
	
	/**
	 * Instantiates a new forward process hook.
	 *
	 * @param thread the thread
	 */
	public ForwardProcessHook(final Node<K, K> thread) {
		super(thread);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.ProcessHook#process()
	 */
	/**
	 * {@inheritDoc}
	 * @see net.ownhero.dev.andama.threads.ProcessHook#process()
	 */
	@Override
	public void process() {
		final K data = getThread().getInputData();
		
		if (Logger.logDebug()) {
			Logger.debug("Providing output data: " + data);
		}
		provideOutputData(data);
	}
	
}
