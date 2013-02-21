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

/**
 * The Class OutputHook.
 *
 * @param <K> the key type
 * @param <V> the value type
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public abstract class OutputHook<K, V> extends Hook<K, V> {
	
	/**
	 * Instantiates a new output hook.
	 *
	 * @param thread the thread
	 */
	public OutputHook(final Node<K, V> thread) {
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
		output();
	}
	
	/**
	 * Output.
	 */
	public abstract void output();
}
